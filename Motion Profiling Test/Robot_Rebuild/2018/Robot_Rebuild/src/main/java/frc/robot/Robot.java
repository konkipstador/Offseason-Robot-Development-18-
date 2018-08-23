/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SerialPort.Port;
import frc.robot.AutoPaths.AdvWaypoint;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;


public class Robot extends TimedRobot {

    RobotDrive m_drivetrain = RobotDrive.getInstance();
    Joystick m_dualshock = new Joystick(0);

    ArrayList<AdvWaypoint> m_paths;
    Trajectory.Config m_trajectoryConfig;
    ArrayList<EncoderFollower> m_leftFollower = null;
    ArrayList<EncoderFollower> m_rightFollower = null;   
    int m_pathNumber = 0;
    double m_timeInManeuver;
    double m_totalTime = 0;

    //SerialPort m_serialPort = new SerialPort(57600, Port.kUSB1);

    public void robotInit() {
        m_drivetrain.setTalonIDs(0,1,2,3,4,5);  // 0-2 are left motors, 3-5 are right motors
        m_drivetrain.setConversionValue((3.94*Math.PI)/512);  // Wheel circumfrence over encoder ticks; ~0.0242 inches per tick. 
        m_drivetrain.invertRight(true);
        m_drivetrain.setEncPhase(true, true);
        m_drivetrain.resetEncoders();
        
        // Pathing method                    // Number of samples          // Time per loop, velocity, Accel, Jerk
        m_trajectoryConfig = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_FAST, 0.02, 100, 200, 2000);

        // Generates the Switch auto path
        m_paths = new ArrayList<AdvWaypoint>();
        m_paths.add(AutoPaths.centerToRightSwitch);
        m_paths.add(AutoPaths.rightSwitchToCenter);
        m_paths.add(AutoPaths.centerToCube);

        m_leftFollower = new ArrayList<EncoderFollower>();
        m_rightFollower = new ArrayList<EncoderFollower>();
        for (int i=0; i<m_paths.size(); i++) 
        { 
            m_leftFollower.add(new EncoderFollower((new TankModifier(Pathfinder.generate(m_paths.get(i).getWaypointArray(),m_trajectoryConfig)).modify(24.5)).getLeftTrajectory()));
            m_leftFollower.get(i).configureEncoder(0,512,3.94);
            m_leftFollower.get(i).configurePIDVA(0.1,0,0.01, 1/50,0);

            m_rightFollower.add(new EncoderFollower((new TankModifier(Pathfinder.generate(m_paths.get(i).getWaypointArray(),m_trajectoryConfig)).modify(24.5)).getRightTrajectory()));
            m_rightFollower.get(i).configureEncoder(0,512,3.94);
            m_rightFollower.get(i).configurePIDVA(0.1,0,0.01, 1/50,0);
        }
    }

    public void autonomousInit() {
        m_pathNumber = 0;
        m_timeInManeuver = 0;
        m_totalTime = 0;
        m_liftCommandSent = false;
    }

    public void disabledInit(){
        for(int i=0; i<m_leftFollower.size(); i++){
            m_leftFollower.get(i).reset();
            m_rightFollower.get(i).reset();
        }        
        m_drivetrain.centerGyro();
        m_drivetrain.resetEncoders();
    }

    double leftOutput = 0;
    double rightOutput = 0;
    boolean m_liftCommandSent = false;

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
    double finishedTime = 0;
    public void autonomousPeriodic() {

        // Checks if the time spent delaying is equal to the delay value
        if(m_timeInManeuver >= m_paths.get(m_pathNumber).m_delay && !m_leftFollower.get(m_pathNumber).isFinished()){
            
            // Flips the encoder input and final output if the path is set to be inverted
            if(m_paths.get(m_pathNumber).isInverted()){
                leftOutput = -m_leftFollower.get(m_pathNumber).calculate(-m_drivetrain.getLeftEnc());
                rightOutput = -m_rightFollower.get(m_pathNumber).calculate(-m_drivetrain.getRightEnc());       
            }else{
                leftOutput = m_leftFollower.get(m_pathNumber).calculate(m_drivetrain.getLeftEnc());
                rightOutput = m_rightFollower.get(m_pathNumber).calculate(m_drivetrain.getRightEnc());
            }

            double gyroHeading = -m_drivetrain.getHeading();
            System.out.println(Pathfinder.r2d(m_leftFollower.get(m_pathNumber).getHeading()));
            double desiredHeading = Pathfinder.r2d(m_leftFollower.get(m_pathNumber).getHeading());
            double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
            double turn = 0.8 * (-1/50) * angleDifference;
            


            m_drivetrain.drive(leftOutput - turn, rightOutput + turn);
            System.out.println(m_drivetrain.getHeading());

            if(!m_liftCommandSent){
                m_liftCommandSent = true;
                // TODO Program lift code
            }
            finishedTime = 0;
        }else{
            

                finishedTime = 0;
                m_drivetrain.stop();
                m_drivetrain.resetEncoders();
                if(m_paths.get(m_pathNumber).shouldLiftRaiseBeforeDelay() && !m_liftCommandSent){
                    m_liftCommandSent = true;
                    // TODO Program lift code
                
            }    
        }

        // Checks if current path is complete
        if(m_leftFollower.get(m_pathNumber).isFinished()){
            m_drivetrain.stop();
            m_drivetrain.resetEncoders();
            m_timeInManeuver = 0;
            if(m_pathNumber != m_paths.size()-1){
                m_pathNumber++;
            }else{

            }        
        }

        // Increases the loop counter
        m_timeInManeuver += 0.02;
        m_totalTime += 0.02;
    }

    public void teleopPeriodic() {
        m_drivetrain.drive(-m_dualshock.getRawAxis(1),-m_dualshock.getRawAxis(5));    // Tank drive
    }

    public void robotPeriodic() {


    }

    public void loadNextManeuver(){
        
    }

}
