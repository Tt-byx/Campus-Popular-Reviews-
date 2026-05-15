# 数据库恢复脚本
# 使用方法: .\scripts\restore-database.ps1 -BackupFile "backups\hmdb_backup_20240101_120000.sql"

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

param(
    [Parameter(Mandatory=$true)]
    [string]$BackupFile
)

if (!(Test-Path $BackupFile)) {
    Write-Host "错误: 备份文件不存在: $BackupFile" -ForegroundColor Red
    exit 1
}

Write-Host "警告: 此操作将覆盖现有数据库!" -ForegroundColor Yellow
$Confirm = Read-Host "是否继续? (y/N)"

if ($Confirm -ne "y" -and $Confirm -ne "Y") {
    Write-Host "操作已取消" -ForegroundColor Gray
    exit 0
}

Write-Host "开始恢复数据库..." -ForegroundColor Green

try {
    mysql -h 127.0.0.1 -P 3306 -u root -p123456 hmdb < $BackupFile

    Write-Host "恢复成功!" -ForegroundColor Green
    Write-Host "已恢复备份文件: $BackupFile" -ForegroundColor Yellow
} catch {
    Write-Host "恢复过程中发生错误: $_" -ForegroundColor Red
    exit 1
}

Write-Host "恢复完成!" -ForegroundColor Green
