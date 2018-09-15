/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.MiniPID;

public class Robot extends TimedRobot {
		
	Encoders m_encoders = new Encoders();	// Encoders	

	AHRS m_ahrs = new AHRS(I2C.Port.kMXP);	// Gyroscope and Accelerometer	

	Elevator m_elevator = Elevator.getInstance();	// Elevator and intake	

	DigitalInput m_beam2 = new DigitalInput(2);	// Break Beam Sensor	

	UsbCamera m_camera = new UsbCamera("Camera",0);	// USB Camera
	MjpegServer m_server = new MjpegServer("Camera Server", 1180);	// Camera Streaming server	

	SendableChooser<String> autoModes = new SendableChooser<String>();	// Auto Selector

	WPI_TalonSRX m_r1 = new WPI_TalonSRX(0),	// Talons
		m_r2 = new WPI_TalonSRX(1),
		m_r3 = new WPI_TalonSRX(2),
		m_l1 = new WPI_TalonSRX(3),
		m_l2 = new WPI_TalonSRX(4),
		m_l3 = new WPI_TalonSRX(5);
	SpeedControllerGroup m_rightMotors = new SpeedControllerGroup(m_r1, m_r2, m_r3),	// Motor controller groups
	 	m_leftMotors = new SpeedControllerGroup(m_l1, m_l2, m_l3);
	DifferentialDrive m_drive = new DifferentialDrive(m_leftMotors, m_rightMotors);	// Drivetrain object
	
	Joystick m_joystick0 = new Joystick(0);	// Flight Stick
	Joystick m_joystick1 = new Joystick(1);	// Xbox Controller 
    
    MiniPID m_driveLoop = new MiniPID(1,0,0);	// Autonomus driving PID loop
    MiniPID m_turnLoop = new MiniPID(1,0,0);	// Autonomus turning PID loop

    AutoFunction m_currentFunction = AutoFunction.DRIVE;	// What function the auto control loop should perform
    double m_autoSetpoint = 0;		// What setpoint the PID loop should track (inches or degrees)
    double m_autoTime = 0;			// How long an autonomus action has held a low motor power OR how long a function has ran in Autonomus
    int m_autoStep = 0;				// What part of the switch statement is Autonomus on
    double m_desiredAngle = 0;		// What angle should be held while driving in Autonomus
    double m_driveOutputLimit = 0;	// Autonomus motor power limit
	boolean m_autoComplete = false;	// Used to tell the function that advances Autonomus forward if a maneuver has been completed
	String m_autoMode = "switch";	// Selected Autonomus mode
	char m_switchPosition = 'R';	// Switch plate location
	char m_scalePosition = 'L';		// Scale plate location
	final int m_cyclesAutoStalled = 12;	// How long the robot should wait after reaching the autonomus setpoint before advancing Autonomus

	// Ran once on robot initalization
	public void robotInit() {	
		
		// Configure the right drivetrain encoder
		m_r1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 50);
		m_r1.setSensorPhase(false);
		
