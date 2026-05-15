# 数据库监控脚本
# 使用方法: .\scripts\monitor-database.ps1 [once]

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$MonitorInterval = 60
$LogDir = "$ScriptDir\..\logs"
$LogFile = "$LogDir\database-monitor.log"

if (!(Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force
}

function Write-Log {
    param([string]$Message)

    $Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $LogEntry = "[$Timestamp] $Message"

    Add-Content -Path $LogFile -Value $LogEntry -Encoding UTF8
    Write-Host $LogEntry -ForegroundColor Gray
}

function Get-DatabaseMetrics {
    $Metrics = @{}

    try {
        $Connections = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW STATUS LIKE 'Threads_connected';" 2>&1
        if ($Connections -match "Threads_connected\s+(\d+)") {
            $Metrics.Connections = $Matches[1]
        }

        $Queries = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW STATUS LIKE 'Queries';" 2>&1
        if ($Queries -match "Queries\s+(\d+)") {
            $Metrics.Queries = $Matches[1]
        }

        $SlowQueries = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW STATUS LIKE 'Slow_queries';" 2>&1
        if ($SlowQueries -match "Slow_queries\s+(\d+)") {
            $Metrics.SlowQueries = $Matches[1]
        }

        $TableSizes = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
            SELECT
                TABLE_NAME,
                ROUND(DATA_LENGTH/1024/1024, 2) as 'DATA_MB',
                ROUND(INDEX_LENGTH/1024/1024, 2) as 'INDEX_MB'
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_SCHEMA = 'hmdb'
            ORDER BY DATA_LENGTH DESC;" 2>&1

        $Metrics.TableSizes = $TableSizes

        return $Metrics

    } catch {
        Write-Log "获取数据库指标失败: $_"
        return $null
    }
}

function Monitor-Database {
    Write-Host "开始数据库监控..." -ForegroundColor Green
    Write-Host "监控间隔: $MonitorInterval 秒" -ForegroundColor Yellow
    Write-Host "日志文件: $LogFile" -ForegroundColor Yellow
    Write-Host ""

    while ($true) {
        $Metrics = Get-DatabaseMetrics

        if ($Metrics) {
            Write-Log "连接数: $($Metrics.Connections)"
            Write-Log "查询数: $($Metrics.Queries)"
            Write-Log "慢查询数: $($Metrics.SlowQueries)"
            Write-Log "---"
        }

        Start-Sleep -Seconds $MonitorInterval
    }
}

if ($args[0] -eq "once") {
    $Metrics = Get-DatabaseMetrics

    if ($Metrics) {
        Write-Host "数据库指标" -ForegroundColor Cyan
        Write-Host "================" -ForegroundColor Cyan
        Write-Host "连接数: $($Metrics.Connections)" -ForegroundColor Yellow
        Write-Host "查询数: $($Metrics.Queries)" -ForegroundColor Yellow
        Write-Host "慢查询数: $($Metrics.SlowQueries)" -ForegroundColor Yellow

        if ($Metrics.TableSizes) {
            Write-Host "`n表大小:" -ForegroundColor Cyan
            Write-Host $Metrics.TableSizes
        }
    }
} else {
    Monitor-Database
}
