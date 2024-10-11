package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.Input;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;
import fqf.qua_mario.oldmariostates.OldMarioState;
import fqf.qua_mario.oldmariostates.states.groundbound.DuckSlide;
import fqf.qua_mario.oldmariostates.states.groundbound.DuckWaddle;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.List;

public class DuckJump extends AirborneState {
	public static final DuckJump INSTANCE = new DuckJump();

	private DuckJump() {
		this.name = "Duck Jump";
		this.isJump = true;
		this.jumpCapStat = CharaStat.DUCK_JUMP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(List.of(
				() -> {
					OldMarioState landingResult = AirborneTransitions.DOUBLE_JUMPABLE_LANDING.evaluate();
					if(landingResult != null) {
						return DuckSlide.enterDuckSlide() ? DuckSlide.INSTANCE: DuckWaddle.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(List.of(
				() -> {
					if(!Input.DUCK.isHeld())
						return Jump.INSTANCE;
					return null;
				}
		));
	}

	@Override
	public boolean getSneakLegality() {
		return true;
	}
}
