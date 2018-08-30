package frc.robot;

import frc.pathing.AdvWaypoint;
import frc.robot.Elevator.LiftLevels;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class AutoPaths{

    public static Waypoint[] centerToRightSwitchWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(52,110,Pathfinder.d2r(90))
    };

    public static Waypoint[] rightSwitchToCenterWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(-52,110,Pathfinder.d2r(90))
    };

    public static Waypoint[] centerToCubesWaypoints = {
        new Waypoint(0,0,Pathfinder.d2r(90)),
        new Waypoint(0,60,Pathfinder.d2r(90))
    };

    public static AdvWaypoint centerToRightSwitch = new AdvWaypoint(centerToRightSwitchWaypoints, false, LiftLevels.SWITCH,0,0,false);
    public static AdvWaypoint rightSwitchToCenter = new AdvWaypoint(rightSwitchToCenterWaypoints, true, LiftLevels.GROUND,1,1,true);
    public static AdvWaypoint centerToCube = new AdvWaypoint(centerToCubesWaypoints, false, LiftLevels.GROUND,-1,0.5,true);  
}