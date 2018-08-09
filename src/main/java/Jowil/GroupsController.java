package Jowil;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import static java.util.Arrays.asList;

public class GroupsController  extends Controller{


    //components
    private JFXTreeTableView groupsTable = new JFXTreeTableView();
    JFXTreeTableColumn<Group,String> groupNamesCol = new JFXTreeTableColumn<>("Group");
    JFXTreeTableColumn<Group,String> qCountCol = new JFXTreeTableColumn<>("Number of Questions");
    final VBox groupsTableVbox = new VBox();
    final VBox choicesTreeVBox = new VBox();
    private AnchorPane tablesAnchoPane=new AnchorPane();
    private JFXButton manualButton= new JFXButton("Edit Manually");
    final Label label = new Label("Groups Overview");
    private JFXComboBox identifierCombo= new JFXComboBox();
    private JFXComboBox formCombo=new JFXComboBox();
    private static TreeView choicesTreeView=new TreeView();
    final Label treeLabel= new Label("Groups Choices");

    Separator separator=new Separator();
    private static HashMap<String,Integer> groupsIndices=new HashMap<String,Integer>();



    //data fields
    ObservableList<Group> tableGroups = FXCollections.observableArrayList();
    ArrayList<Group> detectedGroups;
    private static ArrayList<ArrayList<Boolean>> isPossible;
    private static ArrayList<Group> treeViewGroups;

    //main methods
    GroupsController(Controller back){

        super("ViewGroupsAndSubjs.fxml","Groups",1.25,1.25,true,back);
        CSVHandler.initQuestionsChoices();
    }


    @Override
    protected void initComponents() {
        initGroupsTableVBox();
        initManualButton();
        initTreeView();
        initChoicesTreeVBox();
//        separator.setOrientation(Orientation.VERTICAL);
//        separator.setLayoutX(500);
//        separator.setLayoutY(50);
//        separator.setPrefHeight(400);
//        separator.setStyle("-fx-border-width: 10px");
//
//        tablesAnchoPane.getChildren().add(separator);
    }

    @Override
    protected void updateSizes(){
        super.updateSizes();
        groupsTableVbox.setSpacing(resYToPixels(0.02));
        groupsTableVbox.setPadding(new Insets(rootHeightToPixels(0.05), 0, 0, 0));
        choicesTreeVBox.setSpacing(resYToPixels(0.02));
        choicesTreeVBox.setPadding(new Insets(rootHeightToPixels(0.05), 0, 0, 0));
        groupsTable.setPrefHeight(rootHeightToPixels(0.67));
        groupsTable.setPrefWidth(rootWidthToPixels(0.3125));
        choicesTreeView.setPrefHeight(rootHeightToPixels(0.67));
        choicesTreeView.setPrefWidth(rootWidthToPixels(0.3125));

        manualButton.setPrefHeight(navHeight);
        //manualButton.setPrefWidth(navWidth);
        manualButton.setLayoutX(buttonsHbox.getLayoutX());
        manualButton.setLayoutY(buttonsHbox.getLayoutY()+buttonsHbox.getPadding().getTop());
        AnchorPane.setRightAnchor(choicesTreeVBox,rootWidth/20);
        AnchorPane.setLeftAnchor(groupsTableVbox,rootWidth/20);
        tablesAnchoPane.setPrefWidth(rootPane.getPrefWidth());
        choicesTreeView.setLayoutY(groupsTable.getLayoutY());


    }

    @Override
    protected Controller getNextController() {
        return new WeightsController(this);
    }


    @Override
    protected void buildComponentsGraph(){
        super.buildComponentsGraph();
        groupsTable.getColumns().addAll(groupNamesCol,qCountCol);
        groupsTableVbox.getChildren().addAll(label, groupsTable);
        tablesAnchoPane.getChildren().addAll(groupsTableVbox,choicesTreeVBox);
        //rootPane.getChildren().add(tablesHbox);
        rootPane.getChildren().add(manualButton);
        rootPane.getChildren().add(tablesAnchoPane);




    }

    @Override
    protected void saveChanges() {


        saveTreeViewGroups();
        for(Group group : CSVHandler.getDetectedGroups())
            System.out.println(group.getPossibleAnswers());

        CSVHandler.updateQuestionsChoices();

    }

    @Override
    protected void stabalizeTables(){
        //disableTableDrag(groupsTable);

    }


    /////static utility methods

    public static void addToGroup(String groupName){

        int i=0;
        for(Group group: treeViewGroups){  //group names are unique
            if(group.getCleanedName().equals(groupName)) {
                group.addAnswerToEnd();
                isPossible.get(i).add(true);
                TreeItem groupItem=(TreeItem)(choicesTreeView.getRoot().getChildren().get(i));
                TreeItem rangeItem=(TreeItem)groupItem.getChildren().get(0);
                rangeItem.getChildren().add(new TreeItem<>(group.getPossibleAnswers().get(group.getPossibleAnswers().size()-1)));
                break;
            }
            i++;
        }
    }

    public static void deleteFromGroup(String groupName,String deletedChoice){

        int i=indexOfGroup(groupName);
        isPossible.get(i).set(treeViewGroups.get(i).getPossibleAnswers().indexOf(deletedChoice),false);

    }

