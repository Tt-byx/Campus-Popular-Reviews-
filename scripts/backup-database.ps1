# 数据库备份脚本
# 使用方法: .\scripts\backup-database.ps1

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$BackupDir = "$ScriptDir\..\backups"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupFile = "$BackupDir\hmdb_backup_$Timestamp.sql"

if (!(Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force
}

Write-Host "开始备份数据库..." -ForegroundColor Green

try {
    mysqldump -h 127.0.0.1 -P 3306 -u root -p123456 hmdb > $BackupFile

    if (Test-Path $BackupFile) {
        $FileSize = (Get-Item $BackupFile).Length / 1KB
        Write-Host "备份成功!" -ForegroundColor Green
        Write-Host "备份文件: $BackupFile" -ForegroundColor Yellow
        Write-Host "文件大小: $FileSize KB" -ForegroundColor Yellow
    } else {
        Write-Host "备份失败!" -ForegroundColor Red
    }
} catch {
    Write-Host "备份过程中发生错误: $_" -ForegroundColor Red
}

$RetentionDays = 30
$CutoffDate = (Get-Date).AddDays(-$RetentionDays)

Get-ChildItem $BackupDir -Filter "*.sql" | Where-Object {
    $_.LastWriteTime -lt $CutoffDate
} | ForEach-Object {
    Write-Host "删除旧备份: $($_.Name)" -ForegroundColor Gray
    Remove-Item $_.FullName
}

Write-Host "备份完成!" -ForegroundColor Green
