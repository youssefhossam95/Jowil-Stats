package Jowil;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.util.ArrayList;

public class WeightsController extends Controller{


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

    ///fields

    ObservableList<Question> questions = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();
    private TableView table = new TableView();
    TableColumn headersCol = new TableColumn("Question");
    TableColumn weightsCol = new TableColumn("Weight");
    final VBox tableVbox = new VBox();
    final Button updateWeightsButton = new Button("Update Selected Weights");
    final HBox weightsHBox= new HBox();

    //essentials

    WeightsController(Controller back){
        super("Weights.fxml","Weights",1.25,1.25,true,back);
    }


    protected void updateSizes() {
        super.updateSizes();
        tableVbox.setSpacing(resY/50);
        tableVbox.setAlignment(Pos.CENTER);
        tableVbox.setPadding(new Insets(rootHeight/30, 0, 0, rootWidth/20));
        table.setPrefHeight(rootHeight/1.5);
        weightsHBox.setSpacing(resX/400);
        //deleteButton.setLayoutX(rootWidth/10+addHBox.getWidth()/3);
    }

    protected void initComponents() {
        initTableVBox();
    }


    @Override
    protected Controller getNextController() {

        return null;
    }


    //helper methods

    private void initTableVBox(){


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
                        if(isValidDouble(w))
                            ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(w);
                        else //return to old text before editing
                            ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(t.getOldValue());t.getTableView().refresh();
                    }
                }
        );


        final Label label = new Label("Questions");
        label.setFont(new Font("Arial", 20));

        for(int i=0;i<headers.size();i++)
            questions.add(new Question(headers.get(i),"1.0"));

        initAddHBox();

        table.setEditable(true);
        table.setItems(questions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(headersCol,weightsCol);
        tableVbox.getChildren().addAll(label, table,weightsHBox);
        rootPane.getChildren().addAll(tableVbox);
        headersCol.setSortable(false);
        headersCol.setSortType(TableColumn.SortType.ASCENDING);
        weightsCol.setSortable(true);

    }

    private void initAddHBox(){

        final TextField newWeight = new TextField();
        updateWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String w=newWeight.getText().trim();
                if(!isValidDouble(w)){
                    showAlert(Alert.AlertType.ERROR,stage.getOwner(),"Invalid weight value","Cannot update weights: weight value \"" + w+"\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected=table.getSelectionModel().getSelectedCells();
                for(int i=0;i<selected.size();i++)
                    ((Question) table.getItems().get(selected.get(i).getRow())).setWeight(newWeight.getText());

                table.refresh();

            }
        });

        newWeight.setPromptText("New weight");
        updateWeightsButton.setStyle("-fx-background-color:#4169E1;-fx-text-fill: white;");
        weightsHBox.getChildren().addAll(newWeight,updateWeightsButton);

    }

    protected void saveChanges(){

    }
}
