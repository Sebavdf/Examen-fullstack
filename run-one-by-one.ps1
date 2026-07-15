Clear-Host
Write-Host "Compilando, construyendo y levantando de a un servicio a la vez..." -ForegroundColor Cyan

# Orden: infraestructura -> bases de datos -> microservicios -> gateway
$plan = @(
    @{ name = "eureka-server";       port = 8761; timeoutSec = 90; hasCode = $true  }

    @{ name = "db-auth";             port = 5432; timeoutSec = 30; hasCode = $false }
    @{ name = "db-customer";         port = 5433; timeoutSec = 30; hasCode = $false }
    @{ name = "db-device";           port = 5434; timeoutSec = 30; hasCode = $false }
    @{ name = "db-repair";           port = 5435; timeoutSec = 30; hasCode = $false }
    @{ name = "db-inventory";        port = 5436; timeoutSec = 30; hasCode = $false }
    @{ name = "db-technician";       port = 5437; timeoutSec = 30; hasCode = $false }
    @{ name = "db-payment";          port = 5438; timeoutSec = 30; hasCode = $false }
    @{ name = "db-review";           port = 5439; timeoutSec = 30; hasCode = $false }

    @{ name = "auth-service";        port = 8081; timeoutSec = 90; hasCode = $true  }
    @{ name = "customer-service";    port = 8082; timeoutSec = 90; hasCode = $true  }
    @{ name = "device-service";      port = 8083; timeoutSec = 90; hasCode = $true  }
    @{ name = "repair-service";      port = 8084; timeoutSec = 90; hasCode = $true  }
    @{ name = "inventory-service";   port = 8085; timeoutSec = 90; hasCode = $true  }
    @{ name = "technician-service";  port = 8086; timeoutSec = 90; hasCode = $true  }
    @{ name = "payment-service";     port = 8087; timeoutSec = 90; hasCode = $true  }
    @{ name = "notification-service";port = 8088; timeoutSec = 90; hasCode = $true  }
    @{ name = "review-service";      port = 8089; timeoutSec = 90; hasCode = $true  }

    @{ name = "bff-gateway";         port = 8080; timeoutSec = 90; hasCode = $true  }
)

function Wait-Port {
    param([string]$svc, [int]$port, [int]$timeoutSec)
    $elapsed = 0
    Write-Host -NoNewline "   Esperando a que $svc responda en el puerto $port "
    while ($elapsed -lt $timeoutSec) {
        $test = Test-NetConnection -ComputerName "localhost" -Port $port -WarningAction SilentlyContinue
        if ($test.TcpTestSucceeded) {
            Write-Host " listo (${elapsed}s)" -ForegroundColor Green
            return $true
        }
        Write-Host -NoNewline "."
        Start-Sleep -Seconds 3
        $elapsed += 3
    }
    Write-Host " TIMEOUT" -ForegroundColor Red
    return $false
}

foreach ($step in $plan) {
    $svc = $step.name
    Write-Host "===============================================" -ForegroundColor Yellow
    Write-Host "Servicio: $svc" -ForegroundColor Yellow

    if ($step.hasCode) {
        # 1. Compilar el .jar (esto es lo que más CPU consume; una app a la vez)
        Write-Host "-> Compilando con Gradle..." -ForegroundColor Gray
        Push-Location "./$svc"
        if (Test-Path "./gradlew.bat") {
            cmd.exe /c ".\gradlew.bat bootJar -x test"
        } else {
            cmd.exe /c "gradle bootJar -x test"
        }
        $gradleExit = $LASTEXITCODE
        Pop-Location

        if ($gradleExit -ne 0) {
            Write-Host "Error al compilar $svc" -ForegroundColor Red
            Exit 1
        }
    }

    # 2. Construir SOLO la imagen de este servicio (nombrarlo explícitamente
    #    ya evita que Compose construya otras imágenes en paralelo)
    Write-Host "-> Construyendo imagen Docker..." -ForegroundColor Cyan
    docker compose build $svc
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error al construir la imagen de $svc" -ForegroundColor Red
        Exit 1
    }

    # 3. Levantar el contenedor
    Write-Host "-> Levantando contenedor..." -ForegroundColor Blue
    docker compose up -d $svc
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error al levantar $svc" -ForegroundColor Red
        Exit 1
    }

    # 4. Esperar a que esté realmente arriba antes de pasar al siguiente
    $ok = Wait-Port -svc $svc -port $step.port -timeoutSec $step.timeoutSec
    if (-not $ok) {
        Write-Host "⚠️  $svc no respondió a tiempo. Revisa: docker compose logs $svc" -ForegroundColor DarkYellow
        Write-Host "   Continuando con el siguiente servicio..." -ForegroundColor DarkYellow
    }

    docker system prune -f | Out-Null
}

Write-Host "===============================================" -ForegroundColor Green
Write-Host "PROCESO COMPLETO: todo se compiló, construyó y levantó de a uno." -ForegroundColor Green
Write-Host "Revisa el estado con: docker compose ps" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
