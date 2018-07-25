namespace Thin_Dashboard
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.navigation_bar = new System.Windows.Forms.PictureBox();
            this.toolbar_file = new System.Windows.Forms.ToolStripDropDownButton();
            this.newToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveAsDefaultToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolbar = new System.Windows.Forms.ToolStrip();
            this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.toolbar_edit = new System.Windows.Forms.ToolStripDropDownButton();
            this.addToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.cameraStreamViewerToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.dataReadoutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.labelToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.radioButtonsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.set_team = new System.Windows.Forms.ToolStripMenuItem();
            this.edit_tool = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.team_out = new System.Windows.Forms.ToolStripLabel();
            ((System.ComponentModel.ISupportInitialize)(this.navigation_bar)).BeginInit();
            this.toolbar.SuspendLayout();
            this.SuspendLayout();
            // 
            // navigation_bar
            // 
            this.navigation_bar.BackColor = System.Drawing.SystemColors.ControlLight;
            this.navigation_bar.Location = new System.Drawing.Point(0, 0);
            this.navigation_bar.Name = "navigation_bar";
            this.navigation_bar.Size = new System.Drawing.Size(3840, 21);
            this.navigation_bar.TabIndex = 0;
            this.navigation_bar.TabStop = false;
            // 
            // toolbar_file
            // 
            this.toolbar_file.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolbar_file.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.newToolStripMenuItem,
            this.openToolStripMenuItem,
            this.saveToolStripMenuItem,
            this.saveAsDefaultToolStripMenuItem});
            this.toolbar_file.Font = new System.Drawing.Font("HP Simplified", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.toolbar_file.Image = ((System.Drawing.Image)(resources.GetObject("toolbar_file.Image")));
            this.toolbar_file.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolbar_file.Name = "toolbar_file";
            this.toolbar_file.Size = new System.Drawing.Size(36, 22);
            this.toolbar_file.Text = "File";
            // 
            // newToolStripMenuItem
            // 
            this.newToolStripMenuItem.Name = "newToolStripMenuItem";
            this.newToolStripMenuItem.Size = new System.Drawing.Size(150, 22);
            this.newToolStripMenuItem.Text = "New";
            // 
            // openToolStripMenuItem
            // 
            this.openToolStripMenuItem.Name = "openToolStripMenuItem";
            this.openToolStripMenuItem.Size = new System.Drawing.Size(150, 22);
            this.openToolStripMenuItem.Text = "Open";
            // 
            // saveToolStripMenuItem
            // 
            this.saveToolStripMenuItem.Name = "saveToolStripMenuItem";
            this.saveToolStripMenuItem.Size = new System.Drawing.Size(150, 22);
            this.saveToolStripMenuItem.Text = "Save";
            // 
            // saveAsDefaultToolStripMenuItem
            // 
            this.saveAsDefaultToolStripMenuItem.Name = "saveAsDefaultToolStripMenuItem";
            this.saveAsDefaultToolStripMenuItem.Size = new System.Drawing.Size(150, 22);
            this.saveAsDefaultToolStripMenuItem.Text = "Save as Default";
            this.saveAsDefaultToolStripMenuItem.Click += new System.EventHandler(this.save_as_default);
            // 
            // toolbar
            // 
            this.toolbar.BackColor = System.Drawing.Color.White;
            this.toolbar.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolbar_file,
            this.toolStripLabel1,
            this.toolStripSeparator1,
            this.toolbar_edit,
            this.toolStripSeparator2,
            this.team_out});
            this.toolbar.Location = new System.Drawing.Point(0, 0);
            this.toolbar.Name = "toolbar";
            this.toolbar.Size = new System.Drawing.Size(800, 25);
            this.toolbar.TabIndex = 2;
            this.toolbar.Text = "toolStrip1";
            // 
            // toolStripLabel1
            // 
            this.toolStripLabel1.Name = "toolStripLabel1";
            this.toolStripLabel1.Size = new System.Drawing.Size(0, 22);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
            // 
            // toolbar_edit
            // 
            this.toolbar_edit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolbar_edit.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.addToolStripMenuItem,
            this.set_team,
            this.edit_tool});
            this.toolbar_edit.Font = new System.Drawing.Font("HP Simplified", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.toolbar_edit.Image = ((System.Drawing.Image)(resources.GetObject("toolbar_edit.Image")));
            this.toolbar_edit.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolbar_edit.Name = "toolbar_edit";
            this.toolbar_edit.Size = new System.Drawing.Size(38, 22);
            this.toolbar_edit.Text = "Edit";
            this.toolbar_edit.ToolTipText = "Edit";
            // 
            // addToolStripMenuItem
            // 
            this.addToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cameraStreamViewerToolStripMenuItem,
            this.dataReadoutToolStripMenuItem,
            this.labelToolStripMenuItem,
            this.radioButtonsToolStripMenuItem});
            this.addToolStripMenuItem.Name = "addToolStripMenuItem";
            this.addToolStripMenuItem.Size = new System.Drawing.Size(180, 22);
            this.addToolStripMenuItem.Text = "Add";
            // 
            // cameraStreamViewerToolStripMenuItem
            // 
            this.cameraStreamViewerToolStripMenuItem.Name = "cameraStreamViewerToolStripMenuItem";
            this.cameraStreamViewerToolStripMenuItem.Size = new System.Drawing.Size(188, 22);
            this.cameraStreamViewerToolStripMenuItem.Text = "Camera Stream Viewer";
            this.cameraStreamViewerToolStripMenuItem.Click += new System.EventHandler(this.create_camera_stream);
            // 
            // dataReadoutToolStripMenuItem
            // 
            this.dataReadoutToolStripMenuItem.Name = "dataReadoutToolStripMenuItem";
            this.dataReadoutToolStripMenuItem.Size = new System.Drawing.Size(188, 22);
            this.dataReadoutToolStripMenuItem.Text = "Data Readout";
            this.dataReadoutToolStripMenuItem.Click += new System.EventHandler(this.create_new_data_readout);
            // 
            // labelToolStripMenuItem
            // 
            this.labelToolStripMenuItem.Name = "labelToolStripMenuItem";
            this.labelToolStripMenuItem.Size = new System.Drawing.Size(188, 22);
            this.labelToolStripMenuItem.Text = "Label";
            this.labelToolStripMenuItem.Click += new System.EventHandler(this.create_new_label);
            // 
            // radioButtonsToolStripMenuItem
            // 
            this.radioButtonsToolStripMenuItem.Name = "radioButtonsToolStripMenuItem";
            this.radioButtonsToolStripMenuItem.Size = new System.Drawing.Size(188, 22);
            this.radioButtonsToolStripMenuItem.Text = "Radio Buttons";
            this.radioButtonsToolStripMenuItem.Click += new System.EventHandler(this.add_radio_buttons);
            // 
            // set_team
            // 
            this.set_team.Name = "set_team";
            this.set_team.Size = new System.Drawing.Size(180, 22);
            this.set_team.Text = "Set Team";
            this.set_team.Click += new System.EventHandler(this.set_team_number);
            // 
            // edit_tool
            // 
            this.edit_tool.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(192)))), ((int)(((byte)(192)))));
            this.edit_tool.Name = "edit_tool";
            this.edit_tool.Size = new System.Drawing.Size(180, 22);
            this.edit_tool.Text = "Enable Edit Mode";
            this.edit_tool.Click += new System.EventHandler(this.editModeClicked);
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
            // 
            // team_out
            // 
            this.team_out.Font = new System.Drawing.Font("HP Simplified", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.team_out.Name = "team_out";
            this.team_out.Size = new System.Drawing.Size(75, 22);
            this.team_out.Text = "Team Number";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.Gainsboro;
            this.ClientSize = new System.Drawing.Size(800, 450);
            this.Controls.Add(this.toolbar);
            this.Controls.Add(this.navigation_bar);
            this.Name = "Form1";
            this.Text = "Thin Dashboard";
            ((System.ComponentModel.ISupportInitialize)(this.navigation_bar)).EndInit();
            this.toolbar.ResumeLayout(false);
            this.toolbar.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.PictureBox navigation_bar;
        private System.Windows.Forms.ToolStripDropDownButton toolbar_file;
        private System.Windows.Forms.ToolStripMenuItem newToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveToolStripMenuItem;
        private System.Windows.Forms.ToolStrip toolbar;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripDropDownButton toolbar_edit;
        private System.Windows.Forms.ToolStripMenuItem addToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem cameraStreamViewerToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem dataReadoutToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem labelToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem radioButtonsToolStripMenuItem;
        private System.Windows.Forms.ToolStripLabel toolStripLabel1;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripMenuItem saveAsDefaultToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem set_team;
        private System.Windows.Forms.ToolStripLabel team_out;
        private System.Windows.Forms.ToolStripMenuItem edit_tool;
    }
}