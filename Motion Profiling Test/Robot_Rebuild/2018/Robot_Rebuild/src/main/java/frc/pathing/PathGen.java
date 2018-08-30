package frc.pathing;

import java.util.ArrayList;
import java.util.Collections;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

/**
 * PathGen is a class structured to generate EncoderFollowers for the main Autonomus
 * control loop. These, in conjunction with its respective Advanced Waypoint, contain
 * all the information needed to correctly drive a robot autonomusly.
 * 
 * @author Caleb Shilling (FRC Team 957)
 */
public class PathGen{

    private ArrayList<EncoderFollower> m_leftFollower = new ArrayList<EncoderFollower>();
    private ArrayList<EncoderFollower> m_rightFollower = new ArrayList<EncoderFollower>(); 
    private double m_wheelbase = 24.5;
    private double m_wheelDiameter = 3.94;
    private int m_ticksPerRevolution = 1024;
    private double m_kp = 0.1;
    private double m_kd = 0.01;

    /**
     * Class constructor.
     * 
     * @param wheelbase             Distance between the wheels of the robot
     * @param wheelDiameter         Diameter of the robot's wheels
     * @param ticksPerRevolution    The number of ticks it takes the encoder to equal a full revolution 
     */
    public PathGen(double wheelbase, double wheelDiameter, int ticksPerRevolution){
        m_wheelbase = wheelbase;
        m_wheelDiameter = wheelDiameter;
        m_ticksPerRevolution = ticksPerRevolution;
    }

    /**
     * Sets the P and D values of the control loop
     * 
     * @param kp    Proportional gain value
     * @param kd    Differental gain value
     */
    public void setPD(double kp, double kd){
        m_kp = kp;
        m_kd = kd;
    }

    /**
     * Generates Pathfinder's Encoder Followers, which contain path following data
     * 
     * @param paths             An arraylist which used to hold path waypoints
     * @param trajectoryConfig  An object which sets the max velocity, acceleration, and jerk of a maneuver
     */
    public void generateFollowers(ArrayList<AdvWaypoint> paths, Trajectory.Config trajectoryConfig){
        ArrayList<Trajectory.Config> trajectoryConfigs = new ArrayList<Trajectory.Config>();
        trajectoryConfigs.addAll(Collections.nCopies(paths.size(), trajectoryConfig));
        generateFollowers(paths, trajectoryConfigs);
    }

    /**
     * Generates Pathfinder's Encoder Followers, which contain path following data
     * 
     * @param paths             An arraylist which used to hold path waypoints
     * @param trajectoryConfigs An arraylist containing a trajectory configuration for each path in paths, allowing for each maneuver to have different max values
     * @throws NullPointerException This exception is thrown when the size of paths and trajectoryConfigs don't match
     */
    private void generateFollowers(ArrayList<AdvWaypoint> paths, ArrayList<Trajectory.Config> trajectoryConfigs){

        m_leftFollower = new ArrayList<EncoderFollower>();
        m_rightFollower = new ArrayList<EncoderFollower>();

        if(paths.size() != trajectoryConfigs.size()){
            throw new NullPointerException();
        }

        for (int i=0; i<paths.size(); i++) 
        { 
            m_leftFollower.add(new EncoderFollower((new TankModifier(Pathfinder.generate(paths.get(i).getWaypointArray(),trajectoryConfigs.get(i))).modify(m_wheelbase)).getLeftTrajectory()));
            m_leftFollower.get(i).configureEncoder(0,m_ticksPerRevolution,m_wheelDiameter);
            m_leftFollower.get(i).configurePIDVA(m_kp,0,m_kd, 1/50,0);

            m_rightFollower.add(new EncoderFollower((new TankModifier(Pathfinder.generate(paths.get(i).getWaypointArray(),trajectoryConfigs.get(i))).modify(m_wheelbase)).getRightTrajectory()));
            m_rightFollower.get(i).configureEncoder(0,m_ticksPerRevolution,m_wheelDiameter);
            m_rightFollower.get(i).configurePIDVA(m_kp,0,m_kd, 1/50,0);
        }
    }

    public ArrayList<EncoderFollower> getRightFollowers(){
        return m_rightFollower;
    }

    public ArrayList<EncoderFollower> getLeftFollowers(){
        return m_leftFollower;
    }
}


