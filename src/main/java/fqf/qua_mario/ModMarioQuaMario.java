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
import net.minecraft.entity.player.PlayerEntity;
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

public class ModMarioQuaMario implements ModInitializer {
	public static final String MOD_ID = "qua_mario";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ModConfig CONFIG;
	static {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public static boolean useMarioPhysics(PlayerEntity player, boolean mustBeClient) {
		return(
				// Has to be client-side if required by parameter
				(mustBeClient && player.getWorld().isClient)
				// Has to be Mario (duh)
				&& (player.getWorld().isClient ? MarioClient.isMario : ((IEntityDataSaver) player).getPersistentData().getBoolean("isMario"))
				// Can't be creative-flying or gliding with an Elytra
				&& !player.getAbilities().flying && !player.isFallFlying()
				// Can't be in a vehicle
				&& !player.hasVehicle()
				// Can't be climbing
				&& !player.isClimbing()
		);
	}

	public record SetMarioEnabledPayload(boolean isMario) implements CustomPayload {
		public static final CustomPayload.Id<SetMarioEnabledPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "set_mario_enabled"));
		public static final PacketCodec<RegistryByteBuf, SetMarioEnabledPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SetMarioEnabledPayload::isMario, SetMarioEnabledPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record PlayJumpSfxPayload(boolean isSpin) implements CustomPayload {
		public static final CustomPayload.Id<PlayJumpSfxPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "play_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, PlayJumpSfxPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, PlayJumpSfxPayload::isSpin, PlayJumpSfxPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public String setMarioCommand(CommandContext<ServerCommandSource> context, boolean playerArg) throws CommandSyntaxException {
		boolean isMario = BoolArgumentType.getBool(context, "value");

		ServerPlayerEntity player;
		if(playerArg) player = EntityArgumentType.getPlayer(context, "whoThough");
		else player = context.getSource().getPlayerOrThrow();

		sendMarioPacket(player, isMario);

		IEntityDataSaver playerDataSaver = (IEntityDataSaver) player;
		playerDataSaver.getPersistentData().putBoolean("isMario", isMario);

		return (isMario ? "Enabled" : "Disabled") + " Mario mode for " + player + ".";
	}

	public void sendMarioPacket(ServerPlayerEntity player, boolean isMario) {
		ServerPlayNetworking.send(player, new SetMarioEnabledPayload(isMario));
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(SetMarioEnabledPayload.ID, SetMarioEnabledPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(StompAttack.affirmStompPayload.ID, StompAttack.affirmStompPayload.CODEC);

		PayloadTypeRegistry.playC2S().register(PlayJumpSfxPayload.ID, PlayJumpSfxPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(StompAttack.requestStompPayload.ID, StompAttack.requestStompPayload.CODEC);
//		PayloadTypeRegistry.playC2S().register

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			IEntityDataSaver playerDataSaver = (IEntityDataSaver) (handler.player);
			sendMarioPacket(handler.player, playerDataSaver.getPersistentData().getBoolean("isMario"));
		});

		ServerPlayNetworking.registerGlobalReceiver(PlayJumpSfxPayload.ID, (payload, context) -> {
			LOGGER.info("Received the packet asking to play a sound effect");
		});

		ServerPlayNetworking.registerGlobalReceiver(StompAttack.requestStompPayload.ID, (payload, context) -> {
			LOGGER.info("Received the packet asking to stomp something");
			StompAttack.server_receive(payload, context);
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
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
			);
		});
	}

}