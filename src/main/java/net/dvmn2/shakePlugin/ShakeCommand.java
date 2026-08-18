package net.dvmn2.shakePlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.dvmn2.shakePlugin.managers.SelectorChecker;
import net.dvmn2.shakePlugin.managers.SelectorParser;
import net.dvmn2.shakePlugin.managers.SelectorTab;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShakeCommand implements CommandExecutor, TabCompleter {
    // ВАЖНО: должно совпадать с net.dvmn2.bmcmod.client.shake.CameraShakePayload.ID в моде
    public static final String CHANNEL = "bmcmod:shake";

    // Значения по умолчанию, если аргументы силы/продолжительности не переданы
    private static final int DEFAULT_INTENSITY = 1;
    private static final int DEFAULT_POWER = 5;
    private static final int DEFAULT_DURATION = 20;

    private final JavaPlugin plugin;

    public ShakeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shake.admin")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /shake <players> [интенсивность] [сила] [продолжительность]");
            return true;
        }

        List<Entity> targets = SelectorParser.parse(sender, args[0]);

        if (targets.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден по селектору: " + args[0]);
            return true;
        }

        if (!SelectorChecker.isPlayers(targets)) {
            sender.sendMessage(ChatColor.RED + "Должны быть выделены только игроки: " + args[0]);
            return true;
        }

        List<Player> players = targets.stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .toList();


        int intensity = DEFAULT_INTENSITY;
        int power = DEFAULT_POWER;
        int duration = DEFAULT_DURATION;

        try {
            if (args.length >= 2) intensity = Integer.parseInt(args[1]);
            if (args.length >= 3) power = Integer.parseInt(args[2]);
            if (args.length >= 4) duration = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Интенсивность, сила и продолжительность должны быть числами.");
            return true;
        }

        if (intensity < 0 || power < 0 || duration < 0) {
            sender.sendMessage(ChatColor.RED + "Интенсивность, сила и продолжительность должны быть положительными.");
            return true;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(intensity);
        out.writeInt(power);
        out.writeInt(duration);

        StringBuilder playersNames = new StringBuilder();

        for (Player player : players) {
            player.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
            if (playersNames.isEmpty()) {
                playersNames.append(player.getName());
            } else {
                playersNames.append(", ").append(player.getName()); //
            }
        }


        sender.sendMessage(ChatColor.GREEN + "Тряска отправлена игрокам: " + playersNames);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return SelectorTab.getEntities();
        }
        if (args.length == 2) {
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
        }
        if (args.length == 3) {
            return Arrays.asList("10", "15", "20", "30", "40", "50");
        }
        if (args.length == 4) {
            return Arrays.asList("10", "20", "40", "60", "80");
        }
        return Collections.emptyList();
    }
}
