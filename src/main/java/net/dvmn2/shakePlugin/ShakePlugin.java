package net.dvmn2.shakePlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class ShakePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Сохранение конфигурации по умолчанию (если есть config.yml)
        saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, ShakeCommand.CHANNEL);
        getCommand("shake").setExecutor(new ShakeCommand(this));

        getLogger().info("ShakePlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ShakePlugin disabled!");
    }
}
