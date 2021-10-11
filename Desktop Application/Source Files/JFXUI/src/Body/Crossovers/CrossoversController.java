package Body.Crossovers;

import Database.EvoLoader;
import Header.HeaderController;
import Main.Controller;
import Main.MainController;
import Proporties.Crossover;
import Proporties.CrossoverMethods.AspectOriented;
import Proporties.CrossoverMethods.DayTimeOriented;
import Proporties.Selection;
import Proporties.SelectionMethods.RouletteWheel;
import Proporties.SelectionMethods.Tournament;
import Proporties.SelectionMethods.Truncation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class CrossoversController implements Controller {

    private enum eAspects
    {
        Teacher,
        Class
    }
    private MainController mainController;
    private SimpleBooleanProperty DayTimeSelected;
    private SimpleBooleanProperty AspectSelected;

    @FXML
    private RadioButton DayTimeRB;

    @FXML
    private RadioButton AspectRB;

    @FXML
    private Spinner<Integer> CuttingPointsSpinner;

    @FXML
    private Button ApplyButton;

    @FXML
    private ChoiceBox<eAspects> OrientationBox;

    public CrossoversController()
    {
        DayTimeSelected = new SimpleBooleanProperty(false);
        AspectSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    void ClickedApply(ActionEvent event) {
        int cuttingPoints = CuttingPointsSpinner.getValue();
        Crossover newMethod;
        if(DayTimeSelected.get())
        {
            newMethod = new DayTimeOriented(cuttingPoints, "");
        }
        else
        {
            String orientation = OrientationBox.getValue().toString().toUpperCase();
            newMethod = new AspectOriented(cuttingPoints, "Orientation=" + orientation, orientation);
        }
        EvoLoader.setCrossoverMethod(newMethod);
        mainController.setHeaderMessage("New Crossover Method Set!", HeaderController.messageColor.OK);
    }

    @FXML
    void ClickedAspect(ActionEvent event) {
        setAspect();
    }

    @FXML
    void ClickedDayTime(ActionEvent event) {
        setDayTime();
    }

    @FXML
    public void initialize()
    {
        bindRadioButtonsAndOperands();
        Crossover currCrossMethod = EvoLoader.getCrossoverMethod();
        CuttingPointsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,Integer.MAX_VALUE, currCrossMethod.getCuttingPoints()));
        OrientationBox.getItems().addAll(eAspects.values());
        if(currCrossMethod instanceof DayTimeOriented)
        {
            plantDayTimeInfo((DayTimeOriented)currCrossMethod);
        }
        else
        {
            plantAspectInfo((AspectOriented)currCrossMethod);
        }
    }

    private void bindRadioButtonsAndOperands()
    {
        Bindings.bindBidirectional(DayTimeSelected, DayTimeRB.selectedProperty());
        Bindings.bindBidirectional(AspectSelected, AspectRB.selectedProperty());
    }

    private void plantDayTimeInfo(DayTimeOriented cross)
    {
        setDayTime();
    }

    private void plantAspectInfo(AspectOriented cross)
    {
        setAspect();
        for(eAspects aspect : eAspects.values())
        {
            if(aspect.toString().toUpperCase().equals(cross.getOrientation()))
            {
                OrientationBox.setValue(aspect);
                break;
            }
        }
    }

    private void setDayTime()
    {
        DayTimeSelected.set(true);
        AspectSelected.set(false);
    }

    private void setAspect()
    {
        AspectSelected.set(true);
        DayTimeSelected.set(false);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }
}
