package Body.Solution;

import Data.Rule;
import Data.Solution;
import Database.TTLoader;
import Database.TTRule;
import Main.Controller;
import Main.MainController;
import TTBasic.Setting;
import TTBasic.SettingSet;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import TTSolution.FilledTimeTable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import javax.security.auth.callback.Callback;
import java.util.*;

public class SolutionController implements Controller {

    private MainController mainController;
    private FilledTimeTable solutionToExhibit;
    private final int TeacherBoxIdx = 0;
    private final int ClassBoxIdx = 1;
    private final int OtherBoxIdx = 2;
    private int GenerationJmp;
    private SimpleBooleanProperty isProgressScan;
    private SimpleIntegerProperty currentGeneration;

    @FXML
    private Label GenerationNumLbl;

    @FXML
    private Button PrevBtn;

    @FXML
    private Button NextBtn;

    @FXML
    private Button BestBtn;

    @FXML
    private ChoiceBox<Teacher> TeacherCB;

    @FXML
    private ChoiceBox<Classroom> ClassCB;

    @FXML
    private ChoiceBox<String> OtherCB;

    @FXML
    private TableView<rowInTable> SolutionTable;

    @FXML
    private Label FitnessLbl;

    @FXML
    void initialize()
    {
        SolutionTable.setColumnResizePolicy((c) -> true );
        GenerationNumLbl.textProperty().bind(Bindings.concat(Bindings.format("Generation #%,d", currentGeneration)));
        TeacherCB.getItems().addAll(TTLoader.getTeachers());
        ClassCB.getItems().addAll(TTLoader.getClassrooms());
        OtherCB.getItems().addAll("Raw", "Statistics");
        createTeacherChangeListener();
        createClassChangeListener();
        createOtherChangeListener();
    }

    public SolutionController()
    {
        isProgressScan = new SimpleBooleanProperty(false);
        currentGeneration = new SimpleIntegerProperty(0);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
        ShowBestSolution(null);
        bindEvolutionTravelButtons();
    }

    private void bindEvolutionTravelButtons()
    {
        GenerationJmp = mainController.getGenerationJump();
        NextBtn.disableProperty().bind(currentGeneration.add(GenerationJmp).greaterThan(mainController.getAmountOfGenerations()).or(mainController.getAlgStoppedProp().not()));
        PrevBtn.disableProperty().bind(currentGeneration.isEqualTo(0).or(mainController.getAlgStoppedProp().not()));
        BestBtn.disableProperty().bind(mainController.getAlgStoppedProp().not().or(currentGeneration.isEqualTo(mainController.getBestSolutionGen())));
    }

    public void setIsProgressScan(boolean scan, int initialGeneration)
    {
        isProgressScan.set(scan);
        currentGeneration.set(initialGeneration);
        ShowSolution();
    }

