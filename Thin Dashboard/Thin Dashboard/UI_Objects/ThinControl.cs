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
     * This class is here to allow new controls to be drug around the UI.
     * At the same time, it inherits from Control make it easier for the
     * main class to do things with it.
    */ 
    class ThinControl : Label
    {
        private Control m_activeControl;
        private Point m_previousLocation;
        Cursor m_cursor;

        protected Boolean CanEdit = false;

        // Constructor
        public ThinControl()
        {
            // Event handlers
            this.MouseMove += new MouseEventHandler(mouseMove);
            this.MouseUp += new MouseEventHandler(mouseUp);
            this.MouseDown += mouseDown;
        }

        // Calculates the position of the mouse
        private void mouseMove(object sender, MouseEventArgs e)
        {
            if (m_activeControl == null || m_activeControl != sender)
                return;

            if (CanEdit)
            {
                var location = m_activeControl.Location;
                location.Offset(e.Location.X - m_previousLocation.X, e.Location.Y - m_previousLocation.Y);
                m_activeControl.Location = location;
            }
        }

        // Checks if the mouse is not clicked
        private void mouseUp(object sender, MouseEventArgs e)
        {
            m_activeControl = null;
            m_cursor = Cursors.Default;
        }

        // Checks if the drag button is pushed down
        private void mouseDown(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Left:
                    m_activeControl = sender as Control;
                    m_previousLocation = e.Location;
                    m_cursor = Cursors.Hand;
                    break;
            }
        }
    }
}
