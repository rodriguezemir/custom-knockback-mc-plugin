package site.zvolcan.customkb.command;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import site.zvolcan.customkb.config.KnockbackConfig;

/** Builds the {@code /knockback} command tree using PaperMC's Brigadier-based command API. */
public final class KnockbackCommand {

    private static final String PERMISSION = "customkb.command.knockback";

    private KnockbackCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> create(KnockbackConfig config) {
        return literal("knockback")
                .requires(source -> source.getSender().hasPermission(PERMISSION))
                .then(literal("get").executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(Component.text(
                            "Horizontal: " + config.getHorizontalMultiplier()
                                    + ", Vertical: " + config.getVerticalMultiplier()));
                    return Command.SINGLE_SUCCESS;
                }))
                .then(literal("set")
                        .then(literal("horizontal")
                                .then(argument("value", DoubleArgumentType.doubleArg(0.0))
                                        .executes(ctx -> {
                                            double value = DoubleArgumentType.getDouble(ctx, "value");
                                            config.setHorizontalMultiplier(value);
                                            ctx.getSource().getSender().sendMessage(
                                                    Component.text("Horizontal knockback set to " + value));
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(literal("vertical")
                                .then(argument("value", DoubleArgumentType.doubleArg(0.0))
                                        .executes(ctx -> {
                                            double value = DoubleArgumentType.getDouble(ctx, "value");
                                            config.setVerticalMultiplier(value);
                                            ctx.getSource().getSender().sendMessage(
                                                    Component.text("Vertical knockback set to " + value));
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(literal("reload").executes(ctx -> {
                    config.reload();
                    ctx.getSource().getSender().sendMessage(Component.text("Knockback config reloaded."));
                    return Command.SINGLE_SUCCESS;
                }))
                .build();
    }
}
