package Apps;

import Database.EvoAlgorithem;
import Main.EEException;
import Main.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class JavaFXTimeTable extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, EEException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/Main/Main.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        MainController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root, 904, 670);
        scene.getStylesheets().add(getClass().getResource(CommonResourcePaths.MAIN_DEFAULT_CSS).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Evolutionary Time Table Generator!");
        primaryStage.show();
    }
}