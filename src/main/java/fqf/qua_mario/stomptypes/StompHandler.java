package fqf.qua_mario.stomptypes;

import com.google.common.collect.Lists;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public class StompHandler {
	public static final TagKey<DamageType> USES_FEET_ITEM_TAG = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "uses_feet_item"));
	public static final TagKey<DamageType> USES_LEGS_ITEM_TAG = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "uses_legs_item"));
	public static final TagKey<DamageType> FLATTENS_ENTITIES_TAG = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "flattens_entities"));

	public static final TagKey<EntityType<?>> UNSTOMPABLE_TAG = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "unstompable"));
	public static final TagKey<EntityType<?>> HURTS_TO_STOMP_TAG = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "hurts_to_stomp"));
	public static final TagKey<EntityType<?>> IMMUNE_TO_BASIC_STOMP_TAG = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "immune_to_basic_stomp"));

	public static List<Entity> forbiddenStompTargets = Lists.newArrayList();

	public static void parseRequestStompPacket(RequestStompPayload payload, ServerPlayNetworking.Context context) {
		ServerWorld world = context.player().getServerWorld();
		Entity target = world.getEntityById(payload.target);
		StompType stompType = MarioRegistries.STOMP_TYPES.getOrThrow(payload.stompType);
		assert stompType != null && target != null;

		boolean harmless;
		if(target.getType().isIn(HURTS_TO_STOMP_TAG)) {
			if(stompType.ignoresHurtsToStomp) harmless = true;
			else {
				// Hurt Mario
				return;
			}
		}
		else harmless = false;

		if(stompType.cannotStompOverall(target)) return;

		stompType.executeStompCommon(world, context.player(), target, harmless);
		stompType.executeStompServer(world, context.player(), target, harmless);
		stompType.sendAffirmPacket(context.player(), target, harmless);
	}

	public static void parseAffirmStompPacket(affirmStompPayload payload, ClientPlayNetworking.Context context) {
		Entity target = context.player().getWorld().getEntityById(payload.target); // = context.player().getServerWorld().getEntity(payload.target);
		StompType stompType = MarioRegistries.STOMP_TYPES.getOrThrow(payload.stompType);

		stompType.executeStompCommon(context.player().clientWorld, MarioClient.player, target, payload.harmless);
		stompType.executeStompClient(target, payload.harmless);
		forbiddenStompTargets.clear();
	}

	public record RequestStompPayload(int target, int stompType) implements CustomPayload {
		public static final Id<RequestStompPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "request_stomp"));
		public static final PacketCodec<RegistryByteBuf, RequestStompPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER,
				RequestStompPayload::target,
				PacketCodecs.INTEGER,
				RequestStompPayload::stompType,
				RequestStompPayload::new
		);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record affirmStompPayload(int target, int stompType, boolean harmless) implements CustomPayload {
		public static final Id<affirmStompPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "affirm_stomp"));
		public static final PacketCodec<RegistryByteBuf, affirmStompPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER,
				affirmStompPayload::target,
				PacketCodecs.INTEGER,
				affirmStompPayload::stompType,
				PacketCodecs.BOOL,
				affirmStompPayload::harmless,
				affirmStompPayload::new
		);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}
}
