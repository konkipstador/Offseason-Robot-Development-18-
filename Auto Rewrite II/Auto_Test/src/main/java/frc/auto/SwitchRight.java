package frc.auto;

import java.util.ArrayList;

import frc.Command;
import frc.RobotMath;

public class SwitchRight{

    static DriveToPoint driveForwardSlightly = new DriveToPoint(RobotMath.inchesToTicks(40), .5);

    static ArrayList<Command> getCommandList(){
        ArrayList<Command> commandList = new ArrayList<Command>();

        commandList.add(driveForwardSlightly);

        return commandList;
    }
}