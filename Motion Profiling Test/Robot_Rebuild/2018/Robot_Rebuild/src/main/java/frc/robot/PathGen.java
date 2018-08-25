package frc.robot;

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

    public PathGen(double wheelbase, double wheelDiameter, int ticksPerRevolution){
        m_wheelbase = wheelbase;
        m_wheelDiameter = wheelDiameter;
        m_ticksPerRevolution = ticksPerRevolution;
    }

    // This generates Encoder followers off of a single Trajectory Configuration, making it useful for simple paths.
    public void generateFollowers(ArrayList<AdvWaypoint> paths, Trajectory.Config trajectoryConfig){
        ArrayList<Trajectory.Config> trajectoryConfigs = new ArrayList<Trajectory.Config>();
        trajectoryConfigs.addAll(Collections.nCopies(paths.size(), trajectoryConfig));
        generateEF(paths, trajectoryConfigs);
    }

    // This generates EncoderFollowers off of a list of Trajectory Configurations, allowing for greater path complexity.
    public void generateFollowers(ArrayList<AdvWaypoint> paths, ArrayList<Trajectory.Config> trajectoryConfigs){
        generateEF(paths, trajectoryConfigs);
    }

    // Does the generation for the two classes above.
    private void generateEF(ArrayList<AdvWaypoint> paths, ArrayList<Trajectory.Config> trajectoryConfigs){
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

    public ArrayList<EncoderFollower> getRightFollower(){
        return m_rightFollower;
    }

    public ArrayList<EncoderFollower> getLeftFollower(){
        return m_leftFollower;
    }
}


