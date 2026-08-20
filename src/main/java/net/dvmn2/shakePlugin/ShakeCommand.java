package net.dvmn2.shakePlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ShakeCommand {
    // ВАЖНО: должно совпадать в моде
    public static final String CHANNEL = "shakemod:shake";

    public static LiteralCommandNode<CommandSourceStack> create(JavaPlugin plugin) {
        return Commands.literal("shake")
                .requires(source -> source.getSender().hasPermission("shake.admin"))
                .then(Commands.argument("targets", ArgumentTypes.players())
                        .then(Commands.argument("angle_delta", IntegerArgumentType.integer(0))
                                .then(Commands.argument("position_delta", IntegerArgumentType.integer(0))
                                        .then(Commands.argument("duration", IntegerArgumentType.integer(0))
                                                .executes(ctx -> run(ctx, plugin))))))
                .build();
    }

    private static int run(CommandContext<CommandSourceStack> ctx, JavaPlugin plugin) throws CommandSyntaxException {
        PlayerSelectorArgumentResolver resolver =
                ctx.getArgument("targets", PlayerSelectorArgumentResolver.class);

        // Ключевой момент: резолвим селектор через CommandSourceStack,
        // у которой уже подставлена корректная location из /execute at.
        List<Player> players = resolver.resolve(ctx.getSource());

        int angle_delta = IntegerArgumentType.getInteger(ctx, "angle_delta");
        int position_delta = IntegerArgumentType.getInteger(ctx, "position_delta");
        int duration = IntegerArgumentType.getInteger(ctx, "duration");


        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(angle_delta);
        out.writeInt(position_delta);
        out.writeInt(duration);

        StringBuilder playersNames = new StringBuilder();

        for (Player player : players) {
            player.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
            if (playersNames.isEmpty()) {
                playersNames.append(player.getName());
            } else {
                playersNames.append(", ").append(player.getName());
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
