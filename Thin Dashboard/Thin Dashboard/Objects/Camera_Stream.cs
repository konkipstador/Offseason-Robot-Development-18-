using MjpegProcessor;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Thin_Dashboard.Objects
{
    class Camera_Stream {

        MjpegDecoder m_mjpeg;
        PictureBox camera_display;

        public Camera_Stream(PictureBox pb, string URL, int x, int y, int h, int w)
        {
            m_mjpeg = new MjpegDecoder();
            m_mjpeg.FrameReady += mjpeg_FrameReady;
            camera_display = pb;
            camera_display.Location = new System.Drawing.Point(x, y);
            camera_display.Size = new System.Drawing.Size(h, w);
            camera_display.BackColor = Color.Black;

        }

        private void mjpeg_FrameReady(object sender, FrameReadyEventArgs e)
        {
            camera_display.Image = e.Bitmap;
        }

        private PictureBox get_control()
        {
            return camera_display;
        }
    
    }
}
