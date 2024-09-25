package fqf.qua_mario;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fqf.qua_mario.util.IEntityDataSaver;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.util.Identifier;

public class ModQuakeMovement implements ModInitializer {
	public static final String MOD_ID = "qua_mario";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ModConfig CONFIG;
	static {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public record SetMarioEnabledPayload(boolean isMario) implements CustomPayload {
		public static final CustomPayload.Id<SetMarioEnabledPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "set_mario_enabled"));
		public static final PacketCodec<RegistryByteBuf, SetMarioEnabledPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SetMarioEnabledPayload::isMario, SetMarioEnabledPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public String setMarioCommand(CommandContext<ServerCommandSource> context, boolean playerArg) throws CommandSyntaxException {
		boolean isMario = BoolArgumentType.getBool(context, "value");

		ServerPlayerEntity player;
		if(playerArg) player = context.getSource().getPlayerOrThrow();
		else player = EntityArgumentType.getPlayer(context, "whoThough");

		setMarioPacket(player, isMario);

		IEntityDataSaver playerDataSaver = (IEntityDataSaver) player;
		playerDataSaver.getPersistentData().putBoolean("isMario", isMario);

		return "Toggled Mario " + (isMario ? "on" : "off") + " for " + player.getName();
	}

	public void setMarioPacket(ServerPlayerEntity player, boolean isMario) {
		ServerPlayNetworking.send(player, new SetMarioEnabledPayload(isMario));
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(SetMarioEnabledPayload.ID, SetMarioEnabledPayload.CODEC);
//		PayloadTypeRegistry.playC2S().register

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			IEntityDataSaver playerDataSaver = (IEntityDataSaver) (handler.player);
			setMarioPacket(handler.player, playerDataSaver.getPersistentData().getBoolean("isMario"));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			dispatcher.register(literal("mario")
				.executes(context -> {
					context.getSource().sendFeedback(() -> Text.literal("Called /mario with no arguments"), true);
					return 1;
				})
				.then(literal("setEnabled")
					.then(argument("value", BoolArgumentType.bool())
							.executes(context -> {
								String feedback = setMarioCommand(context, false);
								context.getSource().sendFeedback(() -> Text.literal(feedback), true);
								return 1;
							})
								.then(argument("whoThough", EntityArgumentType.player())
										.executes(context -> {
											String feedback = setMarioCommand(context, true);
											context.getSource().sendFeedback(() -> Text.literal(feedback), true);
											return 1;
										})
								)
					)
				)
			)
		);
	}

}