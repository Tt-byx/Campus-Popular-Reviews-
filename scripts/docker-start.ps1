# Docker Startup Script
# Usage: .\scripts\docker-start.ps1

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$ProjectRoot = "$ScriptDir\.."

Write-Host "Starting Docker containers..." -ForegroundColor Green

try {
    docker info | Out-Null
    Write-Host "Docker is running" -ForegroundColor Green
} catch {
    Write-Host "Error: Docker is not running. Please start Docker Desktop" -ForegroundColor Red
    exit 1
}

Write-Host "Building frontend project..." -ForegroundColor Yellow
& "$ScriptDir\build-frontend.ps1"

Push-Location $ProjectRoot

try {
    Write-Host "Building and starting Docker containers..." -ForegroundColor Yellow
    docker-compose up -d --build

    Write-Host "Docker containers started successfully!" -ForegroundColor Green
    Write-Host "Frontend: http://localhost" -ForegroundColor Cyan
    Write-Host "Backend API: http://localhost:10086" -ForegroundColor Cyan
    Write-Host "Database: localhost:3307" -ForegroundColor Cyan
} finally {
    Pop-Location
}
