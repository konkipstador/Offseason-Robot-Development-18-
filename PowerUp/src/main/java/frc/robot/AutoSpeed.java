package frc.robot;

public enum AutoSpeed{
    TURN_LOW(0.5), TURN_HIGH(0.55), HIGH_1(0), LOW_1(0);

    private double speed;
    public double speed() {
        return this.speed;
    }
    AutoSpeed(double speed) {
        this.speed = speed;
    }
}