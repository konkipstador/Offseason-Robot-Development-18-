using NetworkTables;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Thin_Dashboard.UI_Objects.Objects
{
    
    class Selector : ThinPanel, ThinControlInterface
    {
        String m_key = "";
        Prompt m_user_prompt = new Prompt();
        int m_length = 2;
        Label m_text = new Label();
        Boolean m_alive = true;

        List<RadioButton> m_buttons = new List<RadioButton>();

        // Declaration of our Context Menu, which appears on right click.
        private ContextMenu m_context_menu = new ContextMenu();
        private MenuItem m_change_key = new MenuItem("Change Key"); // Menu option to change Mjpeg stream URL
        private MenuItem m_delete = new MenuItem("Delete");         // Menu option to delete this CameraStream

        public Selector(int x, int y, String key)
        {
            if (key.Equals(""))
            {
                m_key = m_user_prompt.ShowDialog("Set Key", "Thin Dashboard");
            }
            else
            {
                m_key = key;
            }

            this.Location = new Point(x,y);
            this.BringToFront();
            this.AutoSize = true;
            this.Anchor = AnchorStyles.Top | AnchorStyles.Left;
            m_text.Text = m_key;
            m_text.Font = new Font("HP Simplified", 18,FontStyle.Bold);
            m_text.Location = new Point(-5, 0);
            m_text.AutoSize = true;
            this.Controls.Add(m_text);

            m_text.MouseDown += cascadeMouseDown;
            m_text.MouseUp += cascadeMouseUp;
            m_text.MouseMove += cascadeMouseMove;

            m_context_menu.MenuItems.Add(m_change_key);
            m_context_menu.MenuItems.Add(m_delete);

            this.MouseDown += loadContextMenu;    // Brings up the ContextMenu
            m_change_key.Click += urlClick;   // Prompts the user to enter the Mjpeg stream URL
            m_delete.Click += deleteClick;   // Removes the stream from the UI and raises ControlRemoved
        }

        /**
         * These functions load the context menu for this object, AKA the
         * right click menu. It allows the user to change the NT key of
         * this object as well as delete it.
         */
        private void loadContextMenu(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Right:
                    m_context_menu.Show(this, PointToClient(new Point(MousePosition.X, MousePosition.Y)));
                    break;
            }

        }
        private void urlClick(object sender, EventArgs e)
        {
            m_key = m_user_prompt.ShowDialog("Set Key", "Thin Dashboard");
        }
        private void deleteClick(object sender, EventArgs e)
        {
            m_text.Hide();
            m_alive = false;
            m_text.Dispose();

            foreach (RadioButton element in m_buttons)
            {
                element.Dispose();
                m_buttons = null;
            }
        }

        /**
         * The only reason these exist is to allow mouse events triggered on
         * child objects of the Panel to affect the panel itself. When these are called,
         * they effectivly serve as a shortcut to the various functions in ThinPanel.
         */
        private void cascadeMouseDown(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Left:
                    this.mouseDown(this,e);
                    break;

                case MouseButtons.Right:
                    this.loadContextMenu(this, e);
                    break;
            }
        }
        private void cascadeMouseUp(object sender, MouseEventArgs e)
        {
            this.mouseUp(this,e);
        }
        private void cascadeMouseMove(object sender, MouseEventArgs e)
        {
            this.mouseMove(this, e);
        }   

        public bool getAlive()
        {
            return m_alive;
        }

        public string getInfo()
        {
            return m_key;
        }

        public double getSize()
        {
            return 1;
        }

        public string getType()
        {
            return "s";
        }

        public int getX()
        {
            return this.Location.X;
        }

        public int getY()
        {
            return this.Location.Y;
        }

        public void update(NetworkTable table, Boolean edit_mode)
        {

            this.CanEdit = edit_mode;

            try
            {
                // Gets NetTables data
                String[] m_nt_values = table.GetStringArray(m_key);

                if(m_length == m_nt_values.Length)
                {
                    for (int i = 0; i < m_buttons.Count; i++)
                    {
                        m_buttons[i].Text = m_nt_values[i];
                        if (m_buttons[i].Checked == true)
                        {
                            Console.Out.WriteLine(i);
                            m_nt_values[m_nt_values.Length-1] = i.ToString();
                            table.PutStringArray(m_key, m_nt_values);
                        }
                    }
                }
                else
                {
                    foreach (RadioButton element in m_buttons)
                    {
                        element.MouseDown -= cascadeMouseDown;
                        element.MouseUp -= cascadeMouseUp;
                        element.MouseMove -= cascadeMouseMove;
                        this.release();
                        this.Controls.Remove(element);
                    }

                    m_buttons = new List<RadioButton>();

                    for (int i = 0; i < m_nt_values.Length - 1; i++)
                    {
                        m_buttons.Add(new RadioButton());
                        m_buttons[i].MouseDown += cascadeMouseDown;
                        m_buttons[i].MouseUp += cascadeMouseUp;
                        m_buttons[i].MouseMove += cascadeMouseMove;
                        m_buttons[i].Text = m_nt_values[i];
                        m_buttons[i].AutoSize = true;
                        this.Controls.Add(m_buttons[i]);
                        m_buttons[i].Location = new Point(0, 30 + 20 * i);
                    }

                    m_buttons[Convert.ToInt32(m_nt_values[m_nt_values.Length - 1])].Checked = true;

                    m_length = m_nt_values.Length;
                }    
            }
            catch
            {
                Console.Out.WriteLine("Dashboards don't quit. This error is to make sure it doesn't!");
            }
        }
    }
}
