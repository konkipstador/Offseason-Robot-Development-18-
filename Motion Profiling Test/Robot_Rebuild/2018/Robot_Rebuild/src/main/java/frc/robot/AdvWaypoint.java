package frc.robot;

import jaci.pathfinder.Waypoint;

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

    private Waypoint m_targetWaypoints[] = null;
    private Boolean m_isPathInverted = null;
    private int m_liftPositionAtStart = 0;
    private double m_intakeSpeed = 0;
    private double m_delay = 0;
    private boolean m_shouldLiftRaiseBeforeDelay = false;

    /**
     * Class constructor.
     * @param waypoints[]   Pathfinder waypoints used for path generation
     * @param isInverted    Tells the control loop if the robot should drive forwards or backwards during a maneuver
     * @param liftPosition  Tells the control loop the position the elevator should move to
     * @param intakeSpeed   The speed the intake should operate at during the maneuver (-1 to 1)
     * @param delay         The amount of time the robot should delay before moving in seconds
     * @param shouldLiftRaiseBeforeDelay     Tells the control loop if the elevator should move before or after the delay 
     */
    public AdvWaypoint(Waypoint waypoints[], Boolean isInverted, int liftPosition, double intakeSpeed, double delay, Boolean shouldLiftRaiseBeforeDelay){
        m_targetWaypoints = waypoints;
        m_isPathInverted = isInverted;
        m_liftPositionAtStart = liftPosition;
        m_intakeSpeed = intakeSpeed;
        m_delay = delay;
        m_shouldLiftRaiseBeforeDelay = shouldLiftRaiseBeforeDelay;
    }

    public Waypoint[] getWaypointArray(){
        return m_targetWaypoints;
    }

    public boolean isInverted(){
        return m_isPathInverted;
    }

    public int getLiftPosition(){
        return m_liftPositionAtStart;
    }

    public double intakeSpeed(){
        return m_intakeSpeed;
    }

    public double getDelay(){
        return m_delay;
    }
    
    public boolean getShouldLiftRaiseBeforeDelay(){
        return m_shouldLiftRaiseBeforeDelay;
    }  
}