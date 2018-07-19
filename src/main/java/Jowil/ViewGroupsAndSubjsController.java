package Jowil;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class ViewGroupsAndSubjsController  extends Controller{


    //components
    private TableView groupsTable = new TableView();
    TableColumn groupNamesCol = new TableColumn("Group");
    TableColumn qCountCol = new TableColumn("Number of Questions");
    final VBox groupsTableVbox = new VBox();
    private HBox tablesHbox= new HBox();
    private JFXButton manualButton= new JFXButton("Edit Manually");
    final Label label = new Label("Groups");




    //data fields
    ObservableList<Group> tableGroups = FXCollections.observableArrayList();
    ArrayList<Group> detectedGroups=CSVHandler.getDetectedGroups();

    //main methods
    ViewGroupsAndSubjsController(Controller back){
        super("ViewGroupsAndSubjs.fxml","Groups and Subjective Questions",1.25,1.25,true,back);
    }


    @Override
    protected void initComponents() {
        initGroupsTableVBox();
        initManualButton();
        initTablesHBox();
    }

    @Override
    protected void updateSizes(){
        super.updateSizes();
        groupsTableVbox.setSpacing(resY/50);
        //groupsTableVbox.setAlignment(Pos.CENTER);
        groupsTableVbox.setPadding(new Insets(rootHeight/20, 0, 0, rootWidth/20));
        groupsTable.setPrefHeight(rootHeight/1.5);
        groupsTable.setPrefWidth(rootWidth/3.2);

//        nextButton.setLayoutX(rootWidth/1.185);
//        nextButton.setLayoutY(rootHeight/1.17);
        //manualButton.setPrefWidth(resX/15);
        manualButton.setPrefHeight(resX/250);
        manualButton.setLayoutX(buttonsHbox.getLayoutX());
        manualButton.setLayoutY(buttonsHbox.getLayoutY()+buttonsHbox.getPadding().getTop());

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
        tablesHbox.getChildren().add(groupsTableVbox);
        rootPane.getChildren().add(tablesHbox);
        rootPane.getChildren().add(manualButton);

    }


    //helper methods
    private void initGroupsTableVBox(){


        groupNamesCol.setCellValueFactory(
                new PropertyValueFactory<Group,String>("nameProp")
        );


        groupNamesCol.setCellFactory(TextFieldTableCell.forTableColumn());

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
        
        label.setFont(new Font("Arial", headersFontSize));

        for(int i=0;i<detectedGroups.size();i++)
            tableGroups.add(detectedGroups.get(i));

        
        groupsTable.setEditable(true);
        groupsTable.setItems(tableGroups);
        groupsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //groupsTable.setStyle("-fx-border-color:#1E90FF");
        groupNamesCol.setSortable(false);
        qCountCol.setSortable(false);
        qCountCol.setEditable(false);
    }



    private void initManualButton(){
        manualButton.setStyle("-fx-background-color:#4169E1;-fx-text-fill: white;");
//        manualButton.setOnMouseEntered(t->manualButton.setStyle("-fx-background-color:#878a8a;"));
//        manualButton.setOnMouseExited(t->manualButton.setStyle("-fx-background-color:transparent;-fx-border-color:#949797"));

        manualButton.setOnMouseClicked(t->{
            HeadersCreateController controller = new HeadersCreateController(null);
            isContentEdited=true;
            controller.startWindow();
        });

        //buttonsHbox.getChildren().add(manualButton);

    }




    private void initTablesHBox(){

    }
    
    


    protected void saveChanges() {


        ArrayList<String> newHeaders = new ArrayList<>();

        for (Group group : tableGroups) {

            for (int i = 0; i < Integer.parseInt(group.getqCountProp()); i++)
                newHeaders.add(group.getNameProp() + (i + 1));

            CSVHandler.setDetectedQHeaders(newHeaders);
        }

    }
}
