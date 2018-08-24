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
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static Jowil.CSVHandler.NOT_AVAILABLE;
import static java.util.Arrays.asList;

public class GroupsController  extends Controller{


    //components

    @FXML
    Separator midSeparator;

    @FXML
    JFXTreeTableView groupsTable;
    
    @FXML
    VBox groupsTableVbox; 
    
    @FXML
    VBox choicesTreeVBox;


    static TreeView choicesTreeView=new TreeView();
    
    @FXML
    JFXButton manualButton;

    @FXML
    Label groupsLabel;

    @FXML
    Label treeLabel;







    JFXTreeTableColumn<Group,String> groupNamesCol = new JFXTreeTableColumn<>("Group");
    JFXTreeTableColumn<Group,String> qCountCol = new JFXTreeTableColumn<>("Questions Count");




    //data fields
    ObservableList<Group> tableGroups = FXCollections.observableArrayList();
    ArrayList<Group> detectedGroups;
    private static ArrayList<ArrayList<Boolean>> isPossible;
    private static ArrayList<Group> treeViewGroups;
    private static HashMap<String,Integer> groupsIndices=new HashMap<String,Integer>();

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
        midSeparator.setVisible(false);
        //initInfoGridPane();
        choicesTreeVBox.getChildren().add(choicesTreeView);
        treeLabel.setFont(new Font("Arial", headersFontSize));

    }


    @Override
    protected void updateSizes(){
        super.updateSizes();
        groupsTableVbox.setSpacing(resYToPixels(0.02));
        groupsTableVbox.setLayoutY(rootHeightToPixels(0.05));
        groupsTableVbox.setPadding(new Insets(0, 0, 0, 0));
        //groupsLabel.setPrefHeight(rootHeightToPixels(0.05));
        choicesTreeVBox.setSpacing(resYToPixels(0.02));
        choicesTreeVBox.setLayoutY(groupsTableVbox.getLayoutY());
        choicesTreeVBox.setPadding(groupsTableVbox.getPadding());
        groupsTable.setPrefHeight(rootHeightToPixels(0.67));
        groupsTable.setPrefWidth(rootWidthToPixels(0.27));
        choicesTreeView.setPrefHeight(rootHeightToPixels(0.67));
        choicesTreeView.setPrefWidth(rootWidthToPixels(0.27));

        //infoVBox.setLayoutX(groupsTableVbox.getLayoutX()+groupsTable.getPrefWidth()+rootWidthToPixels(0.03));

//        infoVBox.setLayoutY(groupsTableVbox.getLayoutY()+groupsLabel.getPrefHeight()+groupsTableVbox.getSpacing());
//        infoGridPane.setPrefWidth(groupsTable.getPrefWidth());
//        //infoGridPane.setPrefWidth(midSeparator.getLayoutX()-gridPaneContainer.getLayoutX()-rootWidthToPixels(0.02));
//        gridPaneContainer.setMaxWidth(infoGridPane.getPrefWidth());

        manualButton.setPrefHeight(navHeight);
        manualButton.setLayoutX(buttonsHbox.getLayoutX());
        manualButton.setLayoutY(buttonsHbox.getLayoutY()+buttonsHbox.getPadding().getTop());
        groupsTableVbox.setLayoutX(rootWidthToPixels(0.05));
        choicesTreeVBox.setLayoutX(rootWidthToPixels(0.95)-choicesTreeView.getPrefWidth());
        choicesTreeView.setLayoutY(groupsTable.getLayoutY());

        midSeparator.setLayoutX(rootWidthToPixels(0.5));
        midSeparator.setLayoutY(groupsTableVbox.getLayoutY());
        midSeparator.setPrefHeight(rootHeightToPixels(0.75));


        manualButton.toFront();






    }

    @Override
    protected Controller getNextController() {
        return new WeightsController(this);
    }


    @Override
    protected void buildComponentsGraph(){
        super.buildComponentsGraph();
        groupsTable.getColumns().addAll(groupNamesCol,qCountCol);

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

        groupsLabel.setFont(new Font("Arial", headersFontSize));


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


//    private void initInfoGridPane() {
//
//        Insets leftLabelsPadding=new Insets(resY*0.01,resX*0.015,resY*0.01,resX*0.002);
//        Insets rightLabelsPadding=new Insets(resY*0.01,resX*0.007,resY*0.01,resX*0.004);
//
//        Label [] labels={new Label("Number of Objective Questions"),new Label("Number of Objective Groups"),
//                new Label("Number of Subjective Questions"),new Label("Number of Students"),
//                new Label("Number of Forms"),new Label("Identifier Column"),new Label("Form Column")};
//
//        String [] values={Integer.toString(Statistics.getQuestionNames().size()),
//                Integer.toString(CSVHandler.getDetectedGroups().size()),Integer.toString(CSVHandler.getSubjQuestionsCount()),
//                Integer.toString(Statistics.getStudentAnswers().size()),Integer.toString(CSVHandler.getFormsCount()),
//        CSVHandler.getIdentifierColStartIndex()==NOT_AVAILABLE?"None":Statistics.getIdentifierName(),
//                CSVHandler.getFormColName()};
//
//
////        Label objCountLabel=new Label("Number of Objective Questions");
////        Label objGroupsCount=new Label("Number of Objective Groups");
////        Label subjCountLabel=new Label("Number of Subjective Questions");
////        Label studentsCountLabel=new Label("Number of Students");
////        Label formsCountLabel=new Label("Number of Forms");
////        Label identifierColLabel=new Label("Identifier Column");
////        Label formsColLabel=new Label("Form Column");
//
//        //infoGridPane.setAlignment(Pos.CENTER);
//
//
//
//        for (int i=0;i<labels.length;i++){
//            infoGridPane.add(labels[i],0,i);
//            labels[i].setPadding(leftLabelsPadding);
//            labels[i].setStyle("-fx-font-weight: bold;");
//            Label valueLabel=new Label(values[i]);
//            valueLabel.setPadding(rightLabelsPadding);
//            infoGridPane.add(valueLabel,1,i);
//        }
////        infoGridPane.setGridLinesVisible(true);
//
//
//
//        gridPaneContainer.getChildren().add(infoGridPane);
//
//
//        gridPaneContainer.setStyle("-fx-border-width:1;-fx-border-color:#A9A9A9;");
//
//
//
//    }

    private void initManualButton(){
        manualButton.getStyleClass().add("BlueJFXButton");
        manualButton.setOnMouseClicked(t->{

            new ManualModeController(null).startWindow();
        });
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



