package fqf.qua_mario.stomptypes;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.ModMarioQuaMarioClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class StompType {
	protected Identifier ID;
	protected boolean onlyFromAbove;
	protected boolean attemptMount;
	protected boolean ignoresHurtsToStomp;

	public abstract boolean cannotStompSpecific(Entity target);

	public abstract void executeStompServer(ServerWorld world, ServerPlayerEntity mario, Entity target, boolean harmless);
	public abstract void executeStompClient(Entity target, boolean harmless);

	public void attemptStomp() {
		List<Entity> targets = MarioClient.player.getWorld().getOtherEntities(MarioClient.player, MarioClient.player.getBoundingBox());
		if(targets.isEmpty()) return;

		if(targets.removeIf(this::cannotStompOverallClient) && targets.isEmpty()) return;

		StompHandler.forbiddenStompTargets.addAll(targets);
		for(Entity entity : targets) {
			ModMarioQuaMario.LOGGER.info("Sending a Stomp Attempt packet");
			sendAttemptPacket(entity);
		}
	}

	public boolean cannotStompOverall(Entity target) {
		return(
			!(target instanceof LivingEntity) ||
			target.getType().isIn(StompHandler.UNSTOMPABLE_TAG) ||
			cannotStompSpecific(target)
		);
	}
	public boolean cannotStompOverallClient(Entity target) {
		return((false && onlyFromAbove && MarioClient.player.getY() + 1 < target.getY() + target.getHeight()) || cannotStompOverall(target));
	}

	public void sendAttemptPacket(Entity target) {
		ClientPlayNetworking.send(new StompHandler.requestStompPayload(target.getId(), MarioRegistries.STOMP_TYPES.getRawIdOrThrow(this)));
	}
	public void sendAffirmPacket(ServerPlayerEntity player, Entity target, boolean harmless) {
		ServerPlayNetworking.send(player, new StompHandler.affirmStompPayload(target.getId(), MarioRegistries.STOMP_TYPES.getRawId(this), harmless));
	}

	public Identifier getID() {
		return(this.ID);
	}
	public void register() {
		Registry.register(MarioRegistries.STOMP_TYPES, this.getID(), this);
	}
}
