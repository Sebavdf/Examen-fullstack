package com.fullrepar.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// eureka.client.enabled=false evita que el contexto intente registrarse contra
// un Eureka Server real al correr `./gradlew test` fuera de Docker (eso hacía
// que el test se colgara/fallara esperando conexión a localhost:8761).
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
