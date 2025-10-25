# PowerShell script to login and POST a Servicio
# Usage: powershell -NoProfile -ExecutionPolicy Bypass -File scripts\do_login_post.ps1

$s = New-Object Microsoft.PowerShell.Commands.WebRequestSession
try {
    $resp = Invoke-WebRequest -Uri 'http://localhost:8080/login' -WebSession $s -UseBasicParsing -ErrorAction Stop
} catch {
    Write-Error "GET /login failed: $_"
    exit 1
}

# extract CSRF token
$match = [regex]::Match($resp.Content, 'name="_csrf"\s+value="([^"]+)"')
if (-not $match.Success) {
    Write-Error "CSRF token not found in login page"
    exit 1
}
$token = $match.Groups[1].Value
Write-Output "CSRF=$token"

# perform login
try {
    Invoke-WebRequest -Uri 'http://localhost:8080/login' -Method POST -Body @{ username = 'admin'; password = '1234'; _csrf = $token } -WebSession $s -UseBasicParsing -ErrorAction Stop | Out-Null
} catch {
    Write-Error "Login POST failed: $_"
    exit 1
}

Write-Output "Login POST sent. Cookies:"
$s.Cookies.GetCookies('http://localhost') | ForEach-Object { Write-Output ("$($_.Name)=$($_.Value)") }

# POST servicio
$payload = @{ nombre = 'Servicio creado por script'; descripcion = 'creado via script'; precio = 25.0 } | ConvertTo-Json
try {
    $r = Invoke-RestMethod -Uri 'http://localhost:8080/api/servicios' -Method POST -Body $payload -ContentType 'application/json' -WebSession $s -UseBasicParsing -ErrorAction Stop
    Write-Output "Created servicio:" 
    $r | ConvertTo-Json -Depth 5 | Write-Output
} catch {
    Write-Error "POST /api/servicios failed: $_"
    exit 1
}
