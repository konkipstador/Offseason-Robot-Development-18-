using MjpegProcessor;
using NetworkTables;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Thin_Dashboard.UI_Objects
{
    /*  CameraStream is unmanaged by NetworkTables--meaning it has no
     *  connection to the Robot. CameraStream creates an MjpegDecoder
     *  object along with a PictureBox and passes Mjpeg images into
     *  the PictureBox. CameraStream has a right click context menu. */
    class CameraStream : ThinControl, ThinControlInterface
    {
        private MjpegDecoder m_mjpeg_stream;

        // Declaration of our Context Menu, which appears on right click.
        private ContextMenu m_context_menu = new ContextMenu();
        private MenuItem m_url_menu = new MenuItem("URL");       // Menu option to change Mjpeg stream URL
        private MenuItem m_delete_menu = new MenuItem("Delete"); // Menu option to delete this CameraStream
        private MenuItem m_scale_menu = new MenuItem("Set Scalar"); // Menu option to delete this CameraStream

        // Classwide prompt used to ask for user input.
        private Prompt prompt = new Prompt();

        String m_url = "";
        Boolean m_alive = true;

        double m_size = 1;

        Boolean m_server_up = true;

        public CameraStream(int x, int y, double size, String URL)
        {
            // Finish constructing the stream object and PictureBox
            m_mjpeg_stream = new MjpegDecoder();

            this.BringToFront();

            // Defining UI charactaristics of the PictureBox
            this.Location = new Point(x, y);
            this.Size = new Size((int)(320*size), (int)(240*size));
            this.BackColor = Color.Black;

            // Adds menu items into the Context Menu 
            m_context_menu.MenuItems.Add(m_url_menu);
            m_context_menu.MenuItems.Add(m_delete_menu);
            m_context_menu.MenuItems.Add(m_scale_menu);

            // Add event handlers
            m_mjpeg_stream.FrameReady += mjpeg_stream_FrameReady;   // Outputs the current Mjpeg frame into the PictureBox
            this.MouseDown += mouseDown;    // Brings up the ContextMenu
            m_url_menu.Click += urlClick;   // Prompts the user to enter the Mjpeg stream URL
            m_delete_menu.Click += delete_stream;   // Removes the stream from the UI and raises ControlRemoved
            m_scale_menu.Click += setScalar;   // 

            m_size = size;
            setStream(URL);
        }

        private void urlClick(object sender, EventArgs e)
        {
            m_mjpeg_stream.StopStream();    // Stops the Mjpeg Stream
            // Prompts the user for the Mjpeg URL and begins the stream process
            m_url = prompt.ShowDialog("Enter Stream URL", "Thin Dashboard");
            try { m_mjpeg_stream.ParseStream(new System.Uri(m_url)); } catch { } 
            
        }

        private void setScalar(object sender, EventArgs e)
        {
            // Prompts the user for the new scalar value
            m_size = Convert.ToDouble(prompt.ShowDialog("Enter Stream URL", "Thin Dashboard"));
            this.Size = new Size(Convert.ToInt32(320 *m_size),Convert.ToInt32(240*m_size));
        }

        private void setStream(String URL)
        {
            m_url = URL;
            try { m_mjpeg_stream.ParseStream(new System.Uri(m_url)); } catch { }
        }

        // Outputs the Mjpeg into the PictureBox
        private void mjpeg_stream_FrameReady(object sender, FrameReadyEventArgs e)
        {
            this.BackgroundImage = new Bitmap(e.Bitmap, this.Size);
        }

        // Brings up the context menu when the PictureBox is right clicked and enable dragging
        // when it is left clicked
        private void mouseDown(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Right:
                    m_context_menu.Show(this, new Point(e.X, e.Y));
                    break;
            }
        }

        // Removes this object and raises an event to force Form1.cs to delete it
        private void delete_stream(object sender, EventArgs e)
        {
            m_mjpeg_stream.StopStream();
            m_mjpeg_stream = null;
            this.Hide();
            m_alive = false;
        }

        public Boolean getAlive()
        {
            return m_alive;
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

        public String getInfo()
        {
            if (m_url.Equals(""))
            {
                return "/";
            }
            return m_url;
        }

        public Boolean getUsesNetTables()
        {
            return false;
        }

        public void update(NetworkTable table, Boolean edit_mode)
        {
            this.CanEdit = edit_mode;
        }

        public String getType()
        {
            return "cs";
        }
    }
}