import javafx.scene.paint.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button ;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;


public class MainController {

	public static final int IMAGE_WIDTH = 640, IMAGE_HEIGHT = 512;

	@FXML private BorderPane mainBorderPane;
	@FXML private ImageView mainImageView;
	@FXML private Canvas mainImageCanvas;
	@FXML private Button previousButton, nextButton, saveButton, inputFolderButton, outputFolderButton;
	@FXML private TextField inputFolderText, outputFolderText;
	@FXML private Label fileNameLabel, filesCountLabel;
	@FXML private Spinner<Integer> offsetSpinner;
	@FXML private ListView<Category> categoryListView;
	@FXML private ListView<Path> filesListView;
	
	private InMemoryFiles imf;

	public MainController() {
	}
	
	 @FXML 
	 public void initialize() {
		 initListView();
		 filesListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			 if (imf != null && imf.getFilePaths() != null && newValue.intValue() != -1) {
				try {
					Image img = ImageConversions.convertBinaryToImage(Files.readAllBytes(imf.getFilePaths().get(newValue.intValue())), IMAGE_WIDTH, IMAGE_HEIGHT);
					Utils.updateFXControl(mainImageView.imageProperty(), img);
					imf.setFileIndex(newValue.intValue());
					filesCountLabel.setText(String.valueOf(imf.getFilePaths().size()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			 
		});
		 inputFolderText.textProperty().addListener((observable, oldValue, newValue) -> {
			 imf = new InMemoryFiles(newValue); 
			 if (imf.getFilePaths().size() > 0) {
				 try {
					Image img = ImageConversions.convertBinaryToImage(Files.readAllBytes(imf.getFilePaths().get(0)), IMAGE_WIDTH, IMAGE_HEIGHT);
					Utils.updateFXControl(mainImageView.imageProperty(), img);
					filesListView.setItems(FXCollections.observableArrayList(imf.getFilePaths()));
					filesListView.getSelectionModel().select(0);
				} catch (IOException e) {
					e.printStackTrace();
				}				 
			 }
		});
				 
	 }
	 
	 @FXML 
	 public void selectFolder(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();

		File file = directoryChooser.showDialog(mainBorderPane.getScene().getWindow());

		if (file != null) {
			Button source = (Button) event.getSource();
			if (source.equals(inputFolderButton)) inputFolderText.setText(file.getAbsolutePath());
			else if (source.equals(outputFolderButton)) outputFolderText.setText(file.getAbsolutePath());
		}
	 }
	 
	 @FXML
	 public void nextButtonClicked() throws IOException {
		 if (imf != null && imf.getFilePaths() != null) {
			Image img = ImageConversions.convertBinaryToImage(Files.readAllBytes(imf.getNext()), IMAGE_WIDTH, IMAGE_HEIGHT);
			Utils.updateFXControl(mainImageView.imageProperty(), img);
			refreshListView();
		 }
	 }
	 
	 @FXML
	 public void prevButtonClicked() throws IOException {
		 if (imf != null && imf.getFilePaths() != null) {
			Image img = ImageConversions.convertBinaryToImage(Files.readAllBytes(imf.getPrev()), IMAGE_WIDTH, IMAGE_HEIGHT);
			Utils.updateFXControl(mainImageView.imageProperty(), img);
			refreshListView();
		 }
	 }
	 
	 @FXML
	 public void saveButtonClicked() {
		 if (imf != null && imf.getFilePaths() != null && imf.getFilePaths().size() > 0 && !Utils.isNullOrEmpty(outputFolderText.getText()) && categoryListView.getSelectionModel().getSelectedItem() != null) {
			 Path dirPath = Paths.get(outputFolderText.getText(), categoryListView.getSelectionModel().getSelectedItem().getPath());
			 Path filePath = Paths.get(outputFolderText.getText(), categoryListView.getSelectionModel().getSelectedItem().getPath(), imf.getActualFile().getFileName().toString());
			 saveFile(dirPath, filePath);			 
		 }
	 }
	 
	 private void saveFile(Path dirPath, Path filePath) {
		 try {			
			 Files.createDirectories(dirPath);
			 if (!Files.exists(filePath)) {
				 Files.createFile(filePath);
				 Files.write(filePath, Files.readAllBytes(imf.getActualFile()), StandardOpenOption.CREATE);			 
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	 private void deleteActualFile() {
		 try {
			Files.delete(imf.removeActualFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		 filesListView.setItems(FXCollections.observableArrayList(imf.getFilePaths()));
		 refreshListView();	
	 }
	 
	 private void refreshListView() {
		filesListView.getSelectionModel().select(imf.getFileIndex());
		filesListView.scrollTo(imf.getFileIndex());
	 }
	 
	 private void initListView() {
		 ObservableList<Category> items = FXCollections.observableArrayList (new Category("Background", "background"), new Category("Hand with product", "hand_product"),  new Category("Empty hand", "empty_hand"), new Category("Hand in shelf", "hand_in_shelf") );
		 categoryListView.setItems(items);
	 }
	 
	 public void initSceneListener() {
		 mainBorderPane.getScene().setOnKeyPressed((event) -> {
				try {
					Path dirPath;
					Path filePath;
		            switch (event.getCode()) {
		                case DIGIT1: 
		                	dirPath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(0).getPath());
		       			 	filePath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(0).getPath(), imf.getActualFile().getFileName().toString());
		       			 	saveFile(dirPath, filePath);
							break;
		                case DIGIT2: 
		                	dirPath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(1).getPath());
		       			 	filePath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(1).getPath(), imf.getActualFile().getFileName().toString());
		       			 	saveFile(dirPath, filePath);
		                	break;
		                case DIGIT3:  
		                	dirPath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(2).getPath());
		       			 	filePath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(2).getPath(), imf.getActualFile().getFileName().toString());
		       			 	saveFile(dirPath, filePath);
		                	break;
		                case DIGIT4:   
		                	dirPath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(3).getPath());
		       			 	filePath = Paths.get(outputFolderText.getText(), categoryListView.getItems().get(3).getPath(), imf.getActualFile().getFileName().toString());
		       			 	saveFile(dirPath, filePath);
		                	break;
		                case DIGIT5:

		                	break;
		                case DIGIT6: 
		                	
		                	break;
		                case DIGIT7: 

		                	break;
		                case DIGIT8:

		                	break;
		                case DIGIT9: 

		                	break;
		                case LEFT: 
		                	prevButtonClicked();
		                	break;
		                case RIGHT: 
		                	nextButtonClicked();
		                	break;
		                case D:
		                	deleteActualFile();
		                	break;

		                default: break;
		            }  
				} catch (Exception e) {
					System.out.print("Cannot select with key " + event.getCode());
				}
	       });
	 }

}
