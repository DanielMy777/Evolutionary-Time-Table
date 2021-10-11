package Body.Rules;

import Body.Mutations.MutationsController;
import Data.Rule;
import Database.TTLoader;
import Database.TTRule;
import Header.HeaderController;
import Main.Controller;
import Main.MainController;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.io.Serializable;
import java.lang.management.BufferPoolMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RulesController implements Controller {

    //---------------------------------------------------------------

    static class ruleInSystem implements Serializable
    {
        enum eTough {HARD, SOFT, NONE};
        TTRule rule;
        eTough isHard;

        public ruleInSystem(TTRule rule, boolean isHard)
        {
            this.rule = rule;
            this.isHard = isHard ? eTough.HARD : eTough.SOFT;
        }

        public ruleInSystem(TTRule rule)
        {
            this.rule = rule;
            this.isHard = eTough.NONE;
        }

        public void toggleHard(){isHard = isHard == eTough.HARD ? eTough.SOFT : eTough.HARD;}

        private String fromCamelCaseToWords()
        {
            StringBuilder sb = new StringBuilder("");
            char[] ruleString = rule.toString().toCharArray();
            for(int i=0; i<ruleString.length; i++)
            {
                if(ruleString[i] >= 'A' && ruleString[i] <= 'Z' && i!=0)
                    sb.append(' ');
                sb.append(ruleString[i]);
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            if(isHard == eTough.NONE) { return fromCamelCaseToWords();}
            String conf = rule == TTRule.Sequentiality ? " | " + rule.getConfiguration() + " Hrs |" : "";
            String type = isHard == eTough.HARD ? "HARD" : "SOFT";
            return fromCamelCaseToWords() + conf + " - " + type;
        }
    }

    //---------------------------------------------------------------

    private MainController mainController;

    @FXML
    private ImageView ArrowImg;

    @FXML
    private ListView<ruleInSystem> AvailableList;

    @FXML
    private ListView<ruleInSystem> AppliedList;

    static final DataFormat RULES_LIST = new DataFormat("RulesList");

    @FXML
    void AppliedDragDetected(MouseEvent event) {
        dragDetected(event, AppliedList);
    }

    @FXML
    void AppliedDragDone(DragEvent event) {
        dragDone(event, AppliedList);
    }

    @FXML
    void AppliedDragDropped(DragEvent event) {
        dragDropped(event, AppliedList, true);
    }

    @FXML
    void AppliedDragOver(DragEvent event) {
        dragOver(event, AppliedList);
    }

    @FXML
    void AvailableDragDetected(MouseEvent event) {
        dragDetected(event, AvailableList);
    }

    @FXML
    void AvailableDragDone(DragEvent event) {
        dragDone(event, AvailableList);
    }

    @FXML
    void AvailableDragDropped(DragEvent event) {
        dragDropped(event, AvailableList, false);
    }

    @FXML
    void AvailableDragOver(DragEvent event) {
        dragOver(event, AvailableList);
    }

    @FXML
    void ClickedApplied(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY)
            mouseClicked(event, AppliedList);
    }

    @FXML
    void ClickedAvailable(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY)
            mouseClicked(event, AvailableList);
    }

    @FXML
    public void initialize()
    {
        createLists();
        AvailableList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        AppliedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    private void dragDetected(MouseEvent event, ListView<ruleInSystem> list)
    {
        int selectedCount = list.getSelectionModel().getSelectedIndices().size();
        if (selectedCount == 0)
        {
            event.consume();
            return;
        }
        Dragboard dragboard = list.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        ArrayList<ruleInSystem> selectedItems = this.getSelectedRules(list);

        ClipboardContent content = new ClipboardContent();
        content.put(RULES_LIST, selectedItems);

        dragboard.setContent(content);

        event.consume();
    }

    private ArrayList<ruleInSystem> getSelectedRules(ListView<ruleInSystem> listv)
    {

        return new ArrayList<>(listv.getSelectionModel().getSelectedItems());
    }

    private void dragOver(DragEvent event, ListView<ruleInSystem> list)
    {
        Dragboard dragboard = event.getDragboard();
        if (event.getGestureSource() != list && dragboard.hasContent(RULES_LIST))
        {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragDropped(DragEvent event, ListView<ruleInSystem> list, boolean toApply)
    {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();

        if(dragboard.hasContent(RULES_LIST))
        {
            ArrayList<ruleInSystem> rulesToAdd = (ArrayList<ruleInSystem>)dragboard.getContent(RULES_LIST);
            list.getItems().addAll(rulesToAdd);
            for(ruleInSystem r : rulesToAdd)
            {
                if(list == AppliedList) {
                    if (r.isHard == ruleInSystem.eTough.NONE) {
                        r.isHard = ruleInSystem.eTough.SOFT;
                    }
                    TTLoader.addRule(r.rule, r.isHard == ruleInSystem.eTough.HARD);
                }
                else
                    r.isHard = ruleInSystem.eTough.NONE;
            }
            dragCompleted = true;
            if(toApply) {
                mainController.setHeaderMessage("Rules Added Successfully!", HeaderController.messageColor.OK);
                if(mainController.getIsAnimation()) {
                    Platform.runLater(this::arrowLeftToRight);
                }
            }
            else {
                mainController.setHeaderMessage("Rules Un-Applied", HeaderController.messageColor.Warning);
                if(mainController.getIsAnimation()) {
                    Platform.runLater(this::arrowRightToLeft);
                }
            }
        }

        if(!dragCompleted)
            mainController.setHeaderMessage("Please drag a valid rule onto the boxes", HeaderController.messageColor.Error);
        event.setDropCompleted(dragCompleted);
        event.consume();
    }

    private void dragDone(DragEvent event, ListView<ruleInSystem> list)
    {
        TransferMode tm = event.getTransferMode();
        if (tm == TransferMode.MOVE)
        {
            removeSelectedRules(list);
        }
        event.consume();
    }

    private void removeSelectedRules(ListView<ruleInSystem> list)
    {
        List<ruleInSystem> selectedList = new ArrayList<>();

        for(ruleInSystem r : list.getSelectionModel().getSelectedItems())
        {
            selectedList.add(r);
            if(list == AppliedList)
                TTLoader.removeRule(r.rule);
        }

        list.getSelectionModel().clearSelection();

        list.getItems().removeAll(selectedList);
    }

    private void mouseClicked(MouseEvent event, ListView<ruleInSystem> list)
    {
        for(ruleInSystem r : list.getSelectionModel().getSelectedItems())
        {
            r.toggleHard();
            if(list == AppliedList)
                TTLoader.addRule(r.rule, r.isHard == ruleInSystem.eTough.HARD);
        }
        Platform.runLater(this::updateLists);
    }

    private void createLists()
    {
        AppliedList.getItems().clear();
        AvailableList.getItems().clear();
        Map<TTRule, Boolean> appliedRules = TTLoader.AllRules;
        for(TTRule r : TTRule.values()){
            if(appliedRules.keySet().contains(r))
            {
                AppliedList.getItems().add(new ruleInSystem(r, appliedRules.get(r)));
            }
            else
            {
                AvailableList.getItems().add(new ruleInSystem(r));
            }
        }
    }
    private void updateLists()
    {
        for(ruleInSystem r : AvailableList.getItems())
        {
            r.isHard = ruleInSystem.eTough.NONE;
        }
        AppliedList.refresh();
        AvailableList.refresh();
    }

    private void arrowLeftToRight()
    {
        ArrowImg.setScaleX(1);
        moveArrow(-30, 100);
    }

    private void arrowRightToLeft()
    {
        ArrowImg.setScaleX(-1);
        moveArrow(105, -30);
    }

    private void moveArrow(double fromX, double toX)
    {
        ArrowImg.setVisible(true);
        fadeInArrow();
        PathTransition trans = new PathTransition();
        MoveTo moveTo = new MoveTo(fromX, 0);
        trans.setDuration(Duration.valueOf("2s"));
        trans.setNode(ArrowImg);
        Path p = new Path();
        ArcTo arcTo = new ArcTo();
        arcTo.setX(toX);
        arcTo.setY(0);
        arcTo.setRadiusX(0);
        arcTo.setRadiusY(50);
        p.getElements().addAll(moveTo, arcTo);
        trans.setPath(p);
        trans.play();
        trans.setOnFinished(e -> fadeOutArrow());
    }

    private void fadeOutArrow()
    {
        FadeTransition fadeTransitionDown = new FadeTransition(Duration.seconds(0.8), ArrowImg);
        fadeTransitionDown.setFromValue(1.0);
        fadeTransitionDown.setToValue(0.0);
        fadeTransitionDown.play();
    }

    private void fadeInArrow()
    {
        FadeTransition fadeTransitionUp = new FadeTransition(Duration.seconds(0.8), ArrowImg);
        fadeTransitionUp.setFromValue(0.0);
        fadeTransitionUp.setToValue(1.0);
        fadeTransitionUp.play();
    }
}
