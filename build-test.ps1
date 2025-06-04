<#
.SYNOPSIS
    Анализирует покрытие кода тестами в Maven-проектах с использованием JaCoCo
.DESCRIPTION
    Ищет все проекты с pom.xml, собирает их с генерацией отчетов JaCoCo,
    затем анализирует и агрегирует результаты покрытия.
#>

$global:stats = @()
$projects = @()

function Get-CoverageColor {
    param($percent)
    switch ($percent) {
        { $_ -ge 80 } { return "Green" }
        { $_ -ge 50 } { return "Yellow" }
        default { return "Red" }
    }
}

function Get-ProjectName {
    param($path)
    return (Get-Item $path).Name
}

function Find-JaCoCoReport {
    param($projectPath)
    $possiblePaths = @(
        "target/site/jacoco/jacoco.csv",
        "target/site/jacoco-aggregate/jacoco.csv",
        "target/jacoco-report/jacoco.csv",
        "target/coverage-report/jacoco.csv"
    )

    foreach ($path in $possiblePaths) {
        $fullPath = Join-Path $projectPath $path
        if (Test-Path $fullPath) {
            return $fullPath
        }
    }
    return $null
}

function Process-CoverageReport {
    param($reportPath, $projectName)

    try {
        $csv = Import-Csv $reportPath -Delimiter ','
        $total = $csv | Where-Object { $_.GROUP -eq "Total" -and $_.PACKAGE -eq "" }

        if ($total) {
            $missed = [int]$total.INSTRUCTION_MISSED
            $covered = [int]$total.INSTRUCTION_COVERED
        } else {
            Write-Host "  No Total record, calculating sum..." -ForegroundColor Yellow
            $missed = ($csv | Measure-Object -Property INSTRUCTION_MISSED -Sum).Sum
            $covered = ($csv | Measure-Object -Property INSTRUCTION_COVERED -Sum).Sum
        }

        if ($covered + $missed -eq 0) {
            Write-Host "  No coverage data found (all zeros)" -ForegroundColor Red
            return $null
        }

        $percentage = [math]::Round(($covered / ($covered + $missed)) * 100, 2)

        return [PSCustomObject]@{
            Project = $projectName
            Coverage = "$percentage%"
            Covered = $covered
            Missed = $missed
            Total = $covered + $missed
            Color = Get-CoverageColor $percentage
        }
    }
    catch {
        Write-Host "  Error processing report: $_" -ForegroundColor Red
        return $null
    }
}

# Шаг 1: Поиск проектов
Write-Host "`n=== DISCOVERING PROJECTS ===`n" -ForegroundColor Cyan
Get-ChildItem -Directory -Recurse -Depth 2 | Where-Object {
    Test-Path "$($_.FullName)/pom.xml" -PathType Leaf
} | ForEach-Object {
    $projects += $_.FullName
    Write-Host "Found project: $(Get-ProjectName $_.FullName)" -ForegroundColor DarkGray
}

if ($projects.Count -eq 0) {
    Write-Host "No projects found with pom.xml" -ForegroundColor Red
    exit 1
}

# Шаг 2: Сборка проектов
Write-Host "`n=== BUILDING ALL PROJECTS ===`n" -ForegroundColor Cyan
try {
    mvn clean verify -fae -DskipTests=false | Tee-Object -FilePath "full_build.log"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
}
catch {
    Write-Host "Build failed: $_" -ForegroundColor Red
    exit 1
}

# Шаг 3: Анализ отчетов
Write-Host "`n=== ANALYZING COVERAGE REPORTS ===`n" -ForegroundColor Cyan
foreach ($projectPath in $projects) {
    $projectName = Get-ProjectName $projectPath
    Write-Host "Processing: $projectName" -ForegroundColor Yellow

    $reportPath = Find-JaCoCoReport $projectPath
    if (-not $reportPath) {
        Write-Host "  No JaCoCo report found" -ForegroundColor Red
        continue
    }

    Write-Host "  Found report at: $reportPath" -ForegroundColor Green
    $result = Process-CoverageReport $reportPath $projectName
    if ($result) {
        $global:stats += $result
        Write-Host "  Coverage: $($result.Coverage) ($($result.Covered)/$($result.Total))" -ForegroundColor $result.Color
    }
}

# Шаг 4: Итоговый отчет
Write-Host "`n=== SUMMARY REPORT ===`n" -ForegroundColor Cyan

if ($global:stats.Count -gt 0) {
    $global:stats | Sort-Object -Property Project | Format-Table -AutoSize -Property @(
        @{Label="Project"; Expression={$_.Project}}
        @{Label="Coverage"; Expression={$_.Coverage}; Align="Right"}
        @{Label="Covered"; Expression={$_.Covered}; Align="Right"}
        @{Label="Missed"; Expression={$_.Missed}; Align="Right"}
        @{Label="Total"; Expression={$_.Total}; Align="Right"}
    )

    $totalCovered = ($global:stats | Measure-Object -Property Covered -Sum).Sum
    $totalMissed = ($global:stats | Measure-Object -Property Missed -Sum).Sum

    if (($totalCovered + $totalMissed) -gt 0) {
        $overallCoverage = [math]::Round($totalCovered / ($totalCovered + $totalMissed) * 100, 2)
        $overallColor = Get-CoverageColor $overallCoverage
        Write-Host "`nTOTAL COVERAGE: $overallCoverage% ($totalCovered/$($totalCovered + $totalMissed))" -ForegroundColor $overallColor
    } else {
        Write-Host "`nNo valid coverage data for aggregation" -ForegroundColor Red
    }
}
else {
    Write-Host "No coverage data found in any project" -ForegroundColor Red
}

Write-Host "`n=== ANALYSIS COMPLETED ===`n" -ForegroundColor Green