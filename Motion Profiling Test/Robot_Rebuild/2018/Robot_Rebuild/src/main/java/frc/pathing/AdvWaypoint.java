package frc.pathing;

import jaci.pathfinder.Waypoint;
import frc.robot.Elevator.LiftLevels;

/**
 * The Advanced Waypoint class wraps all data for autonomus robot control into a
 * single object: all delays and other robot functions are accounted for here. This
 * class may need to change from year to year, as each year requires different
 * robot functions. This class should have many options at it's disposal, which allows
 * for less roundabout ways of robot navigation later in Robot.java.
 * 
 * @author Caleb Shilling (FRC Team 957)
 */

public class AdvWaypoint{

    /*
        Pathfinder cannot properly generate a path that has a backwards trajectory. That means that without tweaking 
        Pathfinder's path in a control loop, moving backwards is impossible. Advanced Waypoint and its control loop 
        circumvents this by inverting encoder input and reversing motor output, thus mirroring graph quadrants 1 and 2 
        with 3 and 4, making the robot move in reverse.
    */

    private Waypoint m_targetWaypoints[] = null;
    private Boolean m_isPathInverted = null;
    private LiftLevels m_liftPositionAtStart = LiftLevels.GROUND;
    private double m_intakeSpeed = 0;
    private double m_delay = 0;
    private boolean m_shouldElevatorRaiseBeforeDelay = false;

    /**
     * Class constructor.
     * @param waypoints[]   Pathfinder waypoints used for path generation
     * @param isInverted    Tells the control loop if the robot should drive forwards or backwards during a maneuver
     * @param liftPosition  Tells the control loop the position the elevator should move to
     * @param intakeSpeed   The speed the intake should operate at during the maneuver (-1 to 1)
     * @param delay         The amount of time the robot should delay before moving in seconds
     * @param shouldLiftRaiseBeforeDelay     Tells the control loop if the elevator should move before or after the delay 
     */
    public AdvWaypoint(Waypoint waypoints[], Boolean isInverted, LiftLevels liftPosition, double intakeSpeed, double delay, Boolean shouldElevatorRaiseBeforeDelay){
        m_targetWaypoints = waypoints;
        m_isPathInverted = isInverted;
        m_liftPositionAtStart = liftPosition;
        m_intakeSpeed = intakeSpeed;
        m_delay = delay;
        m_shouldElevatorRaiseBeforeDelay = shouldElevatorRaiseBeforeDelay;
    }

    public Waypoint[] getWaypointArray(){
        return m_targetWaypoints;
    }

    public boolean isInverted(){
        return m_isPathInverted;
    }

    public LiftLevels getLiftPosition(){
        return m_liftPositionAtStart;
    }

    public double intakeSpeed(){
        return m_intakeSpeed;
    }

    public double getDelay(){
        return m_delay;
    }
    
    public boolean shouldElevatorRaiseBeforeDelay(){
        return m_shouldElevatorRaiseBeforeDelay;
    }  
}