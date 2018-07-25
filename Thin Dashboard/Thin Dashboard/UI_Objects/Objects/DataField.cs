using NetworkTables;
using System;
using System.Drawing;
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
    class DataField : ThinControl, ThinControlInterface
    {
        // Base objects and controls
        private String m_key;               // NetworkTables key
        private String m_input;             // String to hold data recieved from NetworkTables

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
        public DataField(int x, int y, NetworkTable table, String key)
        {
            this.Location = new System.Drawing.Point(x,y);
            this.Show();
            this.Font = new Font("HP Simplified", 20);
            this.AutoSize = true;
            this.Height = 20;

            if (key.Equals(""))
            {
                m_key = m_user_prompt.ShowDialog("Set Key", "Thin Dashboard");
            }
            else
            {
                m_key = key;
            }

            m_context_menu.MenuItems.Add(m_set_key);
            m_context_menu.MenuItems.Add(m_delete_text);
            this.MouseDown += mouseDown;
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
            this.Hide();
            m_alive = false;
            this.Controls.Remove(this);
        }

        // Updates the text of the label
        public void update(NetworkTable table, Boolean edit_mode)
        {
            this.CanEdit = edit_mode;

            try
            {
                m_input = table.GetString(m_key);
            }
            catch
            {
                m_input = "n/c";
            }
            this.Text = m_key + ": " + m_input;
        }

        // Displays the context menu and enables object dragging
        private void mouseDown(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Right:
                    m_context_menu.Show(this, new Point(e.X, e.Y));
                    break;
            }
        }
        
        public string getInfo()
        {
            return m_key;
        }

        public int getX()
        {
            return this.Location.X;
        }

        public int getY()
        {
            return this.Location.Y;
        }

        public double getSize()
        {
            return m_size;
        }

        public Boolean getAlive()
        {
            return m_alive;
        }

        public bool getUsesNetTables()
        {
            return true;
        }

        public String getType()
        {
            return "df";
        }
    }
}