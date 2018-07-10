using MjpegProcessor;
using NetworkTables;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml;
using Thin_Dashboard.UI_Objects;

namespace Thin_Dashboard
{
    /**
     * This class manages the GUI in addition to saving and loading .XML files.
     * In addition, it also observes NetworkTables in a seperate thread in order
     * to update GUI elements such as data readouts and radio buttons.
    */ 
    public partial class Form1 : Form
    {
        // These lists house all wrapper classes for GUI objects with custom features.
        List<CameraStream> m_cameras = new List<CameraStream>();    // Camera Streams
        List<DataField> m_readouts = new List<DataField>();         // Data Readouts
        List<ThinLabel> m_labels = new List<ThinLabel>();           // GUI Labels

        // NetworkTables related objects
        NetworkTable m_robot_network;
        BackgroundWorker m_network_thread = new BackgroundWorker();
        Stopwatch m_network_timer = new Stopwatch();

        // User iteractive prompt
        Prompt m_user_prompt = new Prompt();

        // Data table/set for saving and loading data
        DataTable m_save_data = new DataTable();
        DataSet m_data_set = new DataSet();

        // Team number string
        String m_team_number = "1";

        public Form1()
        {
            InitializeComponent();

            // Aquire team number from 
            m_team_number = get_team(@"C:\Users\Public\tb_data.txt");
            team_out.Text = m_team_number;

            NetworkTable.SetClientMode();
            NetworkTable.SetTeam(Convert.ToInt32(m_team_number));
            NetworkTable.Initialize();
            m_robot_network = NetworkTable.GetTable("Thin_Dashboard");

            // Configure and start thread and timer managing NetworkTables
            m_network_timer.Start();
            m_network_thread.DoWork += sleep_network_thread;
            m_network_thread.RunWorkerCompleted += network_thread;
            m_network_thread.RunWorkerAsync();

            // Load default save if save file exists
            load_from_xml("data.xml");
        }

        // Generate new CameraStream
        private void create_camera_stream(object sender, EventArgs e)
        {
            m_cameras.Add(new CameraStream(30+30* m_cameras.Count, 30,1));
            this.Controls.Add(m_cameras[m_cameras.Count-1].getStream());
        }

        // Generate new Label
        private void create_new_label(object sender, EventArgs e)
        {
            m_labels.Add(new ThinLabel(30+ 30 * m_labels.Count, 100));
            this.Controls.Add(m_labels[m_labels.Count - 1].getLabel());
        }

        // Generate new data field
        private void create_new_data_readout(object sender, EventArgs e)
        {
            m_readouts.Add(new DataField(30 + 30 * m_readouts.Count,100, m_robot_network));
            this.Controls.Add(m_readouts[m_readouts.Count - 1].getReadout());
        }

        // Saves the current dashboard as the default in XML
        private void save_as_default(object sender, EventArgs e)
        {
            save_to_xml("data.xml");
        }

        // Refreshes everything connected to NetworkTables at 10hz  
        private void sleep_network_thread(object sender, DoWorkEventArgs e)
        {
            try
            {
                Thread.Sleep(100 - (int)m_network_timer.ElapsedMilliseconds);
            }
            catch { }
            m_network_timer.Reset();
        }

        // Resets the thread and updates all objects requiring NetworkTables
        private void network_thread(object sender, RunWorkerCompletedEventArgs e)
        {
            if(m_readouts.Count != 0)
            {
                foreach (DataField element in m_readouts)
                {
                    element.update();
                }
            }
            m_network_thread.RunWorkerAsync();
        }

        // Sets team number then writes it to a file
        private void set_team_number(object sender, EventArgs e)
        {
            m_team_number = m_user_prompt.ShowDialog("Enter Team #", "Thin Dashboard");
            File.WriteAllText(@"C:\Users\Public\tb_data.txt", m_team_number);
            team_out.Text = m_team_number.ToString();
            NetworkTable.SetClientMode();
            NetworkTable.SetTeam(Convert.ToInt32(m_team_number));
            NetworkTable.Initialize();
        }