    private void createTeacherChangeListener()
    {
        TeacherCB.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends Teacher> observable, Teacher oldValue, Teacher newValue) -> {
                    if(newValue != null) {
                        clearOtherBoxes(TeacherBoxIdx);
                        buildTableFromSettingsArray(newValue, null);
                    }
                } );
    }

    private void createClassChangeListener()
    {
        ClassCB.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends Classroom> observable, Classroom oldValue, Classroom newValue) -> {
                    if(newValue != null) {
                        clearOtherBoxes(ClassBoxIdx);
                        buildTableFromSettingsArray(null, newValue);
                    }
                } );

    }
    private void createOtherChangeListener()
    {
        OtherCB.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if(newValue != null) {
                        if (newValue == "Raw")
                            createRawTable();
                        else if (newValue == "Statistics")
                            createStatsTable();
                    }
                } );
    }

    private void buildTableFromSettingsArray(Teacher t, Classroom c)
    {
        InitTable();
        SettingSet[][] allSettings;
        rowInTable[] Information;
        if(c == null)
        {
            allSettings = solutionToExhibit.getAllSettingsOfTeacherAsSettingSet(t);
        }
        else
        {
            allSettings = solutionToExhibit.getAllSettingsOfClassAsSettingSet(c);
        }
        Information = createHourInScheduelArray(allSettings);
        addHoursColumn();
        for(int i=1; i<solutionToExhibit.getDays() + 1; i++)
        {
            int finalI = i;
            TableColumn<rowInTable, String> newCol;
            String columnHeader = "Day" + i;
            newCol = new TableColumn<>(columnHeader);
            newCol.setStyle("-fx-alignment: CENTER;");
            newCol.setStyle("-fx-text-alignment: CENTER;");
            newCol.setCellValueFactory(
                    cellData -> new SimpleStringProperty(cellData.getValue().getSet(finalI).toStringFromView(
                            t == null ? Setting.eViewType.CLASS_VIEW : Setting.eViewType.TEACHER_VIEW
                    )));

            SolutionTable.getColumns().add(newCol);
        }
        List<rowInTable> Data = new ArrayList<>();
        Collections.addAll(Data, Information);
        ObservableList<rowInTable> data = FXCollections.observableArrayList();
        data.addAll(Data);
        SolutionTable.setItems(data);
        Platform.runLater(() -> SolutionTable.refresh());

    }

    private void addHoursColumn()
    {
        TableColumn<rowInTable,String> newCol = new TableColumn<>("");
        newCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper("Hour " + cellData.getValue().getHour()));
        SolutionTable.getColumns().add(newCol);
    }

    private void createRawTable()
    {
        InitTable();
        clearOtherBoxes(OtherBoxIdx);
        TableColumn<rowInTable,String> newCol = new TableColumn<>("<D=Day, H=Hour, C=Class, T=Teacher, S=Subject>");
        newCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData(0)));
        SolutionTable.getColumns().add(newCol);
        ObservableList<rowInTable> data = FXCollections.observableArrayList();
        List<Setting> settingList = solutionToExhibit.getAllSettingsOfTimeTable();
        for(Setting s : settingList)
        {
            data.add(new rowInTable(s.toString()));
        }
        SolutionTable.setItems(data);
        Platform.runLater(() -> SolutionTable.refresh());
    }

    public void createStatsTable()
    {
        InitTable();
        clearOtherBoxes(OtherBoxIdx);
        TableColumn<rowInTable,String> RuleCol = new TableColumn<>("Rule Name");
        TableColumn<rowInTable,String> WeightCol = new TableColumn<>("Hard / Soft");
        TableColumn<rowInTable,String> ScoreCol = new TableColumn<>("Rule Score");
        RuleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData(0)));
        WeightCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData(1)));
        ScoreCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData(2)));
        SolutionTable.getColumns().add(RuleCol);
        SolutionTable.getColumns().add(WeightCol);
        SolutionTable.getColumns().add(ScoreCol);
        ObservableList<rowInTable> data = FXCollections.observableArrayList();
        Map<TTRule, Boolean> allRules = TTLoader.AllRules;
        for(Map.Entry<TTRule, Boolean> e : allRules.entrySet())
        {
            TTRule rule = e.getKey();
            String isHard = e.getValue() ? "Hard" : "Soft";
            data.add(new rowInTable(
                    rule.toString() + (rule.isConfiguration() ? "" : " | " + rule.getConfiguration() + " Hrs |"),
                    isHard,
                    String.valueOf(rule.eval(solutionToExhibit))));
        }
        SolutionTable.setItems(data);
        Platform.runLater(() -> SolutionTable.refresh());
    }

    @FXML
    void ShowBestSolution(ActionEvent event) {
        currentGeneration.set(mainController.getBestSolutionGen());
        solutionToExhibit = (FilledTimeTable) mainController.getBestSolution();
        try {
            triggerSelectionBoxes();
            FitnessLbl.setText(String.format("Fitness: %.3f", solutionToExhibit.getFitness()));
        }
        catch (Exception e) {FitnessLbl.setText("-");}
    }

    @FXML
    void ShowNextSolution(ActionEvent event) {
        currentGeneration.set(currentGeneration.get() + (GenerationJmp - currentGeneration.get() % GenerationJmp));
        ShowSolution();
    }

    @FXML
    void ShowPrevSolution(ActionEvent event) {
        if(currentGeneration.get() % GenerationJmp == 0)
            currentGeneration.set(currentGeneration.get() - GenerationJmp);
        else
            currentGeneration.set(currentGeneration.get() - (currentGeneration.get() % GenerationJmp));
        ShowSolution();
    }

    private void ShowSolution()
    {
        solutionToExhibit = (FilledTimeTable) mainController.getSolutionByGen(currentGeneration.get());
        try {
            triggerSelectionBoxes();
            FitnessLbl.setText(String.format("Fitness: %.3f", solutionToExhibit.getFitness()));
        }
        catch (Exception e) {FitnessLbl.setText("-");}
    }

    private rowInTable[] createHourInScheduelArray(SettingSet[][] settings)
    {
        int hours = settings[0].length;
        rowInTable[] res = new rowInTable[hours];
        for(int i=1; i<=hours; i++)
        {
            rowInTable currHour = new rowInTable(i, settings);
            res[i - 1] = currHour;
        }
        return res;
    }

    private void InitTable()
    {
        SolutionTable.getColumns().remove(0, SolutionTable.getColumns().size());
    }

    private void clearOtherBoxes(int boxSelectedIdx)
    {
        if(boxSelectedIdx != TeacherBoxIdx)
            TeacherCB.getSelectionModel().clearSelection();
        if(boxSelectedIdx != ClassBoxIdx)
            ClassCB.getSelectionModel().clearSelection();
        if(boxSelectedIdx != OtherBoxIdx)
            OtherCB.getSelectionModel().clearSelection();
    }

    private void triggerSelectionBoxes()
    {
        if(ClassCB.getValue() != null)
        {
            Classroom c = ClassCB.getValue();
            ClassCB.setValue(null);
            ClassCB.setValue(c);
        }
        else if(TeacherCB.getValue() != null)
        {
            Teacher t = TeacherCB.getValue();
            TeacherCB.setValue(null);
            TeacherCB.setValue(t);
        }
        if(OtherCB.getValue() != null)
        {
            String s = OtherCB.getValue();
            OtherCB.setValue(null);
            OtherCB.setValue(s);
        }
    }


    private class rowInTable
    {
        int hour;
        List<SettingSet> settings;
        List<String> data;
        public rowInTable(String... manualData)
        {
            data = new ArrayList<>();
            data.addAll(Arrays.asList(manualData));
        }

        public rowInTable(int idx, SettingSet[][] allSettings)
        {
            hour = idx;
            settings = new ArrayList<>();
            for(int i=0; i<solutionToExhibit.getDays(); i++)
            {
                settings.add(allSettings[i][hour - 1]);
            }
        }

        public SettingSet getSet(int day)
        {
            return settings.get(day - 1);
        }

        public int getHour() {
            return hour;
        }

        public String getData(int idx) {
            return data.get(idx);
        }
    }
}
