<#
.SYNOPSIS
    Управляемый сбор Maven проектов с приоритетом core и server модулей
#>

$ErrorActionPreference = "Stop"

# Определяем порядок сборки
$orderedProjects = @(
    "core",    # Первым собираем core
    "server",  # Затем server
    "*"        # Все остальные проекты
)

# 1. Сборка проектов в заданном порядке
Write-Host "=== BUILDING PROJECTS IN ORDER ===" -ForegroundColor Cyan

foreach ($projectPattern in $orderedProjects) {
    Get-ChildItem -Directory | Where-Object {
        # Для "*" выбираем все проекты, кроме уже собранных
        if ($projectPattern -eq "*") {
            $_.Name -notin ("core", "server") -and
            (Test-Path "$($_.FullName)/pom.xml" -PathType Leaf)
        }
        else {
            $_.Name -eq $projectPattern -and
            (Test-Path "$($_.FullName)/pom.xml" -PathType Leaf)
        }
    } | ForEach-Object {
        $project = $_.Name
        Write-Host "`nBUILDING PROJECT: $project" -ForegroundColor Yellow

        try {
            Push-Location $_.FullName
            mvn clean install
        }
        catch {
            Write-Host "BUILD FAILED for $project`: $_" -ForegroundColor Red
            throw
        }
        finally {
            Pop-Location
        }
    }
}

# 2. Генерация отчётов о покрытии
Write-Host "`n=== GENERATING COVERAGE REPORTS ===" -ForegroundColor Cyan

Get-ChildItem -Directory | Where-Object {
    Test-Path "$($_.FullName)/pom.xml" -PathType Leaf
} | ForEach-Object {
    $project = $_.Name
    Write-Host "`nANALYZING COVERAGE FOR: $project" -ForegroundColor Yellow

    try {
        Push-Location $_.FullName

        # Генерация отчёта JaCoCo
        if (Test-Path "target/jacoco.exec") {
            mvn org.jacoco:jacoco-maven-plugin:report

            $csvPath = "target/site/jacoco/jacoco.csv"
            if (Test-Path $csvPath) {
                $csv = Import-Csv $csvPath -Delimiter ','
                $total = $csv | Where-Object { $_.GROUP -eq "Total" -and $_.PACKAGE -eq "" }

                if ($total) {
                    $missed = [int]$total.INSTRUCTION_MISSED
                    $covered = [int]$total.INSTRUCTION_COVERED
                    $percent = if ($covered + $missed -gt 0) {
                        [math]::Round(($covered / ($covered + $missed)) * 100, 2)
                    } else { 0 }

                    Write-Host "Coverage: $percent% ($covered/$($covered + $missed))" -ForegroundColor Green
                }
            }
        } else {
            Write-Host "No coverage data found" -ForegroundColor DarkGray
        }
    }
    catch {
        Write-Host "COVERAGE ANALYSIS FAILED: $_" -ForegroundColor Red
    }
    finally {
        Pop-Location
    }
}

Write-Host "`n=== BUILD AND ANALYSIS COMPLETED ===" -ForegroundColor Green