package frc.auto;

import frc.pathing.AdvWaypoint;
import frc.robot.Elevator.LiftLevels;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class SwitchRight extends AutoMode{

    public SwitchRight(){  
        m_path.add(m_centerToRightSwitch);
        m_path.add(m_rightSwitchToCenter);
        m_path.add(m_centerToCubes);
        m_path.add(m_cubesToBackRight);
        m_path.add(m_backRightToSwitch);
        m_path.add(m_rightSwitchToCenter);
    }

    /* Autonomus Logic */ 

    public static Waypoint[] m_centerToRightSwitchWaypoints = {
        new Waypoint(5.25,0,Pathfinder.d2r(90)),
        new Waypoint(50,60,Pathfinder.d2r(90)),
        new Waypoint(70,95,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_rightSwitchToCenterWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-50,65,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_centerToCubesWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-20,35,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_cubesToBackRightWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(20,35,Pathfinder.d2r(90))
    };

    public static Waypoint[] m_backRightToSwitchWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(50,65,Pathfinder.d2r(90))
    };

    public static AdvWaypoint m_centerToRightSwitch = new AdvWaypoint(m_centerToRightSwitchWaypoints, false, LiftLevels.SWITCH,0,0,false);
    public static AdvWaypoint m_rightSwitchToCenter = new AdvWaypoint(m_rightSwitchToCenterWaypoints, true, LiftLevels.GROUND,1,1,false);
    public static AdvWaypoint m_centerToCubes = new AdvWaypoint(m_centerToCubesWaypoints, false, LiftLevels.GROUND,-1,0.5,true);  
    public static AdvWaypoint m_cubesToBackRight = new AdvWaypoint(m_cubesToBackRightWaypoints, true, LiftLevels.EXCHANGE,-1,0.5,true);  
    public static AdvWaypoint m_backRightToSwitch = new AdvWaypoint(m_backRightToSwitchWaypoints, false, LiftLevels.SWITCH,-1,0.5,true);  
    // Right Switch to Center
}