       /** 
         * Method that loads from a file and places objects on the GUI as 
         * the file describes. This can be used to create GUIs in a text
         * editor like Notepad++ or load a save previously made by this
         * application.
         * 
         * @Param file_name     Name of file to be read
         * @Returns             Returns nothing, used to end method early if no file is detected
        */ 
        private void load_from_xml(String file_name)
        {
            m_save_data = new DataTable();
            m_data_set = new DataSet();

            if (File.Exists("data.xml"))
            {
                m_data_set.ReadXml("data.xml");
            }
            else
            {
                return;
            }

            foreach (DataTable table in m_data_set.Tables)
            {
                foreach (DataRow row in table.Rows)
                {
                    if ((String)row[0] == "cs")
                    {
                        m_cameras.Add(new CameraStream(Convert.ToInt32(row[1]), Convert.ToInt32(row[2]), Convert.ToDouble(row[3])));
                        this.Controls.Add(m_cameras[m_cameras.Count - 1].getStream());
                        m_cameras[m_cameras.Count - 1].setStream(Convert.ToString(row[4]));
                    }

                    if ((String)row[0] == "tl")
                    {
                        m_labels.Add(new ThinLabel(Convert.ToInt32(row[1]), Convert.ToInt32(row[2]), Convert.ToString(row[4])));
                        this.Controls.Add(m_labels[m_labels.Count - 1].getLabel());
                    }

                    if ((String)row[0] == "df")
                    {
                        m_readouts.Add(new DataField(Convert.ToInt32(row[1]), Convert.ToInt32(row[2]), m_robot_network, Convert.ToString(row[4])));
                        this.Controls.Add(m_readouts[m_readouts.Count - 1].getReadout());
                    }
                }
            }
        }

        /** 
         * Method that saves GUI elements into an XML file. 
         * @Param file_name     Name of the file which will be created to save GUI elements
        */
        private void save_to_xml(String file_name)
        {
            m_save_data = new DataTable();
            m_data_set = new DataSet();

            // Creates columns to save data
            m_save_data.Columns.Add(new DataColumn("type", Type.GetType("System.String")));
            m_save_data.Columns.Add(new DataColumn("x", Type.GetType("System.Int32")));
            m_save_data.Columns.Add(new DataColumn("y", Type.GetType("System.Int32")));
            m_save_data.Columns.Add(new DataColumn("size", Type.GetType("System.Double")));
            m_save_data.Columns.Add(new DataColumn("info", Type.GetType("System.String")));


            // Creates rows from CamStream objects
            foreach (CameraStream element in m_cameras)
            {
                if (element.getAlive())
                {
                    DataRow row = m_save_data.NewRow();
                    row[0] = "cs";
                    row[1] = element.getX();
                    row[2] = element.getY();
                    row[3] = element.getSize();
                    row[4] = element.getInfo();
                    m_save_data.Rows.Add(row);
                }
            }

            foreach (DataField element in m_readouts)
            {
                if (element.getAlive())
                {
                    DataRow row = m_save_data.NewRow();
                    row[0] = "df";
                    row[1] = element.getX();
                    row[2] = element.getY();
                    row[3] = element.getSize();
                    row[4] = element.getInfo();
                    m_save_data.Rows.Add(row);
                }
            }

            foreach (ThinLabel element in m_labels)
            {
                if (element.getAlive())
                {
                    DataRow row = m_save_data.NewRow();
                    row[0] = "tl";
                    row[1] = element.getX();
                    row[2] = element.getY();
                    row[3] = element.getSize();
                    row[4] = element.getInfo();
                    m_save_data.Rows.Add(row);
                }
            }

            m_save_data.TableName = "Object";
            m_data_set.Tables.Add(m_save_data);
            m_data_set.DataSetName = "ThinDash_Save_File";
            m_data_set.WriteXml(file_name);
        }

        private String get_team(String file_name)
        {
            if (File.Exists(@file_name))
            {
                m_team_number = (File.ReadAllText(@file_name));
            }

            return m_team_number.ToString();
        }
    }
}