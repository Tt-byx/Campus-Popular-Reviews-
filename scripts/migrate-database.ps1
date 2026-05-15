# 数据库迁移脚本
# 使用方法: .\scripts\migrate-database.ps1

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$MigrationDir = "$ScriptDir\..\migrations"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$MigrationFile = "$MigrationDir\V${Timestamp}_migration.sql"

if (!(Test-Path $MigrationDir)) {
    New-Item -ItemType Directory -Path $MigrationDir -Force
}

$MigrationTemplate = @"
-- 数据库迁移脚本
-- 创建时间: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
-- 描述: [请添加迁移描述]

-- 在此添加迁移SQL语句
-- 示例:
-- ALTER TABLE user ADD COLUMN new_column VARCHAR(100) DEFAULT NULL COMMENT '新字段';
-- CREATE INDEX idx_user_email ON user(email);

"@

Set-Content -Path $MigrationFile -Value $MigrationTemplate -Encoding UTF8

Write-Host "迁移文件已创建: $MigrationFile" -ForegroundColor Green
Write-Host "请编辑迁移文件，添加SQL语句后执行" -ForegroundColor Yellow

Write-Host "`n现有迁移文件:" -ForegroundColor Cyan
Get-ChildItem $MigrationDir -Filter "*.sql" | Sort-Object Name | ForEach-Object {
    Write-Host "  $($_.Name)" -ForegroundColor Gray
}
