package Main;

import Apps.CommonResourcePaths;
import Body.Crossovers.CrossoversController;
import Body.Graph.GraphController;
import Body.Mutations.MutationsController;
import Body.Rules.RulesController;
import Body.Selections.SelectionsController;
import Body.Solution.SolutionController;
import Controls.AlgorithemProgress;
import Controls.ControlsController;
import Controls.UIAdapter;
import Data.Solution;
import Database.EvoAlgorithem;
import Database.EvoLoader;
import Database.TTLoader;
import Header.HeaderController;
import Panel.PanelController;
import TTSolution.FilledTimeTable;
import TTSolution.TTException;
import Tasks.RunAgorithmTask;
import generated.ETTDescriptor;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.swing.plaf.synth.SynthViewportUI;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController {

    private MediaPlayer mediaPlayer;
    private eStyle currentStyle = eStyle.Default;
    private Stage primaryStage;
    private SimpleBooleanProperty isAlgRunning;
    private AlgorithemProgress currentProgress;
    private RunAgorithmTask currentTask;
    private EvoAlgorithem<FilledTimeTable> evoAlgorithem;
    private Thread algorithemRunThread;
    private Solution bestSolution;
    private int bestSolutionGen;
    private int generationJump;
    private NavigableMap<Integer, Solution> bestSolutionPerGen;
    private XYChart.Series<Number, Number> bestFitnessPerGen;
    private XYChart.Series<Number, Number> liveBestFitnessPerGen;
    private SimpleBooleanProperty keepGraphAnimation;
    private int currentGraphScale = 1;

    @FXML
    private ScrollPane HeaderComponent;
    @FXML
    private HeaderController HeaderComponentController;
    @FXML
    private ScrollPane ControlsComponent;
    @FXML
    private ControlsController ControlsComponentController;
    @FXML
    private ScrollPane PanelComponent;
    @FXML
    private PanelController PanelComponentController;
    @FXML
    private ScrollPane BodyComponent;
    @FXML
    private Controller BodyComponentController;

    public MainController() throws EEException {
        bestSolutionPerGen = new TreeMap<>();
        bestFitnessPerGen = new XYChart.Series<>();
        liveBestFitnessPerGen = new XYChart.Series<>();
        bestFitnessPerGen.setName("Progress By Generation");
        liveBestFitnessPerGen.setName("Progress By Generation");
        isAlgRunning = new SimpleBooleanProperty(false);
        keepGraphAnimation = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize()
    {
        if(HeaderComponentController != null) {
            HeaderComponentController.setMainController(this);
        }
        if(ControlsComponentController != null) {
            ControlsComponentController.setMainController(this);
            ControlsComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.CONTROLS_DEFAULT_CSS).toExternalForm());
        }
        if(PanelComponentController != null) {
            PanelComponentController.setMainController(this);
            PanelComponentController.setDisablePropertyForViewAndModificationButtons(
                    HeaderComponentController.getFileSelectedProperty(),
                    ControlsComponentController.getAlgPausedProp(),
                    ControlsComponentController.getAlgStoppedProp());
        }
        ControlsComponentController.bindHeaderAndControls();
    }

    public void setPrimaryStage(Stage ps)
    {
        this.primaryStage = ps;
        HeaderComponentController.setPrimaryStage(ps);
    }

    public void setEvoAlgorithem(EvoAlgorithem ea)
    {
        this.evoAlgorithem = ea;
    }

    public void changeBodyComponent(String fxmlPath) {
        Platform.runLater(() -> {
            try {
                changeBodyComponentAndController(fxmlPath);
                if(currentStyle != eStyle.Default)
                    BodyComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.getBodyStylePath(BodyComponentController, currentStyle)).toExternalForm());
            }
            catch (IOException e) {
                HeaderComponentController.setMessage(
                        "There has been a problem loading your screen, please try again",
                        HeaderController.messageColor.Error);
            }
        });
    }

    private void changeBodyComponentAndController(String fxmlPath) throws IOException
    {
        synchronized (this) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource(fxmlPath);
            fxmlLoader.setLocation(url);
            BodyComponent = (ScrollPane) fxmlLoader.load(url.openStream());

            Scene scene = HeaderComponent.getScene();
            Stage window = (Stage) scene.getWindow();

            ScrollPane sp = (ScrollPane) scene.getRoot();
            BorderPane bp = (BorderPane) sp.getContent();
            bp.setCenter(BodyComponent);
            BodyComponentController = fxmlLoader.getController();
            BodyComponentController.setMainController(this);
            window.setScene(scene);
        }
    }

    public boolean loadDatabaseInfoFromXML(String xmlPath) throws TTException, EEException {
        try {
            clearAllProgressData();
            TTLoader ttLoader = new TTLoader();
            EvoLoader evoLoader = new EvoLoader();
            ETTDescriptor allData = ttLoader.loadXMLFile(xmlPath);
            evoLoader.LoadEngineData(allData);
            setHeaderMessage("Data loaded successfully", HeaderController.messageColor.OK);
            isAlgRunning.set(false);
            return true;
        }
        catch (Exception ex)
        {
            setHeaderMessage(ex.getMessage(), HeaderController.messageColor.Error);
            return false;
        }
    }

    public void setHeaderMessage(String message, HeaderController.messageColor color)
    {
        HeaderComponentController.setMessage(message, color);
    }

    public Solution getBestSolution()
    {
        synchronized (this) {
            return bestSolution;
        }
    }

    public SimpleBooleanProperty getGraphAnimationProp()
    {
        return keepGraphAnimation;
    }

    public Solution getSolutionByGen(int gen)
    {
        return bestSolutionPerGen.get(gen);
    }

    public void setProgressObject(AlgorithemProgress p)
    {
        this.currentProgress = p;
    }

    public void addSolutionToMap(Solution sol) {
        Platform.runLater(() -> {
            synchronized (this) {
                try {
                    if (bestSolutionPerGen != null && bestSolutionPerGen.isEmpty()) {
                        bestSolutionPerGen.put(0, sol);
                        bestSolutionGen = 0;
                        bestFitnessPerGen.getData().add(new XYChart.Data<>(0, sol.getFitness()));
                        addToLiveGenerationProgressXYChart(0, sol.getFitness());
                        bestSolution = sol;
                    } else if (currentProgress != null) {
                        int currGen = currentProgress.getCurrentGeneration();
                        if (currGen % generationJump == 0) {
                            int lastgen = bestSolutionPerGen.lastKey();
                            bestSolutionPerGen.put(currGen, sol);
                        }
                        bestFitnessPerGen.getData().add(new XYChart.Data<>(currGen, sol.getFitness()));
                        addToLiveGenerationProgressXYChart(currGen, sol.getFitness());
                        if (bestSolution.getFitness() < sol.getFitness()) {
                            synchronized (this) {
                                bestSolution = sol;
                                bestSolutionGen = currGen;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void addToLiveGenerationProgressXYChart(int gen, double fitness)
    {
        if(liveBestFitnessPerGen.getData().size() >= 1000)
        {
            currentGraphScale *= 10;
            keepGraphAnimation.set(false);
            liveBestFitnessPerGen.getData().removeIf(n -> n.getXValue().intValue() % currentGraphScale != 0);
            keepGraphAnimation.set(true);
        }
        if(gen % currentGraphScale == 0)
        {
            liveBestFitnessPerGen.getData().add(new XYChart.Data<>(gen, fitness));
        }
    }

    public void beginAlgorithemProcess(UIAdapter ControlsUIAdapter) throws EEException {
        ClearGraph();
        jumpToGraphBodyIfNeeded();
        jumpToNoBodyIfNeeded();
        PanelComponentController.enableProgressButtons();
        bestSolutionPerGen = new TreeMap<>();
        currentGraphScale = 1;
        evoAlgorithem = new EvoAlgorithem<>(new TTLoader().getAllRules());
        currentTask = new RunAgorithmTask(
                evoAlgorithem,
                this::addSolutionToMap,
                currentProgress::reachedEndStatement,
                ControlsComponentController::getAlgPaused,
                ControlsUIAdapter
        );

        currentTask.setOnCancel(() -> {
            ControlsComponentController.StopAlgorithm(null);
            setHeaderMessage("Process Finished!", HeaderController.messageColor.OK);
            algorithemRunThread = null;
        });
        currentTask.setMaxGenerations(currentProgress.getMaxGeneration());
        algorithemRunThread = new Thread(currentTask);
        setHeaderMessage("Process Started!", HeaderController.messageColor.OK);
        algorithemRunThread.start();
        isAlgRunning.set(true);
    }

    public void StepSingleGeneration() throws InterruptedException, EEException {
        if(algorithemRunThread != null)
        {
            synchronized (evoAlgorithem) {
                evoAlgorithem.updateAlgorithemData();
                currentTask.setToPause(this::oneTimeOnlyTrue);
                evoAlgorithem.notify();
                setHeaderMessage("Manually advanced one generation", HeaderController.messageColor.OK);
            }
        }
        else
        {
            ControlsComponentController.StartAlgorithm(null);
            if(currentTask != null)
                currentTask.setToPause(this::oneTimeOnlyTrue);
        }
    }

    private static boolean TRIGGER = true;
    private boolean oneTimeOnlyTrue()
    {
        TRIGGER = !TRIGGER;
        return TRIGGER;
    }

    public void unPauseAlgorithemProcess()
    {
        jumpToGraphBodyIfNeeded();
        synchronized (evoAlgorithem) {
            evoAlgorithem.updateAlgorithemData();
            currentTask.setToPause(ControlsComponentController::getAlgPaused);
            evoAlgorithem.notify();
            setHeaderMessage("Process Unpaused", HeaderController.messageColor.OK);
        }
    }

    public void stopAlgorithemProcess()
    {
        updateSolutionBodyIfNeeded();
        algorithemRunThread.interrupt();
        algorithemRunThread = null;
        isAlgRunning.set(false);
        setHeaderMessage("Process Stopped", HeaderController.messageColor.Error);
    }

    public void clearAllProgressData()
    {
        ControlsComponentController.StopIfRunning();
        if(currentTask != null)
            while (!currentTask.isDone()) {} //wait until finished
        if(currentProgress != null)
            currentProgress.clearAllProgress();
        if(algorithemRunThread != null) {
            algorithemRunThread.interrupt();
            algorithemRunThread = null;
        }
        ClearGraph();
        Platform.runLater(() -> {
            currentGraphScale = 1;
            evoAlgorithem = null;
            currentTask = null;
            bestSolution = null;
            bestSolutionPerGen = null;
            Scene scene = HeaderComponent.getScene();
            Stage window = (Stage) scene.getWindow();
            ScrollPane sp = (ScrollPane) scene.getRoot();
            BorderPane bp = (BorderPane) sp.getContent();
            bp.setCenter(null);
            BodyComponentController = null;
            window.setScene(scene);
        });

        isAlgRunning.set(false);
        HeaderComponentController.getFileSelectedProperty().set(false);
        PanelComponentController.disableProgressButtons();
        ControlsComponentController.clearAllSelections();
        ControlsComponentController.clearAllLabels();
    }

    private void ClearGraph()
    {
        Platform.runLater(() -> {
            synchronized (this) {
                keepGraphAnimation.set(false);
                bestFitnessPerGen.getData().clear();
                liveBestFitnessPerGen = new XYChart.Series<>();
                liveBestFitnessPerGen.setName("Fitness By Generation");
                keepGraphAnimation.set(true);
                if(BodyComponentController instanceof GraphController)
                {
                    ((GraphController)BodyComponentController).pushNewGraphData(liveBestFitnessPerGen);
                }
            }
        });
    }

    public XYChart.Series<Number, Number> getProgressData() {
        return bestFitnessPerGen;
    }

    public XYChart.Series<Number, Number> getLiveProgressData() {
        return liveBestFitnessPerGen;
    }

    public boolean isFileSelected()
    {
        return HeaderComponentController.getFileSelectedProperty().get();
    }

    public SimpleBooleanProperty GetFileSelectedProperty()
    {
        return HeaderComponentController.getFileSelectedProperty();
    }

    public void ShowSolutionBodyOfGeneration(int gen)
    {
        PanelComponentController.ShowBestSolutionBody(null);
        Platform.runLater(() -> {
            SolutionController sc = (SolutionController) BodyComponentController;
            sc.setIsProgressScan(true, gen);
        });

    }

    public void setGenerationJump(int jmp)
    {
        generationJump = jmp;
    }

    public int getGenerationJump()
    {
        return generationJump;
    }

    public int getBestSolutionGen() {
        synchronized (this) {
            return bestSolutionGen;
        }
    }

    public int getAmountOfGenerations()
    {
        return currentProgress.getCurrentGeneration();
    }

    public enum eStyle
    {
        Default,
        Happy,
        Dark,
    }

    public void changeStyle(eStyle wantedStyle)
    {
        currentStyle = wantedStyle;
        switch (wantedStyle)
        {
            case Default:
                clearThemes();
                break;
            case Happy:
                applyHappyTheme();
                break;
            case Dark:
                applyDarkTheme();
                break;
        }
        ControlsComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.CONTROLS_DEFAULT_CSS).toExternalForm());
        triggerMusic(false);
    }

    private void clearThemes()
    {
        Scene mainComponent = HeaderComponent.getScene();
        HeaderComponent.getStylesheets().clear();
        ControlsComponent.getStylesheets().clear();
        if(BodyComponent != null) {
            BodyComponent.getStylesheets().clear();
        }
        mainComponent.getStylesheets().clear();
        mainComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.MAIN_DEFAULT_CSS).toExternalForm());
    }

    private void applyHappyTheme()
    {
        clearThemes();
        Scene mainComponent = HeaderComponent.getScene();
        HeaderComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.HEADER_HAPPY_THEME).toExternalForm());
        ControlsComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.CONTROLS_HAPPY_THEME).toExternalForm());
        mainComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.MAIN_HAPPY_THEME).toExternalForm());
        if(BodyComponent != null)
        {
            BodyComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.getBodyStylePath(BodyComponentController, eStyle.Happy)).toExternalForm());
        }
    }

    private void applyDarkTheme()
    {
        clearThemes();
        Scene mainComponent = HeaderComponent.getScene();
        HeaderComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.HEADER_DARK_THEME).toExternalForm());
        ControlsComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.CONTROLS_DARK_THEME).toExternalForm());
        mainComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.MAIN_DARK_THEME).toExternalForm());
        if(BodyComponent != null)
        {
            BodyComponent.getStylesheets().add(getClass().getResource(CommonResourcePaths.getBodyStylePath(BodyComponentController, eStyle.Dark)).toExternalForm());
        }
    }

    public SimpleBooleanProperty isAlgRunning()
    {
        return isAlgRunning;
    }

    public Number getFitnessOfInitialSolution()
    {
        if(bestSolutionPerGen.get(0) != null) {
            try {
                return bestSolutionPerGen.get(0).getFitness();
            }
            catch (Exception e) {return 0;}
        }
        else
            return 0;
    }

    private void jumpToGraphBodyIfNeeded()
    {
        if(BodyComponentController instanceof RulesController
                || BodyComponentController instanceof SelectionsController
                || BodyComponentController instanceof CrossoversController
                || BodyComponentController instanceof MutationsController) {
            PanelComponentController.ShowGraphBody(null);
        }
    }

    private void jumpToNoBodyIfNeeded()
    {
        if(BodyComponentController instanceof SolutionController)
        {
            Platform.runLater(() -> {
                Scene scene = HeaderComponent.getScene();
                Stage window = (Stage) scene.getWindow();
                ScrollPane sp = (ScrollPane) scene.getRoot();
                BorderPane bp = (BorderPane) sp.getContent();
                bp.setCenter(null);
                BodyComponentController = null;
            });
        }
    }

    private void updateSolutionBodyIfNeeded()
    {
        Platform.runLater(() -> {
            if(BodyComponentController != null && BodyComponentController instanceof SolutionController) {
                BodyComponentController.setMainController(this);
            }
        });
    }

    public void triggerMusic(boolean easter)
    {
        cutMusic();
        if(HeaderComponentController.isSoundOn()) {
            Media media = null;
            if (easter) {
                media = new Media(getClass().getResource(CommonResourcePaths.RICK_MUSIC).toExternalForm());
                ControlsComponentController.changeImage(CommonResourcePaths.RICK_IMG);
            } else {
                switch (currentStyle) {
                    case Happy:
                        media = new Media(getClass().getResource(CommonResourcePaths.HAPPY_MUSIC).toExternalForm());
                        ControlsComponentController.changeImage(CommonResourcePaths.PHARRELL_IMG);
                        break;
                    case Dark:
                        media = new Media(getClass().getResource(CommonResourcePaths.DARK_MUSIC).toExternalForm());
                        ControlsComponentController.changeImage(CommonResourcePaths.SKULL_IMG);
                        break;
                }
            }
            if (media != null) {
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(0.1);
                mediaPlayer.play();
            }
        }
    }

    public SimpleBooleanProperty getAlgStoppedProp()
    {
        return ControlsComponentController.getAlgStoppedProp();
    }

    public void cutMusic()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            ControlsComponentController.changeImage(null);
        }
    }

    public boolean getIsAnimation()
    {
        return HeaderComponentController.isAnimation();
    }
}
