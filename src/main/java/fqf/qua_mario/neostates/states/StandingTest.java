package fqf.qua_mario.neostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.neostates.StateDefinition;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StandingTest implements StateDefinition {
	@Override
	public @NotNull Identifier getID() {
		return Identifier.of(ModMarioQuaMario.MOD_ID, "standing_test");
	}

	@Override
	public void tick() {

	}

	@Override
	public List<NeoMarioTransition> getPreTickTransitions() {
		return List.of(
				new NeoMarioTransition("qua_mario:ducking_test", "duck",
					() -> { return Input.DUCK.isHeld(); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Client ducktest execute!"); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Common ducktest execute!"); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Server ducktest execute!"); }
				),
				new NeoMarioTransition("qua_mario:jumping_test", "jump",
					() -> { return Input.JUMP.isHeld(); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Client jumptest execute!"); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Common jumptest execute!"); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Server jumptest execute!"); }
				)
		);
	}

	@Override
	public List<NeoMarioTransition> getPostTickTransitions() {
		return List.of();
	}

	@Override
	public List<NeoMarioTransition> getPostMoveTransitions() {
		return List.of();
	}
}
