# Frontend Build Script
# Usage: .\scripts\build-frontend.ps1

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$FrontendDir = "$ScriptDir\..\frontend"

Write-Host "Building frontend project..." -ForegroundColor Green
Write-Host "Frontend directory: $FrontendDir" -ForegroundColor Gray

Push-Location $FrontendDir

try {
    Write-Host "Installing dependencies..." -ForegroundColor Yellow
    npm install

    Write-Host "Building production version..." -ForegroundColor Yellow
    npm run build

    Write-Host "Frontend build completed!" -ForegroundColor Green
    Write-Host "Output directory: frontend/dist" -ForegroundColor Cyan
} finally {
    Pop-Location
}
