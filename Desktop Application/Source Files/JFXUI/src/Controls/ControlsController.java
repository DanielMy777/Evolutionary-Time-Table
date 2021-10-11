package Controls;

import Header.HeaderController;
import Main.Controller;
import Main.EEException;
import Main.MainController;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ControlsController implements Controller {

    private MainController mainController;
    private AlgorithemProgress currentProgress;

    private SimpleIntegerProperty currentGen;
    private SimpleFloatProperty currentBestFit;
    private SimpleBooleanProperty isAlgStopped;
    private SimpleBooleanProperty isAlgPaused;
    private SimpleBooleanProperty isAlgRunning;

    @FXML
    private Button StartBtn;

    @FXML
    private Button PauseBtn;

    @FXML
    private Button StopBtn;

    @FXML
    private Button NextGenBtn;

    @FXML
    private RadioButton GenerationsRB;

    @FXML
    private TextField GenerationsTF;

    @FXML
    private RadioButton FitnessRB;

    @FXML
    private TextField FitnessTF;

    @FXML
    private RadioButton DurationRB;

    @FXML
    private Spinner<LocalTime> TimeSpinner;

    @FXML
    private TextField GenerationJmpTF;

    @FXML
    private Label GenerationsLbl;

    @FXML
    private Label BestFitnessLbl;

    @FXML
    private Label GenerationsProgressLbl;

    @FXML
    private Label FitnessProgressLbl;

    @FXML
    private Label DurationProgressLbl;

    @FXML
    private ProgressBar GenerationsProgressBar;

    @FXML
    private ProgressBar FitnessProgressBar;

    @FXML
    private ProgressBar DurationProgressBar;

    @FXML
    private ImageView ThemeImg;

    public ControlsController()
    {
        isAlgStopped = new SimpleBooleanProperty(true);
        isAlgPaused = new SimpleBooleanProperty(false);
        isAlgRunning = new SimpleBooleanProperty(false);
        currentGen = new SimpleIntegerProperty(0);
        currentBestFit = new SimpleFloatProperty(0);
    }

    public void initialize()
    {

        buildTimeSpinner();
        BestFitnessLbl.textProperty().bind(currentBestFit.asString());
        GenerationsLbl.textProperty().bind(currentGen.asString());
        StartBtn.disableProperty().bind(isAlgRunning);
        bindStartButtonToPause();
        PauseBtn.disableProperty().bind(isAlgRunning.not());
        StopBtn.disableProperty().bind(isAlgStopped);
        NextGenBtn.disableProperty().bind(isAlgRunning);
        GenerationsProgressBar.visibleProperty().bind(GenerationsRB.selectedProperty());
        FitnessProgressBar.visibleProperty().bind(FitnessRB.selectedProperty());
        DurationProgressBar.visibleProperty().bind(DurationRB.selectedProperty());
        GenerationsProgressLbl.visibleProperty().bind(GenerationsRB.selectedProperty());
        FitnessProgressLbl.visibleProperty().bind(FitnessRB.selectedProperty());
        DurationProgressLbl.visibleProperty().bind(DurationRB.selectedProperty());
        GenerationsRB.disableProperty().bind(isAlgRunning.or(isAlgPaused));
        FitnessRB.disableProperty().bind(isAlgRunning.or(isAlgPaused));
        DurationRB.disableProperty().bind(isAlgRunning.or(isAlgPaused));
        GenerationsTF.disableProperty().bind(isAlgRunning.or(isAlgPaused));
        FitnessTF.disableProperty().bind(isAlgRunning.or(isAlgPaused));
        TimeSpinner.disableProperty().bind(isAlgRunning.or(isAlgPaused));
        GenerationJmpTF.disableProperty().bind(isAlgRunning.or(isAlgPaused));
    }

    private void bindStartButtonToPause()
    {
        isAlgPaused.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    StartBtn.setText("Resume");
                else
                    StartBtn.setText("Start");
            }
        });
    }

    public void bindHeaderAndControls()
    {
        StartBtn.disableProperty().bind(isAlgRunning.or(mainController.GetFileSelectedProperty().not()));
        NextGenBtn.disableProperty().bind(isAlgRunning.or(mainController.GetFileSelectedProperty().not()));
    }

    private void bindProgressBars()
    {
        int generations = GenerationsRB.selectedProperty().get() ? Integer.parseInt(GenerationsTF.getText()) : AlgorithemProgress.NO_PREFERENCE;
        float fitness = FitnessRB.selectedProperty().get() ? Float.parseFloat(FitnessTF.getText()) : AlgorithemProgress.NO_PREFERENCE;;
        long time = DurationRB.selectedProperty().get() ? TimeSpinner.getValue().toSecondOfDay() : (long)AlgorithemProgress.NO_PREFERENCE;
        currentProgress = new AlgorithemProgress(generations, fitness, time);
        GenerationsProgressBar.progressProperty().bind(currentProgress.generationsProgressProperty());
        FitnessProgressBar.progressProperty().bind(currentProgress.fitnessProgressProperty());
        DurationProgressBar.progressProperty().bind(currentProgress.durationsProgressProperty());
        GenerationsProgressLbl.textProperty().bind(Bindings.concat("Generations Progress: ", Bindings.format("%.1f",currentProgress.generationsProgressProperty().multiply(100)),"%"));
        FitnessProgressLbl.textProperty().bind(Bindings.concat("Fitness Progress: ", Bindings.format("%.1f",currentProgress.fitnessProgressProperty().multiply(100)),"%"));
        DurationProgressLbl.textProperty().bind(Bindings.concat("Duration Progress: ", Bindings.format("%.1f",currentProgress.durationsProgressProperty().multiply(100)),"%"));
        this.mainController.setProgressObject(currentProgress);
    }

    private boolean checkBoxesForValidInput()
    {
        boolean valid = false;
        int generations, genJump;
        float fitness;
        try {
            genJump = Integer.parseInt(GenerationJmpTF.getText());
            if(genJump <= 0)
                throw new NumberFormatException();
            mainController.setGenerationJump(genJump);
        } catch (NumberFormatException e)
        {
            mainController.setHeaderMessage("Generation jump must be a valid positive integer", HeaderController.messageColor.Error);
            highlightTexbox(GenerationJmpTF);
            return false;
        }
        if(GenerationsRB.selectedProperty().get()) {
            try {
                generations = Integer.parseInt(GenerationsTF.getText());
            } catch (NumberFormatException e) {
                mainController.setHeaderMessage("Number of generations must be a valid integer", HeaderController.messageColor.Error);
                highlightTexbox(GenerationsTF);
                return false;
            }
            if (generations < 101) {
                mainController.setHeaderMessage("Number of generations must be over 100", HeaderController.messageColor.Warning);
                highlightTexbox(GenerationsTF);
                return false;
            }
            if(generations < genJump)
            {
                mainController.setHeaderMessage("Generation jump must be lower than total amount of generations", HeaderController.messageColor.Warning);
                highlightTexbox(GenerationsTF);
                highlightTexbox(GenerationJmpTF);
                return false;
            }
        }
        if(FitnessRB.selectedProperty().get()) {
            try {
                fitness = Float.parseFloat(FitnessTF.getText());
            } catch (NumberFormatException e) {
                mainController.setHeaderMessage("Fitness must be a valid decimal number", HeaderController.messageColor.Error);
                highlightTexbox(FitnessTF);
                return false;
            }
            if (fitness < 0 || fitness > 100) {
                mainController.setHeaderMessage("Fitness must be between 0 - 100", HeaderController.messageColor.Warning);
                highlightTexbox(FitnessTF);
                return false;
            }
        }
        if(DurationRB.selectedProperty().get())
        {
            if(TimeSpinner.getValue() == null || TimeSpinner.getValue() == LocalTime.MIDNIGHT)
            {
                mainController.setHeaderMessage("Please enter a valid duration hh:mm:ss (Did you forget to press 'Enter'?)", HeaderController.messageColor.Error);
                highlightSpinner(TimeSpinner);
                return false;
            }
        }
        if(FitnessRB.selectedProperty().get() || GenerationsRB.selectedProperty().get() || DurationRB.selectedProperty().get()) {
            return true;
        }
        else
        {
            mainController.setHeaderMessage("Please select a termination condition!", HeaderController.messageColor.Error);
            highlightRadioButtons();
            return false;
        }
    }

    private void highlightTexbox(TextField tf)
    {
        tf.getStyleClass().add("error");
    }

    private void highlightSpinner(Spinner s)
    {
        s.getStyleClass().add("error");
    }

    private void highlightRadioButtons()
    {
        GenerationsRB.getStyleClass().add("error");
        FitnessRB.getStyleClass().add("error");
        DurationRB.getStyleClass().add("error");
    }

    private void clearHighlights()
    {
        GenerationsRB.getStyleClass().remove("error");
        FitnessRB.getStyleClass().remove("error");
        DurationRB.getStyleClass().remove("error");
        TimeSpinner.getStyleClass().remove("error");
        GenerationsTF.getStyleClass().remove("error");
        FitnessTF.getStyleClass().remove("error");
        TimeSpinner.getStyleClass().remove("error");
        GenerationJmpTF.getStyleClass().remove("error");

    }

    @FXML
    void ClickedDurationRB(ActionEvent event) {

    }

    @FXML
    void ClickedFitnessRB(ActionEvent event) {

    }

    @FXML
    void ClickedGenerationsRB(ActionEvent event) {

    }

    @FXML
    void GenerateNextGeneration(ActionEvent event) throws InterruptedException, EEException {
        mainController.StepSingleGeneration();
        if(isAlgRunning.get()) {
            isAlgRunning.set(false);
            isAlgStopped.set(false);
            isAlgPaused.set(true);
        }
    }

    @FXML
    public void PauseAlgorithm(ActionEvent event) throws InterruptedException {
        isAlgRunning.set(false);
        isAlgStopped.set(false);
        isAlgPaused.set(true);
        mainController.setHeaderMessage("Process Paused", HeaderController.messageColor.Warning);
    }

    @FXML
    public void StartAlgorithm(ActionEvent event) throws EEException {
        clearHighlights();
        if(isAlgStopped.get()) {
            if (checkBoxesForValidInput()) {
                isAlgRunning.set(true);
                isAlgStopped.set(false);
                isAlgPaused.set(false);
                bindProgressBars();
                UIAdapter newUIAdapter = new UIAdapter(currentGen::set, currentBestFit::set, currentProgress);
                mainController.beginAlgorithemProcess(newUIAdapter);
                Platform.runLater(() -> {
                    GenerationsLbl.setTextFill(Paint.valueOf("GREEN"));
                    BestFitnessLbl.setTextFill(Paint.valueOf("GREEN"));
                });
            }
        }
        else
        {
            isAlgRunning.set(true);
            isAlgStopped.set(false);
            isAlgPaused.set(false);
            mainController.unPauseAlgorithemProcess();
        }
    }

    @FXML
    public void StopAlgorithm(ActionEvent event) {
        Platform.runLater(() -> {
            isAlgRunning.set(false);
            isAlgStopped.set(true);
            isAlgPaused.set(false);
            GenerationsLbl.setTextFill(Paint.valueOf("RED"));
            BestFitnessLbl.setTextFill(Paint.valueOf("RED"));
        });
        mainController.stopAlgorithemProcess();
    }

    public void StopIfRunning()
    {
        if(isAlgPaused.get() || isAlgRunning.get())
            StopAlgorithm(null);
    }

    public boolean getAlgPaused()
    {
        return isAlgPaused.get();
    }

    public SimpleBooleanProperty getAlgPausedProp() {return isAlgPaused;}

    public SimpleBooleanProperty getAlgStoppedProp() {return isAlgStopped;}

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    private void buildTimeSpinner()
    {
        TimeSpinner.setValueFactory(new SpinnerValueFactory() {
            {
                //setConverter(new LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm:ss"), null));
                setConverter(new StringConverter<LocalTime>() {

                    @Override
                    public String toString(LocalTime object) {
                        return object.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    }

                    @Override
                    public LocalTime fromString(String string) {
                        String[] hms = string.split(":");
                        if (hms.length != 3)
                            return LocalTime.MIDNIGHT;
                        else {
                            try {
                                int hours = Integer.parseInt(hms[0]);
                                int minutes = Integer.parseInt(hms[1]);
                                int seconds = Integer.parseInt(hms[2]);
                                if (hours < 0 || hours > 24)
                                    throw new ParseException("Hours must be 0 - 24!");
                                if (minutes < 0 || minutes > 59)
                                    throw new ParseException("Minutes must be 0-59!");
                                if (seconds < 0 || seconds > 59)
                                    throw new ParseException("Seconds must be 0-59!");
                                LocalTime lt = LocalTime.MIDNIGHT;
                                lt = lt.withSecond(seconds);
                                lt = lt.withMinute(minutes);
                                lt = lt.withHour(hours);
                                return lt;
                            } catch (Exception e) {
                                mainController.setHeaderMessage("Follow the format - hh:mm:ss", HeaderController.messageColor.Warning);
                                highlightSpinner(TimeSpinner);
                                return LocalTime.MIDNIGHT;
                            }
                        }
                    }
                });
            }

            @Override
            public void decrement(int steps) {
                if (getValue() == null)
                    setValue(LocalTime.MIDNIGHT);
                else {
                    LocalTime time = (LocalTime) getValue();
                    setValue(time.minusMinutes(steps));
                }
            }

            @Override
            public void increment(int steps) {
                if (this.getValue() == null)
                    setValue(LocalTime.MIDNIGHT);
                else {
                    LocalTime time = (LocalTime) getValue();
                    setValue(time.plusMinutes(steps));
                }
            }
        });
        TimeSpinner.setEditable(true);
    }


    public void changeImage(String resource)
    {
        if(resource == null)
        {
            ThemeImg.setVisible(false);
        }
        else
        {
            ThemeImg.setImage(new Image(resource));
            ThemeImg.setVisible(true);
        }
    }

    public void clearAllSelections()
    {
        Platform.runLater(() -> {
        this.TimeSpinner.getValueFactory().setValue(LocalTime.MIDNIGHT);
        this.FitnessTF.setText("");
        this.GenerationsTF.setText("");
        this.DurationRB.setSelected(false);
        this.FitnessRB.setSelected(false);
        this.GenerationsRB.setSelected(false);
        });
    }

    public void clearAllLabels()
    {
        Platform.runLater(() -> {
            currentGen.set(0);
            currentBestFit.set(0);
            GenerationsLbl.setTextFill(Paint.valueOf("BLACK"));
            BestFitnessLbl.setTextFill(Paint.valueOf("BLACK"));
        });
    }
}
