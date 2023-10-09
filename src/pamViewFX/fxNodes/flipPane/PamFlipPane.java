package pamViewFX.fxNodes.flipPane;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import pamViewFX.PamGuiManagerFX;
import pamViewFX.fxGlyphs.PamGlyphDude;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.PamButton;
import pamViewFX.fxNodes.PamHBox;

/**
 * Flip pane which has is supposed to be used for advanced settings. The front
 * of the pane is to be used for standard settings and the pane can then flip to
 * the back to show advanced settings. A back button is included in the back
 * pane which can be used to flip the pane back to the standard settings. The
 * front pane needs a control that calls flipPane.toBack() to show the advanced
 * pane.
 * 
 * @author Jamie Macaulay
 *
 */
public class PamFlipPane extends FlipPane {
	
	public static final double FLIP_TIME =250; //milliseconds - the default is 700ms whihc is way too long. 

	private PamBorderPane advPane;
	
	private PamBorderPane frontPane;
	
	private Label advLabel;

	private PamButton backButton;
	

	public PamFlipPane() {
		super();
		this.advPane = createAdvSettingsPane();
		this.getFront().getChildren().add(frontPane = new PamBorderPane()); 
		
//		this.getFront().setStyle("-fx-background-color: grey;");
//		this.getBack().setStyle("-fx-background-color: grey;");

		this.getBack().getChildren().add(advPane); 
		this.setFlipTime(FLIP_TIME);

	}

	public PamFlipPane(Orientation FLIP_DIRECTION) {
		super(FLIP_DIRECTION);
		this.advPane = createAdvSettingsPane();
		this.getFront().getChildren().add(frontPane = new PamBorderPane()); 
		this.getBack().getChildren().add(advPane); 
		this.setFlipTime(FLIP_TIME);
	}
	
	/**
	 * Get the front pane. 
	 * @return the front pane. 
	 */
	public PamBorderPane getFrontPane() {
		return frontPane;
	}
	
	/**
	 * Get the back pane. 
	 * @return the back pane. 
	 */
	public PamBorderPane getBackPane() {
		return advPane;
	}

	/**
	 * Convenience duplicate of getBackPane(). 
	 * @return the back pane. 
	 */
	public PamBorderPane getAdvPane() {
		return advPane;
	}
	
	
	/**
	 * Set the advanced pane content. 
	 * @param - the content to set. 
	 */
	public void setAdvPaneContent(Node content) {
		 advPane.setCenter(content);
	}
	
	/**
	 * Set the front pane content. 
	 * @param - the content to set. 
	 */
	public void setFrontContent(Node content) {
		frontPane.setCenter(content);
	}


	/**
	 * Create the advanced settings pane which can be accessed by DAQ panes if needed. 
	 */
	private PamBorderPane createAdvSettingsPane() {
		
		backButton = new PamButton(); 
		backButton.setGraphic(PamGlyphDude.createPamIcon("mdi2c-chevron-left", Color.WHITE, PamGuiManagerFX.iconSize));
		//backButton.setStyle("-fx-padding: 0,0,0,0");
		
		backButton.setOnAction((action)->{
		//	System.out.println("FLIP BACK TO FRONT"); 
			this.flipToFront(); 
		});
		
		backButton.setStyle("-fx-background-radius: 0 5 5 0; -fx-border-radius: 0 5 5 0;");

		//backButton.setPrefWidth(150);
		
		PamBorderPane advPane = new PamBorderPane(); 
		//advPane.setPadding(new Insets(5,5,5,5));
		
		PamHBox buttonHolder = new PamHBox(); 
		
		buttonHolder.setBackground(null);
		//buttonHolder.setStyle("-fx-background-color: red;");
		buttonHolder.setAlignment(Pos.CENTER_LEFT);
		buttonHolder.getChildren().addAll(backButton, advLabel = new Label("Adv. Settings")); 

		advLabel.setAlignment(Pos.CENTER);
		advLabel.setMaxWidth(Double.MAX_VALUE); //need to make sure label is in center. 
		PamGuiManagerFX.titleFont2style(advLabel);
		
		advLabel.setAlignment(Pos.CENTER);
		HBox.setHgrow(advLabel, Priority.ALWAYS);
		
		advPane.setTop(buttonHolder);
		
		return advPane; 
		
	}
	

	public Label getAdvLabel() {
		return advLabel;
	}

//	public void setAdvLabel(Label advLabel) {
//		this.advLabel = advLabel;
//	}

	public PamButton getBackButton() {
		return backButton;
	}

	/**
	 * True if the flip pane is showing the front. 
	 */
	public boolean isShowingFront() {
		return super.flipFrontProperty().get();
	}
}
