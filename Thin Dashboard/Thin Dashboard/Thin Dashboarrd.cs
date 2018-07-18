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
using Thin_Dashboard.UI_Objects.Objects;

namespace Thin_Dashboard
{
    /**
     * This class manages the GUI in addition to saving and loading .XML files.
     * In addition, it also observes NetworkTables in a seperate thread in order
     * to update GUI elements such as data readouts and radio buttons.
    */ 
    public partial class Form1 : Form
    {
        // This list house all wrapper classes for GUI objects with custom features.
        List<Control> m_controls = new List<Control>();

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

            try
            {

                NetworkTable.SetClientMode();
                NetworkTable.SetTeam(Convert.ToInt32(m_team_number));
                NetworkTable.Initialize();
                m_robot_network = NetworkTable.GetTable("Thin_Dashboard");
            }
            catch
            {
                NetworkTable.SetClientMode();
                NetworkTable.SetTeam(957);
                NetworkTable.Initialize();
                m_robot_network = NetworkTable.GetTable("Thin_Dashboard");
            }

            

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

            m_controls.Add(new CameraStream(30+30* m_controls.Count, 30,1,"/"));
            this.Controls.Add(m_controls[m_controls.Count-1]);
        }

        // Generate new Label
        private void create_new_label(object sender, EventArgs e)
        {
            m_controls.Add(new DashboardLabel(30+ 30 * m_controls.Count, 100,""));
            this.Controls.Add(m_controls[m_controls.Count - 1]);
        }

        // Generate new data field
        private void create_new_data_readout(object sender, EventArgs e)
        {
            m_controls.Add(new DataField(30 + 30 * m_controls.Count,100, m_robot_network,""));
            this.Controls.Add(m_controls[m_controls.Count - 1]);
        }

        private void add_radio_buttons(object sender, EventArgs e)
        {
            m_controls.Add(new Selector(30 + 30 * m_controls.Count, 100, m_robot_network, ""));
            this.Controls.Add(m_controls[m_controls.Count - 1]);
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
            if(m_controls.Count != 0)
            {
                foreach (ThinControlInterface element in m_controls)
                {
                    if (element.getAlive() && element.getUsesNetTables())
                    {
                        element.update();
                    }
                    
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
            Application.Restart();
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
                        m_controls.Add(new CameraStream(Convert.ToInt32(row[1]), Convert.ToInt32(row[2]), Convert.ToDouble(row[3]), Convert.ToString(row[4])));
                        this.Controls.Add(m_controls[m_controls.Count - 1]);
                    }

                    if ((String)row[0] == "tl")
                    {
                        m_controls.Add(new DashboardLabel(Convert.ToInt32(row[1]), Convert.ToInt32(row[2]), Convert.ToString(row[4])));
                        this.Controls.Add(m_controls[m_controls.Count - 1]);
                    }

                    if ((String)row[0] == "df")
                    {
                        m_controls.Add(new DataField(Convert.ToInt32(row[1]), Convert.ToInt32(row[2]), m_robot_network, Convert.ToString(row[4])));
                        this.Controls.Add(m_controls[m_controls.Count - 1]);
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


            // Creates rows from TCI objects
            foreach (ThinControlInterface element in m_controls)
            {
                if (element.getAlive() && element.getType().Equals("cs"))
                {
                    DataRow row = m_save_data.NewRow();
                    row[0] = "cs";
                    row[1] = element.getX();
                    row[2] = element.getY();
                    row[3] = element.getSize();
                    row[4] = element.getInfo();
                    m_save_data.Rows.Add(row);
                }

                if (element.getAlive() && element.getType().Equals("df"))
                {
                    DataRow row = m_save_data.NewRow();
                    row[0] = "df";
                    row[1] = element.getX();
                    row[2] = element.getY();
                    row[3] = element.getSize();
                    row[4] = element.getInfo();
                    m_save_data.Rows.Add(row);
                }

                if (element.getAlive() && element.getType().Equals("tl"))
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