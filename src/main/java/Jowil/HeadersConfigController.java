package Jowil;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class HeadersConfigController extends Controller{



    ObservableList<Question> questions = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();
    private TableView table = new TableView();
    TableColumn headersCol = new TableColumn("Question");
    TableColumn weightsCol = new TableColumn("Weight");
    final VBox tableVbox = new VBox();
    final Button addButton = new Button("Add Group");
    final HBox addHBox= new HBox();
    final Button deleteButton= new Button("Delete Selection");



    public static class Question {


        private SimpleStringProperty header;
        private SimpleStringProperty weight;
        private Question(String header, String weight){
            this.header=new SimpleStringProperty(header);
            this.weight=new SimpleStringProperty(weight);
        }

        public String getHeader() {
            return header.get();
        }

        public SimpleStringProperty headerProperty() {
            return header;
        }

        public void setHeader(String header) {
            this.header.set(header);
        }

        public String getWeight() {
            return weight.get();
        }

        public SimpleStringProperty weightProperty() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight.set(weight);
        }
    }


    HeadersConfigController(){
        super("HeadersConfig.fxml","Headers and Weights",1.3,1.3,true);
    }


    protected void updateSizes() {
        super.updateSizes();
        tableVbox.setSpacing(resY/50);
        tableVbox.setAlignment(Pos.CENTER);
        tableVbox.setPadding(new Insets(rootHeight/20, 0, 0, rootWidth/10));
        table.setPrefHeight(rootHeight/1.5);
        addHBox.setSpacing(resX/400);
        //deleteButton.setLayoutX(rootWidth/10+addHBox.getWidth()/3);
        deleteButton.setPrefWidth(addHBox.getWidth()/1.5);

    }

    protected void initComponents() {
        initTableVBox();
    }







    public void initTableVBox(){

        final Label label = new Label("Questions");
        label.setFont(new Font("Arial", 20));


        for(int i=0;i<headers.size();i++)
            questions.add(new Question(headers.get(i),"1.0"));


        headersCol.setCellValueFactory(
                new PropertyValueFactory<Question,String>("header")
        );


        headersCol.setCellFactory(TextFieldTableCell.forTableColumn());
        headersCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Question, String>>() {

                    public void handle(TableColumn.CellEditEvent<Question, String> t) {
                        ((Question) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setHeader(t.getNewValue());
                    }
                }
        );

        weightsCol.setCellValueFactory(
                new PropertyValueFactory<Question,String>("weight")
        );

        weightsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weightsCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Question, String>>() {

                    public void handle(TableColumn.CellEditEvent<Question, String> t) {
                        String w=t.getNewValue().trim();
                        try
                        {
                            Double.parseDouble(w);
                        }
                        catch(NumberFormatException e)
                        {
                            ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(t.getOldValue());
                            t.getTableView().refresh();
                            return;
                        }
                        ((Question) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setWeight(w);
                    }
                }
        );

        initAddHBox();
        deleteButton.setStyle("-fx-background-color:red;-fx-text-fill: white;");
        table.setEditable(true);
        table.setItems(questions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(headersCol,weightsCol);
        tableVbox.getChildren().addAll(label, table,addHBox,deleteButton);
        rootPane.getChildren().addAll(tableVbox);
        headersCol.setSortable(false);
        headersCol.setSortType(TableColumn.SortType.ASCENDING);
        weightsCol.setSortable(true);

    }

    private void initAddHBox(){

        final TextField groupName = new TextField();
        groupName.setPromptText("Group Name");
        final TextField questionsCount = new TextField();
        questionsCount.setPromptText("Number of questions");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                for (int i = 1; i <= Integer.parseInt(questionsCount.getText()); i++)
                    questions.add(new Question(groupName.getText() + Integer.toString(i), "1.0"));
                table.refresh();
            }

        });

        addButton.setStyle("-fx-background-color:#3CB371;-fx-text-fill: white;");
        addHBox.getChildren().addAll(groupName, questionsCount,addButton);


    }
}
