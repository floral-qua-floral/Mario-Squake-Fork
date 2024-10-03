package fqf.qua_mario.stomptypes.stomptypes;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.stomptypes.StompType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class StompBasic extends StompType {
	public static final StompBasic INSTANCE = new StompBasic();
	private StompBasic() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "stomp");
		this.onlyFromAbove = true;
		this.attemptMount = true;
		this.ignoresHurtsToStomp = false;
	}

	@Override
	public boolean cannotStompSpecific(Entity target) {
		return false;
	}

	@Override
	public void executeStompServer(ServerWorld world, ServerPlayerEntity mario, Entity target, boolean harmless) {
		ModMarioQuaMario.LOGGER.info("Executing stomp on the server side, against " + target);
	}

	@Override
	public void executeStompClient(Entity target, boolean harmless) {
		ModMarioQuaMario.LOGGER.info("Executing stomp on the client side, against " + target);
	}
}
