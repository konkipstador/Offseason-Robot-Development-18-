using MjpegProcessor;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Thin_Dashboard.UI_Objects
{
    /*  CameraStream is unmanaged by NetworkTables--meaning it has no
     *  connection to the Robot. CameraStream creates an MjpegDecoder
     *  object along with a PictureBox and passes Mjpeg images into
     *  the PictureBox. CameraStream has a right click context menu. */
    class CameraStream
    {
        private MjpegDecoder m_mjpeg_stream;
        private PictureBox m_camera_display;

        // Declaration of our Context Menu, which appears on right click.
        private ContextMenu m_context_menu = new ContextMenu();
        private MenuItem m_url_menu = new MenuItem("URL");       // Menu option to change Mjpeg stream URL
        private MenuItem m_delete_menu = new MenuItem("Delete"); // Menu option to delete this CameraStream

        // Classwide prompt used to ask for user input.
        private Prompt prompt = new Prompt();

        String m_url = "";
        Boolean m_alive = true;

        private Control activeControl;
        private Point previousLocation;
        Cursor cursor;

        double m_size = 0;

        public CameraStream(int x, int y, double size)
        {
            // Finish constructing the stream object and PictureBox
            m_mjpeg_stream = new MjpegDecoder();
            m_camera_display = new PictureBox();
            
            // Defining UI charactaristics of the PictureBox
            m_camera_display.Location = new System.Drawing.Point(x, y);
            m_camera_display.Size = new System.Drawing.Size((int)(320*size), (int)(240*size));
            m_camera_display.BackColor = Color.Black;

            // Adds menu items into the Context Menu 
            m_context_menu.MenuItems.Add(m_url_menu);
            m_context_menu.MenuItems.Add(m_delete_menu);

            // Add event handlers
            m_mjpeg_stream.FrameReady += mjpeg_stream_FrameReady;   // Outputs the current Mjpeg frame into the PictureBox
            m_camera_display.MouseDown += mouseDown;    // Brings up the ContextMenu
            m_url_menu.Click += urlClick;   // Prompts the user to enter the Mjpeg stream URL
            m_delete_menu.Click += delete_stream;   // Removes the stream from the UI and raises ControlRemoved

            // Drag event handlers
            m_camera_display.MouseMove += new MouseEventHandler(m_camera_display_mouseMove);
            m_camera_display.MouseUp += new MouseEventHandler(m_camera_display_mouseUp);

            m_size = size;
        }

        // Moves the control when the mouse is moved and down
        private void m_camera_display_mouseMove(object sender, MouseEventArgs e)
        {
            if (activeControl == null || activeControl != sender)
                return;

            var location = activeControl.Location;
            location.Offset(e.Location.X - previousLocation.X, e.Location.Y - previousLocation.Y);
            activeControl.Location = location;
        }

        // Releases the dragged object
        private void m_camera_display_mouseUp(object sender, MouseEventArgs e)
        {
            activeControl = null;
            cursor = Cursors.Default;
        }

        private void urlClick(object sender, EventArgs e)
        {
            m_mjpeg_stream.StopStream();    // Stops the Mjpeg Stream
            // Prompts the user for the Mjpeg URL and begins the stream process
            m_url = prompt.ShowDialog("Enter Stream URL", "Thin Dashboard");
            try { m_mjpeg_stream.ParseStream(new System.Uri(m_url)); } catch { } 
            
        }

        public void setStream(String URL)
        {
            m_url = URL;
            try { m_mjpeg_stream.ParseStream(new System.Uri(m_url)); } catch { }
        }

        // Outputs the Mjpeg into the PictureBox
        private void mjpeg_stream_FrameReady(object sender, FrameReadyEventArgs e)
        {
            m_camera_display.Image = e.Bitmap;        
        }

        // Brings up the context menu when the PictureBox is right clicked and enable dragging
        // when it is left clicked
        private void mouseDown(object sender, MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Right:
                    m_context_menu.Show(m_camera_display, new Point(e.X, e.Y));
                    break;
                case MouseButtons.Left:
                    activeControl = sender as Control;
                    previousLocation = e.Location;
                    cursor = Cursors.Hand;
                    break;
            }
        }

        // Returns the picture box. This is to add it to the UI in Form1.cs.
        public PictureBox getStream()
        {
            return m_camera_display;
        }

        // Returns the URL of the mjpeg stream
        public String getInfo()
        {
            return m_url + "/";
        }

        // Returns the x location of the PictureBox
        public int getX()
        {
            return m_camera_display.Location.X;
        }

        // Returns the y location of the PictureBox
        public int getY()
        {
            return m_camera_display.Location.Y;
        }

        // Returns the height the of PictureBox
        public int getHeight()
        {
            return m_camera_display.Size.Height;
        }

        // Returns the width the of PictureBox
        public int getWidth()
        {
            return m_camera_display.Size.Width;
        }

        // Returns if the stream object is alive
        public Boolean getAlive()
        {
            return m_alive;
        }

        public double getSize()
        {
            return m_size;
        }

        // Removes this object and raises an event to force Form1.cs to delete it
        private void delete_stream(object sender, EventArgs e)
        {
            m_mjpeg_stream.StopStream();
            m_mjpeg_stream = null;
            m_camera_display.Hide();
            m_alive = false;
        }
    }
}