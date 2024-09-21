package squeek.quakemovement.mariostates;

public class MarioGrounded extends MarioState {
	public static final MarioGrounded INSTANCE = new MarioGrounded();

//	public static class JumpTransition implements MarioStateTransition {
//		@Override
//		public MarioState evaluate() {
//			return null;
//		}
//	}
//	public static class DropTransition implements MarioStateTransition {
//		@Override
//		public MarioState evaluate() {
//			return null;
//		}
//	}

	public String name = "Uwuuber";

	private MarioGrounded() {
//		this.name = "Grounded";

//		transitions = {JumpTransition, DropTransition};
	} // private constructor

	@Override
	void tick() {

	}
}
