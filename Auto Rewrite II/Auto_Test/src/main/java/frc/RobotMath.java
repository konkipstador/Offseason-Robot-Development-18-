package frc;

public class RobotMath{

    public static int inchesToTicks(double inches){
        return (int)(((3.94*Math.PI)/512)*inches);
    }
}