package fqf.qua_mario.neostates;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.RandomSeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedState {
	public final Identifier IDENTIFIER;
	public final StateDefinition DEFINITION;

	private final Map<TransitionPhases, ParsedTransition[]> TRANSITION_LISTS;

	public ParsedState(StateDefinition definition) {
		ModMarioQuaMario.LOGGER.info("Initialized state {} from definition {}", definition.getID(), definition);
		this.IDENTIFIER = definition.getID();
		this.DEFINITION = definition;

		this.TRANSITION_LISTS = new HashMap<>();
	}

	public void tick() {
		this.DEFINITION.tick();
	}

	public void executeTransitions(TransitionPhases phase) {
		for(ParsedTransition tryTransition : TRANSITION_LISTS.get(phase)) {
			if(tryTransition.evaluate()) {
				ModMarioQuaMario.LOGGER.info("Transition to {}", tryTransition.TARGET_STATE.IDENTIFIER);
				tryTransition.execute(MarioClient.player, RandomSeed.getSeed());
			}
		}
	}

	private ParsedTransition[] parseDefinitionTransitionList(List<StateDefinition.NeoMarioTransition> definitionTransitionList) {
		ParsedTransition[] parsedTransitions = new ParsedTransition[definitionTransitionList.size()];
		for(int incrementeroo = 0; incrementeroo < definitionTransitionList.size(); incrementeroo++) {
			StateDefinition.NeoMarioTransition parseMe = definitionTransitionList.get(incrementeroo);

			ModMarioQuaMario.LOGGER.info("Parsing transition to {}...", parseMe.TARGET_ID);

			parsedTransitions[incrementeroo] = new ParsedTransition(
					MarioRegistries.STATES.get(Identifier.of(parseMe.TARGET_ID)),
					parseMe.ANIMATION,
					parseMe.EVALUATOR,
					parseMe.EXECUTE_CLIENT,
					parseMe.EXECUTE_COMMON,
					parseMe.EXECUTE_SERVER
			);

			ModMarioQuaMario.LOGGER.info("Parsed transition to {} ({})", parseMe.TARGET_ID,
					parsedTransitions[incrementeroo].TARGET_STATE.IDENTIFIER);
		}
		return parsedTransitions;
	}

	public void populateTransitionLists() {
		ModMarioQuaMario.LOGGER.info("Parsing {}'s pre-tick transitions:", IDENTIFIER);
		TRANSITION_LISTS.put(TransitionPhases.PRE_TICK, parseDefinitionTransitionList(DEFINITION.getPreTickTransitions()));
		ModMarioQuaMario.LOGGER.info("Parsing {}'s post-tick transitions:", IDENTIFIER);
		TRANSITION_LISTS.put(TransitionPhases.POST_TICK, parseDefinitionTransitionList(DEFINITION.getPostTickTransitions()));
		ModMarioQuaMario.LOGGER.info("Parsing {}'s post-move transitions:", IDENTIFIER);
		TRANSITION_LISTS.put(TransitionPhases.POST_MOVE, parseDefinitionTransitionList(DEFINITION.getPostMoveTransitions()));
	}

	private static class ParsedTransition {
		public final ParsedState TARGET_STATE;
		public final String ANIMATION;
		public final StateDefinition.NeoMarioTransition.TransitionEvaluator EVALUATOR;
		public final StateDefinition.NeoMarioTransition.TransitionExecutor CLIENT_EXECUTE;
		public final StateDefinition.NeoMarioTransition.TransitionExecutor COMMON_EXECUTE;
		public final StateDefinition.NeoMarioTransition.TransitionExecutor SERVER_EXECUTE;

		private ParsedTransition(
				ParsedState targetState, String animation,
				StateDefinition.NeoMarioTransition.TransitionEvaluator evaluator,
				StateDefinition.NeoMarioTransition.TransitionExecutor clientExecutor,
				StateDefinition.NeoMarioTransition.TransitionExecutor commonExecutor,
				StateDefinition.NeoMarioTransition.TransitionExecutor serverExecutor
		) {
			this.TARGET_STATE = targetState;
			this.ANIMATION = animation;
			this.EVALUATOR = evaluator;
			this.CLIENT_EXECUTE = clientExecutor;
			this.COMMON_EXECUTE = commonExecutor;
			this.SERVER_EXECUTE = serverExecutor;
		}

		private boolean evaluate() {
			return this.EVALUATOR.shouldTransition();
		}

		private void execute(PlayerEntity player, long seed) {
			COMMON_EXECUTE.execute(player, seed);
			if(player.getWorld().isClient) {
				CLIENT_EXECUTE.execute(player, seed);
				if(player.isMainPlayer()) {
					// Send C2S state change packet
					ModMarioQuaMario.LOGGER.info("Send packet telling server to change {}'s state to {} (raw ID {})",
							player.getName(), this.TARGET_STATE.IDENTIFIER,MarioRegistries.STATES.getRawIdOrThrow(this.TARGET_STATE));
				}
			}
			else {
				SERVER_EXECUTE.execute(player, seed);
			}
		}
	}
}
