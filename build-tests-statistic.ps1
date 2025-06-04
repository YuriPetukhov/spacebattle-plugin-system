<#
.SYNOPSIS
    Test coverage statistics with summary report
#>

$global:stats = @()

function Get-CoverageColor {
    param($percent)
    if ($percent -ge 80) { return "Green" }
    elseif ($percent -ge 50) { return "Yellow" }
    else { return "Red" }
}

Write-Host "`n=== GENERATING TEST REPORTS ===`n" -ForegroundColor Cyan

Get-ChildItem -Directory | Where-Object {
    Test-Path "$($_.FullName)/pom.xml" -PathType Leaf
} | ForEach-Object {
    $project = $_.Name
    Write-Host "Analyzing: $project" -ForegroundColor Yellow

    try {
        Push-Location $_.FullName

        if (Select-String -Path "pom.xml" -Pattern "jacoco-maven-plugin" -Quiet) {
            mvn test jacoco:report | Out-Null
            $reportPath = "target/site/jacoco/jacoco.csv"

            if (Test-Path $reportPath) {
                $csv = Import-Csv $reportPath -Delimiter ','
                $total = $csv | Where-Object {
                    $_.GROUP -eq "Total" -and $_.PACKAGE -eq ""
                }

                if ($total) {
                    $missed = [int]$total.INSTRUCTION_MISSED
                    $covered = [int]$total.INSTRUCTION_COVERED
                    $percentage = [math]::Round(($covered / ($covered + $missed)) * 100, 2)

                    $global:stats += [PSCustomObject]@{
                        Project = $project
                        Coverage = "$percentage%"
                        Covered = $covered
                        Missed = $missed
                        Total = $covered + $missed
                        Color = Get-CoverageColor $percentage
                    }

                    Write-Host "Success | Coverage: $percentage%" -ForegroundColor Green
                }
            }
        }
        else {
            Write-Host "JaCoCo not configured" -ForegroundColor Magenta
        }
    }
    finally {
        Pop-Location
    }
}

Write-Host "`n=== SUMMARY REPORT ===" -ForegroundColor Cyan

if ($global:stats.Count -gt 0) {
    $global:stats | Format-Table -AutoSize -Property `
        @{Label="Project"; Expression={$_.Project}},
        @{Label="Coverage"; Expression={$_.Coverage}; Align="Right"},
        @{Label="Covered/Missed"; Expression={"$($_.Covered)/$($_.Missed)"}}

    $totalCovered = ($global:stats | Measure-Object -Property Covered -Sum).Sum
    $totalMissed = ($global:stats | Measure-Object -Property Missed -Sum).Sum
    $overallCoverage = [math]::Round($totalCovered / ($totalCovered + $totalMissed) * 100, 2)
    $overallColor = Get-CoverageColor $overallCoverage

    Write-Host "`nTOTAL COVERAGE: $overallCoverage% ($totalCovered/$($totalCovered + $totalMissed))" -ForegroundColor $overallColor
}
else {
    Write-Host "No coverage data found" -ForegroundColor Red
}

Write-Host "`n=== ANALYSIS COMPLETED ===" -ForegroundColor Green