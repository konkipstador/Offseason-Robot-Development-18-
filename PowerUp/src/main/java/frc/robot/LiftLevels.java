package frc.robot;

public enum LiftLevels{
    GROUND(30), EXCHANGE(1724), PORTAL(5750), SWITCH(10400), SCALELOW(20000), SCALEMID(24236), SCALEHIGH(28250);

    private int encoderPosition;
    public int encoderPosition() {
        return this.encoderPosition;
    }
    LiftLevels(int encoderPosition) {
        this.encoderPosition = encoderPosition;
    }
}