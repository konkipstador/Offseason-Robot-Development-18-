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
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class Robot extends TimedRobot {
    
    AHRS m_navx = new AHRS(I2C.Port.kMXP);  // The Nav-X attached to the MXP

    Joystick _dualshock4 = new Joystick(0);  // Dualshock 4 gamepad serving as drive controller

    TalonSRX _talon_left_master = new TalonSRX(1);  // Talons
    TalonSRX _talon_left_slave = new TalonSRX(2);
    // ID 3 should only be used in a 3 CIM system
    TalonSRX _talon_right_master = new TalonSRX(4);
    TalonSRX _talon_right_slave = new TalonSRX(5);
    // ID 6 should only be used in a 3 CIM system

    int _talon_timeout = 50; // Talon timeout set to 50 millisecondss

    PowerDistributionPanel _pdp = new PowerDistributionPanel();    // PDP must be set to ID 0

    // Do all distance measurements in inches, not feet or meters
    Waypoint[] _waypoints = new Waypoint[] {
        new Waypoint(0,0,Pathfinder.d2r(0)),
        new Waypoint(60,0,Pathfinder.d2r(90)),
        new Waypoint(60,60,Pathfinder.d2r(180)),
    };

    // Pathfinder Trajectory Generation
    double _control_loop_timestep = 0.05;   // 50 milliseconds
    double _max_velocity = 66.9;            // 66.9 inches/sec
    double _max_acceleration = 78.7;        // 78.7 inches/sec^2
    double _max_jerk = 2362.2;              // 2362.2 inches/sec^3
    Trajectory.Config _trajectory_config = new Trajectory.Config
        (Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, _control_loop_timestep, _max_velocity, _max_acceleration, _max_jerk);
    Trajectory _robot_trajectory = Pathfinder.generate(_waypoints, _trajectory_config);

    // Trajectory modification data
    double _wheelbase_width = 24.75;    // TestyBot is 24.75 inches wide from Colson to Colson
    TankModifier _trajectory_modifier = new TankModifier(_robot_trajectory);
    double _wheel_size = 4; // 4 inch Colsons on TestyBot
    int _encoder_ticks_per_revolution = 512; // TestyBot uses 128 tick Quadrature encoders, so 128*4 ticks

    // Pathfinder EncoderFollower objects
    EncoderFollower _left_encoder;
    EncoderFollower _right_encoder;

    // EncoderFollower PID variables
    double _encoder_kp = 1; // Proportianal gain
    double _encoder_kd = 0; // Derivative gain, tweak for better pathfinding
    double _encoder_kv = 1/_max_acceleration;   // Max Acceleration PID argument (automatially calculated)
    double _encoder_ka = 0; // Acceleration gain, tweak for better acceleration

    public void robotInit() { 
        
        // ---PATHFINDER---
        _trajectory_modifier.modify(_wheelbase_width);  // Modify the trajectory based on the robot's width

        // Left Side
        _left_encoder = new EncoderFollower(_trajectory_modifier.getLeftTrajectory());
        _left_encoder.configureEncoder(0, _encoder_ticks_per_revolution, _wheel_size);
        _left_encoder.configurePIDVA(_encoder_kp, 0, _encoder_kd, _encoder_kv, _encoder_ka);

        // Right Side
        _right_encoder = new EncoderFollower(_trajectory_modifier.getRightTrajectory());
        _right_encoder.configureEncoder(0, _encoder_ticks_per_revolution, _wheel_size);
        _right_encoder.configurePIDVA(_encoder_kp, 0, _encoder_kd, _encoder_kv, _encoder_ka);
        // ---END OF PATHFINDER SETUP--


        // Set slave Talons to follow their masters
        _talon_left_slave.set(ControlMode.Follower, _talon_left_master.getBaseID());
        _talon_right_slave.set(ControlMode.Follower, _talon_right_master.getBaseID());

        // Configure the Talon SRX encoders plugged into the master Talons
        _talon_left_master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, _talon_timeout);
        _talon_right_master.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, _talon_timeout);

        // Invert right side Talons
        _talon_right_master.setInverted(true);
        _talon_right_slave.setInverted(true);
    } 

    public void autonomousInit() { 
        // Zero sensors
        m_navx.reset();
        _talon_left_master.setSelectedSensorPosition(0, 0, _talon_timeout);
        _talon_right_master.setSelectedSensorPosition(0, 0, _talon_timeout);
    }

    public void disabledPeriodic() { 

    }

    public void autonomousPeriodic() { 

    }

    public void teleopPeriodic() { 
        // Tank Drive
        _talon_left_master.set(ControlMode.PercentOutput, _dualshock4.getRawAxis(1));
        _talon_right_master.set(ControlMode.PercentOutput, _dualshock4.getRawAxis(5));
    }

    public void robotPeriodic() {
        SmartDashboard.putNumber("Amp Draw", _pdp.getTotalCurrent());   // Total amp draw Dashboard readout
    }
}