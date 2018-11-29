package controllers;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.algorithms.AlgorithmModel;
import models.algorithms.Random;
import models.algorithms.SquareDiamond;

public class FXMLController {
	
	// Naming Convention where_what_why
	
	@FXML
    private MenuItem main_menu_run_run;
	
	@FXML
	private MenuButton main_menu_btn_algorithm_list;
	
	@FXML
	private VBox main_vbox_random;
		
	@FXML
	private TextField random_vbox_text_field_size;
	
	@FXML
	private TextField random_vbox_text_field_min;
	
	@FXML
	private TextField random_vbox_text_field_max;
	
	@FXML
	private VBox main_vbox_square_diamond;
	
	@FXML
	private ImageView main_image_view_map;
	
	@FXML
	private Button main_button_cancel;
	
	@FXML
	private Label main_label_status;
	
	@FXML
	private ProgressBar main_progress_bar_progress_bar;
	
	private TextField[] numericTextFields;
	
	StringProperty algorithmName = new SimpleStringProperty();
	AlgorithmModel algo;
	
	public void initialize() {
		numericTextFields = new TextField[] {random_vbox_text_field_size, random_vbox_text_field_min, random_vbox_text_field_max};
		setNumericFields();
    }
	
	@FXML
    void menuRun(ActionEvent event) {
		int size = 0;
				
		//Checking size != 0
		size = Integer.valueOf(random_vbox_text_field_size.getText());
		if (size == 0) {
			alertDialog("Error in parameters", "Argument size cannot be 0.", "To generate a Map, you must enter a positive integer in the size field.");
			return;
		}
		switch (algorithmName.getValueSafe()) {
			case "Random":
				
				//Check min>max
				int min = Integer.valueOf(random_vbox_text_field_min.getText());
				int max = Integer.valueOf(random_vbox_text_field_max.getText());
				if (min>max) {
					alertDialog("Error in parameters", "Argument min is greater than max.", "To generate a Map, you must enter 2 value : min and max.");
					return;
				}
				
				algo = new Random(size);
				Map <String, String> hm = new HashMap<String, String>();
				hm.put("min", String.valueOf(min));
				hm.put("max", String.valueOf(max));
				algo.setParameters(hm);
				break;
			case "Square Diamond":
				algo = new SquareDiamond(129);
				break;
			default:
					alertDialog("Error unknown Algorithm", "Algorithm you have selected is not recognized.", "You must select one algorithm from the menu list.");
				return;
		}
		
		main_progress_bar_progress_bar.progressProperty().bind(algo.progressProperty());
		algo.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
			    new EventHandler<WorkerStateEvent>() {
			    @Override
			    public void handle(WorkerStateEvent t) {
			    	main_image_view_map.setImage(algo.getValue());
			    }
			});
		Thread t = new Thread(algo);
		t.start();
    }
	
	@FXML
    void cancelTask(ActionEvent event) {
		if (algo != null)
			algo.cancel();
		main_progress_bar_progress_bar.progressProperty().unbind();
		main_progress_bar_progress_bar.setProgress(0.0);
    }
	
	@FXML
    private void setRandomMenuItem(ActionEvent event) {
		algorithmName.setValue("Random");
		main_menu_btn_algorithm_list.setText(algorithmName.getValueSafe());
		main_vbox_square_diamond.setVisible(false);
		main_vbox_random.setVisible(true);
    }
	
	@FXML
	private void setSquareDiamondMenuItem(ActionEvent event) {
		algorithmName.setValue("Square Diamond");
		main_menu_btn_algorithm_list.setText(algorithmName.getValueSafe());
		main_vbox_random.setVisible(false);
		main_vbox_square_diamond.setVisible(true);
	}
	
	private void alertDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	private void setNumericFields() {
		for (TextField tf : numericTextFields) {
			tf.textProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue, 
			        String newValue) {
			        if (!newValue.matches("\\d")) {
			        	tf.setText(newValue.replaceAll("[^\\d]", ""));
			        }
			        //Avoid buffer overflow
			        if (tf.getText().length() > 9) {
		                String s = tf.getText().substring(0, 9);
		                tf.setText(s);
		            }
			    }
			});
		}
	}
}