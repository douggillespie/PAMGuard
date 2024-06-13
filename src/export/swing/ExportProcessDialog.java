package export.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fileicons.FileIcons;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.swing.FontIcon;

import PamController.PamController;
import PamUtils.PamFileChooser;
import PamView.dialog.PamButton;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamPanel;
import PamguardMVC.PamDataBlock;
import export.PamExporterManager;
import export.layoutFX.ExportParams;
import offlineProcessing.OLProcessDialog;
import offlineProcessing.OfflineTaskGroup;

/**
 * Handles an offline dialog for processing offline data and exporting to bespoke file types.
 *  
 * @author Jamie Macaulay
 *
 */
public class ExportProcessDialog {


	/**
	 * The offline task group
	 */
	private OfflineTaskGroup dlOfflineGroup;


	private ExportOLDialog mtOfflineDialog;

	/**
	 * Reference to the export manager. 
	 */
	private PamExporterManager exportManager;

	/**
	 * The current paramters. 
	 */
	private ExportParams currentParams;

	public ExportProcessDialog(PamExporterManager exportManager) {
		//create the offline task group. 
		this.exportManager=exportManager;
		dlOfflineGroup = new ExportTaskGroup("Export data");
	}


	public void createExportGroup() {

		//clear current tasks. 
		dlOfflineGroup.clearTasks();

		//go through every data block we have and check if we can export the data units...
		ArrayList<PamDataBlock> dataBlocks= PamController.getInstance().getDataBlocks();

		for (int i=0; i<dataBlocks.size(); i++) {
			if (exportManager.canExportDataBlock(dataBlocks.get(i))) {
				dlOfflineGroup.addTask(new ExportTask(dataBlocks.get(i), exportManager));
			}
		}
		
	}
	////---Swing stuff----/// should not be here but this is how PG works. 

	public void showOfflineDialog(Frame parentFrame, ExportParams params) {

		createExportGroup();

		//if null open the dialog- also create a new offlineTask group if the datablock has changed. 
		if (mtOfflineDialog == null) {
			mtOfflineDialog = new ExportOLDialog(parentFrame, 
					dlOfflineGroup, "Export Data");
			//batchLocaliseDialog.setModalityType(Dialog.ModalityType.MODELESS);
		}
		mtOfflineDialog.setParams(params); 
		mtOfflineDialog.enableControls();
		mtOfflineDialog.setVisible(true);
	}


	/**
	 * Custom dialog which shows some extra options/ 
	 * @author Jamie Macaulay
	 *
	 */
	class ExportOLDialog extends OLProcessDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The current parameters for exporting. 
		 */
		private ExportParams currentParams;

		/**
		 * The file chooser. 
		 */
		private JFileChooser fc;

		/**
		 * S	hows the folder stuff is going to export to. 
		 */
		private JTextField exportTo;

		/**
		 * Spinner for setting the maximum file size. 
		 */
		private JSpinner spinner;

		private ButtonGroup buttonGroup;

		/**
		 * A list of the export buttons so they are easy to select. 
		 */
		private JToggleButton[] exportButtons;


		public ExportOLDialog(Window parentFrame, OfflineTaskGroup taskGroup, String title) {
			super(parentFrame, taskGroup, title);

			//remove the notes panel - don't need this for export. 
			super.removeNotePanel();
			//remove delete database entried - not used. 
			super.getDeleteOldDataBox().setVisible(false);

			//construc tthe panel. 
			PamPanel mainPanel = new PamPanel();

			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			mainPanel.setBorder(new TitledBorder("Export Settings"));

			buttonGroup = new ButtonGroup();

			PamPanel buttonPanel = new PamPanel();

			ActionListener listener = actionEvent -> {
//				System.out.println(actionEvent.getActionCommand() + " Selected");
				//TODO set the buttons to be disabled or enabled. 
				enableTasks(getExportSelection());
			};

			exportButtons = new JToggleButton[exportManager.getNumExporters()];
			for (int i = 0; i < exportManager.getNumExporters(); i++) {
				JToggleButton b = new JToggleButton();
				b.setToolTipText("Export to " + exportManager.getExporter(i).getName() + " file ("  + exportManager.getExporter(i).getFileExtension() + ")");

				FontIcon icon = FontIcon.of(getIconFromString(exportManager.getExporter(i).getIconString()));
				icon.setIconSize(25);
				icon.setIconColor(Color.DARK_GRAY);

				b.setIcon(icon);

				b.addActionListener(listener);

				exportButtons[i]=b;
				buttonGroup.add(b);
				buttonPanel.add(b);
			}


			PamPanel p = new PamPanel(new GridBagLayout());
			GridBagConstraints c = new PamGridBagContraints();
			c.gridwidth = 3;
			c.gridx = 0;
			c.gridy = 0; 

			addComponent(p, exportTo = new JTextField(), c);
			exportTo.setMinimumSize(new Dimension(180, 25));
			exportTo.setPreferredSize(new Dimension(180, 25));

			c.gridx +=3;
			c.gridwidth = 1;
			PamButton button = new PamButton("Browse...");

			fc = new PamFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			button.addActionListener((action)->{
				int returnVal = fc.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File yourFolder = fc.getSelectedFile();
					exportTo.setText(yourFolder.getAbsolutePath()); 
					exportTo.setToolTipText(yourFolder.getAbsolutePath()); 
				}
			});		

