package frc.robot;

public enum ControllerButtons{
	// Dualshock 4 Buttons
	SQUARE(1), CROSS(2), CIRCLE(3), TRIANGLE(4), L1(5), R1(6), L2(7), R2(8), SHARE(9), OPTIONS(10),
	L3(11), R3(12), HOME(13), TOUCHPAD(14),
	// XBox 360 Buttons
	A(1), B(2), X(3), Y(4), LB(5), RB(6), START(7), SELECT(8), LEFT_STICK_DOWN(10);

	private int buttonValue;
	public int get() {
		return this.buttonValue;
	}
	ControllerButtons(int buttonValue) {
		this.buttonValue = buttonValue;
	}
}