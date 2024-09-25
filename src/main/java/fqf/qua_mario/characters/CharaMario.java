package fqf.qua_mario.characters;

public class CharaMario extends MarioCharacter {
	public static final CharaMario INSTANCE = new CharaMario();
	private CharaMario() {
		this.name = "Mario";
		this.fullName = "Mario Mario";
	}

	@Override
	public String getSoundPrefix() {
		return "mario_";
	}

	@Override
	public CharacterStats getStats() {
//		return BASIC_STATS;
		return BASIC_STATS;
	}
}
