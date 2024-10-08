package fqf.qua_mario;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MarioCommand {
	private static ServerPlayerEntity getPlayerFromCmd(CommandContext<ServerCommandSource> context, boolean playerArgGiven) throws CommandSyntaxException {
		return playerArgGiven ? EntityArgumentType.getPlayer(context, "whoThough") : context.getSource().getPlayerOrThrow();
	}
	private static int sendFeedback(CommandContext<ServerCommandSource> context, String feedback) {
		context.getSource().sendFeedback(() -> Text.literal(feedback), true);
		return 1;
	}

	private static int setEnabled(CommandContext<ServerCommandSource> context, boolean playerArgGiven) throws CommandSyntaxException {
		return sendFeedback(context, ModMarioQuaMario.setIsMario(
				getPlayerFromCmd(context, playerArgGiven),
				BoolArgumentType.getBool(context, "value")
		));
	}

	private static int setCharacter(CommandContext<ServerCommandSource> context, boolean playerArgGiven) throws CommandSyntaxException {
		return sendFeedback(context, ModMarioQuaMario.setCharacter(
				getPlayerFromCmd(context, playerArgGiven),
				RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newCharacter", MarioRegistries.CHARACTERS_KEY).value()
		));
	}

	private static int setPowerUp(CommandContext<ServerCommandSource> context, boolean playerArgGiven) throws CommandSyntaxException {
		return sendFeedback(context, ModMarioQuaMario.setPowerUp(
				getPlayerFromCmd(context, playerArgGiven),
				RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newPowerUp", MarioRegistries.POWER_UPS_KEY).value()
		));
	}

	private static int setFullData(CommandContext<ServerCommandSource> context, boolean playerArgGiven) throws CommandSyntaxException {
		return sendFeedback(context, ModMarioQuaMario.setFullMarioData(
				getPlayerFromCmd(context, playerArgGiven),
				BoolArgumentType.getBool(context, "isEnabled"),
				RegistryEntryReferenceArgumentType.getRegistryEntry(context, "character", MarioRegistries.CHARACTERS_KEY).value(),
				RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newPowerUp", MarioRegistries.POWER_UPS_KEY).value()
		));
	}

	public static void registerMarioCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("mario")
				.then(literal("setEnabled")
					.then(argument("value", BoolArgumentType.bool())
						.executes(context -> setEnabled(context, false))
						.then(argument("whoThough", EntityArgumentType.player())
							.executes(context -> setEnabled(context, true))
						)
					)
				)
				.then(literal("setCharacter")
					.then(argument("newCharacter", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MarioRegistries.CHARACTERS_KEY))
						.executes(context -> setCharacter(context, false))
						.then(argument("whoThough", EntityArgumentType.player())
							.executes(context -> setCharacter(context, true))
						)
					)
				)
				.then(literal("setPowerUp")
					.then(argument("newPowerUp", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MarioRegistries.POWER_UPS_KEY))
						.executes(context -> setPowerUp(context, false))
						.then(argument("whoThough", EntityArgumentType.player())
							.executes(context -> setPowerUp(context, true))
						)
					)
				)
				.then(literal("setFullData")
					.then(argument("character", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MarioRegistries.CHARACTERS_KEY))
						.then(argument("powerUp", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MarioRegistries.POWER_UPS_KEY))
							.then(argument("isEnabled", BoolArgumentType.bool())
								.executes(context -> setFullData(context, false))
								.then(argument("whoThough", EntityArgumentType.player())
									.executes(context -> setFullData(context, true))
								)
							)
						)
					)
				)
			);
		});
	}
}
