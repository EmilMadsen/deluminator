package dk.emilmadsen.deluminator.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.MessageCreateSpec;
import dk.emilmadsen.deluminator.service.HomeLightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordBot {

    public static final String CHANNELID_INDE = "935309834788683857";
    public static final String CHANNELID_LYS = "935309791348285471";
    public static final String CHANNELID_UDE = "935309664369901648";

    private static final Map<String, Command> commands = new HashMap<>();

    private boolean buttonSpawned = false;

    private final DiscordClient discordClient;
    private final HomeLightService homeLightService;

    @PostConstruct
    public void run() {

        setupCommands();

        // setup bot listener thread.
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
    }

    public void spawnShutdownButton() {

        if (!buttonSpawned) {
            buttonSpawned = true;

            String id = UUID.randomUUID().toString();
            createButton(id);

            Thread t = new Thread(() -> {
                Mono<Void> m = discordClient.withGateway((GatewayDiscordClient gateway) ->
                        gateway.getChannelById(Snowflake.of(CHANNELID_LYS))
                                .flatMap(channel -> gateway.on(ButtonInteractionEvent.class, event -> {
                                            if (event.getCustomId().equals(id)) {
                                                homeLightService.turnOffLights();
                                                return event.reply("Initiating shutdown sequence!").withEphemeral(true);
                                            }
                                            return Mono.empty();
                                        }).timeout(Duration.ofMinutes(60 * 24))
                                        // Handle TimeoutException that will be thrown when the above times out
                                        .onErrorResume(TimeoutException.class, e -> {
                                            buttonSpawned = false;
                                            return Mono.empty();
                                        })
                                        .then()));
                m.block();
            });

            t.start();
        }


    }

    public void createButton(String id) {

        Button button = Button.primary(id, "Shut Down!!");
        MessageCreateSpec mcs = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(button))
                .build();

        discordClient.getChannelById(Snowflake.of(CHANNELID_LYS))
                .createMessage(mcs.asRequest())
                .subscribe();

    }


    public void notifyChannel(String message, String channelId) {
        discordClient.getChannelById(Snowflake.of(channelId))
                .createMessage(message).subscribe();
    }

}
