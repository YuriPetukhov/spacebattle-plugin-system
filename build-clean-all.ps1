<#
.SYNOPSIS
  Очищает все папки target в проектах
#>

Write-Host "=== Очистка папок target ===" -ForegroundColor Cyan

# 1. Удаление папок target
Get-ChildItem -Directory -Recurse -Path "." -Filter "target" |
Where-Object { $_.FullName -notlike "*\node_modules\*" } |
ForEach-Object {
    $targetPath = $_.FullName
    Write-Host "Удаление: $targetPath" -ForegroundColor Yellow
    try {
        Remove-Item -Path $targetPath -Recurse -Force -ErrorAction Stop
        Write-Host "Успешно удалено" -ForegroundColor Green
    }
    catch {
        Write-Host "Ошибка при удалении: $_" -ForegroundColor Red
    }
}