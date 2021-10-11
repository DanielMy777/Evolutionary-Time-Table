package Body.Mutations;

import Database.EvoLoader;
import Database.TTLoader;
import Header.HeaderController;
import Main.Controller;
import Main.MainController;
import Proporties.Mutation;
import Proporties.MutationMethods.Flipping;
import Proporties.MutationMethods.Sizer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sun.invoke.empty.Empty;

import java.util.List;

public class MutationsController implements Controller {

    private enum eMutations
    {
        Flipper,
        Sizer,
    }
    private enum eComponents
    {
        Day, Hour, Class, Teacher, Subject
    }

    private MainController mainController;
    private SimpleBooleanProperty flipperSelected;
    private SimpleBooleanProperty sizerSelected;

    @FXML
    private ChoiceBox<eMutations> TypeChoiceBox;

    @FXML
    private Slider ProbabilitySlider;

    @FXML
    private Label Componentlbl;

    @FXML
    private Label TotalTupplesLbl;

    @FXML
    private ChoiceBox<eComponents> ComponentChoiceBox;

    @FXML
    private Spinner<Integer> TotalTupplesSpinner;

    @FXML
    private Label MaxTupplesLbl;

    @FXML
    private Spinner<Integer> MaxTupplesSpinner;

    @FXML
    private ListView<Mutation> MutationsList;

    @FXML
    private Button AddBtn;

    @FXML
    private Button RemoveBtn;

    @FXML
    void AddBtnClicked(ActionEvent event) {
        Mutation newMethod;
        double prob = ProbabilitySlider.getValue();
        Flipping.Component comp;
        if(TypeChoiceBox.getValue() == eMutations.Flipper)
        {
            int maxT = MaxTupplesSpinner.getValue();
            try {
                comp = Flipping.Component.valueOf(String.valueOf(ComponentChoiceBox.getValue().toString().charAt(0)));
            }
            catch (Exception e)
            {
                mainController.setHeaderMessage("Please select a valid component", HeaderController.messageColor.Warning);
                return;
            }
            newMethod = new Flipping(prob, "MaxTupples="+maxT, maxT, comp);
        }
        else
        {
            int totalT = TotalTupplesSpinner.getValue();
            newMethod = new Sizer(prob, "TotalTupples="+totalT, totalT);
        }
        EvoLoader.addMutation(newMethod);
        mainController.setHeaderMessage("New Mutation Added!", HeaderController.messageColor.OK);
        Platform.runLater(this::updateMutationsList);
    }

    @FXML
    void RemoveBtnClicked(ActionEvent event) {
        Mutation toRemove = MutationsList.getSelectionModel().getSelectedItem();
        EvoLoader.removeMutation(toRemove);
        Platform.runLater(this::updateMutationsList);
    }

    public MutationsController()
    {
        flipperSelected = new SimpleBooleanProperty(false);
        sizerSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize()
    {
        bindMenuItemsAndProperties();
        int amountOfTupples = TTLoader.getDays() * TTLoader.getHours();
        TypeChoiceBox.getItems().addAll(eMutations.values());
        ComponentChoiceBox.getItems().addAll(eComponents.values());
        TotalTupplesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                (amountOfTupples*(-1)),amountOfTupples, 0));
        MaxTupplesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0,amountOfTupples, 5));
        ProbabilitySlider.setSnapToTicks(true);
        ProbabilitySlider.setMajorTickUnit(0.1f);
        ProbabilitySlider.setBlockIncrement(0.1f);
        ProbabilitySlider.setMinorTickCount(0);
        updateMutationsList();
    }

    public void bindMenuItemsAndProperties()
    {
        flipperSelected.bind(TypeChoiceBox.valueProperty().isEqualTo(eMutations.Flipper));
        sizerSelected.bind(TypeChoiceBox.valueProperty().isEqualTo(eMutations.Sizer));
        Componentlbl.visibleProperty().bind(flipperSelected);
        ComponentChoiceBox.visibleProperty().bind(flipperSelected);
        MaxTupplesLbl.visibleProperty().bind(flipperSelected);
        MaxTupplesSpinner.visibleProperty().bind(flipperSelected);
        TotalTupplesLbl.visibleProperty().bind(sizerSelected);
        TotalTupplesSpinner.visibleProperty().bind(sizerSelected);
        AddBtn.disableProperty().bind(TypeChoiceBox.valueProperty().isNotNull().not());
        RemoveBtn.disableProperty().bind(MutationsList.getSelectionModel().selectedItemProperty().isNotNull().not());
    }

    public void updateMutationsList()
    {
        List<Mutation> allMutations = EvoLoader.getMutationMethods();
        MutationsList.getItems().clear();
        MutationsList.getItems().addAll(allMutations);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }
}
