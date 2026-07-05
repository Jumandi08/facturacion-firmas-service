package org.ups.facturacionfirmas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class FacturacionFirmasServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void should_invoke_spring_application_run_when_main_is_called() {
		try (var springApplicationMock = mockStatic(org.springframework.boot.SpringApplication.class)) {
			FacturacionFirmasServiceApplication.main(new String[] {});

			springApplicationMock.verify(() -> org.springframework.boot.SpringApplication.run(
					FacturacionFirmasServiceApplication.class, new String[] {}));
		}
	}

}
