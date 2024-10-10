package fqf.qua_mario.stomptypes.stomptypes;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.GroundedState;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.mariostates.states.airborne.Stomp;
import fqf.qua_mario.stomptypes.StompHandler;
import fqf.qua_mario.stomptypes.StompType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StompBasic extends StompType {
	public static final StompBasic INSTANCE = new StompBasic();
	private StompBasic() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "stomp");
		this.onlyFromAbove = true;
		this.attemptMount = true;
		this.ignoresHurtsToStomp = false;
		this.damageType = MarioRegistries.STOMP_DAMAGE;
	}

	@Override
	public boolean cannotStompSpecific(Entity target) {
		return target.getType().isIn(StompHandler.IMMUNE_TO_BASIC_STOMP_TAG);
	}

	@Override
	public void executeStompServer(ServerWorld world, ServerPlayerEntity mario, Entity target, boolean harmless) {
		ModMarioQuaMario.LOGGER.info("Executing basic stomp on the server side, against " + target);
		world.playSound(
				null,
				mario.getBlockPos(),
				MarioRegistries.STOMP_SOUND_EVENT,
				SoundCategory.PLAYERS,
				1.0F,
				1.0F
		);

		damageTarget(mario, world, target, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void executeStompClient(Entity target, boolean harmless) {
		ModMarioQuaMario.LOGGER.info("Executing basic stomp on the client side, against " + target);
		MarioClient.changeState(Stomp.INSTANCE);

	}

	@Override
	public void executeStompCommon(World world, PlayerEntity mario, Entity target, boolean harmless) {
		mario.move(MovementType.SELF, new Vec3d(0, (target.getY() + target.getHeight()) - mario.getY(), 0));
//		mario.setPos(mario.getX(), target.getY() + target.getHeight(), mario.getZ());
		Vec3d marioVelocity = mario.getVelocity();
		mario.setVelocity(marioVelocity.x, CharaStat.STOMP_BASE_VELOCITY.getValue(mario), marioVelocity.z);
	}
}
