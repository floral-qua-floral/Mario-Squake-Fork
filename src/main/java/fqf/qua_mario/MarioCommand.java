package fqf.qua_mario;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.powerups.PowerUp;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MarioCommand {
	public static void registerMarioCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("mario")
				.then(literal("setEnabled")
					.then(argument("value", BoolArgumentType.bool())
						.executes(context -> {
							String feedback = ModMarioQuaMario.setIsMario(context.getSource().getPlayerOrThrow(), BoolArgumentType.getBool(context, "value"));
							context.getSource().sendFeedback(() -> Text.literal(feedback), true);
							return 1;
						})
						.then(argument("whoThough", EntityArgumentType.player())
							.executes(context -> {
								String feedback = ModMarioQuaMario.setIsMario(EntityArgumentType.getPlayer(context, "whoThough"), BoolArgumentType.getBool(context, "value"));
								context.getSource().sendFeedback(() -> Text.literal(feedback), true);
								return 1;
							})
						)
					)
				)
				.then(literal("setCharacter")
					.then(argument("newCharacter", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MarioRegistries.CHARACTERS_KEY))
						.executes(context -> {
							RegistryEntry.Reference<MarioCharacter> reference = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newCharacter", MarioRegistries.CHARACTERS_KEY);
							String feedback = ModMarioQuaMario.setCharacter(context.getSource().getPlayerOrThrow(), reference.value());
							context.getSource().sendFeedback(() -> Text.literal(feedback), true);
							return 1;
						})
						.then(argument("whoThough", EntityArgumentType.player())
								.executes(context -> {
									RegistryEntry.Reference<MarioCharacter> reference = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newCharacter", MarioRegistries.CHARACTERS_KEY);
									String feedback = ModMarioQuaMario.setCharacter(EntityArgumentType.getPlayer(context, "whoThough"), reference.value());
									context.getSource().sendFeedback(() -> Text.literal(feedback), true);
									return 1;
								})
						)
					)
				)
				.then(literal("setPowerUp")
					.then(argument("newPowerUp", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MarioRegistries.POWER_UPS_KEY))
						.executes(context -> {
							RegistryEntry.Reference<PowerUp> reference = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newPowerUp", MarioRegistries.POWER_UPS_KEY);
							String feedback = ModMarioQuaMario.setPowerUp(context.getSource().getPlayerOrThrow(), reference.value());
							context.getSource().sendFeedback(() -> Text.literal(feedback), true);
							return 1;
						})
						.then(argument("whoThough", EntityArgumentType.player())
								.executes(context -> {
									RegistryEntry.Reference<PowerUp> reference = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "newPowerUp", MarioRegistries.POWER_UPS_KEY);
									String feedback = ModMarioQuaMario.setPowerUp(EntityArgumentType.getPlayer(context, "whoThough"), reference.value());
									context.getSource().sendFeedback(() -> Text.literal(feedback), true);
									return 1;
								})
						)
					)
				)
			);
		});
	}
}
