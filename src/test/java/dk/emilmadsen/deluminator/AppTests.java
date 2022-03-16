package dk.emilmadsen.deluminator;

import discord4j.core.DiscordClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AppTests {

	@MockBean
	private DiscordClient discordClient;

	@Test
	void contextLoads() {
	}

}
