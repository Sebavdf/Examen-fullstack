# FullRepar — Arquitectura de Microservicios (EFT DSY1103)

Sistema de gestión para un taller de reparación de dispositivos, compuesto por
1 API Gateway (BFF) + 9 microservicios.

## Integrantes
- (completar nombres del equipo)

## Arquitectura (10 piezas)


| Servicio | Puerto | Base de datos | Descripción |
|---|---|---|---|
| bff-gateway | 8080 | - | Punto de entrada único (Spring Cloud Gateway) |
| eureka-server | 8761 | - | Service discovery |
| auth-service | 8081 | fullrepar_auth | Registro/login de usuarios |
| customer-service | 8082 | fullrepar_customer | Clientes del taller |
| device-service | 8083 | fullrepar_device | Equipos de los clientes |
| repair-service | 8084 | fullrepar_repair | **Core**: órdenes de reparación |
| inventory-service | 8085 | fullrepar_inventory | Repuestos y stock |
| technician-service | 8086 | fullrepar_technician | Técnicos del taller |
| payment-service | 8087 | fullrepar_payment | Pagos de órdenes |
| notification-service | 8088 | (sin DB) | Notificaciones de eventos |
| review-service | 8089 | fullrepar_review | Reseñas de órdenes completadas |

## Comunicación entre microservicios
- `device-service` → `customer-service` (valida que el cliente exista)
- `repair-service` → `customer-service` + `device-service` (valida ambos antes de crear la orden)
- `payment-service` → `repair-service` (valida que la orden exista)
- `review-service` → `repair-service` (valida que la orden esté COMPLETED/DELIVERED)

## Reglas de negocio destacadas
- `repair-service`: máquina de estados (RECEIVED → IN_PROGRESS → COMPLETED → DELIVERED, o CANCELLED), transiciones inválidas son rechazadas.
- `inventory-service`: el stock nunca puede quedar negativo.
- `review-service`: solo se puede reseñar una orden completada, y solo una vez.
- `payment-service`: solo se puede confirmar un pago en estado PENDING.

## Cómo levantar todo con Docker

```bash
docker compose up --build
```

Esto levanta: Eureka, las 8 bases de datos Postgres (una por servicio con datos) y los 10 servicios Java.
Revisa el estado de registro en Eureka: http://localhost:8761

## Ejecución local (sin Docker)
Cada servicio puede levantarse individualmente con:
```bash
./gradlew bootRun
```
En ese caso, cada `application.yml` ya apunta a `localhost` para su base de datos y Eureka.



## Estructura del repositorio
```
/eureka-server
/bff-gateway
/auth-service
/customer-service
/device-service
/repair-service
/inventory-service
/technician-service
/payment-service
/notification-service
/review-service
docker-compose.yml
```
