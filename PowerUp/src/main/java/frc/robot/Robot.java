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

public class Robot extends TimedRobot {
		
	Encoders m_encoders = new Encoders();	// Encoders	

	AHRS m_ahrs = new AHRS(I2C.Port.kMXP);	// Gyroscope and Accelerometer	

	Elevator m_elevator = null;	// Elevator and intake	

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

    double m_autoTime = 0;			// How long an autonomus action has held a low motor power OR how long a function has ran in Autonomus
    int m_autoStep = 0;				// What part of the switch statement is Autonomus on
	String m_autoMode = "switch";	// Selected Autonomus mode
	char m_switchPosition = 'R';	// Switch plate location
	char m_scalePosition = 'L';		// Scale plate location
	double speed = 0;
	int m_autoCount = 0;
	int m_encoderOffset = 0;
	double m_rpm = 0;

	// Ran once on robot initalization
	public void robotInit() {	

		m_elevator = Elevator.getInstance();

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
		autoModes.addObject("Scale: Right Start", "scale right");
		autoModes.addObject("Scale: Right Slam", "slam right");
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
        resetNavigation();	
		m_ahrs.reset();
		m_autoStep = 0;	

		m_autoMode = autoMode();
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
		SmartDashboard.putNumber("m_rpm",m_rpm);

		// SWITCH CENTER AUTO
		if(m_autoMode.equals("switch")) {
			switch(m_autoStep) {
			
			// Drive forward a small amount to get away from the wall
			case 0:
				m_elevator.setLevel(LiftLevels.EXCHANGE);
				if(m_encoders.getDistance() > 6) {
					m_drive.tankDrive(0, 0);
					m_autoStep = 1;
				}else {
					driveStraight(0.6,0);
				}
				
				break;
				
			// Roll forward to 1 foot then move to the next step
			case 1:
				
				if(m_encoders.getDistance() > 11) {
					m_autoStep = 2;
				}
				break;
			
			// Turn towards the side of the Switch is ours
			case 2:
				
				if(m_switchPosition == 'L') {
					if(m_ahrs.getYaw() < -35) {
						speed = AutoSpeed.TURN_LOW.speed();
					}else {
						speed = AutoSpeed.TURN_HIGH.speed();
					}
					
					if(m_ahrs.getYaw() < -43) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 3;
						m_encoders.reset();
					}
					m_drive.tankDrive(-speed,speed);
				}
				
				if(m_switchPosition == 'R') {
					if(m_ahrs.getYaw() > 35) {
						speed = AutoSpeed.TURN_LOW.speed();
					}else {
						speed = AutoSpeed.TURN_HIGH.speed();
					}
					
					if(m_ahrs.getYaw() > 43) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 3;
						m_encoders.reset();
					}
					m_drive.tankDrive(speed,-speed);
				}
				
				break;
				
			// Drive forward to about the center of our switch
			case 3:

				if(m_switchPosition == 'R') {
					if(m_encoders.getDistance() > 30) {
						speed = 0.6;
					}else {
						speed = 0.7;
					}	
					
					if(m_encoders.getDistance() > 50) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 4;
					}else {
						if(m_switchPosition == 'R')
						driveStraight(speed,45);
						if(m_switchPosition == 'L')
							driveStraight(speed,-45);
					}
				}
				
