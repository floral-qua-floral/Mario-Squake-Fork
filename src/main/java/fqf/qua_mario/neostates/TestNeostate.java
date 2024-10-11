package fqf.qua_mario.neostates;

import fqf.qua_mario.ModMarioQuaMario;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestNeostate implements NeoMarioState {
	@Override @NotNull public Identifier getID() {
		return Identifier.of(ModMarioQuaMario.MOD_ID, "test_state");
	}

	@Override
	public void tick() {
//		goobulor();

	}

	@Override
	public List<NeoMarioTransition> getPreTickTransitions() {
		return null;
	}

	@Override
	public List<NeoMarioTransition> getPostTickTransitions() {
		return null;
	}

	@Override
	public List<NeoMarioTransition> getPostMoveTransitions() {
		return null;
	}

//	@Override

}
