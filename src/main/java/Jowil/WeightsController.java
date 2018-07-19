package Jowil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;;
import javafx.geometry.Insets;
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


    //components

    private TableView objTable = new TableView();
    TableColumn objHeadersCol = new TableColumn("Question");
    ArrayList<TableColumn> objWeightsCols = new ArrayList<TableColumn>();
    TableColumn objWeightsCol=new TableColumn("Weight");
    final VBox objTableVbox = new VBox();
    final HBox objHBox= new HBox();
    private VBox subjTableVbox=new VBox();
    private TableView subjTable= new TableView();
    TableColumn subjNamesCol = new TableColumn("Question");
    TableColumn subjWeightsCol = new TableColumn("Weight");
    private HBox objTablesHbox= new HBox();
    final HBox subjHBox= new HBox();
    final TextField objWeightText = new TextField();
    final Button objWeightsButton = new Button("Update Selected Weights");
    final TextField subjWeightText = new TextField();
    final Button subjWeightsButton = new Button("Update Selected Weights");
    final Label subjLabel = new Label("Subjective Questions");
    final Label objLabel = new Label("Objective Questions");



    ///data fields
    ObservableList<Question> objQuestions = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();
    ObservableList<SubjQuestion> subjQuestions = FXCollections.observableArrayList();








    //Main methods

    WeightsController(Controller back){
        super("Weights.fxml","Weights",1.25,1.25,true,back);
    }


    protected void updateSizes() {
        super.updateSizes();
        objTableVbox.setSpacing(resY/50);
        //objTableVbox.setAlignment(Pos.CENTER);
        objTableVbox.setPadding(new Insets(rootHeight/20, 0, 0, rootWidth/20));
        objTable.setPrefHeight(rootHeight/1.5);
        //objTableVbox.setPrefWidth(rootWidth/3.2);
        objHBox.setSpacing(resX/200);
        subjHBox.setSpacing(resX/200);
        subjTable.setPrefHeight(rootHeight/1.5);
        //subjTableVbox.setPrefWidth(rootWidth/3.2);
        subjTableVbox.setSpacing(resY/50);
        objTablesHbox.setSpacing(rootWidth-2*objHBox.getWidth()-2*rootWidth/20);
        subjTableVbox.setPadding(new Insets(rootHeight/20,0, 0, 0));

        //deleteButton.setLayoutX(rootWidth/10+addHBox.getWidth()/3);
    }

    protected void initComponents() {
        initWeightsHBoxes();
        initWeightsTableVBox();
        initSubjTableVBox();
    }


    @Override
    protected Controller getNextController() {

        return null;
    }

    @Override
    protected void saveChanges(){

        ArrayList<Double> maxSubjScores = new ArrayList<>();
        for (SubjQuestion question : subjQuestions)
            maxSubjScores.add(Double.parseDouble(question.getMaxScore()));

        Statistics.setSubjMaxScores(maxSubjScores);
    }

    @Override
    protected void buildComponentsGraph(){

        super.buildComponentsGraph();

        objHBox.getChildren().addAll(objWeightText,objWeightsButton);
        subjHBox.getChildren().addAll(subjWeightText,subjWeightsButton);

        objTable.getColumns().addAll(objHeadersCol,objWeightsCol);
        subjTable.getColumns().addAll(subjNamesCol,subjWeightsCol);


        objTableVbox.getChildren().addAll(objLabel, objTable,objHBox);
        subjTableVbox.getChildren().addAll(subjLabel, subjTable,subjHBox);


        objTablesHbox.getChildren().add(objTableVbox);
        objTablesHbox.getChildren().add(subjTableVbox);

        rootPane.getChildren().add(objTablesHbox);

    }

    //helper methods

    private void initWeightsTableVBox(){


        objHeadersCol.setCellValueFactory(
                new PropertyValueFactory<Question,String>("header")
        );


        objHeadersCol.setCellFactory(TextFieldTableCell.forTableColumn());



        objWeightsCol.setCellValueFactory( new PropertyValueFactory<Question,String>("weight"));


        objWeightsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        objWeightsCol.setOnEditCommit(
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


        objLabel.setFont(new Font("Arial", headersFontSize));

        for(int i=0;i<headers.size();i++)
            objQuestions.add(new Question(headers.get(i),"1.0"));


        objTable.setEditable(true);
        objTable.setItems(objQuestions);
        objTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        objTable.getSelectionModel().setCellSelectionEnabled(true);
        objTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(() -> objTable.refresh());
        objHeadersCol.setSortable(false);
        objHeadersCol.setEditable(false);
        objHeadersCol.setSortType(TableColumn.SortType.ASCENDING);
        objWeightsCol.setSortable(false);


    }

    private void initWeightsHBoxes(){

        //objective hbox

        objWeightText.setPromptText("New weight");
        objWeightsButton.setStyle("-fx-background-color:#4169E1;-fx-text-fill: white;");
        objWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                String w;
                if((w=tryDouble(objWeightText.getText()))==null){
                    showAlert(Alert.AlertType.ERROR,stage.getOwner(),"Invalid weight value","Cannot update objective weights: weight value \"" + objWeightText.getText()+"\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected=objTable.getSelectionModel().getSelectedCells();
                for(int i=0;i<selected.size();i++)
                    ((Question) objTable.getItems().get(selected.get(i).getRow())).setWeight(w);

                objTable.refresh();

            }
        });

        ////subjective hbox

        subjWeightText.setPromptText("New weight");
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




        subjWeightsCol.setCellValueFactory(
                new PropertyValueFactory<WeightsController.SubjQuestion,String>("maxScore")
        );

        subjWeightsCol.setCellFactory(TextFieldTableCell.forTableColumn());

        subjWeightsCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<SubjQuestion,String>>) t-> {

            String w;
            if((w=tryDouble(t.getNewValue()))==null){
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(t.getOldValue());
                subjTable.refresh();
            }
            else
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(w);

        });

        subjLabel.setFont(new Font("Arial", headersFontSize));

        for(int i=0;i<CSVHandler.getSubjQuestionsCount();i++)
            subjQuestions.add(new SubjQuestion(Integer.toString(i+1),"10"));



        subjTable.setEditable(true);
        subjTable.setItems(subjQuestions);
        subjTable.setPlaceholder(new Label("No subjective objQuestions detected"));
        //subjTable.setStyle("-fx-border-color:#1E90FF");
        subjTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(()->subjTable.refresh());
        subjNamesCol.setSortable(false);
        subjWeightsCol.setSortable(false);
        subjNamesCol.setEditable(false);

    }








}
