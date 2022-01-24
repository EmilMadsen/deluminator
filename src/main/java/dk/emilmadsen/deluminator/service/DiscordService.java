package dk.emilmadsen.deluminator.service;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class DiscordService {

    private final DiscordClient discordClient;

    public DiscordService(DiscordClient discordClient) {
        this.discordClient = discordClient;
    }


    @PostConstruct
    public void init() {

        Thread thread = new Thread(() -> {

            Mono<Void> login = discordClient.withGateway((GatewayDiscordClient gateway) ->
                    gateway.on(MessageCreateEvent.class, event -> {
                        Message message = event.getMessage();

                        if (message.getContent().equalsIgnoreCase("!ping")) {
                            return message.getChannel()
                                    .flatMap(channel -> channel.createMessage("pong!"));
                        }

                        return Mono.empty();
                    }));
            login.block();
        });

        thread.start();

    }


    public void notifyDiscord() {
        // TODO implement.
    }

}
