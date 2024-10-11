package fqf.qua_mario.neostates;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface NeoMarioState {
	@NotNull Identifier getID();
	void tick();
	List<NeoMarioTransition> getPreTickTransitions();
	List<NeoMarioTransition> getPostTickTransitions();
	List<NeoMarioTransition> getPostMoveTransitions();

	default int goobulor() {
		return 0;
	}

	interface NeoMarioTransition {

	}
}
