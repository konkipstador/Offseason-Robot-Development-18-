package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Elevator {

	private static Elevator m_elevatorSystem;	// Synchronized Elevator object

	TalonSRX elevator = new TalonSRX(6);	// Elevator Talon
	int targetPosition = 0;
	int currentPos = 0;	
	boolean getCurrentPosition = true;

	TalonSRX m_armMaster = new TalonSRX(7),	// Left arm Talon
			m_armSlave = new TalonSRX(8);	// Right arm Talon
		
	public Elevator() {

		elevator.configPeakCurrentLimit(29, 20);
		elevator.configPeakCurrentDuration(10, 20);
		elevator.configContinuousCurrentLimit(25, 20);
		elevator.enableCurrentLimit(true);
		elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 20);
		elevator.setSensorPhase(true);
		elevator.config_kF(0,.2177, 20);
		elevator.config_kP(0, 1.5493, 20);
		elevator.config_kI(0, 0.016, 20);
		elevator.config_kD(0,15.493, 20);
		elevator.config_IntegralZone(0, 50, 20);
		elevator.configMotionCruiseVelocity(2000,20);
		elevator.configMotionAcceleration(2000, 20);
		elevator.setSelectedSensorPosition(0, 0, 20);
	
		m_armMaster.configPeakCurrentLimit(10, 20);
		m_armMaster.configPeakCurrentDuration(10, 20);
		m_armMaster.configContinuousCurrentLimit(7, 20);
		m_armMaster.enableCurrentLimit(true);
		m_armSlave.configPeakCurrentLimit(10, 20);
		m_armSlave.configPeakCurrentDuration(10, 20);
		m_armSlave.configContinuousCurrentLimit(7, 20);
		m_armSlave.enableCurrentLimit(true);

		m_armSlave.setInverted(true);
		m_armSlave.set(ControlMode.Follower, 7);
	}
	
	public void setLevel(LiftLevels level) {	

		getCurrentPosition = true;
		targetPosition = level.encoderPosition();
		elevator.set(ControlMode.MotionMagic, targetPosition);
	}

	public int getRaw() {
		return elevator.getSelectedSensorPosition(0);
	}

	public double percent() {
		currentPos = getRaw();

		if(currentPos <= 1300 && currentPos <= 10500) {
			return 1;
		}

		if(currentPos > 10500 && currentPos <= 20500) {
			return 0.65;
		}

		if(currentPos > 20500 && currentPos <= 26000) {
			return 0.5;
		}

		return 0.5;
	}

	public double returnTurn() {
		currentPos = getRaw();
		
		if(currentPos <= 1300 && currentPos <= 10500) {
			return 0.75;
		}

		if(currentPos > 10500 && currentPos <= 20500) {
			return 0.6;
		}

		if(currentPos > 20500 && currentPos <= 26000) {
			return 0.5;
		}

		return 0.5;
	}
	
	int tickValue = 40;
	public void moveGranulary(double joystick){
		if(getCurrentPosition) {
			getCurrentPosition = false;
			targetPosition = elevator.getSelectedSensorPosition(0);
		}
	     targetPosition = (int) (targetPosition + (tickValue * joystick));
	     if(targetPosition > LiftLevels.SCALEHIGH.encoderPosition()) {
	    	 targetPosition = LiftLevels.SCALEHIGH.encoderPosition();
	     }
	     if(targetPosition < LiftLevels.GROUND.encoderPosition()) {
	    	 targetPosition = LiftLevels.GROUND.encoderPosition();
	     }
	     elevator.set(ControlMode.MotionMagic, targetPosition);
	}


	public void disabled() {
		elevator.set(ControlMode.PercentOutput, 0);
	}

	public void grab() {
		m_armMaster.set(ControlMode.PercentOutput, -1);
		m_armSlave.set(ControlMode.PercentOutput, -1);
	}
	
	public void eject() {
		m_armMaster.set(ControlMode.PercentOutput, 1);
		m_armSlave.set(ControlMode.PercentOutput, 1);
	}
	
	public void stopIntake() {
		m_armMaster.set(ControlMode.PercentOutput, 0);
		m_armSlave.set(ControlMode.PercentOutput, 0);
	}
	
	public void directControl(double speed) {
		m_armSlave.set(ControlMode.PercentOutput, speed);
		m_armSlave.set(ControlMode.PercentOutput, speed);
	}

	public static synchronized Elevator getInstance(){
        if (m_elevatorSystem == null)
			m_elevatorSystem = new Elevator();

        return m_elevatorSystem;     
    }
}