    public static void saveTreeViewGroups(){

        ArrayList<Group> newDetectedGroups=new ArrayList<>();

        for(Group group:treeViewGroups) //copy by value using copy constructor to have different possible answers array
            newDetectedGroups.add(new Group(group));


        for(int i=0;i<newDetectedGroups.size();i++)
            newDetectedGroups.get(i).updatePossibleAnswers(isPossible.get(i));

        CSVHandler.setDetectedGroups(newDetectedGroups);

    }

    public static boolean isChoicePossible(String groupName,String choiceName){

        int i=indexOfGroup(groupName);

        return isPossible.get(i).get(treeViewGroups.get(i).getPossibleAnswers().indexOf(choiceName));
    }


    public static void restoreToGroup(String groupName,String restoredChoice){

        int i=indexOfGroup(groupName);

        isPossible.get(i).set(treeViewGroups.get(i).getPossibleAnswers().indexOf(restoredChoice),true);


    }

    public static int getFirstPossible(String groupName){

        int i=indexOfGroup(groupName);
        return isPossible.get(i).indexOf(true);

    }

    public static int getLastPossible(String groupName){

        int i=indexOfGroup(groupName);
        return isPossible.get(i).lastIndexOf(true);
    }


    public void populateTreeViewGroups(){
        treeViewGroups=new ArrayList<Group>();

        int i=0;
        for(Group group:CSVHandler.getDetectedGroups()){ //copy by value using copy constructor.
            groupsIndices.put(group.getCleanedName(),i);
            treeViewGroups.add(new Group(group));
            i++;
        }

    }




    //helper methods
    private void initGroupsTableVBox(){


        groupNamesCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Group, String> param) -> {
           return param.getValue().getValue().getCleanedNameProp();
        });


        groupNamesCol.setCellFactory((TreeTableColumn<Group, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));



        qCountCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Group, String> param) -> {
            return param.getValue().getValue().getObsQcountProp();
        });

        qCountCol.setCellFactory((TreeTableColumn<Group, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));


        final TreeItem<Group> root = new RecursiveTreeItem<>(tableGroups, RecursiveTreeObject::getChildren);

        groupsTable.setRoot(root);
        groupsTable.setShowRoot(false);


        detectedGroups=CSVHandler.getDetectedGroups();

        for(int i=0;i<detectedGroups.size();i++)
            tableGroups.add(detectedGroups.get(i));

        label.setFont(new Font("Arial", headersFontSize));


        groupsTable.setEditable(false);


        groupsTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        //groupsTable.setSelectionModel(null);
        //groupsTable.setStyle("-fx-border-color:#1E90FF");
        groupNamesCol.setSortable(false);

        groupsTable.setOnMousePressed(t->{
//            for(Object pos:groupsTable.getSelectionModel().getSelectedIndices())
//                groupsTable.getSelectionModel().clearSelection((Integer)pos);

            });
        qCountCol.setSortable(false);
        qCountCol.setEditable(false);
        //groupsTable.setStyle("-fx-font-size:"+Double.toString(resX/106.7));

    }



    private void initManualButton(){
        manualButton.getStyleClass().add("BlueJFXButton");
//        manualButton.setOnMouseEntered(t->manualButton.setStyle("-fx-background-color:#878a8a;"));
//        manualButton.setOnMouseExited(t->manualButton.setStyle("-fx-background-color:transparent;-fx-border-color:#949797"));

        manualButton.setOnMouseClicked(t->{
//            HeadersCreateController controller = new HeadersCreateController(null);
//            isContentEdited=true;
//            controller.startWindow();
        });

        //buttonsHbox.getChildren().add(manualButton);

    }

    private void initIdentifierCombo(){
        ArrayList<String> infoHeaders=CSVHandler.getDetectedInfoHeaders();

    }

    public void initChoicesTreeVBox(){

        choicesTreeVBox.getChildren().addAll(treeLabel,choicesTreeView);
        treeLabel.setFont(new Font("Arial", headersFontSize));

    }


    private void initTreeView(){


        populateTreeViewGroups();

        isPossible=new ArrayList<ArrayList<Boolean>>();

        for(Group group: treeViewGroups){  //init isPossible
            ArrayList<Boolean> possibles=new ArrayList<Boolean>();
            for(int i=0;i<group.getPossibleAnswers().size();i++)
                possibles.add(true);
            isPossible.add(possibles);
        }


        choicesTreeView.setEditable(true);

        choicesTreeView.setCellFactory(t->new TreeViewCustomCell());

        constructChoicesTreeView(choicesTreeView);



        choicesTreeView.setShowRoot(false);
    }
    
    private static void constructChoicesTreeView(TreeView treeView){


        TreeItem<String> rootItem =
                new TreeItem<String>("Groups");

        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);

        int i=0;

        for(Group group: treeViewGroups){
            TreeItem groupItem=new TreeItem<>(group.getCleanedName());
            groupItem.setExpanded(true);
            rootItem.getChildren().add(groupItem);
            TreeItem rangeItem=new TreeItem();
            groupItem.getChildren().add(rangeItem);
                //treeView.scrollTo(i);

            for(String answer: group.getPossibleAnswers())
                rangeItem.getChildren().add(new TreeItem<>(answer));

            i++;
        }

    }



    private static int indexOfGroup(String groupName){

        return groupsIndices.get(groupName);
    }

}



