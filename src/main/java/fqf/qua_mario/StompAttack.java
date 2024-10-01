package fqf.qua_mario;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class StompAttack {
	public enum StompType {
		STOMP,
		SPIN_JUMP,
		GROUND_POUND
	}
	private static final StompType[] STOMP_TYPE_VALUES = StompType.values();

	public static void client_send(UUID target, StompType stompType) {
		ClientPlayNetworking.send(new requestStompPayload(target, stompType.ordinal()));
	}

	public static void server_receive(requestStompPayload payload, ServerPlayNetworking.Context context) {
		Entity target = context.player().getServerWorld().getEntity(payload.target);
		StompType stompType = STOMP_TYPE_VALUES[payload.stompType];

		if(target == null) return;

		switch(stompType) {
			case STOMP:
				int damageAmount = 0;

				break;

			case SPIN_JUMP:
				break;

			case GROUND_POUND:
				break;
		}
	}

	public static void server_send() {

	}

	public static void client_receive() {

	}

	public record requestStompPayload(UUID target, int stompType) implements CustomPayload {
		public static final Id<requestStompPayload> ID = new Id<>(Identifier.of(ModQuakeMovement.MOD_ID, "play_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, requestStompPayload> CODEC = PacketCodec.tuple(
				Uuids.PACKET_CODEC,
				requestStompPayload::target,
				PacketCodecs.INTEGER,
				requestStompPayload::stompType,
				requestStompPayload::new
		);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record affirmStompPayload(UUID target, int stompType) implements CustomPayload {
		public static final Id<affirmStompPayload> ID = new Id<>(Identifier.of(ModQuakeMovement.MOD_ID, "play_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, affirmStompPayload> CODEC = PacketCodec.tuple(
				Uuids.PACKET_CODEC,
				affirmStompPayload::target,
				PacketCodecs.INTEGER,
				affirmStompPayload::stompType,
				affirmStompPayload::new
		);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}
}
