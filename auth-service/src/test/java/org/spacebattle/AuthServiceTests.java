package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class AuthServiceTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testMainMethod() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			AuthService.main(new String[]{});
			mocked.verify(() -> SpringApplication.run(AuthService.class, new String[]{}));
		}
	}

}
