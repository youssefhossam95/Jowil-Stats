package Jowil;


import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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


    public class SubjQuestion{

        private SimpleStringProperty name;
        private SimpleStringProperty maxScore=new SimpleStringProperty("10");

        SubjQuestion(String name, String maxScore){
            this.name=new SimpleStringProperty("Subjective Question "+name);
            this.maxScore=new SimpleStringProperty(maxScore);
        }

        public String getMaxScore() {
            return maxScore.get();
        }

        public SimpleStringProperty maxScoreProperty() {
            return maxScore;
        }

        public void setMaxScore(String maxScore) {
            this.maxScore.set(maxScore);
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

    }


    ///fields

    @FXML
    JFXButton forwardButton;

    @FXML
    JFXButton backwardButton;


    ObservableList<Question> questions = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();
    private TableView table = new TableView();
    TableColumn headersCol = new TableColumn("Question");
    TableColumn weightsCol = new TableColumn("Weight");
    final VBox weightsTableVbox = new VBox();
    final HBox objHBox= new HBox();
    private VBox subjTableVbox=new VBox();
    private TableView subjTable= new TableView();
    TableColumn subjNamesCol = new TableColumn("Question");
    TableColumn maxScoreCol = new TableColumn("Weight");
    ObservableList<SubjQuestion> subjQuestions = FXCollections.observableArrayList();
    private HBox tablesHbox= new HBox();
    final HBox subjHBox= new HBox();
    final HBox navigationHBox=new HBox();



    //essentials

    WeightsController(Controller back){
        super("Weights.fxml","Weights",1.25,1.25,true,back);
    }


    protected void updateSizes() {
        super.updateSizes();
        weightsTableVbox.setSpacing(resY/50);
        //weightsTableVbox.setAlignment(Pos.CENTER);
        weightsTableVbox.setPadding(new Insets(rootHeight/20, 0, 0, rootWidth/20));
        table.setPrefHeight(rootHeight/1.5);
        //weightsTableVbox.setPrefWidth(rootWidth/3.2);
        objHBox.setSpacing(resX/200);
        subjHBox.setSpacing(resX/200);
        subjTable.setPrefHeight(rootHeight/1.5);
        //subjTableVbox.setPrefWidth(rootWidth/3.2);
        subjTableVbox.setSpacing(resY/50);
        tablesHbox.setSpacing(rootWidth-2*objHBox.getWidth()-2*rootWidth/20);
        subjTableVbox.setPadding(new Insets(rootHeight/20,0, 0, 0));
        navigationHBox.setLayoutX(table.getWidth()+rootWidth/18);
        navigationHBox.setLayoutY(table.getLayoutY()+table.getPrefHeight()+rootHeight/20-forwardButton.getHeight());
        forwardButton.setPadding(Insets.EMPTY);
        backwardButton.setPadding(Insets.EMPTY);
        //deleteButton.setLayoutX(rootWidth/10+addHBox.getWidth()/3);
    }

    protected void initComponents() {
        initWeightsHBoxes();
        initWeightsTableVBox();
        initSubjTableVBox();
        initTablesHBox();
        initNavigationHBox();


    }


    @Override
    protected Controller getNextController() {

        return null;
    }


    //helper methods

    private void initWeightsTableVBox(){


        headersCol.setCellValueFactory(
                new PropertyValueFactory<Question,String>("header")
        );


        headersCol.setCellFactory(TextFieldTableCell.forTableColumn());


        weightsCol.setCellValueFactory(
                new PropertyValueFactory<Question,String>("weight")
        );

        weightsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weightsCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Question, String>>() {

                    public void handle(TableColumn.CellEditEvent<Question, String> t) {
                        String w;
                        if((w=tryDouble(t.getNewValue()))!=null)
                            ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(w);
                        else //return to old text before editing
                            ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWeight(t.getOldValue());t.getTableView().refresh();
                    }
                }
        );


        final Label label = new Label("Objective Questions");
        label.setFont(new Font("Arial", 20));

        for(int i=0;i<headers.size();i++)
            questions.add(new Question(headers.get(i),"1.0"));


        table.setEditable(true);
        table.setItems(questions);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(headersCol,weightsCol);
        weightsTableVbox.getChildren().addAll(label, table,objHBox);
        headersCol.setSortable(false);
        headersCol.setEditable(false);
        headersCol.setSortType(TableColumn.SortType.ASCENDING);
        weightsCol.setSortable(true);
        tablesHbox.getChildren().add(weightsTableVbox);

    }

    private void initWeightsHBoxes(){

        //objective hbox

        final TextField objWeightText = new TextField();
        objWeightText.setPromptText("New weight");
        final Button objWeightsButton = new Button("Update Selected Weights");
        objWeightsButton.setStyle("-fx-background-color:#4169E1;-fx-text-fill: white;");
        objWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                String w;
                if((w=tryDouble(objWeightText.getText()))==null){
                    showAlert(Alert.AlertType.ERROR,stage.getOwner(),"Invalid weight value","Cannot update objective weights: weight value \"" + objWeightText.getText()+"\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected=table.getSelectionModel().getSelectedCells();
                for(int i=0;i<selected.size();i++)
                    ((Question) table.getItems().get(selected.get(i).getRow())).setWeight(w);

                table.refresh();

            }
        });
        objHBox.getChildren().addAll(objWeightText,objWeightsButton);

        ////subjective hbox

        final TextField subjWeightText = new TextField();
        subjWeightText.setPromptText("New weight");
        final Button subjWeightsButton = new Button("Update Selected Weights");
        subjWeightsButton.setStyle("-fx-background-color:#4169E1;-fx-text-fill: white;");
        subjWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String w;
                if((w=tryDouble(subjWeightText.getText()))==null){
                    showAlert(Alert.AlertType.ERROR,stage.getOwner(),"Invalid weight value","Cannot update subjective weights: weight value \"" + subjWeightText.getText()+"\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected=subjTable.getSelectionModel().getSelectedCells();
                for(int i=0;i<selected.size();i++)
                    ((SubjQuestion) subjTable.getItems().get(selected.get(i).getRow())).setMaxScore(w);

                subjTable.refresh();

            }
        });
        subjHBox.getChildren().addAll(subjWeightText,subjWeightsButton);
        if(CSVHandler.getSubjQuestionsCount()==0){
            subjWeightsButton.setDisable(true);
            subjWeightText.setDisable(true);
        }

    }

    private void initSubjTableVBox(){


        subjNamesCol.setCellValueFactory(
                new PropertyValueFactory<SubjQuestion,String>("name")
        );


        subjNamesCol.setCellFactory(TextFieldTableCell.forTableColumn());




        maxScoreCol.setCellValueFactory(
                new PropertyValueFactory<WeightsController.SubjQuestion,String>("maxScore")
        );

        maxScoreCol.setCellFactory(TextFieldTableCell.forTableColumn());

        maxScoreCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<SubjQuestion,String>>) t-> {

            String w;
            if((w=tryDouble(t.getNewValue()))==null){
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(t.getOldValue());
                subjTable.refresh();
            }
            else
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(w);

        });

        final Label label = new Label("Subjective Questions");
        label.setFont(new Font("Arial", 20));

        for(int i=0;i<CSVHandler.getSubjQuestionsCount();i++)
            subjQuestions.add(new SubjQuestion(Integer.toString(i+1),"10"));



        subjTable.setEditable(true);
        subjTable.setItems(subjQuestions);
        subjTable.setPlaceholder(new Label("No subjective questions detected"));
        //subjTable.setStyle("-fx-border-color:#1E90FF");
        subjTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        subjTable.getColumns().addAll(subjNamesCol,maxScoreCol);
        subjTableVbox.getChildren().addAll(label, subjTable,subjHBox);
        tablesHbox.getChildren().add(subjTableVbox);
        subjNamesCol.setSortable(false);
        maxScoreCol.setSortable(false);
        subjNamesCol.setEditable(false);



    }

    private void initTablesHBox(){
        rootPane.getChildren().add(tablesHbox);
    }

    private void initNavigationHBox(){

        navigationHBox.getChildren().addAll(backwardButton,forwardButton);
        forwardButton.setStyle("-fx-border-width:0;fx-background-color:transparent");
        backwardButton.setStyle("-fx-border-width:0;fx-background-color:transparent");


        rootPane.getChildren().add(navigationHBox);
    }
    protected void saveChanges(){


        ArrayList<Double> maxSubjScores = new ArrayList<>();
        for (SubjQuestion question : subjQuestions)
            maxSubjScores.add(Double.parseDouble(question.getMaxScore()));

        Statistics.setSubjMaxScores(maxSubjScores);
    }




}
