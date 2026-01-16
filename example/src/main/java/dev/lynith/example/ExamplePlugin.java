package dev.lynith.example;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

public class ExamplePlugin extends JavaPlugin {

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, (event) -> {

            event.getPlayer().sendMessage(
                Message.join(
                    Message.raw("Hello ").color(Color.YELLOW),
                    Message.raw(event.getPlayer().getDisplayName()).color(Color.CYAN).bold(true),
                    Message.raw("!").color(Color.yellow)
                )
            );

        });

        getLogger().atInfo().log("Setup example plugin!");
    }
}
