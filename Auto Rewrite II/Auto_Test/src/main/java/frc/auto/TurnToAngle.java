package frc.auto;

import frc.Command;

public class TurnToAngle implements Command{

    int m_finalAngle = 0; 
    double m_calculatedPower = 0;
    double m_maxPower = 0;

    public TurnToAngle(int finalPosition, double maxPower){
        m_maxPower = maxPower;
        m_finalAngle = finalPosition;
        
        driveLoop.setSetpoint(finalPosition);
        driveLoop.setOutputLimits(maxPower);
    }

    public double calculate(double gyroInput){
        m_calculatedPower = turnLoop.getOutput(gyroInput);
        return m_calculatedPower;
    }

    public char getType(){
        return 'a';
    }
}