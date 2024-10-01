package fqf.qua_mario;

public enum Input {
	FORWARD,
	BACKWARD,
	LEFT,
	RIGHT,

	JUMP,
	DUCK,
	SPIN;

	private boolean isHeld;
	private int pressBuffer;

	Input() {
		this.isHeld = false;
		this.pressBuffer = 0;
	}

	public static void update(net.minecraft.client.input.Input input) {
		FORWARD.update(input.pressingForward);
		BACKWARD.update(input.pressingBack);
		RIGHT.update(input.pressingRight);
		LEFT.update(input.pressingLeft);

		JUMP.update(input.jumping);
		DUCK.update(input.sneaking);

		boolean spinKeybind = ModMarioQuaMario.CONFIG.getSpinputType() != ModConfig.SpinputType.LEFTRIGHT
				&& exhaustSpinBindingWasPressed();
		boolean spinLeftRightPress =
				ModMarioQuaMario.CONFIG.getSpinputType() != ModConfig.SpinputType.KEYBIND
				& LEFT.isPressedNoUnbuffer() && RIGHT.isPressedNoUnbuffer();
		boolean spinLeftRightHold =
				ModMarioQuaMario.CONFIG.getSpinputType() != ModConfig.SpinputType.KEYBIND
				& LEFT.isHeld() && RIGHT.isHeld();
		SPIN.update(spinKeybind || spinLeftRightPress, spinKeybind || spinLeftRightHold);
	}

	/**
	 * <p>This method is stupid.
	 * @return whether the Spin key is being held
	 */
	private static boolean exhaustSpinBindingWasPressed() {
		boolean bindingStatus = false;
		while(ModMarioQuaMarioClient.spinBinding.wasPressed())
			bindingStatus = true;
		return bindingStatus;
	}

	private void update(boolean holdStatus) {
		this.update(holdStatus, holdStatus);
	}

	private void update(boolean pressStatus, boolean holdStatus) {
		if(pressStatus && holdStatus && !isHeld)
			this.pressBuffer = ModMarioQuaMario.CONFIG.getBufferLength();
		else
			this.pressBuffer--;
		this.isHeld = holdStatus;
	}

	public boolean isHeld() {
		return isHeld;
	}

	public boolean isPressed() {
		boolean isPressed = isPressedNoUnbuffer();
		if(isPressed) unbuffer();
		return isPressed;
	}

	public void unbuffer() {
		this.pressBuffer = 0;
	}

	public boolean isPressedNoUnbuffer() {
		return pressBuffer > 0;
	}
}
