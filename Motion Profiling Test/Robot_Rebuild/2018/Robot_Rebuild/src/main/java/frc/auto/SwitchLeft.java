package frc.auto;

import frc.pathing.AdvWaypoint;
import frc.robot.Elevator.LiftLevels;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class SwitchLeft extends AutoMode{

    public SwitchLeft(){  
        m_path.add(m_centerToLeftSwitch);
        m_path.add(m_leftSwitchToCenter);
        m_path.add(m_centerToCubes);
        m_path.add(m_cubesToBackLeft);
        m_path.add(m_backLeftToSwitch);
        m_path.add(m_leftSwitchToCenter);
    }

    /* Autonomus Logic */ 

    public static Waypoint[] m_centerToLeftSwitchWaypoints = {
        new Waypoint(5.25,0,Pathfinder.d2r(90)),
        new Waypoint(-50,60,Pathfinder.d2r(90)),
        new Waypoint(-70,95,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_leftSwitchToCenterWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(50,65,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_centerToCubesWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(20,35,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_cubesToBackLeftWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-20,35,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_backLeftToSwitchWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-50,65,Pathfinder.d2r(90))
    };

    public static AdvWaypoint m_centerToLeftSwitch = new AdvWaypoint(m_centerToLeftSwitchWaypoints, false, LiftLevels.SWITCH,0,0,false);
    public static AdvWaypoint m_leftSwitchToCenter = new AdvWaypoint(m_leftSwitchToCenterWaypoints, true, LiftLevels.GROUND,1,1,false);
    public static AdvWaypoint m_centerToCubes = new AdvWaypoint(m_centerToCubesWaypoints, false, LiftLevels.GROUND,-1,0.5,true);  
    public static AdvWaypoint m_cubesToBackLeft = new AdvWaypoint(m_cubesToBackLeftWaypoints, true, LiftLevels.EXCHANGE,-1,0.5,true);  
    public static AdvWaypoint m_backLeftToSwitch = new AdvWaypoint(m_backLeftToSwitchWaypoints, false, LiftLevels.SWITCH,-1,0.5,true);  
    // Right Switch to Center
}