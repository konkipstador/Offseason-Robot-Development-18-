package frc.robot;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class AutoPaths{

    public static Waypoint[] centerToRightSwitchWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-30,30,Pathfinder.d2r(90))
    };

    public static Waypoint[] rightSwitchToCenterWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-52,130,Pathfinder.d2r(90))
    };

    public static Waypoint[] centerToCubesWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(0,91,Pathfinder.d2r(90))
    };

    public static AdvWaypoint centerToRightSwitch = new AdvWaypoint(centerToRightSwitchWaypoints, false, 2,0,0,false);
    public static AdvWaypoint rightSwitchToCenter = new AdvWaypoint(rightSwitchToCenterWaypoints, true, 0,1,1,true);
    public static AdvWaypoint centerToCube = new AdvWaypoint(centerToCubesWaypoints, false, 0,-1,0.5,true);

    /**
     * All information needed for the Robot.java control loop to navigate the bot succesfully
     * This wrapper class will differ per year due to the abilities of the robot. Some things will stay the same, however:
     * 
     * @ m_targetWaypoints  Which waypoints to be used to generate a path
     * 
     * @ m_isPathInverted   Tells the control loop to invert the wheels and encoders. 
     * This is to circumvent the fact Pathfinder cannot create a backwards trajectory
     * 
     * @ m_delay            Tells the control loop how long to wait after all other pre-path commands have been finished.
     * 
     * What will change from year to year:
     * 
     * @ m_liftPositionAtStart  What level the lift should raise to upon starting the path
     * 
     * @ m_intakeSpeed          What speed the intake should be moving at during the maneuver (-1 to 1)
     * 
     * @ shouldWaitFirst        Tells the control loop if the robot should wait before lifting the elevator
     */
    public static class AdvWaypoint{

        Waypoint m_targetWaypoints[] = null;
        Boolean m_isPathInverted = null;
        int m_liftPositionAtStart = 0;
        double m_intakeSpeed = 0;
        double m_delay = 0;
        boolean m_shouldLiftRaiseBeforeDelay = false;

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

        public int liftPosition(){
            return m_liftPositionAtStart;
        }

        public double intakeSpeed(){
            return m_intakeSpeed;
        }

        public double waitTime(){
            return m_delay;
        }

        public boolean shouldLiftRaiseBeforeDelay(){
            return m_shouldLiftRaiseBeforeDelay;
        }
    }

    public double inchesToMeters(double inches){
        return inches * 0.0254;
    }
}