			addComponent(p, button, c);

			c.gridx = 1;
			c.gridy++;
			c.gridwidth = 2;

			JLabel label = new JLabel("Maximum file size", SwingConstants.RIGHT);
			addComponent(p, label, c);

			c.gridwidth = 1;
			c.gridx +=2;

			SpinnerListModel list = new SpinnerListModel(new Double[] {10.,30., 60., 100., 200., 300., 600., 1000.});

			spinner = new JSpinner(list);
			//don't want the user to to able to set values
			((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
			spinner.setBounds(50, 80, 70, 100);
			addComponent(p, spinner, c);

			c.gridx ++;
			addComponent(p, new JLabel("MB"), c);



			mainPanel.add(p);
			mainPanel.add(buttonPanel);

			//add the main panel at a different index. 
			getMainPanel().add(mainPanel, 1);

			pack();

		}	


		/**
		 * Enable which task are disables and enabled. 
		 * @param exportSelection
		 */
		private void enableTasks(int exportSelection) {
			this.currentParams = getExportParams();
			exportManager.setExportParams(currentParams);
//			ExportTask task;
//			for (int i=0; i<this.getTaskGroup().getNTasks(); i++) {
//				task = (ExportTask) this.getTaskGroup().getTask(i);
//			}
			enableControls();
		}


		private Ikon getIconFromString(String iconString) {

			Ikon icon = null;
			/**
			 * This is NASTY but we won't have many exporters and this is the only
			 * good way to get this to work in Swing. 
			 */
			switch (iconString) {
			case "file-matlab":
				icon=FileIcons.MATLAB;
				break;
			case "file-r":
				icon=FileIcons.R;
				break;
			case "mdi2f-file-music":
				icon=MaterialDesignF.FILE_MUSIC;
				break;
			case "mdi2f-file-table-outline":
				icon=MaterialDesignF.FILE_TABLE_OUTLINE;
				break;
			}
			return icon;
		}
		
		private int getExportSelection() {
			int sel=-1;
			for (int i=0; i<exportButtons.length; i++) {
				if (this.exportButtons[i].isSelected()) {
					sel=i;
					break;
				}
			}
			return sel;
		}

		
		public ExportParams getExportParams() {
			currentParams.folder = null;
			
			if (exportTo.getText().length()>0) {

				File file = new File(exportTo.getText());

				if (!(file.exists() && file.isDirectory())) {
					currentParams.folder = null;
				}
				else {
					currentParams.folder  = file.getAbsolutePath();
				}
			}
			
			currentParams.exportChoice =  getExportSelection();
			currentParams.maximumFileSize = (Double) spinner.getValue();
			
			return currentParams;
		}

		@Override
		public boolean getParams() {
			//make sure we update the current paramters before processing starts. 
			this.currentParams = getExportParams();
			exportManager.setExportParams(currentParams);

			if (this.currentParams.folder==null) {
				return PamDialog.showWarning(super.getOwner(), "No folder or file selected", "You must select an output folder");
			}


			return super.getParams();
		}


		public void setParams(ExportParams params) {
			if (params ==null) currentParams = new ExportParams(); 
			currentParams = params.clone(); 

			buttonGroup.clearSelection();
			exportButtons[params.exportChoice].setSelected(true);
			
			exportTo.setText(currentParams.folder);
			
			spinner.setValue(currentParams.maximumFileSize);
		}



	}


	class ExportTaskGroup extends OfflineTaskGroup {

		public ExportTaskGroup(String settingsName) {
			super(null, settingsName);

		}

