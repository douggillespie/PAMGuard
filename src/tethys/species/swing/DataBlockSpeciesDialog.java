package tethys.species.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamController.PamController;
import PamView.PamGui;
import PamView.dialog.PamDialog;
import PamView.panel.PamNorthPanel;
import PamguardMVC.PamDataBlock;
import tethys.species.SpeciesMapManager;

public class DataBlockSpeciesDialog extends PamDialog {

	private static final long serialVersionUID = 1L;

	private DataBlockSpeciesPanel speciesPanel;
	
	private DataBlockSpeciesDialog(Window parentFrame, PamDataBlock dataBlock) {
		super(parentFrame, dataBlock.getDataName() +  " species", false);
		JPanel mainPanel = new JPanel(new BorderLayout());
		speciesPanel = new DataBlockSpeciesPanel(dataBlock);
		mainPanel.add(BorderLayout.CENTER, speciesPanel.getDialogComponent());
		
		JButton itisButton = new JButton("Go to ITIS web site");
		itisButton.setToolTipText("Go to ITIS website to search for species codes");
		itisButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gotoITIS();
			}
		});
		JPanel nPanel = new JPanel(new BorderLayout());
		nPanel.setBorder(new TitledBorder("Code management"));
		nPanel.add(BorderLayout.EAST, new PamNorthPanel(itisButton));
		String otherMsg = 
				"<html>Specify an ITIS taxonomic serial number (coding)."
				+ "<br>Press the Find button to look up TSNs by Latin or common name.  "
				+ "<br>Anthropogenic signals should be coded as Homo sapiens (180092). "
				+ "<br>Noise Measurements and geophonic sounds should be coded as " 
				+ "\"Other Phenomena\" (-10).  "
				+ "<br>When known, a call or sound type should "
				+ "be specified (see help for more information).</html>";
		nPanel.add(BorderLayout.CENTER, new JLabel(otherMsg , JLabel.LEFT));		
		
		mainPanel.add(BorderLayout.NORTH, nPanel);
		setDialogComponent(mainPanel);
		setResizable(true);
		setHelpPoint("utilities.tethys.docs.tethys_speciescodes");
	}
	
	protected void gotoITIS() {
		PamGui.openURL("https://www.itis.gov");
	}

	public static void showDialog(Window parentFrame, PamDataBlock dataBlock) {
		DataBlockSpeciesDialog speciesDialog = new DataBlockSpeciesDialog(parentFrame, dataBlock);
		speciesDialog.setParams();
		speciesDialog.setVisible(true);
	}

	private void setParams() {
		speciesPanel.setParams();
	}

	@Override
	public boolean getParams() {
		return speciesPanel.getParams();
	}

	@Override
	public void cancelButtonPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub

	}

}
