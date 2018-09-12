package frc.auto;

import frc.Command;

public class DriveToPoint implements Command{

    int m_finalPosition = 0; 
    double m_calculatedPower = 0;
    double m_maxPower = 0;

    public DriveToPoint(int finalPosition, double maxPower){
        m_maxPower = maxPower;
        m_finalPosition = finalPosition;

        driveLoop.setSetpoint(finalPosition);
        driveLoop.setOutputLimits(maxPower);
    }

    public double calculate(double encoderInput){
        m_calculatedPower = driveLoop.getOutput(encoderInput);
        return m_calculatedPower;
    }

    public char getType(){
        return 'd';
    }
}