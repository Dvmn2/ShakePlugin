package net.dvmn2.shakePlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class ShakePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, ShakeCommand.CHANNEL);

        getLifecycleManager().registerEventHandler(
                io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS,
                event -> event.registrar().register(
                        ShakeCommand.create(this),
                        "Трясёт камеру игрока"
                )
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("ShakePlugin disabled!");
    }
}
