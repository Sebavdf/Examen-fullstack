package com.fullrepar.bff.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// eureka.client.enabled=false evita que el contexto intente registrarse contra
// un Eureka Server real al correr `./gradlew test` fuera de Docker.
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class BffGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
