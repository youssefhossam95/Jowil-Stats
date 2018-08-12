package Jowil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
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

    private TableView<ObservableList<StringProperty>> objTable = new TableView();
    TableColumn objHeadersCol = new TableColumn("Question");
    TableColumn objWeightsCol=new TableColumn("Weight");
    final VBox objTableVbox = new VBox();
    final HBox objHBox= new HBox();
    private VBox subjTableVbox=new VBox();
    private TableView subjTable= new TableView();
    TableColumn subjNamesCol = new TableColumn("Question");
    TableColumn subjWeightsCol = new TableColumn("Weight");
    private HBox tablesHbox= new HBox();
    final HBox subjHBox= new HBox();
    final TextField objWeightText = new TextField();
    final Button objWeightsButton = new Button("Update Selected Weights");
    final TextField subjWeightText = new TextField();
    final Button subjWeightsButton = new Button("Update Selected Weights");
    final Label subjLabel = new Label("Subjective Questions");
    final Label objLabel = new Label("Objective Questions");



    ///data fields
    ObservableList<ObservableList<StringProperty>> objQuestions = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();
    ObservableList<SubjQuestion> subjQuestions = FXCollections.observableArrayList();








    //Main methods

    WeightsController(Controller back){
        super("Weights.fxml","Weights",1.25,1.25,true,back);
    }


    protected void updateSizes() {
        super.updateSizes();
        objTableVbox.setSpacing(resYToPixels(0.015));
        objTableVbox.setPadding(new Insets(rootHeightToPixels(0.04), 0, 0, rootWidthToPixels(0.05)));
        objTable.setPrefHeight(rootHeightToPixels(0.67));
        objHBox.setSpacing(resXToPixels(0.005));
        subjHBox.setSpacing(resXToPixels(0.005));
        subjTable.setPrefHeight(rootHeightToPixels(0.67));
        subjTableVbox.setSpacing(resYToPixels(0.015));
        tablesHbox.setSpacing(rootWidth-2*objHBox.getWidth()-2*rootWidth/20);
        subjTableVbox.setPadding(new Insets(rootHeightToPixels(0.04),0, 0, 0));
        objTable.setPrefWidth(objHBox.getWidth());
        if(objTable.getPrefWidth()>getObjTableColumnsWidth())
            objTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        else
            objTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


        //deleteButton.setLayoutX(rootWidth/10+addHBox.getWidth()/3);
    }

    protected void initComponents() {
        initWeightsHBoxes();
        initObjTableVBox();
        initSubjTableVBox();
    }


    @Override
    protected Controller getNextController() {

        return new GradeBoundariesController(this);
    }

    @Override
    protected void saveChanges(){

        //save objective weights
        ArrayList<ArrayList<Double>> objWeights= new ArrayList<>();


        for (int i = 1; i < objQuestions.get(0).size(); i++)
        {
            objWeights.add(new ArrayList<Double>());
            for (int j = 0; j < objQuestions.size(); j++)
                objWeights.get(objWeights.size() - 1).add(Double.parseDouble(objQuestions.get(j).get(i).get()));

        }


        Statistics.setQuestionWeights(objWeights);


        //Save subjective weights
        ArrayList<Double> maxSubjScores = new ArrayList<>();
        for (SubjQuestion question : subjQuestions)
            maxSubjScores.add(Double.parseDouble(question.getMaxScore()));

        Statistics.setSubjMaxScores(maxSubjScores);

        Statistics.initMaxScore();

    }

    @Override
    protected void buildComponentsGraph(){

        super.buildComponentsGraph();

        objHBox.getChildren().addAll(objWeightText,objWeightsButton);
        subjHBox.getChildren().addAll(subjWeightText,subjWeightsButton);

//        objTable.getColumns().addAll(objHeadersCol,objWeightsCol); objTable construction in "populateObjTable" method
        subjTable.getColumns().addAll(subjNamesCol,subjWeightsCol);


        objTableVbox.getChildren().addAll(objLabel, objTable,objHBox);
        subjTableVbox.getChildren().addAll(subjLabel, subjTable,subjHBox);


        tablesHbox.getChildren().add(objTableVbox);
        tablesHbox.getChildren().add(subjTableVbox);

        rootPane.getChildren().add(tablesHbox);

    }

    @Override
    protected void stabalizeTables(){
        disableTableDrag(subjTable);
        disableTableDrag(objTable);
    }

    //helper methods

    private void initObjTableVBox(){

        objLabel.setFont(new Font("Arial", headersFontSize));

//        for(int i=0;i<headers.size();i++)
//            objQuestions.add(new Question(headers.get(i),"1.0"));



        populateObjTable();
        objTable.setEditable(true);
        objTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        objTable.getSelectionModel().setCellSelectionEnabled(true);
//        objTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(() -> objTable.refresh());



    }

    private void initWeightsHBoxes(){

        //objective hbox

        objWeightText.setPromptText("New weight");
        objWeightsButton.getStyleClass().add("BlueJFXButton");
        objWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                String w;
                if((w=tryDouble(objWeightText.getText()))==null){
                    showAlert(Alert.AlertType.ERROR,stage.getOwner(),"Invalid weight value","Cannot update objective weights: weight value \"" + objWeightText.getText()+"\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected=objTable.getSelectionModel().getSelectedCells();
                for(int i=0;i<selected.size();i++) {
                    int row=selected.get(i).getRow();
                    int col=selected.get(i).getColumn();
                    if(col==0)
                        continue;

                    objTable.getItems().get(row).set(col,new SimpleStringProperty(w));
                }

                objTable.refresh();

            }
        });

        ////subjective hbox

        subjWeightText.setPromptText("New weight");
        subjWeightsButton.getStyleClass().add("BlueJFXButton");
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


        subjNamesCol.setCellFactory((t) -> EditCell.createStringEditCell());




        subjWeightsCol.setCellValueFactory(
                new PropertyValueFactory<WeightsController.SubjQuestion,String>("maxScore")
        );

        subjWeightsCol.setCellFactory((t) -> EditCell.createStringEditCell());

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
            subjQuestions.add(new SubjQuestion(Integer.toString(i+1),"10.0"));



        subjTable.setEditable(true);
        subjTable.setItems(subjQuestions);
        subjTable.setPlaceholder(new Label("No subjective  Questions detected"));
        //subjTable.setStyle("-fx-border-color:#1E90FF");
        subjTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(()->subjTable.refresh());
        subjNamesCol.setSortable(false);
        subjWeightsCol.setSortable(false);
        subjNamesCol.setEditable(false);

    }

    private void populateObjTable() {

        ObservableList<StringProperty> headers = FXCollections.observableArrayList();

        //initialize table
        objTable.getItems().clear();
        objTable.getColumns().clear();
        objTable.setPlaceholder(new Label("Loading..."));
        objTable.setOnKeyPressed(event -> {
            TablePosition<ObservableList<StringProperty>, ?> pos = objTable.getFocusModel().getFocusedCell() ;
            if (pos != null && event.getCode().isDigitKey()) {
                objTable.edit(pos.getRow(), pos.getTableColumn());
            }
        });

        initializeObjQuestions();
        objTable.setItems(objQuestions);

        //add Question column
        objTable.getColumns().add(createColumn(0, "Question"));
        objTable.getColumns().get(0).setEditable(false);
        objTable.getColumns().get(0).setSortable(false);


        int formsCount=CSVHandler.getFormsCount();
        //add Weight columns
        if(formsCount==1) {
            objTable.getColumns().add(createColumn(1, "Weight"));
        }
        else{
//            TableColumn weightCol=new TableColumn<>("Weight");
//            objTable.getColumns().add(weightCol);
            for (int i=0; i< formsCount; i++) {
                //weightCol.getColumns().add(createColumn(i+1, ""));
                objTable.getColumns().add(createColumn(i+1, ""));
            }
        }







    }

    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Form " + (columnIndex)+" Weight";
        } else {
            title = columnTitle;
        }

        column.setCellFactory((t) -> EditCell.createStringEditCell());
        column.setSortable(true);


        column.setText(title);
        column
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(
                            TableColumn.CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
                        ObservableList<StringProperty> values = cellDataFeatures.getValue();
                        if (columnIndex >= values.size()) {
                            return new SimpleStringProperty("");
                        } else {
                            return cellDataFeatures.getValue().get(columnIndex);
                        }
                    }
                });



        column.setOnEditCommit( t->{
            String w;
            int row=t.getTablePosition().getRow();
            int col=t.getTablePosition().getColumn();
            if((w=tryDouble(t.getNewValue()))!=null) {
                 t.getTableView().getItems().get(row).set(col,new SimpleStringProperty(w));
                 objTable.refresh();
               System.out.println(objTable.getItems());
            }
            else { //return to old text before editing
                t.getTableView().getItems().get(row).set(col, new SimpleStringProperty(t.getOldValue()));
                objTable.refresh();
            }
        }


        );



        return column;
    }

    private void initializeObjQuestions(){

        for(int i=0;i<CSVHandler.getDetectedQHeaders().size();i++){
            ObservableList<StringProperty> row= FXCollections.observableArrayList();
            row.add(new SimpleStringProperty(CSVHandler.getDetectedQHeaders().get(i)));
            for(int j=0;j<CSVHandler.getFormsCount();j++)
                row.add(new SimpleStringProperty("1.0"));
            objQuestions.add(row);
        }

    }

    private double getObjTableColumnsWidth(){
        int totalWidth=0;
        for(TableColumn column: objTable.getColumns())
            totalWidth+=column.getWidth();
        return totalWidth;
    }

    
    








}
