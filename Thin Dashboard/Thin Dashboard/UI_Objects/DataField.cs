using NetworkTables;
using NetworkTables.Tables;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Thin_Dashboard.UI_Objects
{
    /**
     * This is a wrapper class for a Label control. This control outputs data
     * to a Windows Form collected from NetworkTables. NetworkTables is a data
     * transfer system used on First Robotics Competition robots to enable
     * fluid data transfer between multiple clients, while simultaneously 
     * allowing all connected clients to read all network data.
    */
    class DataField
    {
        // Base objects and controls
        private Label m_output;             // Label used to display data from NetworkTables
        private String m_key;               // NetworkTables key
        private NetworkTable m_robot_table; // The specific table data is taken from
        private String m_input;             // String to hold data recieved from NetworkTables

        // Used to drag the object around the Form
        private Control m_control_to_drag;          // Drug control
        private Point m_previous_mouse_location;    // Mouse pointer location
        private Cursor m_cursor;                    // Mouse pointer object

        // Context Menu and respective menu items
        private ContextMenu m_context_menu = new ContextMenu();     // Menu itself
        private MenuItem m_set_key = new MenuItem("Change Key");    // Menu item to change NetworkTables key
        private MenuItem m_delete_text = new MenuItem("Delete");    // Menu item to remove data field from GUI

        int m_size = 1;

        private Prompt m_user_prompt = new Prompt();    // Prompt used to set NetworkTables key

        private Boolean m_alive = true; // Boolean variable holding if the object is deleted or not

        /**
         * Constructor for the DataField object, which displays a value from NetworkTables
         * 
         * @param x         X position of the control
         * @param y         Y position of the control
         * @Param table     NetworkTable where the data will be pulled from
        */ 
        public DataField(int x, int y, NetworkTable table)
        {
            m_output = new Label();
            m_output.Location = new System.Drawing.Point(x,y);
            m_output.Show();
            m_output.Font = new Font("HP Simplified", 20);
            m_output.AutoSize = true;
            m_output.BackColor = Color.Transparent;

            m_key = m_user_prompt.ShowDialog("Set Key", "Thin Dashboard");

            m_robot_table = table;

            m_output.MouseDown += label_mouseDown;   
            m_output.MouseMove += label_mouseMove;
            m_output.MouseUp += label_display_mouseUp;

            m_context_menu.MenuItems.Add(m_set_key);
            m_context_menu.MenuItems.Add(m_delete_text);

            m_set_key.Click += change_text;
            m_delete_text.Click += delete_label;
        }

        // Constructor used for loading.
        // @Param key   NetworkTables key
        public DataField(int x, int y, NetworkTable table, String key)
        {
            m_output = new Label();
            m_output.Location = new System.Drawing.Point(x, y);
            m_output.Show();
            m_output.Font = new Font("HP Simplified", 20);
            m_output.AutoSize = true;
            m_output.BackColor = Color.Transparent;

            m_key = key;

            m_robot_table = table;

            m_output.MouseDown += label_mouseDown;
            m_output.MouseMove += label_mouseMove;
            m_output.MouseUp += label_display_mouseUp;

            m_context_menu.MenuItems.Add(m_set_key);
            m_context_menu.MenuItems.Add(m_delete_text);

            m_set_key.Click += change_text;
            m_delete_text.Click += delete_label;
        }

        // Changes the key which NetworkTables collects data from
        private void change_text(object sender, EventArgs e)
        {
            m_key = m_user_prompt.ShowDialog("Enter label text:", "ThinDashboard");
        }

        // Event triggered to delete the DataField object
        private void delete_label(object sender, EventArgs e)
        {
            m_output.Hide();
            m_output = null;
            m_alive = false;
        }

        // Updates the text of the label
        public void update()
        {
            try
            {
                m_input = m_robot_table.GetString(m_key);
            }
            catch
            {
                m_input = "n/c";
            }
            m_output.Text = m_key + ": " + m_input;
        }

        // Returns the main control, in this case a label
        public Label getReadout()
        {
            return m_output;
        }

        // Moves the drug object
        private void label_mouseMove(object sender, MouseEventArgs e)
        {
            if (m_control_to_drag == null || m_control_to_drag != sender)
                return;

            var location = m_control_to_drag.Location;
            location.Offset(e.Location.X - m_previous_mouse_location.X, e.Location.Y - m_previous_mouse_location.Y);
            m_control_to_drag.Location = location;
        }

        // Releases the drug object
        private void label_display_mouseUp(object sender, MouseEventArgs e)
        {
            m_control_to_drag = null;
            m_cursor = Cursors.Default;
        }

        // Displays the context menu and enables object dragging
        private void label_mouseDown(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Right:
                    m_context_menu.Show(m_output, new Point(e.X, e.Y));
                    break;
                case MouseButtons.Left:
                    m_control_to_drag = sender as Control;
                    m_previous_mouse_location = e.Location;
                    m_cursor = Cursors.Hand;
                    break;
            }
        }
        
        public string getInfo()
        {
            return m_key;
        }

        public int getX()
        {
            return m_output.Location.X;
        }

        public int getY()
        {
            return m_output.Location.Y;
        }

        public int getSize()
        {
            return m_size;
        }

        public Boolean getAlive()
        {
            return m_alive;
        }
    }
}
