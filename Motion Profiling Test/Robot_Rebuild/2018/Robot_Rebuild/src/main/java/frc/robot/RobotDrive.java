package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.I2C;

public class RobotDrive{

    private static RobotDrive m_robotDrive; // Static synchronized robot drive object

    AHRS m_navX = new AHRS(I2C.Port.kMXP);  // NavX gyroscope
    TalonSRX m_leftMotor1, m_leftMotor2, m_leftMotor3, m_rightMotor1, m_rightMotor2, m_rightMotor3;    // Drivetrain motor controllers
    
    private int m_leftTalonEnc = 0, m_rightTalonEnc = 0;        // Variables for storing raw encoder values
    private double m_leftTalonDist = 0, m_rightTalonDist = 0;   // Variables for storing distance values derived from the encoder
    private double m_conversionValue = 0;                       // Stores the value that converts from raw encoder output to the desired units
    private double m_heading = 0;

    private RobotDrive(){}  // Empty Constructor

    // Creates the drivetrain object if it is not already created, then returns it
    public static synchronized RobotDrive getInstance(){
        if (m_robotDrive == null)
            m_robotDrive = new RobotDrive();

        return m_robotDrive;     
    }

    // Sets TalonSRX IDs
    public void setTalonIDs(int left1, int left2, int left3, int right1, int right2, int right3){

        // Left 1 will be the talon which the encoder will pull from
        m_leftMotor1 = new TalonSRX(left1);
        m_leftMotor2 = new TalonSRX(left2);
        //m_leftMotor3 = new TalonSRX(left3);
        m_leftMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 50);

        // Right 1 will be the talon which the encoder will pull from
        m_rightMotor1 = new TalonSRX(right1);
        m_rightMotor2 = new TalonSRX(right2);
       // m_rightMotor3 = new TalonSRX(right3);
        m_rightMotor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 50);
    }

    // Sets both sides of the Robot to move at different motor powers
    public void drive(double leftPower, double rightPower){
        m_leftMotor1.set(ControlMode.PercentOutput, leftPower);
        m_leftMotor2.set(ControlMode.PercentOutput, leftPower);
        //m_leftMotor3.set(ControlMode.Position, leftPower);

        m_rightMotor1.set(ControlMode.PercentOutput, rightPower);
        m_rightMotor2.set(ControlMode.PercentOutput, rightPower);
        //m_rightMotor3.set(ControlMode.Position, rightPower);
    }

    // Disables motor power
    public void stop(){
        m_leftMotor1.set(ControlMode.PercentOutput, 0);
        m_leftMotor2.set(ControlMode.PercentOutput, 0);
        //m_leftMotor3.set(ControlMode.Position, 0);

        m_rightMotor1.set(ControlMode.PercentOutput, 0);
        m_rightMotor2.set(ControlMode.PercentOutput, 0);
        //m_rightMotor3.set(ControlMode.Position, 0);
    }

    public void invertRight(boolean value){
        m_rightMotor1.setInverted(value);
        m_rightMotor2.setInverted(value);
        //m_rightMotor3.setInverted(true);
    }

    public void invertLeft(boolean value){
        m_leftMotor1.setInverted(value);
        m_leftMotor2.setInverted(value);
        //m_leftMotor3.setInverted(true);
    }

    // Sets if phase should be shifted for each encoder
    public void setEncPhase(boolean leftPhase, boolean rightPhase){
        m_leftMotor1.setSensorPhase(leftPhase);
        m_rightMotor1.setSensorPhase(rightPhase);
    }

    // Sets the left talon encoder value to it's respective variable and returns it
    public int getLeftEnc(){
        m_leftTalonEnc = m_leftMotor1.getSelectedSensorPosition(0);
        return m_leftTalonEnc;
    }

    // Sets the right talon encoder value to it's respective variable and returns it
    public int getRightEnc(){
        m_rightTalonEnc = m_rightMotor1.getSelectedSensorPosition(0);
        return m_rightTalonEnc;
    }

    // Sets the conversion value used to convert between raw encoder ticks to 
    public void setConversionValue(double conversionValue){
        m_conversionValue = conversionValue;
    }

    // Returns the distance the left side of the robot has traveled, calculated by multiplying the encoder value by the conversion value
    public double getLeftDistance(){
        m_leftTalonDist = getLeftEnc()*m_conversionValue;
        return m_leftTalonDist;
    }

    // Returns the distance the right side of the robot has traveled, calculated by multiplying the encoder value by the conversion value
    public double getRightDistance(){
        m_rightTalonDist = getRightEnc()*m_conversionValue;
        return m_rightTalonDist;
    }

    // Returns the NavX's calculated robot heading
    public double getHeading(){
        m_heading = m_navX.getAngle();
        return m_heading;
    }

    public void resetEncoders(){
        m_leftMotor1.setSelectedSensorPosition(0, 0, 50);
        m_rightMotor1.setSelectedSensorPosition(0, 0, 50);
    }

    public double getLeftSpeed(){
        return m_leftMotor1.getMotorOutputPercent();
    }

    public double getRightSpeed(){
        return m_rightMotor1.getMotorOutputPercent();
    }

    public void centerGyro(){
        m_navX.reset();
    }
}