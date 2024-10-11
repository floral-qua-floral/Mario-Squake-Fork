package fqf.qua_mario.neostates;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface StateDefinition {
	@NotNull Identifier getID();
	void tick();
	List<NeoMarioTransition> getPreTickTransitions();
	List<NeoMarioTransition> getPostTickTransitions();
	List<NeoMarioTransition> getPostMoveTransitions();

	class NeoMarioTransition {
		@FunctionalInterface public interface TransitionEvaluator {
			boolean shouldTransition();
		}
		@FunctionalInterface public interface TransitionExecutor {
			void execute(PlayerEntity mario, long seed);
		}

		public final TransitionEvaluator EVALUATOR;
		public final String TARGET_ID;
		public final String ANIMATION;

		public final TransitionExecutor EXECUTE_CLIENT, EXECUTE_SERVER, EXECUTE_COMMON;

		public NeoMarioTransition(
				@NotNull String targetID,
				@Nullable String animation,
				@NotNull TransitionEvaluator evaluator,
				@Nullable TransitionExecutor clientExecute,
				@Nullable TransitionExecutor commonExecute,
				@Nullable TransitionExecutor serverExecute
		) {
			this.EVALUATOR = evaluator;
			this.TARGET_ID = targetID;
			this.ANIMATION = animation;

			this.EXECUTE_CLIENT = clientExecute;
			this.EXECUTE_COMMON = commonExecute;
			this.EXECUTE_SERVER = serverExecute;
		}

		public NeoMarioTransition(
				@NotNull String targetID,
				@Nullable String animation,
				@NotNull TransitionEvaluator evaluator
		) {
			this(targetID, animation, evaluator, null, null, null);
		}

		public NeoMarioTransition(
				@NotNull String targetID,
				@Nullable String animation,
				@NotNull TransitionEvaluator evaluator,
				@Nullable TransitionExecutor clientExecute
		) {
			this(targetID, animation, evaluator, clientExecute, null, null);
		}

		public NeoMarioTransition(
				@NotNull String targetID,
				@Nullable String animation,
				@NotNull TransitionEvaluator evaluator,
				@Nullable TransitionExecutor clientExecute,
				@Nullable TransitionExecutor commonExecute
		) {
			this(targetID, animation, evaluator, clientExecute, commonExecute, null);
		}
	}
}
