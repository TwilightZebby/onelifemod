package twilightzebby.onelifesmp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;

public class Onelifesmp implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("Mod Load Initiated.");

        // Register Commands
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("smite")
                    .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2 or higher
                    .then(argument("target", EntityArgumentType.player())
                    .executes(ctx -> {
                        // Player
                        ServerPlayerEntity targetPlayer = getPlayer(ctx, "target");

                        // Lightning
                        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(ctx.getSource().getWorld());

                        if ( lightningEntity != null ) {
                            lightningEntity.setPosition(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
                            ctx.getSource().getWorld().spawnEntity(lightningEntity);
                        } else {
                            throw new SimpleCommandExceptionType(new LiteralText("An unexpected error occurred with the smite command")).create();
                        }

                        // ACK to Command Sender
                        Text ackMessage = Text.of("Smiting " + targetPlayer.getName().asString() + "...");
                        ctx.getSource().getPlayer().sendMessage(new LiteralText(ackMessage.getString()).formatted(Formatting.YELLOW), false);

                        // Notify Command Target
                        Text targetMessage = Text.of("You have been smitten!");
                        targetPlayer.sendMessage(new LiteralText(targetMessage.getString()).formatted(Formatting.YELLOW), false);

                        // Notify in Console Log for Server Admins
                        System.out.println("[" + ctx.getSource().getPlayer().getName().asString() + " issued command /smite]");

                        return Command.SINGLE_SUCCESS;
                    })))
                    .createBuilder();
        }));

        System.out.println("Mod Load Completed.");
    }
}
