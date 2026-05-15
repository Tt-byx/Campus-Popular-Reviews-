# 数据库管理工具
# 使用方法: .\scripts\db-manager.ps1 [command]

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
# 可用命令: backup / restore / migrate / reset / status / help

param(
    [Parameter(Position=0)]
    [ValidateSet("backup", "restore", "migrate", "reset", "status", "help")]
    [string]$Command = "help"
)

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

function Show-Help {
    Write-Host "数据库管理工具" -ForegroundColor Cyan
    Write-Host "================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "可用命令:" -ForegroundColor Yellow
    Write-Host "  backup   - 备份数据库" -ForegroundColor Gray
    Write-Host "  restore  - 恢复数据库" -ForegroundColor Gray
    Write-Host "  migrate  - 创建迁移文件" -ForegroundColor Gray
    Write-Host "  reset    - 重置数据库" -ForegroundColor Gray
    Write-Host "  status   - 查看数据库状态" -ForegroundColor Gray
    Write-Host "  help     - 显示帮助信息" -ForegroundColor Gray
    Write-Host ""
    Write-Host "使用示例:" -ForegroundColor Yellow
    Write-Host "  .\scripts\db-manager.ps1 backup" -ForegroundColor Gray
    Write-Host "  .\scripts\db-manager.ps1 restore -BackupFile 'backups\backup.sql'" -ForegroundColor Gray
    Write-Host "  .\scripts\db-manager.ps1 migrate" -ForegroundColor Gray
}

function Backup-Database {
    Write-Host "执行数据库备份..." -ForegroundColor Green
    & "$ScriptDir\backup-database.ps1"
}

function Restore-Database {
    param([string]$BackupFile)
    
    if (!$BackupFile) {
        Write-Host "错误: 请指定备份文件" -ForegroundColor Red
        Write-Host "使用方法: .\scripts\db-manager.ps1 restore -BackupFile 'backups\backup.sql'" -ForegroundColor Yellow
        return
    }
    
    Write-Host "执行数据库恢复..." -ForegroundColor Green
    & "$ScriptDir\restore-database.ps1" -BackupFile $BackupFile
}

function Migrate-Database {
    Write-Host "创建数据库迁移..." -ForegroundColor Green
    & "$ScriptDir\migrate-database.ps1"
}

function Reset-Database {
    Write-Host "警告: 此操作将删除所有数据!" -ForegroundColor Yellow
    $Confirm = Read-Host "是否继续? (y/N)"
    
    if ($Confirm -ne "y" -and $Confirm -ne "Y") {
        Write-Host "操作已取消" -ForegroundColor Gray
        return
    }
    
    Write-Host "重置数据库..." -ForegroundColor Green
    
    mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "DROP DATABASE IF EXISTS hmdb;"
    mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "CREATE DATABASE hmdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
    mysql -h 127.0.0.1 -P 3306 -u root -p123456 hmdb < "$ScriptDir\..\backend\src\main\resources\schema.sql"
    
    Write-Host "数据库重置完成!" -ForegroundColor Green
}

function Get-DatabaseStatus {
    Write-Host "数据库状态:" -ForegroundColor Cyan
    Write-Host "================" -ForegroundColor Cyan
    
    try {
        $Result = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT 1;" 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "MySQL连接: 正常" -ForegroundColor Green
        } else {
            Write-Host "MySQL连接: 失败" -ForegroundColor Red
            return
        }
    } catch {
        Write-Host "MySQL连接: 失败" -ForegroundColor Red
        return
    }
    
    $DbExists = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'hmdb';" 2>&1
    if ($DbExists -match "hmdb") {
        Write-Host "数据库: 存在" -ForegroundColor Green
    } else {
        Write-Host "数据库: 不存在" -ForegroundColor Red
        return
    }
    
    $TableCount = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'hmdb';" 2>&1
    Write-Host "表数量: $TableCount" -ForegroundColor Yellow
    
    $UserCount = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT COUNT(*) FROM hmdb.user;" 2>&1
    Write-Host "用户数量: $UserCount" -ForegroundColor Yellow
    
    $PostCount = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT COUNT(*) FROM hmdb.post;" 2>&1
    Write-Host "帖子数量: $PostCount" -ForegroundColor Yellow
}

switch ($Command) {
    "backup"    { Backup-Database }
    "restore"   { Restore-Database -BackupFile $args[0] }
    "migrate"   { Migrate-Database }
    "reset"     { Reset-Database }
    "status"    { Get-DatabaseStatus }
    "help"      { Show-Help }
    default     { Show-Help }
}
