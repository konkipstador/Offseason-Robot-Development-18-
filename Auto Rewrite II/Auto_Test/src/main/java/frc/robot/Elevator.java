package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Class used to control the elevator on the 2018 robot. Uses a VersaPlanetary
 * encoder slice and a 775 Pro or a 755 Redline motor. Uses a PID control loop
 * ran on a Talon SRX to calculate position values.
 * 
 * @author Caleb Shilling (Team 957)
*/
public class Elevator {
	
	private static Elevator m_elevatorSystem;	// Synchronized Elevator object

	TalonSRX m_elevator = new TalonSRX(6);
	TalonSRX m_intakeMaster = new TalonSRX(7);	// Left intake motor
	TalonSRX m_intakeSlave = new TalonSRX(8);	// Right intake motor

	int m_targetPosition = 0;					// Target elevator position
	boolean m_checkElevatorPosition = true;		// Used to check if moveGranularly should set the target position used by the formula to the elevator's target position
	
	/**
	 * Initalizes all PIDF values and motion profiling fields associated with the
	 * elevator
	 */
	private Elevator() {
		m_elevator.configPeakCurrentLimit(29, 50);	// Peak current limit
		m_elevator.configPeakCurrentDuration(10, 50);	// Peak current limit time
		m_elevator.configContinuousCurrentLimit(25, 50);	// Max sustained current limit
		m_elevator.enableCurrentLimit(true);
		m_elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 50);	// Encoder initalization
		m_elevator.setSensorPhase(true);	// Inverts the encoder
		m_elevator.config_kF(0,.2177, 50);	// Feed forward
		m_elevator.config_kP(0, 1.5493, 50);	// Proportional gain
		m_elevator.config_kI(0, 0.016, 50);	// Integral gain
		m_elevator.config_kD(0,15.493, 50);	// Differential gain
		m_elevator.config_IntegralZone(0, 50, 20);	// Integral zone
		m_elevator.configMotionCruiseVelocity(2000,50);	// Max speed
		m_elevator.configMotionAcceleration(2000, 50);	// Max acceleration
		m_elevator.setSelectedSensorPosition(0, 0, 50);	// Zeroes the encoder

		m_intakeSlave.setInverted(true);	// Sets the right intake motor to invert it's input
		m_intakeSlave.set(ControlMode.Follower, 7);	// Sets the right intake motor to follow the left inake motor
	}
	
	/**
	 * Sets the target position of the elevator
	 */
	public void setLevel(LiftLevels level) {	
		m_checkElevatorPosition = true;
		m_targetPosition = level.encoderPosition();
		m_elevator.set(ControlMode.MotionMagic, level.encoderPosition());
	}
	
	/**
	 * Gets the raw encoder output from the elevator encoder
	 */
	public int getRaw() {
		return m_elevator.getSelectedSensorPosition(0);
	}
	
	/**
	 * Returns the max robot speed, which varies when the elevator is raised
	 */ 
	public double maxDrivetrainVelocity() {
		return 1-(getRaw()/52000);
	}

	/**
	 * Returns the max robot turning speed, which varies when the elevator is raised
	 */ 
	public double maxDrivetrainTurnSpeed() {
		return 0.75-(getRaw()/104000);
	}
	
	/**
	 * Moves the targeted elevator position by adjusting a joystick
	 */
	public void moveGranulary(double joystick){
		if(m_checkElevatorPosition) {
			m_checkElevatorPosition = false;
			m_targetPosition = m_elevator.getSelectedSensorPosition(0);
		}
	     m_targetPosition = (int) (m_targetPosition + (40 * joystick));
	     if(m_targetPosition > LiftLevels.SCALEHIGH.encoderPosition()) {
	    	m_targetPosition = LiftLevels.SCALEHIGH.encoderPosition();
	     }
	     if(m_targetPosition < LiftLevels.GROUND.encoderPosition()) {
	    	 m_targetPosition = LiftLevels.GROUND.encoderPosition();
	     }
	     m_elevator.set(ControlMode.MotionMagic, m_targetPosition);
	}

	/**
	 * Sets the speed of the intake (-1 to 1)
	 */
	public void intakeSpeed(double power){
		m_intakeMaster.set(ControlMode.PercentOutput, power);
	}

	/**
	 * Disables the elevator
	 */
	public void disable() {
		m_elevator.set(ControlMode.PercentOutput, 0);
	}

	/**
	 * Preset heights for the elevator, which also store the encoder position they correspond to
	 */
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

	/**
	 * Constructor for class synchronization
	 */
	public static synchronized Elevator getInstance(){
        if (m_elevatorSystem == null)
			m_elevatorSystem = new Elevator();

        return m_elevatorSystem;     
    }
}