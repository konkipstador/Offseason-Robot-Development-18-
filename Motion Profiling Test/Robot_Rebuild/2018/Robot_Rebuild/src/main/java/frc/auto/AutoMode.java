package frc.auto;

import java.util.ArrayList;
import frc.pathing.AdvWaypoint;

public class AutoMode{

    private static AutoMode m_autoMode; // Static synchronized object
    ArrayList<AdvWaypoint> m_path = new ArrayList<AdvWaypoint>();

    public ArrayList<AdvWaypoint> getPath(){
        return m_path;
    };

    // Creates the automode object if it is not already created, then returns it
    public static synchronized AutoMode getInstance(){
        if (m_autoMode == null)
            m_autoMode = new AutoMode();

        return m_autoMode;     
    }
}