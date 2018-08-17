import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
            BorderPane root = (BorderPane) loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("FLIR A65 Image viewer");
            primaryStage.setScene(scene);
            primaryStage.show();
            MainController controller = loader.getController();
            controller.initSceneListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


}
