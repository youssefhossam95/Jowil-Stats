package Jowil;

import com.jfoenix.controls.*;
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
import javafx.geometry.Insets;
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
import java.util.List;
import java.util.function.Predicate;
import static java.util.Arrays.asList;

public class GroupsController  extends Controller{


    //components
    private TableView groupsTable = new TableView();
    TableColumn groupNamesCol = new TableColumn("Group");
    TableColumn qCountCol = new TableColumn("Number of Questions");
    final VBox groupsTableVbox = new VBox();
    final VBox choicesTreeVBox = new VBox();
    private AnchorPane tablesAnchoPane=new AnchorPane();
    private JFXButton manualButton= new JFXButton("Edit Manually");
    final Label label = new Label("Groups Overview");
    private JFXComboBox identifierCombo= new JFXComboBox();
    private JFXComboBox formCombo=new JFXComboBox();
    private TreeView choicesTreeView=new TreeView();
    final Label treeLabel= new Label("Groups Choices");





    //data fields
    ObservableList<Group> tableGroups = FXCollections.observableArrayList();
    ArrayList<Group> detectedGroups;
    private static ArrayList<ArrayList<Boolean>> isPossible;
    private static ArrayList<Group> treeViewGroups;

    //main methods
    GroupsController(Controller back){

        super("ViewGroupsAndSubjs.fxml","Groups",1.25,1.25,true,back);
        CSVHandler.updateQuestionsChoices();
    }


    @Override
    protected void initComponents() {
        initGroupsTableVBox();
        initManualButton();
        initTreeView();
        initChoicesTreeVBox();
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

        manualButton.setPrefHeight(resXToPixels(0.004));
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

        ArrayList<Group> newGroups=new ArrayList<>();
        for (Group group : tableGroups)
            newGroups.add(group);

        CSVHandler.updateGroupsAndQHeaders(newGroups);




        saveToTreeViewGroups();


        CSVHandler.setDetectedGroups(treeViewGroups);

    }

    @Override
    protected void stabalizeTables(){
        disableTableDrag(groupsTable);

    }

    //helper methods
    private void initGroupsTableVBox(){


        groupNamesCol.setCellValueFactory(
                new PropertyValueFactory<Group,String>("cleanedNameProp")
        );


        groupNamesCol.setCellFactory((t) -> EditCell.createStringEditCell());

        groupNamesCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Group,String>>)t-> {
                    if(t.getNewValue().length()==0){
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setNameProp(t.getOldValue());
                        groupsTable.refresh();
                    }
                    else {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setNameProp(t.getNewValue());
                        isContentEdited=true;;
                    }

                }
        );


        qCountCol.setCellValueFactory(
                new PropertyValueFactory<Group,String>("qCountProp")
        );

        qCountCol.setCellFactory(TextFieldTableCell.forTableColumn());
        

        detectedGroups=CSVHandler.getDetectedGroups();

        for(int i=0;i<detectedGroups.size();i++)
            tableGroups.add(detectedGroups.get(i));

        label.setFont(new Font("Arial", headersFontSize));


        groupsTable.setEditable(false);

        groupsTable.setItems(tableGroups);
        groupsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        groupsTable.setSelectionModel(null);
        //groupsTable.setStyle("-fx-border-color:#1E90FF");
        groupNamesCol.setSortable(false);
        qCountCol.setSortable(false);
        qCountCol.setEditable(false);
        //groupsTable.setStyle("-fx-font-size:"+Double.toString(resX/106.7));

    }



    private void initManualButton(){
        manualButton.getStyleClass().add("BlueJFXButton");
//        manualButton.setOnMouseEntered(t->manualButton.setStyle("-fx-background-color:#878a8a;"));
//        manualButton.setOnMouseExited(t->manualButton.setStyle("-fx-background-color:transparent;-fx-border-color:#949797"));

        manualButton.setOnMouseClicked(t->{
            HeadersCreateController controller = new HeadersCreateController(null);
            isContentEdited=true;
            controller.startWindow();
        });

        //buttonsHbox.getChildren().add(manualButton);

    }

    private void initIdentifierCombo(){
        ArrayList<String> infoHeaders=CSVHandler.getDetectedInfoHeaders();

    }




    private void initTreeView(){


        treeViewGroups=CSVHandler.getDetectedGroups();

        isPossible=new ArrayList<ArrayList<Boolean>>();

        for(Group group: treeViewGroups){  //init isPossible
            ArrayList<Boolean> possibles=new ArrayList<Boolean>();
            for(int i=0;i<group.getqCount();i++)
                possibles.add(true);
            isPossible.add(possibles);
        }


        choicesTreeView.setEditable(true);

        choicesTreeView.setCellFactory(t->new TreeViewCustomCell());

        constructChoicesTreeView(choicesTreeView);



        choicesTreeView.setShowRoot(false);
    }
    
    public static void constructChoicesTreeView(TreeView treeView){


        TreeItem<String> rootItem =
                new TreeItem<String>("Groups");

        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        

        for(Group group: treeViewGroups){
            TreeItem groupItem=new TreeItem<>(group.getCleanedName());
            groupItem.setExpanded(true);
            rootItem.getChildren().add(groupItem);
            TreeItem rangeItem=new TreeItem(group.getPossibleAnswers().get(0)+"-"+group.getPossibleAnswers().get(group.getPossibleAnswers().size()-1));
            groupItem.getChildren().add(rangeItem);

            for(String answer: group.getPossibleAnswers())
                rangeItem.getChildren().add(new TreeItem<>(answer));

        }

    }

    public static void addToGroup(String groupName){

        int i=0;
        for(Group group: treeViewGroups){  //group names are unique
            if(group.getCleanedName().equals(groupName)) {
                group.addAnswerToEnd();
                isPossible.get(i).add(true);
                break;
            }
            i++;
        }
    }

    public static void deleteFromGroup(String groupName,String deletedChoice){

        int i=0;
        for(Group group: treeViewGroups){  //group names are unique
            if(group.getCleanedName().equals(groupName)) {
                isPossible.get(i).set(group.getPossibleAnswers().indexOf(deletedChoice),false);
                break;
            }
            i++;
        }

    }

    public static void saveToTreeViewGroups(){

        for(int i=0;i<treeViewGroups.size();i++)
            treeViewGroups.get(i).updatePossibleAnswers(isPossible.get(i));

    }

    public static boolean isChoicePossible(String groupName,String choiceName){

        int i=0;
        for(Group group: treeViewGroups){  //group names are unique

            if(group.getCleanedName().equals(groupName))
                return isPossible.get(i).get(group.getPossibleAnswers().indexOf(choiceName));

            i++;
        }

        return true;
    }


    public void initChoicesTreeVBox(){

        choicesTreeVBox.getChildren().addAll(treeLabel,choicesTreeView);
        treeLabel.setFont(new Font("Arial", headersFontSize));

    }

    public static void restoreToGroup(String groupName,String restoredChoice){

        int i=0;
        for(Group group: treeViewGroups){  //group names are unique
            if(group.getCleanedName().equals(groupName)) {
                isPossible.get(i).set(group.getPossibleAnswers().indexOf(restoredChoice),true);
                break;
            }
            i++;
        }

    }

}