				if(m_switchPosition == 'L') {
					if(m_encoders.getDistance() > 50) {
						speed = 0.6;
					}else {
						speed = 0.7;
					}	
					
					if(m_encoders.getDistance() > 70) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 4;
					}else {
						if(m_switchPosition == 'R')
						driveStraight(speed,45);
						if(m_switchPosition == 'L')
							driveStraight(speed,-45);
					}
				}


				
				break;
				
				
			// Turn forward to face the switch
			case 4:
				
				if(m_switchPosition == 'L') {
					if(m_ahrs.getYaw() > -20) {
						speed = AutoSpeed.TURN_LOW.speed();
					}else {
						speed = AutoSpeed.TURN_HIGH.speed();
					}
					
					if(m_ahrs.getYaw() > -6) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 5;
						m_encoders.reset();
					}
					m_drive.tankDrive(speed,-speed);
				}
				
				if(m_switchPosition == 'R') {
					if(m_ahrs.getYaw() < 20) {
						speed = AutoSpeed.TURN_LOW.speed();
					}else {
						speed = AutoSpeed.TURN_HIGH.speed();
					}
					
					if(m_ahrs.getYaw() < 3) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 5;
						m_encoders.reset();
						m_autoCount = 0;
					}
					m_drive.tankDrive(-speed,speed);
				}
				
				break;
			
			// Raise lift and drive forward to the switch
			case 5:
				m_elevator.setLevel(LiftLevels.SWITCH);
				m_autoCount++;
				driveStraight(0.6, 0);
				if(m_rpm < 2 && m_autoCount > 75) {
					m_drive.tankDrive(0, 0);
					m_autoStep = 6;
					m_autoCount = 0;
				}
				break;
			
			// Eject the cube
			case 6:
				
				m_elevator.eject();
				m_autoCount++;
				
				if(m_autoCount > 50) {
					m_elevator.stopIntake();
					m_autoStep = 7;
					m_autoCount = 0;
					m_encoders.reset();
				}
				break;
			
			// Back up for 60 inches
			case 7:

				if(m_encoders.getDistance()<-10)
					m_elevator.setLevel(LiftLevels.GROUND);
				
				if(m_encoders.getDistance() < -55) {
					m_drive.tankDrive(0, 0);
					m_autoStep = 8;
					
				}else {
					driveStraight(-0.65,0);
				}
				
				break;
				
			case 8:
				if(m_switchPosition == 'R') {
					if(m_ahrs.getYaw() < -35) {
						speed = AutoSpeed.TURN_LOW.speed();
					}else {
						speed = AutoSpeed.TURN_HIGH.speed();
					}
					
					if(m_ahrs.getYaw() < -40) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 9;
						m_encoders.reset();
						m_autoCount = 0;
					}
					m_drive.tankDrive(-speed,speed);
				}

				if(m_switchPosition == 'L') {
					if(m_ahrs.getYaw() > 35) {
						speed = AutoSpeed.TURN_LOW.speed();
					}else {
						speed = AutoSpeed.TURN_HIGH.speed();
					}
					
					if(m_ahrs.getYaw() > 40) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 9;
						m_encoders.reset();
						m_autoCount = 0;
					}
					m_drive.tankDrive(speed,-speed);
				}
				break;

			case 9:

				m_elevator.grab();
				if(m_encoders.getDistance() > 47) {
					speed = 0.6;
				}else {
					speed = 0.65;
				}	
				
				if(m_encoders.getDistance() > 55) {
					m_drive.tankDrive(0, 0);
					m_autoStep = 10;
					m_autoTime = 0;
				}else {
					if(m_switchPosition == 'R')
						driveStraight(speed,-45);
					if(m_switchPosition == 'L')
						driveStraight(speed,45);
				}
				
				break;

			case 10:

				m_autoTime++;
				if(m_autoTime == 25)
					m_elevator.stopIntake();

				if(m_autoTime == 35){
					m_elevator.setLevel(LiftLevels.EXCHANGE);
				}
					

				break;
			}
		}
		
		// CROSS AUTOLINE AUTO
		if(m_autoMode.equals("ctl")) {
			switch(m_autoStep) {
			case 0:
				
				if(m_encoders.getDistance() > 70) {
					speed = 0.4;
				}else {
					speed = 0.6;
				}
				
				
				if(m_encoders.getDistance() > 101) {
					m_drive.tankDrive(0, 0);
					m_autoStep = 1;
				}else {
					driveStraight(speed,0);
				}
				
				break;
				
			case 1:
				
				break;
			}
		}
		
		// Right Cross Scale
		if(m_autoMode.equals("scale right")) {
			// Our Side
			if(m_scalePosition == 'R') {
				switch(m_autoStep) {
				// Drive forward
				case 0:
					
					if(m_encoders.getDistance() > 190) {
						speed = 0.55;
					}else {
						speed = 0.7;
					}
					if((m_encoders.getDistance() > 220)) {
						m_drive.tankDrive(0, 0);
						m_autoStep = 1;
						m_encoders.reset();
						m_autoCount = 0;
					}
					break;
				case 7:
				break;				
				}	
			}
		}	

		if(m_autoMode.equals("slam right")){
			// If the scale is on our side, proceed driving to the scale
				if(m_scalePosition == 'R') {
					
					switch(m_autoStep) {
					
					// Drive to the scale
					case 0:		
						if(m_encoders.getDistance() > 230) {
							speed = 0.5;
						}else {
							speed = 0.8;
						}		
						if((m_encoders.getDistance() > 292)) {
							m_drive.tankDrive(0, 0);
							m_autoStep = 1;
							m_encoders.reset();
							m_autoCount = 0;
						}else {
							driveStraight(speed,0);
						}		
						if( m_encoders.getDistance() > 384) {
							m_drive.tankDrive(0, 0);
						}
						
						break;
					
					// Turn to face the Scale
					case 1:
			
						if(m_ahrs.getYaw() < -80) {
							speed = 0.5;
						}else {
							speed = 0.6;
						}
						
						if(m_ahrs.getYaw() < -88) {
							m_drive.tankDrive(0, 0);
							m_autoStep = 2;
							m_encoders.reset();
							m_autoCount = 0;
						}
						m_drive.tankDrive(-speed,speed);
						
						break;
					
					// Back up and square against the wall
					case 2:
						m_autoCount++;
						m_elevator.setLevel(LiftLevels.EXCHANGE);
						driveStraight(-0.55, -90);
						if(m_rpm < 0.1 && m_autoCount > 25) {
							m_autoStep = 3;
							m_autoCount = 0;
						}
						
						break;
						
					// Lift the elevator and proceed after 2 seconds	
					case 3:
						m_autoCount++;
						m_elevator.setLevel(LiftLevels.SCALEHIGH);
						if(m_autoCount > 100) {
							m_autoStep = 4;
							m_encoders.reset();
						}
						break;
					
					// Drive forward to the Scale
					case 4:
						driveStraight(0.5, -90);
						if(m_encoders.getDistance() > 24) {
							m_drive.tankDrive(0, 0);
							m_autoStep = 5;
							m_autoCount = 0;
						}
						break;
						
					// Eject the cube
					case 5:
						m_elevator.eject();
						m_autoCount++;
						
						if(m_autoCount > 50) {
							m_elevator.stopIntake();
							m_autoStep = 6;
							m_autoCount = 0;
						}
						break;
						
					// Drive backwards until the wall is hit
					case 6:
						driveStraight(-0.5, -90);
						m_autoCount++;
						
						if(m_autoCount > 120 || (m_rpm < 0.1 && m_autoCount > 50)) {
							m_drive.tankDrive(0,0);
							m_autoStep = 7;
							m_autoCount = 0;
						}
						break;
					
					// Lower the lift
					case 7:
						m_elevator.setLevel(LiftLevels.GROUND);
						break;
						
					// Empty Case
					case 8:
						break;
					}
				
				// If the scale is not on our side, cross the line
				}else {
					switch(m_autoStep) {
					
					// Cross the line
					case 0:
						m_elevator.setLevel(LiftLevels.EXCHANGE);
						if(m_encoders.getDistance() > 115) {
							speed = 0.45;
						}else {
							speed = 0.6;
						}
						
						
						if(m_encoders.getDistance() > 135) {
							m_drive.tankDrive(0, 0);
							
							if(m_switchPosition == 'R') {
								m_autoStep = 1;
								m_elevator.setLevel(LiftLevels.SWITCH);
							}
							
						}else {
							driveStraight(speed,0);
						}
						break;
					
					// Turn
					case 1:
						
						if(m_ahrs.getYaw() > -70) {
							speed = 0.6;
						}else {
							speed = 0.5;
						}
						
						if(m_ahrs.getYaw() < -80) {
							m_drive.tankDrive(0, 0);
							m_autoStep = 2;
							m_autoCount = 0;
						}
						
						m_drive.tankDrive(-speed, speed);
						
					break;
					
					case 2:
						
						driveStraight(0.5, -90);
						m_autoCount++;
						if(m_rpm < 2 && m_autoCount > 25) {
							m_drive.tankDrive(0, 0);
							m_autoStep = 3;
							m_autoCount = 0;
						}
						
						break;
						
					case 3:
						
						m_elevator.eject();
						m_autoCount++;
						
						if(m_autoCount > 50) {
							m_elevator.stopIntake();
							m_autoStep = 4;
							m_autoCount = 0;
						}
						
						break;
						
						
						
					case 4:
						
						driveStraight(-0.5,-90);
						m_autoCount++;
						if(m_rpm < 2 && m_autoCount > 25) {
							m_drive.tankDrive(0, 0);
							m_elevator.setLevel(LiftLevels.GROUND);
							m_autoStep = 5;
						}
						
						break;
						
					case 5:
						break;
				}	
			}	
		}
    }

	public void driveStraight(double speed, double heading) {
		double gyro = m_ahrs.getYaw();
		double turn = 0;
		if(gyro > heading+0.5) {
			turn = -0.35;
		}
		if(gyro < heading-0.5) {
			turn = 0.35;	
		}
		if(gyro >= heading-0.5 && gyro <= heading + 0.5) {
			turn = 0;
		}
		
		m_drive.arcadeDrive(speed, turn);
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


		m_rpm = Math.abs(((m_encoders.getRaw()-m_encoderOffset)/204.8)*50);
		m_encoderOffset = m_encoders.getRaw();
		SmartDashboard.putNumber("feedback", m_rpm);
	}

	// Class to manage Talon encoder feedback
	public class Encoders{	
		double cycles = 0;
		double toInches = 55.245648042746041975368465658798;
	
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

		m_autoTime = 0;
	}

	public String autoMode(){
		return autoModes.getSelected();
	}
}