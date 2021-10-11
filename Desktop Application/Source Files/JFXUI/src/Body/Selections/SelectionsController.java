package Body.Selections;

import Database.EvoLoader;
import Database.TTLoader;
import Header.HeaderController;
import Main.Controller;
import Main.MainController;
import Proporties.Selection;
import Proporties.SelectionMethods.RouletteWheel;
import Proporties.SelectionMethods.Tournament;
import Proporties.SelectionMethods.Truncation;
import com.sun.org.apache.xml.internal.serializer.ToUnknownStream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class SelectionsController implements Controller {

    private MainController mainController;
    private SimpleBooleanProperty truncationSelected;
    private SimpleBooleanProperty rouletteSelected;
    private SimpleBooleanProperty tournamentSelected;

    @FXML
    private Tooltip QuestionToolTip;

    @FXML
    private RadioButton TruncationRB;

    @FXML
    private RadioButton RouletteRB;

    @FXML
    private RadioButton TournamentRB;

    @FXML
    private Slider TruncationSlider;

    @FXML
    private TextField TruncationTopPercent;

    @FXML
    private TextField PTETB;

    @FXML
    private Spinner<Integer> ElitismSpinner;

    @FXML
    private Button ApplyBtn;

    public SelectionsController()
    {
        truncationSelected = new SimpleBooleanProperty(false);
        rouletteSelected = new SimpleBooleanProperty(false);
        tournamentSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    void ClickedApply(ActionEvent event) {
        int elitism = ElitismSpinner.getValue();
        Selection newMethod;
        if(truncationSelected.get())
        {
            int top = (int) TruncationSlider.getValue();
            newMethod = new Truncation(elitism, "TopPercent="+top, top);
        }
        else if (rouletteSelected.get())
        {
            newMethod = new RouletteWheel(elitism, "");
        }
        else
        {
            if(checkPTEBox()) {
                double pte = Double.parseDouble(PTETB.getText());
                newMethod = new Tournament(elitism, "PTE=" + pte, pte);
            }
            else
            {
                return;
            }
        }
        EvoLoader.setSelectionMethod(newMethod);
        mainController.setHeaderMessage("New Selection Method Set!", HeaderController.messageColor.OK);
    }

    @FXML
    void ClickedRoulette(ActionEvent event) {
        setRoulette();
    }

    @FXML
    void ClickedTournament(ActionEvent event) {
        setTournament();
    }

    @FXML
    void ClickedTruncation(ActionEvent event) {
        setTruncation();
    }

    @FXML
    void MovedTruncationSlider(MouseEvent event) {

    }

    @FXML
    void TypedPTE(KeyEvent event) {
        checkPTEBox();
    }

    private boolean checkPTEBox()
    {
        try {
            double d = Double.parseDouble(PTETB.getText());
            if(d < 0 || d > 1)
                throw new NumberFormatException("");
        }
        catch (NumberFormatException ex)
        {
            mainController.setHeaderMessage("For PTE please type a decimal number between 1-0", HeaderController.messageColor.Error);
            PTETB.setText("");
            return false;
        }
        return true;
    }

    @FXML
    public void initialize()
    {
        bindRadioButtonsAndOperands();
        Selection currSelectionMethod = EvoLoader.getSelectionMethod();
        ElitismSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0,EvoLoader.getInitialPopulation(), currSelectionMethod.getElitism()));
        TruncationSlider.setSnapToTicks(true);
        TruncationSlider.setMajorTickUnit(1f);
        TruncationSlider.setBlockIncrement(1f);
        TruncationSlider.setMinorTickCount(0);
        if(currSelectionMethod instanceof Truncation)
        {
            plantTruncationInfo((Truncation)currSelectionMethod);
        }
        else if(currSelectionMethod instanceof RouletteWheel)
        {
            plantRouletteInfo((RouletteWheel)currSelectionMethod);
        }
        else
        {
            plantTournamentInfo((Tournament)currSelectionMethod);
        }
    }

    private void bindRadioButtonsAndOperands()
    {
        Bindings.bindBidirectional(truncationSelected, TruncationRB.selectedProperty());
        Bindings.bindBidirectional(rouletteSelected, RouletteRB.selectedProperty());
        Bindings.bindBidirectional(tournamentSelected, TournamentRB.selectedProperty());
        TruncationSlider.disableProperty().bind(TruncationRB.selectedProperty().not());
        TruncationTopPercent.disableProperty().bind(TruncationRB.selectedProperty().not());
        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(TruncationTopPercent.textProperty(), TruncationSlider.valueProperty(), converter);
        PTETB.disableProperty().bind(TournamentRB.selectedProperty().not());
    }

    private void plantTruncationInfo(Truncation selection)
    {
        setTruncation();
        TruncationSlider.setValue(selection.getTopPercent());
    }

    private void plantRouletteInfo(RouletteWheel selection)
    {
        setRoulette();
    }

    private void plantTournamentInfo(Tournament selection)
    {
        setTournament();
        PTETB.setText(String.valueOf(selection.getPTE()));
    }

    private void setTruncation()
    {
        truncationSelected.set(true);
        rouletteSelected.set(false);
        tournamentSelected.set(false);
    }

    private void setRoulette()
    {
        rouletteSelected.set(true);
        truncationSelected.set(false);
        tournamentSelected.set(false);
    }

    private void setTournament()
    {
        tournamentSelected.set(true);
        rouletteSelected.set(false);
        truncationSelected.set(false);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }
}
