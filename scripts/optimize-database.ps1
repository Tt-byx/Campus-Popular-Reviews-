# 数据库性能优化脚本
# 使用方法: .\scripts\optimize-database.ps1 [tips]

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$LogDir = "$ScriptDir\..\logs"
$OptimizationLog = "$LogDir\database-optimization.log"

if (!(Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force
}

function Write-Log {
    param([string]$Message)

    $Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $LogEntry = "[$Timestamp] $Message"

    Add-Content -Path $OptimizationLog -Value $LogEntry -Encoding UTF8
    Write-Host $LogEntry -ForegroundColor Gray
}

function Optimize-Database {
    Write-Host "开始数据库性能优化..." -ForegroundColor Green

    Write-Host "1. 分析表结构..." -ForegroundColor Yellow
    $Tables = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW TABLES FROM hmdb;" 2>&1

    foreach ($Table in $Tables) {
        if ($Table -match "^(\w+)$") {
            $TableName = $Matches[1]
            Write-Log "分析表: $TableName"
            mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "ANALYZE TABLE hmdb.$TableName;" 2>&1
        }
    }

    Write-Host "2. 优化表结构..." -ForegroundColor Yellow
    foreach ($Table in $Tables) {
        if ($Table -match "^(\w+)$") {
            $TableName = $Matches[1]
            Write-Log "优化表: $TableName"
            mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "OPTIMIZE TABLE hmdb.$TableName;" 2>&1
        }
    }

    Write-Host "3. 检查索引使用情况..." -ForegroundColor Yellow
    $IndexUsage = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            TABLE_NAME,
            INDEX_NAME,
            CARDINALITY
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = 'hmdb'
        ORDER BY CARDINALITY DESC;" 2>&1

    Write-Log "索引使用情况:"
    Write-Log $IndexUsage

    Write-Host "4. 检查慢查询配置..." -ForegroundColor Yellow
    $SlowQueryConfig = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE 'slow_query%';" 2>&1
    Write-Log "慢查询配置"
    Write-Log $SlowQueryConfig

    Write-Host "5. 检查查询缓存..." -ForegroundColor Yellow
    $QueryCache = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE 'query_cache%';" 2>&1
    Write-Log "查询缓存配置:"
    Write-Log $QueryCache

    Write-Host "6. 检查连接池配置..." -ForegroundColor Yellow
    $ConnectionConfig = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE 'max_connections';" 2>&1
    Write-Log "最大连接数:"
    Write-Log $ConnectionConfig

    Write-Host "7. 检查表空间使用情况..." -ForegroundColor Yellow
    $Tablespace = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            TABLE_NAME,
            ROUND(DATA_LENGTH/1024/1024, 2) as 'DATA_MB',
            ROUND(INDEX_LENGTH/1024/1024, 2) as 'INDEX_MB',
            ROUND(DATA_FREE/1024/1024, 2) as 'FREE_MB'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA = 'hmdb'
        ORDER BY DATA_LENGTH DESC;" 2>&1

    Write-Log "表空间使用情况"
    Write-Log $Tablespace

    Write-Host "8. 检查表碎片..." -ForegroundColor Yellow
    $Fragmentation = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            TABLE_NAME,
            ROUND(DATA_FREE/1024/1024, 2) as 'FRAGMENT_MB'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA = 'hmdb' AND DATA_FREE > 0
        ORDER BY DATA_FREE DESC;" 2>&1

    Write-Log "表碎片情况"
    Write-Log $Fragmentation

    Write-Host "数据库优化完成" -ForegroundColor Green
}

function Show-OptimizationTips {
    Write-Host "数据库性能优化建议:" -ForegroundColor Cyan
    Write-Host "====================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. 索引优化:" -ForegroundColor Yellow
    Write-Host "   - 为常用查询字段添加索引" -ForegroundColor Gray
    Write-Host "   - 避免过度索引" -ForegroundColor Gray
    Write-Host "   - 定期分析索引使用情况" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. 查询优化:" -ForegroundColor Yellow
    Write-Host "   - 使用EXPLAIN分析查询计划" -ForegroundColor Gray
    Write-Host "   - 避免SELECT *查询" -ForegroundColor Gray
    Write-Host "   - 使用JOIN代替子查询" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. 配置优化:" -ForegroundColor Yellow
    Write-Host "   - 调整innodb_buffer_pool_size" -ForegroundColor Gray
    Write-Host "   - 配置合适的max_connections" -ForegroundColor Gray
    Write-Host "   - 启用慢查询日志" -ForegroundColor Gray
    Write-Host ""
    Write-Host "4. 维护优化:" -ForegroundColor Yellow
    Write-Host "   - 定期执行ANALYZE TABLE" -ForegroundColor Gray
    Write-Host "   - 定期执行OPTIMIZE TABLE" -ForegroundColor Gray
    Write-Host "   - 监控表空间使用情况" -ForegroundColor Gray
}

if ($args[0] -eq "tips") {
    Show-OptimizationTips
} else {
    Optimize-Database
}
