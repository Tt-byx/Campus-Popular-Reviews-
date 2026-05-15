# 数据库性能监控仪表板
# 使用方法: .\scripts\database-dashboard.ps1

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$RefreshInterval = 5

function Get-DatabaseStatus {
    $Status = @{}

    try {
        $ServerStatus = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW STATUS;" 2>&1
        $Status.ServerStatus = $ServerStatus

        $ServerVariables = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES;" 2>&1
        $Status.ServerVariables = $ServerVariables

        $ProcessList = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW PROCESSLIST;" 2>&1
        $Status.ProcessList = $ProcessList

        $TableStatus = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
            SELECT
                TABLE_NAME,
                TABLE_ROWS,
                DATA_LENGTH,
                INDEX_LENGTH,
                DATA_FREE
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_SCHEMA = 'hmdb';" 2>&1
        $Status.TableStatus = $TableStatus

        return $Status

    } catch {
        Write-Host "获取数据库状态失败: $_" -ForegroundColor Red
        return $null
    }
}

function Show-Dashboard {
    Clear-Host

    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "       数据库性能监控仪表板" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""

    $Status = Get-DatabaseStatus

    if ($Status) {
        Write-Host "服务器状态" -ForegroundColor Yellow
        Write-Host "----------------" -ForegroundColor Yellow

        $Uptime = ($Status.ServerStatus | Select-String "Uptime\s+(\d+)").Matches.Groups[1].Value
        $Connections = ($Status.ServerStatus | Select-String "Threads_connected\s+(\d+)").Matches.Groups[1].Value
        $Queries = ($Status.ServerStatus | Select-String "Queries\s+(\d+)").Matches.Groups[1].Value
        $SlowQueries = ($Status.ServerStatus | Select-String "Slow_queries\s+(\d+)").Matches.Groups[1].Value

        Write-Host "运行时间: $Uptime 秒" -ForegroundColor Gray
        Write-Host "当前连接: $Connections" -ForegroundColor Gray
        Write-Host "查询总数: $Queries" -ForegroundColor Gray
        Write-Host "慢查询数: $SlowQueries" -ForegroundColor Gray

        Write-Host ""

        Write-Host "表状态" -ForegroundColor Yellow
        Write-Host "----------------" -ForegroundColor Yellow

        $Status.TableStatus | ForEach-Object {
            if ($_ -match "^(\w+)\s+(\d+)\s+(\d+)\s+(\d+)\s+(\d+)$") {
                $TableName = $Matches[1]
                $TableRows = $Matches[2]
                $DataLength = [math]::Round($Matches[3]/1024/1024, 2)
                $IndexLength = [math]::Round($Matches[4]/1024/1024, 2)
                $DataFree = [math]::Round($Matches[5]/1024/1024, 2)

                Write-Host "$TableName:" -ForegroundColor Gray
                Write-Host "  行数: $TableRows" -ForegroundColor Gray
                Write-Host "  数据: $DataLength MB" -ForegroundColor Gray
                Write-Host "  索引: $IndexLength MB" -ForegroundColor Gray
                Write-Host "  碎片: $DataFree MB" -ForegroundColor Gray
            }
        }

        Write-Host ""

        Write-Host "活动进程:" -ForegroundColor Yellow
        Write-Host "----------------" -ForegroundColor Yellow

        $ProcessCount = ($Status.ProcessList | Measure-Object -Line).Lines - 1
        Write-Host "活动进程数: $ProcessCount" -ForegroundColor Gray

        Write-Host ""

        Write-Host "刷新间隔: $RefreshInterval 秒" -ForegroundColor Gray
        Write-Host "按 Ctrl+C 退出" -ForegroundColor Gray

    } else {
        Write-Host "无法获取数据库状态" -ForegroundColor Red
    }
}

function Start-Dashboard {
    Write-Host "启动数据库监控仪表板..." -ForegroundColor Green
    Write-Host "按 Ctrl+C 退出" -ForegroundColor Yellow

    while ($true) {
        Show-Dashboard
        Start-Sleep -Seconds $RefreshInterval
    }
}

Start-Dashboard
