package Jowil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class ViewGroupsAndSubjsController  extends Controller{


//    //same as Group class but modified to be compatible with the TableView class
//    public static class TableGroup{
//
//        private SimpleStringProperty groupName;
//        private SimpleStringProperty qCount;
//    }

    //components
    private TableView table = new TableView();
    TableColumn groupsCol = new TableColumn("Group");
    TableColumn qCountCol = new TableColumn("Number of Questions");
    final VBox tableVbox = new VBox();



    //data fields
    ObservableList<Group> Groups = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();


    //main methods
    ViewGroupsAndSubjsController(){
        super("ViewGroupsAndSubjs.fxml","Groups and Subjective Questions Review",1.25,1.25,true);
    }


    @Override
    protected void initComponents() {

    }

    @Override
    protected void updateSizes(){
        super.updateSizes();
    }

    //helper methods
    private void initTableVBox(){


        headersCol.setCellValueFactory(
                new PropertyValueFactory<HeadersEditController.Question,String>("header")
        );


        headersCol.setCellFactory(TextFieldTableCell.forTableColumn());
        headersCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<HeadersEditController.Question, String>>() {

                    public void handle(TableColumn.CellEditEvent<HeadersEditController.Question, String> t) {
                        ((HeadersEditController.Question) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setHeader(t.getNewValue());
                    }
                }
        );

        weightsCol.setCellValueFactory(
                new PropertyValueFactory<HeadersEditController.Question,String>("weight")
        );

        weightsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weightsCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<HeadersEditController.Question, String>>() {

                    public void handle(TableColumn.CellEditEvent<HeadersEditController.Question, String> t) {
                        String w=t.getNewValue().trim();
                        if(isValidDouble(w))
                            ((HeadersEditController.Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(w);
                        else //return to old text before editing
                            ((HeadersEditController.Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(t.getOldValue());t.getTableView().refresh();
                    }
                }
        );


        final Label label = new Label("Questions");
        label.setFont(new Font("Arial", 20));

        for(int i=0;i<headers.size();i++)
            questions.add(new HeadersEditController.Question(headers.get(i),"1.0"));

        initAddHBox();
        deleteButton.setStyle("-fx-background-color:red;-fx-text-fill: white;");
        table.setEditable(true);
        table.setItems(questions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(headersCol,weightsCol);
        tableVbox.getChildren().addAll(label, table,weightsHBox,deleteButton);
        rootPane.getChildren().addAll(tableVbox);
        headersCol.setSortable(false);
        headersCol.setSortType(TableColumn.SortType.ASCENDING);
        weightsCol.setSortable(true);

    }

}
