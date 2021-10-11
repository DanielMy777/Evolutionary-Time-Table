package Body.Properties;

import Database.TTLoader;
import Main.Controller;
import Main.MainController;
import TTInfo.Classroom;
import TTInfo.Subject;
import TTInfo.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class PropertiesController implements Controller {

    private MainController mainController;

    @FXML
    private TableView<Subject> SubjectsTable;

    @FXML
    private TableColumn<Subject, String> SubjectsNumberCol;

    @FXML
    private TableColumn<Subject, String> SubjectsNameCol;

    @FXML
    private TableView<Teacher> TeachersTable;

    @FXML
    private TableColumn<Teacher, String> TeachersNumberCol;

    @FXML
    private TableColumn<Teacher, String> TeachersNameCol;

    @FXML
    private TableColumn<Teacher, String> TeachersSubjectsCol;

    @FXML
    private TableView<Classroom> ClassTable;

    @FXML
    private TableColumn<Classroom, String> ClassesNumberCol;

    @FXML
    private TableColumn<Classroom, String> ClassesNameCol;

    @FXML
    private TableColumn<Classroom, String> ClassesStudyCol;

    @FXML
    private TableColumn<Classroom, String> ClassesHoursCol;

    public PropertiesController()
    {
        SubjectsNumberCol = new TableColumn<>("Subject ID");
        SubjectsNameCol = new TableColumn<>("Subject Name");
        TeachersNumberCol = new TableColumn<>("Teacher ID");
        TeachersNameCol = new TableColumn<>("Teacher Name");
        TeachersSubjectsCol = new TableColumn<>("Teachable Subjects");
        ClassesNumberCol = new TableColumn<>("Class ID");
        ClassesNameCol = new TableColumn<>("Class Name");
        ClassesStudyCol = new TableColumn<>("Need To Study");
        ClassesHoursCol = new TableColumn<>("For How Many Hours");

    }

    @FXML
    public void initialize()
    {
        SubjectsNumberCol.setCellValueFactory(new PropertyValueFactory<Subject,String>("id"));
        SubjectsNameCol.setCellValueFactory(new PropertyValueFactory<Subject,String>("name"));
        TeachersNumberCol.setCellValueFactory(new PropertyValueFactory<Teacher, String>("id"));
        TeachersNameCol.setCellValueFactory(new PropertyValueFactory<Teacher, String>("name"));
        TeachersSubjectsCol.setCellValueFactory(new PropertyValueFactory<Teacher, String>("subjects"));
        ClassesNumberCol.setCellValueFactory(new PropertyValueFactory<Classroom, String>("id"));
        ClassesNameCol.setCellValueFactory(new PropertyValueFactory<Classroom, String>("name"));
        ClassesStudyCol.setCellValueFactory(new PropertyValueFactory<Classroom, String>("studys"));
        ClassesHoursCol.setCellValueFactory(new PropertyValueFactory<Classroom, String>("hours"));
        putSubjectsInTable();
        putTeachersInTable();
        putClassesInTable();
    }

    private void putSubjectsInTable()
    {
        List<Subject> subjectData = TTLoader.getSubjects();
        ObservableList<Subject> data = FXCollections.observableArrayList();
        data.addAll(subjectData);
        SubjectsTable.setItems(data);
    }

    private void putTeachersInTable()
    {
        List<Teacher> teacherData = TTLoader.getTeachers();
        ObservableList<Teacher> data = FXCollections.observableArrayList();
        data.addAll(teacherData);
        TeachersTable.setItems(data);
    }

    private void putClassesInTable()
    {
        List<Classroom> classData = TTLoader.getClassrooms();
        List<Classroom> devidedList = new ArrayList<>();
        for(Classroom c : classData)
        {
            devidedList.add(new Classroom(c.getId(), c.getName()));
            for(Subject s : c.getLearnableSubjects())
            {
                Classroom dummyClassroom = new Classroom(c.getId(), "");
                dummyClassroom.alterLearningHours(s, c.getLearningHoursOfSubject(s));
                devidedList.add(dummyClassroom);
            }
        }
        ObservableList<Classroom> data = FXCollections.observableArrayList();
        data.addAll(devidedList);
        ClassTable.setItems(data);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }
}
