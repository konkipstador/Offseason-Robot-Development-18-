package frc.team957.robot;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class ThinDashboard{

    // Creates the Network Table for this dashboard
    NetworkTable m_table = NetworkTableInstance.getDefault().getTable("Thin_Dashboard");

    public class RadioButtons{

        // ArrayList to hold and compile data before sending
        private ArrayList<String> m_data_to_send = null;

        // Which Network Table key we are targeting
        private NetworkTableEntry m_entry = null;

        // Raw value of Network Table key
        private String m_key = null;

        // Variable to hold the data returned by NetworkTables
        private String[] m_returned_data;

        /*
        @param key              The Network Table Entry key
        @param values           A string array holding all text for the RadioButtons
        @param defaultSelected  The radio button number which will be selected by default
        */
        public RadioButtons(String key, String[] values, int defaultSelected){

            // Sets the Network Table entry's key
            m_entry = m_table.getEntry(key);
            m_key = key;

            // Defines the ArrayList based on the values string array
            m_data_to_send = new ArrayList<String>(Arrays.asList(values));

            // Adds the data which determines which button will be selected by default
            m_data_to_send.add(Integer.toString(defaultSelected));

            try{
                // Sends the data out via NetworkTables
                m_entry.setStringArray(m_data_to_send.toArray(new String[m_data_to_send.size()]));  
            }catch(Exception e){}       
        }

        /*
        @return     The radio button selected. This value is zero indexed.
        */
        public int grabSelected(){
            // Attempts to retrieve NetworkTables data and place it into the m_returned_data String array.
            // Else, it creates an array based on the values in m_data_to_send, defined in the constructor.
            try{
                m_returned_data = m_entry.getStringArray(m_data_to_send.toArray(new String[m_data_to_send.size()]));
            }catch(Exception e){
                m_returned_data = (String[])m_data_to_send.toArray();
            }
           
            // Returns the last value in the String array, which will be the selected radio button on the dashboard
            return Integer.valueOf(m_returned_data[m_returned_data.length-1]);
        }

        /*
        @return     The Network Table Entry key for this RadioButtons object
        */
        public String getKey(){
            return m_key;
        }
    }

    public class DataReadout{

        // String to hold and compile data before sending
        private String m_data_to_send = null;

        // Which Network Table key we are targeting
        private NetworkTableEntry m_entry = null;

        // Raw value of Network Table key
        private String m_key = null;

        /*
        @param key              The Network Table Entry key
        @param values           A string array holding all text for the RadioButtons
        @param defaultSelected  The radio button number which will be selected by default
        */
        public DataReadout(String key, Object value){

            // Sets the Network Table entry's key
            m_entry = m_table.getEntry(key);
            m_key = key;

            // Defines m_data_to_send based on the Object value, and converts value to a string
            m_data_to_send = value.toString();

            try{
                // Sends the data out via NetworkTables
                m_entry.setString(m_data_to_send);
            }catch(Exception e){}
        }

        /*
        @param value    The object which will be sent to the dashboard
        */
        public void update(Object value){
            
            // Converts the Object value to a String
            m_data_to_send = value.toString();

            try{
                // Sends the data out via NetworkTables
                m_entry.setString(m_data_to_send);
            }catch(Exception e){}
            
        }

        /*
        @return     The Network Table Entry key for this RadioButtons object
        */
        public String getKey(){
            return m_key;
        }
    }
}