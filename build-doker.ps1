# 3. Build Docker images
Write-Host "=== Building Docker images ===" -ForegroundColor Cyan

$dockerProjects = @(
    @{Path="."; ImageName="spacebattle-server"},
    @{Path="./gateway-service"; ImageName="spacebattle-gateway"}
)

foreach ($project in $dockerProjects) {
    Write-Host "Building $($project.ImageName)" -ForegroundColor Yellow
    try {
        docker build -t $($project.ImageName) -f "$($project.Path)/Dockerfile" $($project.Path)
        if ($LASTEXITCODE -ne 0) { throw "Docker build failed" }
    }
    catch {
        Write-Host "ERROR building $($project.ImageName): $_" -ForegroundColor Red
        exit 1
    }
}