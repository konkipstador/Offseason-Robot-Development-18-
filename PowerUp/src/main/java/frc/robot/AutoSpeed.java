package frc.robot;

public enum AutoSpeed{
    TURN_LOW(0.55), TURN_HIGH(0.6);

    private double speed;
    public double speed() {
        return this.speed;
    }
    AutoSpeed(double speed) {
        this.speed = speed;
    }
}