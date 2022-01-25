package dk.emilmadsen.deluminator.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DiscordBot {

    public static final String CHANNELID_MILIEU = "935309834788683857";
    public static final String CHANNELID_LYS = "935309791348285471";
    public static final String CHANNELID_WEATHER = "935309664369901648";

    private static final Map<String, Command> commands = new HashMap<>();

    private final DiscordClient discordClient;

    public DiscordBot(DiscordClient discordClient) {
        this.discordClient = discordClient;
    }

    @PostConstruct
    public void run() {

        setupCommands();

        // setup blocking bot listener i new thread.
        Thread thread = new Thread(() -> {
            Mono<Void> login = discordClient.withGateway((GatewayDiscordClient gateway) ->
                    gateway.on(MessageCreateEvent.class, event -> {

                        // loop commands and trigger response if match
                        final String content = event.getMessage().getContent();
                        for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                            if (content.startsWith('!' + entry.getKey())) {
                                entry.getValue().execute(event);
                                break;
                            }
                        }
                        return Mono.empty();
                    }));
            login.block();
        });

        thread.start();

    }

    private void setupCommands() {

        commands.put("ping", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Pong!").block());

        // TODO:

    }


    public void notifyChannel(String message, String channelId) {
        discordClient.getChannelById(Snowflake.of(channelId))
                .createMessage(message).subscribe();
    }

}