		// Configure the left drivetrain encoder
		m_l3.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 50);
		m_l3.setSensorPhase(true);
		
		// Reset encoders
		m_encoders.reset();		
		
		// Camera settings
		m_camera.setFPS(30);
		m_camera.setResolution(160, 120);
		m_server.setSource(m_camera);

		// Autonomus Modes
		autoModes.addDefault("Cross Auto Line", "ctr");
		autoModes.addObject("Switch: Center Start", "switch");
		autoModes.addObject("Scale: Right Start", "scale");
		SmartDashboard.putData(autoModes);
	}

	public void disabledPeriodic() {
		m_elevator.disabled();
	}
	public void teleopInit() {
		m_elevator.disabled();
	}
	
	// Resets navigation instrumination 
	public void autonomousInit() {
        m_elevator.disabled();
        m_currentFunction = AutoFunction.DRIVE;
        resetNavigation();	
		m_ahrs.reset();
		m_autoStep = 0;	

		String gameSpecificMessage = DriverStation.getInstance().getGameSpecificMessage();
		if(gameSpecificMessage.equals("")){
			m_autoMode = "error";
		}else{
			char[] plateLocations = gameSpecificMessage.toCharArray();
			m_switchPosition = plateLocations[0];
			m_scalePosition = plateLocations[1]; 
		}
    }
    
	
	public void autonomousPeriodic() {
    
        double turnPower = 0;
        double basePower = 0;
		double heading = -m_ahrs.getAngle(); // Gyro is inverted;

		if(m_autoMode.equals("switch")){
			// The alliance's Switch is on the right
			if(m_switchPosition == 'R'){
				switch(m_autoStep){
					case 0:	// Drive straight 1 foot and drop the arms
						m_elevator.setLevel(LiftLevels.EXCHANGE);
						driveStraightValues(12,0,0.7);	
					break;
	
					case 1:	// Turn the robot 
						turnValues(-48.7,0.7);
					break;
	
					case 2:	// Drive near the switch
						driveStraightValues(62.2,-48.7,0.7);
					break;
	
					case 3:	// Turn towards the switch and raise the elevator to switch height
						m_elevator.setLevel(LiftLevels.SWITCH);
						turnValues(0,0.7);
					break;
	
					case 4:	// Drive up to the switch
						driveStraightValues(41,0,0.6); 
					break;
	
					case 5:	// Eject the cube
						m_currentFunction = AutoFunction.SHOOT;
					break;
	
					case 6:	// Drive away from the switch and lower the elevator after backing up 6 inches
						driveStraightValues(-60,0,0.6);
						if(m_encoders.getDistance() < -6)
							m_elevator.setLevel(LiftLevels.GROUND);				
					break;
	
					case 7:	// Turn to face the cube pile
						turnValues(52.4,0.7);
					break;
	
					case 8:	// Enable the intake and drive to the cubes
						m_elevator.grab();
						driveStraightValues(40.6,52.4,0.7);
					break;
	
					case 9:	// Stop the bot for 12 program cycles (1/4th of a second)
						m_currentFunction = AutoFunction.STOP;
						if(m_autoTime > 12)
							m_autoComplete = true;
					break;
	
					case 10:// Drive backwards near the switch
						driveStraightValues(-40.6,52.4,0.7);
					break;
	
					case 11:// Turn towards the switch and raise the elevator
						m_elevator.setLevel(LiftLevels.SWITCH);
						turnValues(0,0.7);
					break;
	
					case 12:// Drive to the switch
						driveStraightValues(41,0,0.6); 
					break;
	
					case 13:// Eject the cube
						m_currentFunction = AutoFunction.SHOOT;
					break;
	
					case 14:// Back up and lower the elevator after travelling 6 inches
						driveStraightValues(-60,0,0.6);
						if(m_encoders.getDistance() < -6)
							m_elevator.setLevel(LiftLevels.GROUND);				
					break;

					case 15:// Stop the robot and wait for Teleoperated
						m_currentFunction = AutoFunction.STOP;
					break;
				}
			}

			// The alliance's Switch is on the left
			if(m_switchPosition == 'L'){
				switch(m_autoStep){
					case 0:	// Drive straight 1 foot and drop the arms
						m_elevator.setLevel(LiftLevels.EXCHANGE);
						driveStraightValues(12,0,0.7);	
					break;
	
					case 1:	// Turn the robot 
						turnValues(54.4,0.7);
					break;
	
					case 2:	// Drive near the switch
						driveStraightValues(70.4,54.4,0.7);
					break;
	
					case 3:	// Turn towards the switch and raise the elevator to switch height
						m_elevator.setLevel(LiftLevels.SWITCH);
						turnValues(0,0.7);
					break;
	
					case 4:	// Drive up to the switch
						driveStraightValues(41,0,0.6); 
					break;
	
					case 5:	// Eject the cube
						m_currentFunction = AutoFunction.SHOOT;
					break;
	
					case 6:	// Drive away from the switch and lower the elevator after backing up 6 inches
						driveStraightValues(-60,0,0.6);
						if(m_encoders.getDistance() < -6)
							m_elevator.setLevel(LiftLevels.GROUND);				
					break;
	
					case 7:	// Turn to face the cube pile
						turnValues(-52.4,0.7);
					break;
	
					case 8:	// Enable the intake and drive to the cubes
						m_elevator.grab();
						driveStraightValues(40.6,-52.4,0.7);
					break;
	
					case 9:	// Stop the bot for 12 program cycles (1/4th of a second)
						m_currentFunction = AutoFunction.STOP;
						if(m_autoTime > 12)
							m_autoComplete = true;
					break;
	
					case 10:// Drive backwards near the switch
						driveStraightValues(-40.6,-52.4,0.7);
					break;
	
					case 11:// Turn towards the switch and raise the elevator
						m_elevator.setLevel(LiftLevels.SWITCH);
						turnValues(0,0.7);
					break;
	
					case 12:// Drive to the switch
						driveStraightValues(41,0,0.6); 
					break;
	
					case 13:// Eject the cube
						m_currentFunction = AutoFunction.SHOOT;
					break;
	
					case 14:// Back up and lower the elevator after travelling 6 inches
						driveStraightValues(-60,0,0.6);
						if(m_encoders.getDistance() < -6)
							m_elevator.setLevel(LiftLevels.GROUND);				
					break;

					case 15:// Stop the robot and wait for Teleoperated
						m_currentFunction = AutoFunction.STOP;
					break;
				}
			}
		}

        switch(m_currentFunction){

            // Driving function
            case DRIVE:

                m_driveLoop.setOutputLimits(m_driveOutputLimit);
                basePower = m_driveLoop.getOutput(m_encoders.getDistance(), m_autoSetpoint);
                
                if(heading > m_desiredAngle + 0.5)
                    turnPower = 0.4;
                if(heading > m_desiredAngle + 0.5)
                    turnPower = -0.4;

                if(Math.abs(basePower) < 0.1){
                    m_autoTime++;
                }else{
                    m_autoTime = 0;
                }

				if(m_cyclesAutoStalled > m_autoTime){    // 1/4th of a second of being still 
                    m_drive.arcadeDrive(basePower, turnPower);
                }else{
					m_drive.arcadeDrive(0,0);
					m_autoComplete = true;
                }

            break;

            // Turning funtion
            case TURN:

                turnPower = m_turnLoop.getOutput(m_desiredAngle, m_autoSetpoint);

                if(Math.abs(turnPower) < 0.1){
                    m_autoTime++;
                }else{
                    m_autoTime = 0;
                }

				if(m_cyclesAutoStalled > m_autoTime){    // 1/4th of a second of being still 
                    m_drive.arcadeDrive(0, turnPower);
                }else{
					m_drive.arcadeDrive(0,0);
					m_autoComplete = true;
                }

            break;

            // Shooting function
            case SHOOT:

				m_drive.arcadeDrive(0,0);

                if(m_autoTime < 25){    // 0.5 seconds
                    m_elevator.eject();
                }else{
                    m_elevator.stopIntake();
                    m_autoComplete = true;
                }
                m_autoTime++;

            break;

            // Makes the robot do absolutely nothing.
            default:
                m_drive.tankDrive(0, 0);
                m_elevator.stopIntake();
                m_autoTime++;
			break;
		}
		
		// Automatically advances Autonomus once part of it is complete
		if(m_autoComplete){
			m_autoComplete = false;
			resetNavigation();
			m_autoStep++;
		}
    }

	// Converts values attached to understandable names into variables are used in the Autonomus control loop,
	// while making auto switch statements easier to code and understand
    public void driveStraightValues(double setpoint, double desiredAngle, double outputLimit){
        m_autoSetpoint = setpoint;
        m_desiredAngle = desiredAngle;
		m_driveOutputLimit = outputLimit;
		m_currentFunction = AutoFunction.DRIVE;
    }
    public void turnValues(double desiredAngle, double outputLimit){
		m_autoSetpoint = desiredAngle;
		m_driveOutputLimit = outputLimit;
		m_currentFunction = AutoFunction.TURN;
    }
     
	//This enum allows a switch statement to switch between multiple auto functions
    public enum AutoFunction {
        DRIVE, TURN, SHOOT, STOP;
    }

	// Code ran during the driver-operated period of the Match.
	public void teleopPeriodic() {

		m_drive.arcadeDrive(-m_joystick0.getRawAxis(1)*m_elevator.percent(), m_joystick0.getRawAxis(2)*m_elevator.returnTurn());
		
		if(!(Math.abs(m_joystick1.getRawAxis(1))>0.1)) {
			if(m_joystick1.getRawButton(XBox.LBumper.value())) {
				m_elevator.setLevel(LiftLevels.GROUND);
			}
			if(m_joystick1.getRawButton(XBox.RBumper.value())) {
				m_elevator.setLevel(LiftLevels.EXCHANGE);
			}
			if(m_joystick1.getRawButton(XBox.A.value())) {
				m_elevator.setLevel(LiftLevels.SWITCH);
			}
			if(m_joystick1.getRawButton(XBox.Select.value())) {
				m_elevator.setLevel(LiftLevels.PORTAL);
			}
			if(m_joystick1.getRawButton(XBox.B.value())) {
				m_elevator.setLevel(LiftLevels.SCALELOW);
			}
			if(m_joystick1.getRawButton(XBox.X.value())) {
				m_elevator.setLevel(LiftLevels.SCALEMID);
			}
			if(m_joystick1.getRawButton(XBox.Y.value())) {
				m_elevator.setLevel(LiftLevels.SCALEHIGH);
			}
		}else {

			m_elevator.moveGranulary(-m_joystick1.getRawAxis(1));
		}
 
		if(m_joystick0.getRawButton(2)) {
			m_elevator.eject();
		}else {
			if(m_joystick0.getRawButton(1)) {
				m_elevator.grab();
			}else {
				m_elevator.stopIntake();
			}
		}
	}
	
	public void robotPeriodic() {
		SmartDashboard.putBoolean("BreakBeamSensor", !m_beam2.get());
	}

	// Class to manage Talon encoder feedback
	public class Encoders{	
		double cycles = 0;
		double toInches =55.245648042746041975368465658798;
	
		public void reset() {
			m_r1.setSelectedSensorPosition(0, 0, 50);
			m_l3.setSelectedSensorPosition(0, 0, 50);
		}
		
		public double getDistance() {
			cycles = (m_r1.getSelectedSensorPosition(0) + m_l3.getSelectedSensorPosition(0))/2;
			return (cycles/toInches);
		}
		
		public int getRaw() {
			return (m_r1.getSelectedSensorPosition(0) + m_l3.getSelectedSensorPosition(0))/2;
		}
	}	

	public void setCurrentLimits(WPI_TalonSRX talon, int constantDraw, int spikeDraw, int maxSpikeTime) {
		talon.configContinuousCurrentLimit(constantDraw, 20);
		talon.configPeakCurrentLimit(spikeDraw, 20);
		talon.configPeakCurrentDuration(maxSpikeTime, 20);
		talon.enableCurrentLimit(true);
	}

	// Enum for XBox buttons
	public enum XBox {
		A(1), B(2), X(3), Y(4), LBumper(5), RBumper(6), Select(8), LeftStickButton(10);	
		private int value;	
		private XBox(int val) {
			this.value = val;
		}
		public int value() {
			return this.value;
		}
    }
    
    public void resetNavigation(){	
		m_encoders.reset();
        m_driveLoop.reset();
        m_turnLoop.reset();
        m_autoSetpoint = 0;
		m_autoTime = 0;
        m_autoComplete = false;
	}
}