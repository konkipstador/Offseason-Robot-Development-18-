package frc.team957.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.stormbots.MiniPID;

public class Robot extends TimedRobot {
    
    AHRS _navx = new AHRS(I2C.Port.kMXP);  // The Nav-X attached to the MXP

    Joystick _dualshock4 = new Joystick(0);  // Dualshock 4 gamepad serving as drive controller

    TalonSRX _talon_left_master = new TalonSRX(1);  // Talons
    TalonSRX _talon_left_slave = new TalonSRX(2);
    // ID 3 should only be used in a 3 CIM system
    TalonSRX _talon_right_master = new TalonSRX(4);
    TalonSRX _talon_right_slave = new TalonSRX(5);
    // ID 6 should only be used in a 3 CIM system

    int _talon_timeout = 50; // Talon timeout set to 50 millisecondss

    PowerDistributionPanel _pdp = new PowerDistributionPanel();    // PDP must be set to ID 0  

    public void robotInit() { 

        // Set slave Talons to follow their masters
        _talon_left_slave.set(ControlMode.Follower, 1);
        _talon_right_slave.set(ControlMode.Follower, 4);

        // Configure the Talon SRX encoders plugged into the master Talons
        _talon_left_master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, _talon_timeout);
        _talon_right_master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, _talon_timeout);
        _talon_left_master.setSensorPhase(true);
        _talon_right_master.setSensorPhase(true);

        // Invert right side Talons
        _talon_right_master.setInverted(true);
        _talon_right_slave.setInverted(true);

        // Set PID paramaters
        _turning_loop.setOutputLimits(1);   // Sets output of the loop to a value the Talons can read
        _turning_loop.setOutputFilter(1);
        _turning_loop.setOutputRampRate(.2);

        _drive_loop.setOutputLimits(1);   // Sets output of the loop to a value the Talons can read
        _drive_loop.setOutputFilter(1);
        _drive_loop.setOutputRampRate(.1);
    } 

    public void autonomousInit() { 
        // Zero sensors
        
    }

    public void disabledPeriodic() { 
        _navx.reset();
        _talon_left_master.setSelectedSensorPosition(0, 0, _talon_timeout);
        _talon_right_master.setSelectedSensorPosition(0, 0, _talon_timeout);
        _turning_loop.reset();
    }

    double _motor_power = 0;  
    public void autonomousPeriodic() { 
        
        driveToPosition(toCycles(48));
        
    }

    public void teleopPeriodic() { 
        // Tank Drive
        _talon_left_master.set(ControlMode.PercentOutput, _dualshock4.getRawAxis(1));   
        _talon_right_master.set(ControlMode.PercentOutput, _dualshock4.getRawAxis(5));
    }

    public void robotPeriodic() {
        SmartDashboard.putNumber("Amp Draw", _pdp.getTotalCurrent());   // Total amp draw Dashboard readout
    }

    public static int toCycles(double inches){
        return (int)(inches*41.259408031924259196794170555305);
    }

    MiniPID _drive_loop = new MiniPID(.001,0,.02);
    public void driveToPosition(int setpoint){
        _motor_power = _drive_loop.getOutput((_talon_right_master.getSelectedSensorPosition(0) + _talon_left_master.getSelectedSensorPosition(0))/2,setpoint);
        _talon_left_master.set(ControlMode.PercentOutput, _motor_power);
        _talon_right_master.set(ControlMode.PercentOutput, _motor_power);
    }

    MiniPID _turning_loop = new MiniPID(.042,0.001,.32);
    int _cycles_still = 0;
    public boolean turnToAngle(double setpoint){
        _motor_power = _turning_loop.getOutput(_navx.getAngle(),setpoint) * .65;

        if(_navx.getAngle() > setpoint - 0.6 && _navx.getAngle() < setpoint + 0.6){
            _motor_power = 0;
            _cycles_still++;
        
        }else{
            _cycles_still = 0;
        }

        _talon_left_master.set(ControlMode.PercentOutput, _motor_power);
        _talon_right_master.set(ControlMode.PercentOutput, -_motor_power);
        //System.out.println(_motor_power);

        if(_cycles_still == 5){
            return true;
        }
        return false;
        
    }
}
