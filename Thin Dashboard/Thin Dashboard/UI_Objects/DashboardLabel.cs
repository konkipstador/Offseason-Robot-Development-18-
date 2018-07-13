  using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing;

namespace Thin_Dashboard.UI_Objects
{
    class DashboardLabel : ThinControl, ThinControlInterface
    {
        // Promt to set label text
        Prompt m_user_prompt = new Prompt();

        // Context menu and corresponding menu items
        ContextMenu m_context_menu = new ContextMenu();
        MenuItem m_set_text = new MenuItem("Change Text");
        MenuItem m_delete_text = new MenuItem("Delete");

        // Tells the main thread if this object is "alive" (i.e. not deleted)
        Boolean m_alive = true;

        // Constructor
        public DashboardLabel(int x, int y, String text)
        {
            // Finishes the construction of the label object
            if (text.Equals(""))
            {
                this.Text = m_user_prompt.ShowDialog("Enter label text:", "ThinDashboard");
            }
            else
            {
                this.Text = text;
            }
            
            this.Location = new Point(x, y);
            this.Font = new Font("HP Simplified", 24, FontStyle.Bold | FontStyle.Italic);
            this.AutoSize = true;
            this.BackColor = Color.Transparent;

            m_context_menu.MenuItems.Add(m_set_text);
            m_context_menu.MenuItems.Add(m_delete_text);

            this.MouseDown += mouseDown;

            m_set_text.Click += change_text;
            m_delete_text.Click += delete_label;
        }

        // Event triggered to delete label
        private void delete_label(object sender, EventArgs e)
        {
            this.Hide();
            m_alive = false;
        }

        // Event triggered to change text of the label
        private void change_text(object sender, EventArgs e) {
            this.Text = m_user_prompt.ShowDialog("Enter label text:", "ThinDashboard");
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

        // Returns the string contained by the label
        public String getInfo()
        {
            return this.Text;
        }

        // Returns the x position of the label
        public int getX()
        {
            return this.Location.X;
        }

        // Returns the y position of the label
        public int getY()
        {
            return this.Location.Y;
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

        public Boolean getUsesNetTables()
        {
            return false;
        }

        public void update()
        {

        }

        public String getType()
        {
            return "tl";
        }
    }
}