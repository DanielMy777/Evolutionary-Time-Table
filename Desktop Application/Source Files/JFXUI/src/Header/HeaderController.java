package Header;

import Apps.CommonResourcePaths;
import Database.EvoLoader;
import Database.TTLoader;
import Database.TTRule;
import Main.Controller;
import Main.EEException;
import Main.MainController;
import TTSolution.TTException;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.Console;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class HeaderController implements Controller {

    public enum messageColor{
        OK,
        Error,
        Warning
    }

    private MainController mainController;
    private SimpleBooleanProperty isAnimation;
    private Stage primaryStage;
    private SimpleStringProperty selectedFileProperty;
    private SimpleBooleanProperty isFileSelected;

    @FXML
    private Menu Logo;

    @FXML
    private Menu AnimationsMenu;

    @FXML
    private MenuItem EnableAnimationsBtn;

    @FXML
    private MenuItem DisableAnimationsBtn;

    @FXML
    private Menu ThemesMenu;

    @FXML
    private MenuItem DefaultThemeBtn;

    @FXML
    private MenuItem DarkThemeBtn;

    @FXML
    private MenuItem HappyThemeBtn;

    @FXML
    private Menu HelpMenu;

    @FXML
    private MenuItem EasterEggBtn;

    @FXML
    private Button LoadXMLBtn;

    @FXML
    private Label FileLbl;

    @FXML
    private Label MessageLbl;

    @FXML
    private ToggleButton SoundBtn;

    @FXML
    private ImageView SoundImg;

    public HeaderController()
    {
        isFileSelected = new SimpleBooleanProperty(false);
        isAnimation = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty("");
    }

    @FXML
    void initialize()
    {
        FileLbl.textProperty().bind(Bindings.concat("File: ", selectedFileProperty));
    }

    @FXML
    void ClickedAnimations(ActionEvent event) {

    }

    @FXML
    void ClickedHelpMenu(ActionEvent event) {
        mainController.triggerMusic(true);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void ClickedLoadXML(ActionEvent event) throws TTException, EEException {
        if(isFileSelected.get())
        {
            clearAllData();
        }
        else
        {
            loadFromXML();
        }

    }

    private void loadFromXML() throws TTException, EEException {
        boolean isValid;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select words file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        selectedFileProperty.set(absolutePath);
        isValid = mainController.loadDatabaseInfoFromXML(absolutePath) || isFileSelected.get();
        isFileSelected.set(isValid);
        if(isValid)
            LoadXMLBtn.setText("Clear All");
    }

    private void clearAllData()
    {
        mainController.clearAllProgressData();
        isFileSelected.set(false);
        selectedFileProperty.set("");
        LoadXMLBtn.setText("Load XML");
        setMessage("All Data Cleared...", messageColor.Warning);
    }

    public void setMessage(String message, messageColor color)
    {
        Platform.runLater(() -> {
            if(isAnimation.get()) {fadeOutMessage();}
            MessageLbl.setText("Message: " + message);
            switch (color)
            {
                case OK:
                    MessageLbl.setTextFill(Paint.valueOf("GREEN"));
                    break;
                case Error:
                    MessageLbl.setTextFill(Paint.valueOf("RED"));
                    break;
                case Warning:
                    MessageLbl.setTextFill(Paint.valueOf("ORANGE"));
                    break;
            }
            if(isAnimation.get()) {fadeInMessage();}
        });
    }

    public Label getMessageLbl()
    {
        return MessageLbl;
    }

    private void fadeOutMessage()
    {
        FadeTransition fadeTransitionDown = new FadeTransition(Duration.seconds(0.2), MessageLbl);
        fadeTransitionDown.setFromValue(1.0);
        fadeTransitionDown.setToValue(0.0);
        fadeTransitionDown.play();
    }

    private void fadeInMessage()
    {
        FadeTransition fadeTransitionUp = new FadeTransition(Duration.seconds(0.2), MessageLbl);
        fadeTransitionUp.setFromValue(0.0);
        fadeTransitionUp.setToValue(1.0);
        fadeTransitionUp.play();
    }

    public SimpleBooleanProperty getFileSelectedProperty()
    {
        return isFileSelected;
    }

    @FXML
    void ClickedThemes(ActionEvent event) {

    }

    @FXML
    void DisableAnimations(ActionEvent event) {
        isAnimation.set(false);
        DisableAnimationsBtn.setText(DisableAnimationsBtn.getText().split("<")[0] + "<");
        EnableAnimationsBtn.setText(EnableAnimationsBtn.getText().split("<")[0]);
    }

    @FXML
    void EnableAnimations(ActionEvent event) {
        isAnimation.set(true);
        EnableAnimationsBtn.setText(EnableAnimationsBtn.getText().split("<")[0] + "<");
        DisableAnimationsBtn.setText(DisableAnimationsBtn.getText().split("<")[0]);
    }

    @FXML
    void ShowEasterEgg(ActionEvent event) {

    }

    @FXML
    void SwitchToDarkTheme(ActionEvent event) {
        mainController.changeStyle(MainController.eStyle.Dark);
        DarkThemeBtn.setText(DarkThemeBtn.getText().split("<")[0] + "<");
        HappyThemeBtn.setText(HappyThemeBtn.getText().split("<")[0]);
        DefaultThemeBtn.setText(DefaultThemeBtn.getText().split("<")[0]);
    }

    @FXML
    void SwitchToDefaultTheme(ActionEvent event) {
        mainController.changeStyle(MainController.eStyle.Default);
        DefaultThemeBtn.setText(DefaultThemeBtn.getText().split("<")[0] + "<");
        HappyThemeBtn.setText(HappyThemeBtn.getText().split("<")[0]);
        DarkThemeBtn.setText(DarkThemeBtn.getText().split("<")[0]);
    }

    @FXML
    void SwitchToHappyTheme(ActionEvent event) {
        mainController.changeStyle(MainController.eStyle.Happy);
        HappyThemeBtn.setText(HappyThemeBtn.getText().split("<")[0] + "<");
        DefaultThemeBtn.setText(DefaultThemeBtn.getText().split("<")[0]);
        DarkThemeBtn.setText(DarkThemeBtn.getText().split("<")[0]);
    }

    @FXML
    void ToggleSound(ActionEvent event) {
        if(!SoundBtn.isSelected())
        {
            mainController.triggerMusic(false);
            SoundImg.setImage(new Image(CommonResourcePaths.SOUND_ON_IMG));
        }
        else
        {
            mainController.cutMusic();
            SoundImg.setImage(new Image(CommonResourcePaths.SOUND_OFF_IMG));
        }
    }

    public boolean isSoundOn()
    {
        return !SoundBtn.isSelected();
    }

    public boolean isAnimation()
    {
        return isAnimation.get();
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }
}