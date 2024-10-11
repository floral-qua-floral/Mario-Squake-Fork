package fqf.qua_mario.neostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.neostates.StateDefinition;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JumpingTest implements StateDefinition {
	@Override
	public @NotNull Identifier getID() {
		return Identifier.of(ModMarioQuaMario.MOD_ID, "jumping_test");
	}

	@Override
	public void tick() {
		
	}

	@Override
	public List<NeoMarioTransition> getPreTickTransitions() {
		return List.of(
				new NeoMarioTransition("qua_mario:standing_test", null,
					() -> { return !Input.JUMP.isHeld(); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Client standtest execute!"); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Common standtest execute!"); },
					(mario, seed) -> { ModMarioQuaMario.LOGGER.info("Server standtest execute!"); }
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
