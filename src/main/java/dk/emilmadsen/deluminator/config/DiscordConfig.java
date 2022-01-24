package dk.emilmadsen.deluminator.config;

import discord4j.core.DiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    @Value("${discord.token}")
    private String discordToken;

    @Bean
    public DiscordClient discordClient() {
        return DiscordClient.create(discordToken);
    }

}
