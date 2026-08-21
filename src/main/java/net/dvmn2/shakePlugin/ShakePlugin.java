package net.dvmn2.shakePlugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Главный класс серверного плагина (Paper).
 * Регистрирует исходящий plugin-messaging канал и команду {@code /shake},
 * которая шлёт клиентам параметры тряски камеры — саму тряску обрабатывает
 * мод ShakeMod на стороне Fabric-клиента.
 */
public final class ShakePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Загружаем/создаём config.yml по умолчанию (если он есть в ресурсах плагина).
        saveDefaultConfig();

        // Регистрируем исходящий канал, через который будем слать пакеты клиентам.
        // Без регистрации Bukkit заблокирует отправку plugin-message на этот канал.
        getServer().getMessenger().registerOutgoingPluginChannel(this, ShakeCommand.CHANNEL);

        // Регистрируем команду /shake через Lifecycle API Paper (актуальный способ
        // регистрации команд начиная с Paper 1.20.6+, используется и в 1.21.11).
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