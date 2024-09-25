package fqf.qua_mario.characters;

public abstract class MarioCharacter {
	public abstract static class CharacterStats {
		public double walkAccel = 1;
		public double walkFromStandstillAccel = 1;
		public double walkSpeed = 1;

		public double runAccel = 1;
		public double runSpeed = 1;
		public double pSpeed = 1;

		public double strafeAccel = 1;
		public double strafeSpeed = 1;

		public double backpedalAccel = 1;
		public double backpedalSpeed = 1;
		public double skidFactor = 1;

		public double jumpVel = 1;
		public double sideflipBackwardsSpeed = 1;
		public double tripleJumpThreshold = 1;
	}

	private static class BasicStats extends CharacterStats {}
	protected static final CharacterStats BASIC_STATS = new BasicStats();

	public String name;
	public String fullName;
	public abstract String getSoundPrefix();

	protected CharacterStats stats;

	public CharacterStats getStats() {
		// If character-specific stats are disabled in the config, use the BASIC_STATS.
		if(true) {
			return BASIC_STATS;
		}
		return stats;
	}

}
