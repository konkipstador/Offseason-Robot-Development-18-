  using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing;

namespace Thin_Dashboard.UI_Objects
{
    class ThinLabel
    {
        // Main label object (all events correspond to this label
        Label m_main_label = new Label();

        // Promt to set label text
        Prompt m_user_prompt = new Prompt();

        // Used to drag the object around the Form
        private Control m_control_to_drag;
        private Point m_previous_mouse_location;
        Cursor m_cursor;

        // Context menu and corresponding menu items
        ContextMenu m_context_menu = new ContextMenu();
        MenuItem m_set_text = new MenuItem("Change Text");
        MenuItem m_delete_text = new MenuItem("Delete");

        // Tells the main thread if this object is "alive" (i.e. not deleted)
        Boolean m_alive = true;

        // Constructor
        public ThinLabel(int x, int y)
        {
            // Finishes the construction of the label object
            m_main_label.Text = m_user_prompt.ShowDialog("Enter label text:","ThinDashboard");
            m_main_label.Location = new Point(x, y);
            m_main_label.Font = new Font("HP Simplified", 24, FontStyle.Bold | FontStyle.Italic);
            m_main_label.AutoSize = true;
            m_main_label.BackColor = Color.Transparent;

            m_context_menu.MenuItems.Add(m_set_text);
            m_context_menu.MenuItems.Add(m_delete_text);

            m_main_label.MouseDown += label_mouseDown;    // Brings up the ContextMenu
            m_main_label.MouseMove += label_mouseMove;
            m_main_label.MouseUp += label_display_mouseUp;

            m_set_text.Click += change_text;
            m_delete_text.Click += delete_label;
        }

        // Constructor used to load object from save
        public ThinLabel(int x, int y, String text)
        {
            // Finishes the construction of the label object
            m_main_label.Text = text;
            m_main_label.Location = new Point(x, y);
            m_main_label.Font = new Font("HP Simplified", 24, FontStyle.Bold | FontStyle.Italic);
            m_main_label.AutoSize = true;
            m_main_label.BackColor = Color.Transparent;

            m_context_menu.MenuItems.Add(m_set_text);
            m_context_menu.MenuItems.Add(m_delete_text);

            m_main_label.MouseDown += label_mouseDown;    // Brings up the ContextMenu
            m_main_label.MouseMove += label_mouseMove;
            m_main_label.MouseUp += label_display_mouseUp;

            m_set_text.Click += change_text;
            m_delete_text.Click += delete_label;
        }

        // Event triggered to delete label
        private void delete_label(object sender, EventArgs e)
        {
            m_main_label.Hide();
            m_main_label = null;    
            m_alive = false;
        }

        // Event triggered to change text of the label
        private void change_text(object sender, EventArgs e) {
            m_main_label.Text = m_user_prompt.ShowDialog("Enter label text:", "ThinDashboard");
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
                    m_context_menu.Show(m_main_label, new Point(e.X, e.Y));
                    break;
                case MouseButtons.Left:
                    m_control_to_drag = sender as Control;
                    m_previous_mouse_location = e.Location;
                    m_cursor = Cursors.Hand;
                    break;
            }
        }

        // Returns the label object
        public Label getLabel()
        {
            return m_main_label;
        }

        // Returns the string contained by the label
        public String getInfo()
        {
            return m_main_label.Text;
        }

        // Returns the x position of the label
        public int getX()
        {
            return m_main_label.Location.X;
        }

        // Returns the y position of the label
        public int getY()
        {
            return m_main_label.Location.Y;
        }

        public double getSize()
        {
            return 0;
        }

        // Returns if the object is alive
        public Boolean getAlive()
        {
            return m_alive;
        }
    }
}