		@Override
		public String getUnitType() {
			return "Export Data";
		}
		
		
		/**
		 * Override the tasks o it runs through all tasks for each datablock. Usually
		 * task groups deal with just one parent datablock but exporters export from
		 * different data blocks. The only way to deal with this is to let the task run
		 * again and again through all tasks and letting tasks themselves check the
		 * correct data units are being exported.
		 */
		@Override
		public boolean runTasks() {
			boolean OK = true;
			for (int i=0; i<getNTasks(); i++) {
				this.setPrimaryDataBlock(getTask(i).getDataBlock());
				super.runTasks();
			}
			return OK;
		}
	
//			
//			
//			int nDatas = primaryDataBlock.getUnitsCount();
//			int nSay = Math.max(1, nDatas / 100);
////			int nDone = 0;
//			int nTasks = getNTasks();
//			PamDataUnit dataUnit;
//			OfflineTask aTask;
//			boolean unitChanged;
//			DataUnitFileInformation fileInfo;
//			String dataName;
//			if (mapPoint != null) {
//				dataName = mapPoint.getName();
//			}
//			else {
//				dataName = "Loaded Data";
//			}
//			/**
//			 * Make sure that any data from required data blocks is loaded. First check the 
//			 * start and end times of the primary data units we actually WANT to process
//			 * Also get a count of found data - may be able to leave without having to do anything at all
//			 */
//			ListIterator<PamDataUnit> it = primaryDataBlock.getListIterator(0);
//			long procDataStart = Long.MAX_VALUE;
//			long procDataEnd = 0;
//			int nToProcess = 0;
//			while (it.hasNext()) {
//				dataUnit = it.next();
//				/**
//				 * Make sure we only process data units within the current time interval. 
//				 */
//				if (dataUnit.getTimeMilliseconds() < processStartTime) {
//					continue;
//				}
//				if (dataUnit.getTimeMilliseconds() > processEndTime) {
//					break;
//				}
////				if (shouldProcess(dataUnit) == false) {
////					continue;
////				}
//				procDataStart = Math.min(procDataStart, dataUnit.getTimeMilliseconds());
//				procDataEnd = Math.max(procDataEnd, dataUnit.getEndTimeInMilliseconds());
//				// do this one too - just to make sure in case end time returns zero. 
//				procDataEnd = Math.max(procDataEnd, dataUnit.getTimeMilliseconds());
//				nToProcess++; // increase toprocess counter
//			}
//			if (nToProcess == 0) {
//				return;
//			}
//			PamDataBlock aDataBlock;
//			RequiredDataBlockInfo blockInfo;
//			/* 
//			 * if the data interval is < 1 hour, then load it all now
//			 * otherwise we'll do it on a data unit basis. 
//			 */
//////			long maxSecondaryLoad = 1800L*1000L;
//////			if (procDataEnd - procDataStart < maxSecondaryLoad) {
////				loadSecondaryData(procDataStart, procDataEnd);
//////			}
//			// remember the end time of the data so we can use the "new data" selection flag. 
//			taskGroupParams.lastDataTime = Math.min(primaryDataBlock.getCurrentViewDataEnd(),processEndTime);
//			//			synchronized(primaryDataBlock) {
//			/*
//			 * Call newDataLoaded for each task before getting on with processing individual data units. 
//			 */
//
//			/**
//			 * Now process the data
//			 */
//			it = primaryDataBlock.getListIterator(0);
//			unitChanged = false;
//			int totalUnits = 0;
//			int unitsChanged = 0;
//			boolean doTasks = false;
//			while (it.hasNext()) {
//				dataUnit = it.next();
//				totalUnits++;
//				doTasks = true;
//				/**
//				 * Make sure we only process data units within the current time interval. 
//				 */
//				if (dataUnit.getTimeMilliseconds() < processStartTime) {
//					continue;
//				}
//				if (dataUnit.getTimeMilliseconds() > processEndTime) {
//					break;
//				}
//				
//				if (shouldProcess(dataUnit) == false) {
//					doTasks = false;
//				}
//				
//				if (doTasks) {
//					/*
//					 *  load the secondary datablock data. this can be called even if
//					 *  it was called earlier on since it wont' reload if data are already
//					 *  in memory.  
//					 */
////					loadSecondaryData(dataUnit.getTimeMilliseconds(), dataUnit.getEndTimeInMilliseconds());
//
//					for (int iTask = 0; iTask < nTasks; iTask++) {
//						aTask = getTask(iTask);
//						if (aTask.isDoRun() == false ||  !isInTimeChunk(dataUnit, taskGroupParams.timeChunks)) {
//							continue;
//						}
//						cpuMonitor.start();
//						unitChanged |= aTask.processDataUnit(dataUnit);
//						cpuMonitor.stop();
//					}
//					if (unitChanged) {
//						fileInfo = dataUnit.getDataUnitFileInformation();
//						if (fileInfo != null) {
//							fileInfo.setNeedsUpdate(true);
//						}
//						dataUnit.updateDataUnit(System.currentTimeMillis());
//					}
//					dataUnit.freeData();
//				}
//				if (instantKill) {
//					break;
//				}
//				unitsChanged++;
//				if (totalUnits%nSay == 0) {
//					publish(new TaskMonitorData(TaskStatus.RUNNING, TaskActivity.PROCESSING, nToProcess, totalUnits, dataName,
//							dataUnit.getTimeMilliseconds()));
//				}
//			}
//			for (int iTask = 0; iTask < nTasks; iTask++) {
//				aTask = getTask(iTask);
//				if (aTask.isDoRun() == false) {
//					continue;
//				}
//				aTask.loadedDataComplete();
//			}
//			//			}
//			publish(new TaskMonitorData(TaskStatus.RUNNING, TaskActivity.SAVING, nToProcess, totalUnits, dataName,
//					processEndTime));
//			for (int i = 0; i < affectedDataBlocks.size(); i++) {
//				//System.out.println("SAVE VIEWER DATA FOR: " + affectedDataBlocks.get(i) );
//				aDataBlock = affectedDataBlocks.get(i);
//				aDataBlock.saveViewerData();
//			}
//			Debug.out.printf("Processd %d out of %d data units at " + mapPoint + "\n", unitsChanged, totalUnits);
//			commitDatabase();
//		}
	}
	








}