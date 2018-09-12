package frc;

public interface Command{
    
    MiniPID driveLoop = new MiniPID(.001,0,.02);
    MiniPID turnLoop = new MiniPID(.042,0.001,.32);

    // Some commands won't use both inputs at once; use null values as applicable
    public double calculate(double input);

    public char getType();
}