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
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import frc.pathing.PathGen;
import frc.pathing.AdvWaypoint;
import frc.robot.Elevator;
import frc.auto.*;

public class Robot extends TimedRobot {

    RobotDrive m_drivetrain = RobotDrive.getInstance();
    Elevator m_elevator = Elevator.getInstance();
    Joystick m_dualshock = new Joystick(0);

    ArrayList<AdvWaypoint> m_paths;
    Trajectory.Config m_trajectoryConfig;
    ArrayList<EncoderFollower> m_leftFollowers = null;
    ArrayList<EncoderFollower> m_rightFollowers = null;   
    int m_pathNumber = 0;
    double m_timeInManeuver;
    double m_finishedTime = 0;
    double m_leftOutput = 0;
    double m_rightOutput = 0;
    double m_totalTime = 0;
    boolean m_liftCommandSent = false;
    PathGen m_pathGen[] = {new PathGen(24.5, 3.94, 512), new PathGen(24.5, 3.94, 512)};

    public void robotInit() {
        m_drivetrain.setTalonIDs(0,1,2,3,4,5);  // 0-2 are left motors, 3-5 are right motors
        m_drivetrain.setConversionValue((3.94*Math.PI)/512);  // Wheel circumfrence over encoder ticks; ~0.0242 inches per tick. 
        m_drivetrain.invertRight(true);
        m_drivetrain.setEncPhase(true, true);
        m_drivetrain.resetEncoders();
        
        m_trajectoryConfig = makeConfig(100,100,2000);

        // Generates the Switch auto path
        m_paths = SwitchRight.path();

        m_pathGen[0].setPD(0.1, 0.01);
        m_pathGen[0].generateFollowers(m_paths, m_trajectoryConfig);
        
        m_leftFollowers = m_pathGen[0].getLeftFollowers();
        m_rightFollowers = m_pathGen[0].getRightFollowers();
        
    }

    public void autonomousInit() {
        m_pathNumber = 0;
        m_timeInManeuver = 0;
        m_totalTime = 0;
        m_liftCommandSent = false;
    }

    public void disabledInit(){
        for(int i=0; i<m_leftFollowers.size(); i++){
            m_leftFollowers.get(i).reset();
            m_rightFollowers.get(i).reset();
        }        
        m_drivetrain.centerGyro();
        m_drivetrain.resetEncoders();
    }

    /**
     * This is the control loop for Pathfinder. The control loop functions as such:
     * 
     * 1.   The loop checks if the delay value has passed. If it hasn't, it will raise the elevator if it is
     *      programmed to be raised immediatly. The command to raise it will be sent once.
     * 
     * 2.   When the delay is up, it will update the left/right EncoderFollower object for the current maneuver.
     *      
     * 3.   The loop will then attempt to calculate how far the robot has strayed from it's path. It will then
     *      initalize a variable with an offset value to keep the robot on the right track.
     * 
     * 4.   The robot will then set its velocity to the EncoderFollower's output plus or minus the turn
     *      value. Steps 2-4 will repeat until the maneuver is complete
     * 
     * 5.   Once the maneuver is complete, it will stop the robot, reset the encoders, reset m_timeInManeuver,
     *      and load the next maneuver if it exists.
     * 
     * The loop can also check when to move the elevator
     */
    public void autonomousPeriodic() {

        // Checks if the time spent delaying is equal to the delay value
        if(m_timeInManeuver >= m_paths.get(m_pathNumber).getDelay() && !m_leftFollowers.get(m_pathNumber).isFinished()){
            
            // Flips the encoder input and final output if the path is set to be inverted
            if(m_paths.get(m_pathNumber).isInverted()){
                m_leftOutput = -m_leftFollowers.get(m_pathNumber).calculate(-m_drivetrain.getLeftEnc());
                m_rightOutput = -m_rightFollowers.get(m_pathNumber).calculate(-m_drivetrain.getRightEnc());       
            }else{
                m_leftOutput = m_leftFollowers.get(m_pathNumber).calculate(m_drivetrain.getLeftEnc());
                m_rightOutput = m_rightFollowers.get(m_pathNumber).calculate(m_drivetrain.getRightEnc());
            }

            double gyroHeading = -m_drivetrain.getHeading();
            System.out.println(Pathfinder.r2d(m_leftFollowers.get(m_pathNumber).getHeading()));
            double desiredHeading = Pathfinder.r2d(m_leftFollowers.get(m_pathNumber).getHeading());
            double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
            double turn = 0.8 * (-1/50) * angleDifference;

            m_drivetrain.drive(m_leftOutput - turn, m_rightOutput + turn);
            //System.out.println(m_drivetrain.getHeading());

            if(!m_liftCommandSent){
                m_liftCommandSent = true;
                m_elevator.setLevel(m_paths.get(m_pathNumber).getLiftPosition());
            }
            m_finishedTime = 0;
        }else{
                m_finishedTime = 0;
                m_drivetrain.stop();
                m_drivetrain.resetEncoders();
                if(m_paths.get(m_pathNumber).shouldElevatorRaiseBeforeDelay() && !m_liftCommandSent){
                    m_liftCommandSent = true;
                    m_elevator.setLevel(m_paths.get(m_pathNumber).getLiftPosition());             
            }    
        }

        // Checks if current path is complete
        if(m_leftFollowers.get(m_pathNumber).isFinished()){
            m_drivetrain.stop();
            m_drivetrain.resetEncoders();
            m_timeInManeuver = 0;
            if(m_pathNumber != m_paths.size()-1){
                m_pathNumber++;
            }       
        }

        // Sets the intake speed
        m_elevator.intakeSpeed(m_paths.get(m_pathNumber).intakeSpeed());

        // Increases the loop counter
        m_timeInManeuver += 0.02;
        m_totalTime += 0.02;
    }

    public void teleopPeriodic() {
        m_drivetrain.drive(-m_dualshock.getRawAxis(1),-m_dualshock.getRawAxis(5));    // Tank drive
    }

    public void disabledPeriodic(){
        m_elevator.disable();
    }

    /**
     * Generates a Trajectory.Config object based off of fewer values. This makes it easier to make trajectory config lists.
     * @param maxSpeed  Max speed of the robot during the maneuver
     * @param maxAccel  Max acceleration of the robot during the maneuver
     * @param maxJerk   Max jerk (change in acceleration per second) of the robot during the maneuver
     */
    public static Trajectory.Config makeConfig(double maxSpeed, double maxAccel, double maxJerk){
        return new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_FAST, 0.02, maxSpeed, maxAccel, maxJerk);
    }
}