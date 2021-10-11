package Panel;

import Body.BodyComponentPaths;
import Main.Controller;
import Main.MainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Collection;

public class PanelController implements Controller {

    private MainController mainController;

    @FXML
    private Button SchoolBtn;

    @FXML
    private Button RulesBtn;

    @FXML
    private Button SelectionBtn;

    @FXML
    private Button CrossoverBtn;

    @FXML
    private Button MutationsBtn;

    @FXML
    private Button GraphBtn;

    @FXML
    private Button SolutionBtn;

    @FXML
    public void initialize()
    {
        disableProgressButtons();
    }

    @FXML
    public void ShowBestSolutionBody(ActionEvent event) {
        String componentPath = BodyComponentPaths.SOLUTION_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);
    }

    @FXML
    void ShowCrossoverBody(ActionEvent event) throws IOException {
        String componentPath = BodyComponentPaths.CROSSOVERS_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);

    }

    @FXML
    public void ShowGraphBody(ActionEvent event) {
        String componentPath = BodyComponentPaths.GRAPH_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);
    }

    @FXML
    void ShowMutationsBody(ActionEvent event) throws IOException {
        String componentPath = BodyComponentPaths.MUTATIONS_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);
    }

    @FXML
    void ShowRulesBody(ActionEvent event) throws IOException {
        String componentPath = BodyComponentPaths.RULES_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);
    }

    @FXML
    void ShowSchoolBody(ActionEvent event) throws IOException {
        String componentPath = BodyComponentPaths.PROPERTIES_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);
    }

    @FXML
    void ShowSelectionBody(ActionEvent event) throws IOException {
        String componentPath = BodyComponentPaths.SELECTIONS_BODY_COMPONENT_PATH;
        mainController.changeBodyComponent(componentPath);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    public void setDisablePropertyForViewAndModificationButtons(SimpleBooleanProperty FileSelectedProp, SimpleBooleanProperty AlgPausedProp,SimpleBooleanProperty AlgStoppedProp)
    {
        SchoolBtn.disableProperty().bind(FileSelectedProp.not());
        RulesBtn.disableProperty().bind(mainController.isAlgRunning().or(FileSelectedProp.not()));
        SelectionBtn.disableProperty().bind(FileSelectedProp.not().or(AlgPausedProp.not().and(AlgStoppedProp.not())));
        CrossoverBtn.disableProperty().bind(FileSelectedProp.not().or(AlgPausedProp.not().and(AlgStoppedProp.not())));
        MutationsBtn.disableProperty().bind(FileSelectedProp.not().or(AlgPausedProp.not().and(AlgStoppedProp.not())));
    }

    public void enableProgressButtons()
    {
        GraphBtn.setDisable(false);
        SolutionBtn.setDisable(false);
    }

    public void disableProgressButtons()
    {
        GraphBtn.setDisable(true);
        SolutionBtn.setDisable(true);
    }
}
