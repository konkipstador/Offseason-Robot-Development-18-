/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.auto.*;
import frc.robot.Elevator.LiftLevels;
import frc.Command;

public class Robot extends TimedRobot {

    ArrayList<Command> m_autoCommands = new ArrayList<Command>();

    Joystick m_dualshock = new Joystick(0);
    RobotDrive m_drivetrain = RobotDrive.getInstance();
    Elevator m_elevator = Elevator.getInstance();

    public void robotInit() {

    }

    public void robotPeriodic() {
        
    }

    public void autonomousInit() {

    }

    public void autonomousPeriodic() {

    }

    public void teleopPeriodic() {
        // Drive code
        double xPercent = -m_dualshock.getRawAxis(1)*m_elevator.maxDrivetrainVelocity();
        double yPercent = -m_dualshock.getRawAxis(0)*m_elevator.maxDrivetrainTurnSpeed();
        m_drivetrain.drive(yPercent + xPercent, yPercent - xPercent);

        // Preset elevator heights
        if(m_dualshock.getRawButton(ControllerButtons.L1.get())) {
            m_elevator.setLevel(LiftLevels.GROUND);
        }
        if(m_dualshock.getRawButton(ControllerButtons.R1.get())) {
            m_elevator.setLevel(LiftLevels.EXCHANGE);
        }
        if(m_dualshock.getRawButton(ControllerButtons.CROSS.get())) {
            m_elevator.setLevel(LiftLevels.SWITCH);
        }
        if(m_dualshock.getRawButton(ControllerButtons.OPTIONS.get())) {
            m_elevator.setLevel(LiftLevels.PORTAL);
        }
        if(m_dualshock.getRawButton(ControllerButtons.CIRCLE.get())) {
            m_elevator.setLevel(LiftLevels.SCALELOW);
        }
        if(m_dualshock.getRawButton(ControllerButtons.SQUARE.get())) {
            m_elevator.setLevel(LiftLevels.SCALEMID);
        }
        if(m_dualshock.getRawButton(ControllerButtons.TRIANGLE.get())) {
            m_elevator.setLevel(LiftLevels.SCALEHIGH);
        }

        // Semi-granular elevator movement
        if(m_dualshock.getPOV() == 0){
            m_elevator.moveGranulary(1);
        }
        if(m_dualshock.getPOV() == 180){
            m_elevator.moveGranulary(-1);
        }

        // Granular intake control
        if(Math.abs(m_dualshock.getRawAxis(5)) > 0.1){
            m_elevator.intakeSpeed(-m_dualshock.getRawAxis(5));
        }
    }
}
