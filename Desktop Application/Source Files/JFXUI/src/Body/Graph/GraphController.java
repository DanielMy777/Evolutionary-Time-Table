package Body.Graph;

import Data.Solution;
import Main.Controller;
import Main.MainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.Pair;

public class GraphController implements Controller {

    private MainController mainController;

    private XYChart.Series<Number, Number> Data;
    private XYChart.Series<Number, Number> liveData;

    @FXML
    private LineChart<Number, Number> ProgressGraph;

    @FXML
    private TableView<XYChart.Data<Number, Number>> ProgressChart;

    @FXML
    private TableColumn<XYChart.Data<Number, Number>, String> GenerationCol;

    @FXML
    private TableColumn<XYChart.Data<Number, Number>, String> FitnessCol;

    @FXML
    private TableColumn<XYChart.Data<Number, Number>, String> ImprovementCol;

    @FXML
    private TableColumn<XYChart.Data<Number, Number>, Void> SolutionCol;

    @FXML
    private CheckBox FullTableCB;

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
        ProgressGraph.animatedProperty().bind(mainController.getGraphAnimationProp());
        Data = mainController.getProgressData();
        synchronized (mainController) {
            liveData = mainController.getLiveProgressData();
            pushNewGraphData(liveData);
        }
        Platform.runLater(() -> {
            ProgressChart.setItems(Data.getData());
            ProgressChart.refresh();
        });
    }

    public GraphController() {

    }

    @FXML
    void initialize() {
        ProgressGraph.setAnimated(false);
        initializeGraph();
        initializeTable(true);
    }

    private void initializeGraph() {
        ProgressGraph.getXAxis().setLabel("Generation");
        ProgressGraph.getYAxis().setLabel("Best Fitness");
        ((NumberAxis) ProgressGraph.getXAxis()).setLowerBound(0);
        ProgressGraph.getYAxis().setAutoRanging(false);
        ((NumberAxis) ProgressGraph.getYAxis()).setTickUnit(10);
        ((NumberAxis) ProgressGraph.getYAxis()).setLowerBound(0);
        ((NumberAxis) ProgressGraph.getYAxis()).setUpperBound(100);
        ProgressGraph.getYAxis().setTickLabelGap(20);
        ProgressGraph.setCreateSymbols(false);
    }

    public void pushNewGraphData(XYChart.Series<Number, Number> s)
    {
        Platform.runLater(() -> {
            ProgressGraph.getData().removeIf(n -> true);
            ProgressGraph.getData().add(s);
        });
    }

    public void turnAnimatedOff()
    {
        ProgressGraph.setAnimated(false);
    }

    public void turnAnimatedOn()
    {
        ProgressGraph.setAnimated(true);
    }

    private void initializeTable(boolean fullTable) {
        GenerationCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getXValue())));
        FitnessCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getYValue())));
        ImprovementCol.setCellValueFactory(cellData -> {
            final int currRow;
            currRow = fullTable == true ?
                    cellData.getValue().getXValue().intValue() :
                    cellData.getValue().getXValue().intValue() / mainController.getGenerationJump();
            final int prevRow = currRow - 1;
            double prevFitness = 0, currFitness, diff, absDiff;
            if (prevRow >= 0) {
                XYChart.Data<Number, Number> tempItem = ProgressChart.getItems().get(prevRow);
                prevFitness = tempItem.getYValue().doubleValue();
            }
            currFitness = cellData.getValue().getYValue().doubleValue();
            diff = currFitness - prevFitness;
            absDiff = Math.abs(diff);
            String prefix = diff == 0 ? "\t    " : diff < 0 ? "-" : "\t\t    +";
            return new SimpleStringProperty(prevRow < 0 ? "\t    init" : String.format(prefix + "%.2f", absDiff));
        });

        SolutionCol.setCellFactory(createButtonCallBackFactory());
        SolutionCol.setStyle( "-fx-alignment: CENTER;");
    }

    private Callback<TableColumn<XYChart.Data<Number, Number>, Void>, TableCell<XYChart.Data<Number, Number>, Void>> createButtonCallBackFactory() {
        Callback<TableColumn<XYChart.Data<Number, Number>, Void>, TableCell<XYChart.Data<Number, Number>, Void>> cellFactory =
                new Callback<TableColumn<XYChart.Data<Number, Number>, Void>, TableCell<XYChart.Data<Number, Number>, Void>>() {
                    @Override
                    public TableCell<XYChart.Data<Number, Number>, Void> call(TableColumn<XYChart.Data<Number, Number>, Void> param) {
                        final TableCell<XYChart.Data<Number, Number>, Void> cell = new TableCell<XYChart.Data<Number, Number>, Void>() {

                            private final Button btn = new Button("Take me there!");

                            {
                                btn.setOnAction((ActionEvent event) -> {
                                    XYChart.Data<Number, Number> data = getTableView().getItems().get(getIndex());
                                    mainController.ShowSolutionBodyOfGeneration(data.getXValue().intValue());
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty ||
                                        getTableView().getItems().get(getIndex()).getXValue().intValue() % mainController.getGenerationJump() != 0) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(btn);
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

    @FXML
    void ToggleFullTable(ActionEvent event) {
        if(!FullTableCB.isSelected())
        {
            Platform.runLater(() -> {
                initializeTable(true);
                ProgressChart.setItems(Data.getData());
                ProgressChart.refresh();
            });
        }
        else
        {
            Platform.runLater(() -> {
                initializeTable(false);
                ProgressChart.setItems(extractJumpDataOnly().getData());
                ProgressChart.refresh();
            });
        }
    }

    private XYChart.Series<Number, Number> extractJumpDataOnly()
    {
        XYChart.Series<Number, Number> res = new XYChart.Series<>();
        XYChart.Series<Number, Number> originalData = mainController.getProgressData();
        for(XYChart.Data<Number, Number> item : originalData.getData())
        {
            if(item.getXValue().intValue() % mainController.getGenerationJump() == 0)
                res.getData().add(item);
        }
        return res;
    }

    public static void ObserveGeneration(Number generationToObserve)
    {
        System.out.println("Clicked on gen " + generationToObserve);
    }
}
