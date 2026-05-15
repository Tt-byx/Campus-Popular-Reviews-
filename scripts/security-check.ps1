# 数据库安全检查脚本
# 使用方法: .\scripts\security-check.ps1 [tips]

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$LogDir = "$ScriptDir\..\logs"
$SecurityLog = "$LogDir\database-security.log"

if (!(Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force
}

function Write-Log {
    param([string]$Message)

    $Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $LogEntry = "[$Timestamp] $Message"

    Add-Content -Path $SecurityLog -Value $LogEntry -Encoding UTF8
    Write-Host $LogEntry -ForegroundColor Gray
}

function Check-Security {
    Write-Host "开始数据库安全检查..." -ForegroundColor Green
    Write-Host ""

    Write-Host "1. 检查用户权限..." -ForegroundColor Yellow
    $Users = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            User,
            Host,
            Super_priv,
            Create_priv,
            Drop_priv,
            Grant_priv
        FROM mysql.user;" 2>&1

    Write-Log "用户权限检查"
    Write-Log $Users

    Write-Host "2. 检查密码策略..." -ForegroundColor Yellow
    $PasswordPolicy = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE 'validate_password%';" 2>&1
    Write-Log "密码策略:"
    Write-Log $PasswordPolicy

    Write-Host "3. 检查连接安全..." -ForegroundColor Yellow
    $SSLConfig = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE '%ssl%';" 2>&1
    Write-Log "SSL配置:"
    Write-Log $SSLConfig

    Write-Host "4. 检查审计日志..." -ForegroundColor Yellow
    $AuditLog = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE '%audit%';" 2>&1
    Write-Log "审计日志配置:"
    Write-Log $AuditLog

    Write-Host "5. 检查二进制日志..." -ForegroundColor Yellow
    $BinaryLog = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE '%binlog%';" 2>&1
    Write-Log "二进制日志配置"
    Write-Log $BinaryLog

    Write-Host "6. 检查慢查询日志..." -ForegroundColor Yellow
    $SlowQueryLog = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE 'slow_query_log%';" 2>&1
    Write-Log "慢查询日志配置"
    Write-Log $SlowQueryLog

    Write-Host "7. 检查错误日志..." -ForegroundColor Yellow
    $ErrorLog = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SHOW VARIABLES LIKE 'log_error%';" 2>&1
    Write-Log "错误日志配置:"
    Write-Log $ErrorLog

    Write-Host "8. 检查表权限..." -ForegroundColor Yellow
    $TablePrivileges = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            GRANTEE,
            TABLE_NAME,
            PRIVILEGE_TYPE
        FROM INFORMATION_SCHEMA.TABLE_PRIVILEGES
        WHERE TABLE_SCHEMA = 'hmdb';" 2>&1
    Write-Log "表权限"
    Write-Log $TablePrivileges

    Write-Host "9. 检查存储过程权限..." -ForegroundColor Yellow
    $RoutinePrivileges = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            GRANTEE,
            ROUTINE_NAME,
            PRIVILEGE_TYPE
        FROM INFORMATION_SCHEMA.ROUTINE_PRIVILEGES
        WHERE ROUTINE_SCHEMA = 'hmdb';" 2>&1
    Write-Log "存储过程权限:"
    Write-Log $RoutinePrivileges

    Write-Host "10. 检查事件权限..." -ForegroundColor Yellow
    $EventPrivileges = mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
        SELECT
            GRANTEE,
            EVENT_NAME,
            PRIVILEGE_TYPE
        FROM INFORMATION_SCHEMA.EVENT_PRIVILEGES
        WHERE EVENT_SCHEMA = 'hmdb';" 2>&1
    Write-Log "事件权限:"
    Write-Log $EventPrivileges

    Write-Host "安全检查完成" -ForegroundColor Green
}

function Show-SecurityTips {
    Write-Host "数据库安全建议" -ForegroundColor Cyan
    Write-Host "================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. 用户管理:" -ForegroundColor Yellow
    Write-Host "   - 使用强密码" -ForegroundColor Gray
    Write-Host "   - 限制用户权限" -ForegroundColor Gray
    Write-Host "   - 定期审查用户权限" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. 连接安全:" -ForegroundColor Yellow
    Write-Host "   - 启用SSL连接" -ForegroundColor Gray
    Write-Host "   - 限制远程访问" -ForegroundColor Gray
    Write-Host "   - 使用防火墙规则" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. 数据安全:" -ForegroundColor Yellow
    Write-Host "   - 定期备份数据" -ForegroundColor Gray
    Write-Host "   - 加密敏感数据" -ForegroundColor Gray
    Write-Host "   - 使用二进制日志" -ForegroundColor Gray
    Write-Host ""
    Write-Host "4. 审计监控:" -ForegroundColor Yellow
    Write-Host "   - 启用审计日志" -ForegroundColor Gray
    Write-Host "   - 监控异常访问" -ForegroundColor Gray
    Write-Host "   - 定期审查日志" -ForegroundColor Gray
    Write-Host ""
    Write-Host "5. 配置安全:" -ForegroundColor Yellow
    Write-Host "   - 禁用不必要的功能" -ForegroundColor Gray
    Write-Host "   - 更新到最新版本" -ForegroundColor Gray
    Write-Host "   - 定期安全评估" -ForegroundColor Gray
}

if ($args[0] -eq "tips") {
    Show-SecurityTips
} else {
    Check-Security
}
