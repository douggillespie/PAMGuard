package group3dlocaliser.algorithm.hyperbolic;

import PamController.SettingsPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;

import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamHBox;
import pamViewFX.fxNodes.PamSpinner;
import pamViewFX.fxNodes.utilityPanes.PamToggleSwitch;

/**
 * Hyperbolic settings pane. 
 * @author Jamie Macaulay
 *
 */
public class HyperbolicSettingsPane extends SettingsPane<HyperbolicParams> {
	
	PamBorderPane mainPane;
	
	/**
	 * The toggle switch. 
	 */
	private PamToggleSwitch hyperbolicToggleSwitch;

	private PamSpinner<Integer> numIterations;

	public HyperbolicSettingsPane() {
		super(null);
		mainPane = new PamBorderPane(); 
		mainPane.setPadding(new Insets(5,5,5,5));
		
		hyperbolicToggleSwitch = new PamToggleSwitch("Calculate errors"); 
		hyperbolicToggleSwitch.selectedProperty().addListener((obsVal, oldVal, newVal)->{
			numIterations.setDisable(!hyperbolicToggleSwitch.isSelected());
		});

		numIterations = new PamSpinner<Integer>(0,1000,100,1); 
		numIterations.setDisable(!hyperbolicToggleSwitch.isSelected());
		numIterations.setEditable(true);
		
		PamHBox hBox = new PamHBox( ); 
		hBox.setSpacing(5);
		hBox.getChildren().addAll(hyperbolicToggleSwitch, numIterations, new Label("Iterations")); 
		
		mainPane.setTop(hBox);
		
	}
	
	

	@Override
	public HyperbolicParams getParams(HyperbolicParams currParams) {
		currParams.bootStrapN = numIterations.getValue();
		currParams.calcErrors = hyperbolicToggleSwitch.isSelected();
		
		return currParams;
	}

	@Override
	public void setParams(HyperbolicParams input) {
		numIterations.getValueFactory().setValue(input.bootStrapN);
		hyperbolicToggleSwitch.setSelected(input.calcErrors);
	}

	@Override
	public String getName() {
		return "Hyperbolic parameters";
	}

	@Override
	public Node getContentNode() {
		return mainPane;
	}

	@Override
	public void paneInitialized() {
		// TODO Auto-generated method stub
		
	}

}
