package Jowil;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static Jowil.ManualModeController.isIgnoreSavedObjectiveWeights;

public class WeightsController extends Controller {

    public static class Grade extends RecursiveTreeObject<Grade> {


        private SimpleStringProperty gradeName;
        private SimpleStringProperty minPercentScore;
        private SimpleStringProperty frequency;
        private SimpleStringProperty percentage;


        Grade(String name, String score) {

            this.gradeName = new SimpleStringProperty(name);
            this.minPercentScore = new SimpleStringProperty(score);
        }

        Grade(String name, String score,String frequency) {

            this.gradeName = new SimpleStringProperty(name);
            this.minPercentScore = new SimpleStringProperty(score);
            this.frequency=new SimpleStringProperty(frequency);

            double percent=Double.parseDouble(frequency)/Statistics.getStudentAnswers().size()*100;
            this.percentage=new SimpleStringProperty(String.format("%.1f",percent)+"%");
        }



        public String getGradeName() {
            return gradeName.get();
        }

        public SimpleStringProperty gradeNameProperty() {
            return gradeName;
        }

        public void setGradeName(String gradeName) {
            this.gradeName.set(gradeName);
        }

        public String getMinPercentScore() {
            return minPercentScore.get();
        }

        public SimpleStringProperty minPercentScoreProperty() {
            return minPercentScore;
        }

        public void setMinPercentScore(String minPercentScore) {
            this.minPercentScore.set(minPercentScore);
        }
        public String getFrequency() {
            return frequency.get();
        }

        public SimpleStringProperty frequencyProperty() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency.set(frequency);
        }

        public String getPercentage() {
            return percentage.get();
        }

        public SimpleStringProperty percentageProperty() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage.set(percentage);
        }


    }


    public static class Question {


        private SimpleStringProperty header;
        private SimpleStringProperty weight;

        private Question(String header, String weight) {
            this.header = new SimpleStringProperty(header);
            this.weight = new SimpleStringProperty(weight);
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


    public class SubjQuestion {

        private SimpleStringProperty name;
        private SimpleStringProperty maxScore = new SimpleStringProperty("10");

        SubjQuestion(String name, String maxScore) {
            this.name = new SimpleStringProperty("Subjective Question " + name);
            this.maxScore = new SimpleStringProperty(maxScore);
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

    final static String BUTTONS_TEXT = "Update Selection";
    private TableView<ObservableList<StringProperty>> objTable = new TableView();
    final VBox objTableVbox = new VBox();
    final HBox objHBox = new HBox();
    private VBox subjTableVbox = new VBox();
    private TableView subjTable = new TableView();
    TableColumn subjNamesCol = new TableColumn("Question");
    TableColumn subjWeightsCol = new TableColumn("Weight");
    final HBox subjHBox = new HBox();
    final TextField objWeightText = new TextField();
    final Button objWeightsButton = new Button(BUTTONS_TEXT);
    final TextField subjWeightText = new TextField();
    final Button subjWeightsButton = new Button(BUTTONS_TEXT);
    final Label subjLabel = new Label("Subjective Questions");
    final Label objLabel = new Label("Objective Questions");
    Separator midSeparator = new Separator();
    CategoryAxis xAxis ;
    NumberAxis yAxis ;
    BarChart<String,Number> barChart ;
    XYChart.Series barChartSeries = new XYChart.Series();

    JFXTreeTableView gradesFreqTable = new JFXTreeTableView();

    ObservableList<Grade> gradesFreqData = FXCollections.observableArrayList();

    ImageView objButtonGraphic=new ImageView(new Image("Images/whiteRefresh.png"));

    ImageView subjButtonGraphic=new ImageView(new Image("Images/whiteRefresh.png"));

    StackPane contextMenuExpandButton=new StackPane();
    ImageView contextMenuIcon=new ImageView(new Image("Images/blueMenu.png"));
    ContextMenu contextMenu=new ContextMenu();


    AnchorPane fullMarksAnc=new AnchorPane();
    Label fullMarksLabel=new Label("Full Mark:");
    TextField fullMarksTextField=new TextField();
    CustomMenuItem fullMarksMenuItem= new CustomMenuItem(fullMarksAnc);

    AnchorPane bonusMarksAnc=new AnchorPane();
    Label bonusMarksLabel=new Label("Bonus Marks:");
    TextField bonusMarksTextField=new TextField();
    CustomMenuItem bonusMarksMenuItem= new CustomMenuItem(bonusMarksAnc);

    AnchorPane checkBoxAnc=new AnchorPane();
    CheckBox contextMenuCheckBox=new JFXCheckBox("Allow exceeding full mark",14);
    CustomMenuItem checkBoxMenuItem=new CustomMenuItem(checkBoxAnc);






    ///data fields
    ObservableList<ObservableList<StringProperty>> objQuestions = FXCollections.observableArrayList();
    ObservableList<SubjQuestion> subjQuestions = FXCollections.observableArrayList();
    boolean isMouseClickedWhileFocused=false;
    private double buttAbsX,buttAbsY,contextXPos,contextYPos;

    //Main methods

    WeightsController(Controller back) {
        super("Weights.fxml", "Weights", 1.25, 1.23, true, back, "4.png", 2, "Questions Weights");
        gradeScalesJsonObj=null; //reload grade scales json object for every project
    }


    protected void updateSizes() {
        super.updateSizes();

        double tablesShift = 0.13;

        objTableVbox.setSpacing(rootHeightToPixels(0.019));
        objTableVbox.setLayoutY(rootHeight*0.04);
        subjTableVbox.setLayoutY(objTableVbox.getLayoutY());
        objTableVbox.setPadding(new Insets(0, 0, 0, 0));
        objTable.setPrefHeight(rootHeightToPixels(0.63));
        objHBox.setSpacing(rootWidthToPixels(0.00625));
        subjHBox.setSpacing(resXToPixels(0.005));
        subjTable.setPrefHeight(objTable.getPrefHeight());
        subjTableVbox.setSpacing(resYToPixels(0.015));
        subjTableVbox.setPadding(new Insets(0));

        objTableVbox.setPrefWidth(rootWidthToPixels(0.27));
        subjTableVbox.setPrefWidth(rootWidthToPixels(0.27));
        objTable.setPrefWidth(objTableVbox.getPrefWidth());
        subjTable.setPrefWidth(subjTableVbox.getPrefWidth());
        objHBox.setPrefWidth(objTable.getPrefWidth());
        subjHBox.setPrefWidth(subjTable.getPrefWidth());
        objLabel.setPrefWidth(objTable.getPrefWidth());
        subjLabel.setPrefWidth(subjTable.getPrefWidth());
        HBox.setHgrow(objWeightsButton, Priority.ALWAYS);
        HBox.setHgrow(subjWeightsButton, Priority.ALWAYS);
        HBox.setHgrow(objWeightText, Priority.ALWAYS);
        HBox.setHgrow(subjWeightText, Priority.ALWAYS);
        double sidePadding=resX*8/1280;
        double verPadding=resX*4/1280;

        objWeightsButton.setPadding(new Insets(verPadding,sidePadding,verPadding,sidePadding));
        objWeightsButton.setGraphicTextGap(resX*4/1280);
        System.out.println("objawy"+objWeightsButton.getWidth());
        objWeightsButton.setMinWidth(resX*123/1280);

        subjWeightsButton.setMinWidth(resX*123/1280);
        subjWeightsButton.setGraphicTextGap(resX*4/1280);
        subjWeightsButton.setPadding(new Insets(verPadding,sidePadding,verPadding,sidePadding));


        //contextMenuIcon.setSize(Double.toString(resX*18/1280));
        contextMenuIcon.setFitWidth(resX*18/1280);
        contextMenuIcon.setFitHeight(resX*18/1280);
        contextMenuExpandButton.setLayoutX((midSeparator.getLayoutX()+(subjTableVbox.getLayoutX()+subjTableVbox.getPrefWidth()))/2-contextMenuIcon.getFitWidth()/2); //mid point between separator and subjVbox
        contextMenuExpandButton.setLayoutY(objTableVbox.getLayoutY()+rootHeight*0.01);
        contextMenuExpandButton.setOnMouseClicked(event -> {
            
            buttAbsX=contextMenuIcon.localToScreen(contextMenuIcon.getBoundsInLocal()).getMinX();
            buttAbsY=contextMenuIcon.localToScreen(contextMenuIcon.getBoundsInLocal()).getMinY();
            double buttSize=contextMenuIcon.getFitWidth();
            contextMenu.show(rootPane,0,0);
            contextMenu.hide();
            contextXPos=buttAbsX+buttSize/4-contextMenuCheckBox.getWidth();
            contextYPos=buttAbsY+buttSize;
            contextMenu.show( contextMenuExpandButton,contextXPos,contextYPos);

        });

        double verPad=resY*4/680,sidePad=resX*5/1280;
        fullMarksAnc.setPrefWidth(125);
        fullMarksTextField.setPrefWidth(50);
        //fullMarksLabel.setFont(new Font(resX*12/1280));
        fullMarksLabel.setPadding(new Insets(4,0,0,0));



        bonusMarksAnc.setPrefWidth(fullMarksAnc.getPrefWidth());
        bonusMarksTextField.setPrefWidth(fullMarksTextField.getPrefWidth());
        //bonusMarksLabel.setFont(fullMarksLabel.getFont());
        bonusMarksLabel.setPadding(fullMarksLabel.getPadding());


        contextMenuCheckBox.setStyle("");


        AnchorPane.setLeftAnchor(fullMarksLabel,0.0);
        AnchorPane.setLeftAnchor(bonusMarksLabel,0.0);
        AnchorPane.setRightAnchor(fullMarksTextField,0.0);
        AnchorPane.setRightAnchor(bonusMarksTextField,0.0);




        midSeparator.setLayoutX(rootWidthToPixels(0.665));
        midSeparator.setLayoutY(rootHeight * 0.03);
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));

        objTableVbox.setLayoutX(buttonsHbox.getLayoutX());
        subjTableVbox.setLayoutX(objTableVbox.getLayoutX() + objTable.getPrefWidth() + rootWidth * 0.06);

        gradesFreqTable.setPrefWidth(objTable.getPrefWidth());
        gradesFreqTable.setLayoutX(buttonsHbox.getLayoutX()+buttonsHbox.getPrefWidth()-gradesFreqTable.getPrefWidth());
        gradesFreqTable.setLayoutY(objTableVbox.getLayoutY());
        gradesFreqTable.setPrefHeight(objTable.getPrefHeight()*0.6);

        double graphicSize=resX*12/1280;
        objButtonGraphic.setFitWidth(graphicSize);
        objButtonGraphic.setFitHeight(graphicSize);

        subjButtonGraphic.setFitWidth(graphicSize);
        subjButtonGraphic.setFitHeight(graphicSize);





        initBarChart();
        double shift=resX*30.0/1280;
        barChart.setLayoutX(gradesFreqTable.getLayoutX()-shift);
        barChart.setLayoutY(gradesFreqTable.getLayoutY()+gradesFreqTable.getPrefHeight()+rootHeight*0.05);
        barChart.setPrefHeight(buttonsHbox.getLayoutY()-barChart.getLayoutY()-rootHeight*0.01);
        barChart.setPrefWidth(gradesFreqTable.getPrefWidth()+shift);

        if (objTable.getPrefWidth() > getObjTableColumnsWidth())
            objTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        else
            objTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


    }

    protected void initComponents() {
        initWeightsHBoxes();
//        subjLabel.setAlignment(Pos.CENTER);
//        objLabel.setAlignment(Pos.CENTER);

        double initialUserMaxScore=isOpenMode?(Double)currentOpenedProjectJson.get(USER_MAX_SCORE_JSON_KEY):-1;
        double initialBonus=isOpenMode?(Double)currentOpenedProjectJson.get(BONUS_MARKS_JSON_KEY):0.0;



        Statistics.setUserMaxScore(initialUserMaxScore);
        Statistics.setBonus(initialBonus);

        Statistics.initAnswersStats();
        Statistics.initCorrectAnswersPercent();
        initObjTableVBox();
        initSubjTableVBox();
        refreshGradesDistribution();
        initGradesFreqTable();
        initBarChart();
        initContextMenu();


        Tooltip tooltipAdd = new Tooltip("Tweak Grades");
        Tooltip.install(contextMenuExpandButton, tooltipAdd);
        contextMenuExpandButton.setOnMouseEntered(event->contextMenuIcon.setImage(new Image("Images/lightBlueMenu.png")));
        contextMenuExpandButton.setOnMouseExited(event -> contextMenuIcon.setImage(new Image("Images/blueMenu.png")));
        //contextMenuIcon.setRotate(90);

        midSeparator.setVisible(true);
        midSeparator.setOrientation(Orientation.VERTICAL);
    }




    @Override
    protected Controller getNextController() {

        return new GradeBoundariesController(this);
    }

    @Override
    protected void goToNextWindow(){

        saveChanges();
        if(!checkBalancedFormsSums())
            return;



        if(generalPrefsJson!=null) {
            generalPrefsJson.put(ALLOW_EXCEED_FULL_MARK_JSON_KEY, contextMenuCheckBox.isSelected());
            saveJsonObj(GENERAL_PREFS_FILE_NAME, generalPrefsJson);
        }


        if(next==null || isContentEdited) { //if first time or edit manually has been pressed
            next = getNextController();
            next.startWindow();
        }
        else
            next.showWindow();

        isContentEdited=false;
        stage.close();
    }

    @Override
    public void showWindow(){
        super.showWindow();
        gradeScalesJsonObj=null; //force reload to avoid json simple bug
        refreshGradesDistribution();

    }


    private boolean checkBalancedFormsSums() {

        ArrayList<Double> subjWeights=Statistics.getSubjMaxScores();
        double subjSum=0;

        for(Double d:subjWeights)
            subjSum+=d;


        ArrayList<ArrayList<Double>> objWeights=Statistics.getQuestionWeights();

        ArrayList<Double> firstFormWeights=objWeights.get(0);
        double firstSum=0;

        for(Double weight:firstFormWeights)
            firstSum+=weight;

        ArrayList<Double> formsSums=new ArrayList<>();
        formsSums.add(firstSum+subjSum);
        boolean success=true;

        for(int i=1;i<objWeights.size();i++){
            double currentSum=0;
            for(Double weight:objWeights.get(i))
                currentSum+=weight;
            if(currentSum!=firstSum)
                success=false;
            formsSums.add(currentSum+subjSum);
        }

        if(!success) {
            String errorMessage="All forms must have the same max score. The forms have max scores (";
            for(Double d:formsSums)
                errorMessage+=String.format("%.1f",d)+"-";

            errorMessage=errorMessage.substring(0,errorMessage.length()-1);
            errorMessage+=") respectively.";

            showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Invalid Form Weights",errorMessage);
            return false;
        }
        return true;
    }

    @Override
    protected void saveChanges() {
        saveWeights();
    }

    private void saveWeights() {


        //save objective weights
        ArrayList<ArrayList<Double>> objWeights = new ArrayList<>();


        for (int i = 1; i < objQuestions.get(0).size(); i++) {
            if(i%2!=0) //ignore odd columns -> correct percentage columns
                continue;
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
    protected void buildComponentsGraph() {

        super.buildComponentsGraph();

        objHBox.getChildren().addAll(objWeightText, objWeightsButton);
        subjHBox.getChildren().addAll(subjWeightText, subjWeightsButton);

//        objTable.getColumns().addAll(objHeadersCol,objWeightsCol); objTable construction in "populateObjTable" method
        subjTable.getColumns().addAll(subjNamesCol, subjWeightsCol);

        objTableVbox.getChildren().addAll(objLabel, objTable, objHBox);
        subjTableVbox.getChildren().addAll(subjLabel, subjTable, subjHBox);

        contextMenuExpandButton.getChildren().add(contextMenuIcon);

        fullMarksAnc.getChildren().addAll(fullMarksLabel,fullMarksTextField);
        bonusMarksAnc.getChildren().addAll(bonusMarksLabel,bonusMarksTextField);
        checkBoxAnc.getChildren().add(contextMenuCheckBox);
        contextMenu.getItems().addAll(fullMarksMenuItem,bonusMarksMenuItem,new SeparatorMenuItem(),checkBoxMenuItem);


        rootPane.getChildren().addAll(objTableVbox, subjTableVbox,midSeparator,gradesFreqTable,contextMenuExpandButton);


    }

    @Override
    protected void stabalizeTables() {
        disableTableDrag(subjTable);
        disableTableDrag(objTable);
    }

    @Override
    public void startWindow(){
        super.startWindow();


        Platform.runLater(()->gradesFreqTable.refresh());




    }

    //helper methods

    private void initObjTableVBox() {

        objLabel.setFont(new Font("Arial", headersFontSize));

//        for(int i=0;i<headers.size();i++)
//            objQuestions.add(new Question(headers.get(i),"1.0"));


        objTable.setStyle("-fx-alignment:center");
        populateObjTable();
        objTable.setEditable(true);
        objTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        objTable.getSelectionModel().setCellSelectionEnabled(true);
//        objTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(() -> objTable.refresh());


    }

    private void initWeightsHBoxes() {

        //objective hbox

        objWeightText.setPromptText("New weight");
        objWeightsButton.setGraphic(objButtonGraphic);
        objWeightsButton.getStyleClass().add("BlueJFXButton");
        objWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                String w;
                if ((w = tryDouble(objWeightText.getText())) == null) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Invalid Weight Value", "Cannot update objective weights: weight value \"" + objWeightText.getText() + "\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected = objTable.getSelectionModel().getSelectedCells();
                for (int i = 0; i < selected.size(); i++) {
                    int row = selected.get(i).getRow();
                    int col = selected.get(i).getColumn();
                    String title=selected.get(i).getTableColumn().getText();
                    if (!title.toLowerCase().contains("weight"))
                        continue;

                    objTable.getItems().get(row).set(col, new SimpleStringProperty(w));
                }

                objTable.refresh();
                refreshGradesDistribution();
            }
        });

        ////subjective hbox

        subjWeightText.setPromptText("New weight");
        subjWeightsButton.setGraphic(subjButtonGraphic);
        subjWeightsButton.getStyleClass().add("BlueJFXButton");
        subjWeightsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String w;
                if ((w = tryDouble(subjWeightText.getText())) == null) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "Invalid Weight Value", "Cannot update subjective weights: weight value \"" + subjWeightText.getText() + "\" is invalid.");
                    return;
                }

                ObservableList<TablePosition> selected = subjTable.getSelectionModel().getSelectedCells();
                for (int i = 0; i < selected.size(); i++)
                    ((SubjQuestion) subjTable.getItems().get(selected.get(i).getRow())).setMaxScore(w);

                subjTable.refresh();
                refreshGradesDistribution();

            }
        });
        if (CSVHandler.getSubjQuestionsCount() == 0) {
            subjWeightsButton.setDisable(true);
            subjWeightText.setDisable(true);
        }

    }

    private void initSubjTableVBox() {

        subjNamesCol.setCellValueFactory(
                new PropertyValueFactory<SubjQuestion, String>("name")
        );


        subjNamesCol.setCellFactory((t) -> EditCell.createStringEditCell(this));


        subjWeightsCol.setCellValueFactory(
                new PropertyValueFactory<WeightsController.SubjQuestion, String>("maxScore")
        );

        subjWeightsCol.setCellFactory((t) -> EditCell.createStringEditCell(this));

        subjWeightsCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<SubjQuestion, String>>) t -> {

            String w;
            if ((w = tryDouble(t.getNewValue())) == null) {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(t.getOldValue());
                subjTable.refresh();
            } else{
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(w);
            }


        });

        subjLabel.setFont(new Font("Arial", headersFontSize));

        for (int i = 0; i < CSVHandler.getSubjQuestionsCount(); i++)
            subjQuestions.add(new SubjQuestion(Integer.toString(i + 1), "10.0"));


        subjTable.setEditable(true);
        subjTable.setItems(subjQuestions);
        Label placeLabel = new Label("No Subjective Questions Detected");
        placeLabel.setStyle("-fx-font-weight: bold;");
        subjTable.setPlaceholder(placeLabel);
        //subjTable.setStyle("-fx-border-color:#1E90FF");
        subjTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subjTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(() -> subjTable.refresh());
        subjNamesCol.setSortable(false);
        subjWeightsCol.setSortable(false);
        subjNamesCol.setEditable(false);

    }

    private void initGradesFreqTable() {

        JFXTreeTableColumn<Grade,String> gradeNamesCol = new JFXTreeTableColumn<>("Grade");

        JFXTreeTableColumn<Grade,String> gradeFreqCol = new JFXTreeTableColumn<>("Relative Freq.");


        gradeNamesCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Grade, String> param) -> {
            return param.getValue().getValue().gradeName;
        });


        gradeNamesCol.setCellFactory((TreeTableColumn<Grade, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));



        gradeFreqCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Grade, String> param) -> {
            return param.getValue().getValue().percentage;
        });

        gradeFreqCol.setCellFactory((TreeTableColumn<Grade, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));


        final TreeItem<Grade> root = new RecursiveTreeItem<>(gradesFreqData, RecursiveTreeObject::getChildren);

        gradesFreqTable.setRoot(root);
        gradesFreqTable.setShowRoot(false);
        gradesFreqTable.setEditable(false);
        gradesFreqTable.getColumns().addAll(gradeNamesCol,gradeFreqCol);
        gradesFreqTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        gradeNamesCol.setSortable(false);
        gradeFreqCol.setSortable(false);





    }

    private void initBarChart() {

        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        rootPane.getChildren().remove(barChart);
        barChart = new BarChart<String,Number>(xAxis,yAxis);

        rootPane.getChildren().add(barChart);

        barChart.setTitle(null);
        xAxis.setLabel("Grade");
        yAxis.setLabel("Number of Students");

        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        barChart.getStyleClass().add("weightsBarChart");
        barChart.getData().add(barChartSeries);
        barChart.layout();
        barChart.applyCss();

        for(Node n:barChart.lookupAll(".axis-label"))
            n.setStyle("-fx-font-size:"+resX*10.8/1280);

        for(Node n:barChart.lookupAll(".axis"))
            n.setStyle("-fx-tick-label-font-size:"+resX*7.2/1280);



    }

    private void initContextMenu() {


        contextMenu.setOnHidden(event-> {
            if(!saveContextMenuChanges(false))
                Platform.runLater(()->contextMenu.show(contextMenuExpandButton,contextXPos,contextYPos)); //if invalid show context menu again



        }
        );




        fullMarksMenuItem.setHideOnClick(false);
        bonusMarksMenuItem.setHideOnClick(false);
        checkBoxMenuItem.setHideOnClick(false);


        fullMarksMenuItem.getStyleClass().add("nonSelectableMenuItem");
        bonusMarksMenuItem.getStyleClass().add("nonSelectableMenuItem");
        checkBoxMenuItem.getStyleClass().add("nonSelectableMenuItem");
        contextMenuCheckBox.setStyle("-fx-text-fill:-fx-text-base-color;-fx-font-size:"+resX*12/1280);


        bonusMarksTextField.setText(Double.toString(Statistics.getBonus()));


        fullMarksTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                isMouseClickedWhileFocused=false;

            if(!isMouseClickedWhileFocused)
                fullMarksTextField.requestFocus();
        });

        bonusMarksTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                isMouseClickedWhileFocused=false;

            if(!isMouseClickedWhileFocused)
                bonusMarksTextField.requestFocus();
        });

        contextMenu.addEventFilter(MouseEvent.MOUSE_PRESSED, event ->{

            //ignore any mouse click on an already focused text field
            if(fullMarksTextField.isFocused() &&
                    fullMarksTextField.contains(fullMarksTextField.sceneToLocal(new Point2D(event.getSceneX(),event.getSceneY()))))
                return;

            if(bonusMarksTextField.isFocused() &&
                    bonusMarksTextField.contains(bonusMarksTextField.sceneToLocal(new Point2D(event.getSceneX(),event.getSceneY()))))
                return;

                isMouseClickedWhileFocused=true;
        });



        fullMarksTextField.setOnKeyPressed(event -> {

            if(event.getCode()==KeyCode.ENTER)
                saveContextMenuChanges(true);

        });



        bonusMarksTextField.setOnKeyPressed(event -> {

            if(event.getCode()==KeyCode.ENTER)
                saveContextMenuChanges(true);
        });




        if(generalPrefsJson==null && !isOpenMode)
            contextMenuCheckBox.setSelected(true);
        else {
            JSONObject obj=isOpenMode?currentOpenedProjectJson:generalPrefsJson;
            contextMenuCheckBox.setSelected((Boolean) obj.get(ALLOW_EXCEED_FULL_MARK_JSON_KEY));
        }
        contextMenuCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> Statistics.setAllowExceedMaxScore(newValue));


    }

    private boolean saveFullMarkChange(boolean isCalledFromEnter) {
        String s=tryDouble(fullMarksTextField.getText());
        if(s==null) {

            if(isCalledFromEnter) //triggered by Enter button -> hide context menu so that full validation will take place, to avoid double alerts
                contextMenu.hide();
            else //triggered by context menu hide-> show alert
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Invalid Full Mark Value", "Full mark value\"" + fullMarksTextField.getText() + "\" is invalid.");

            fullMarksTextField.setText(Double.toString(Statistics.getMaxScore()));

            return false;
        }

        double userMaxScore=Double.parseDouble(s);
        if(userMaxScore!=Statistics.getMaxScore())  //only set user max score if it was edited
            Statistics.setUserMaxScore(userMaxScore);

        refreshGradesDistribution();
        return true;
    }

    private boolean saveBonusMarkChange(boolean isCalledFromEnter) {
        String s=tryDouble(bonusMarksTextField.getText());
        if(s==null) {

            if(isCalledFromEnter)
                contextMenu.hide();
            else
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Invalid Bonus Marks Value", "Bonus marks value\"" + bonusMarksTextField.getText() + "\" is invalid.");

            bonusMarksTextField.setText(Double.toString(Statistics.getBonus()));

            return false;
        }
        Statistics.setBonus(Double.parseDouble(s));
        refreshGradesDistribution();
        return true;
    }

    private boolean saveContextMenuChanges(boolean isCalledFromEnter){

        boolean success=saveFullMarkChange(isCalledFromEnter);
        if(success)
            return saveBonusMarkChange(isCalledFromEnter);
        else
            return false;

    }


    private void populateObjTable() {

        ObservableList<StringProperty> headers = FXCollections.observableArrayList();

        //initialize table
        objTable.getItems().clear();
        objTable.getColumns().clear();
        objTable.setPlaceholder(new Label("Loading..."));
        objTable.setOnKeyPressed(event -> {
            TablePosition<ObservableList<StringProperty>, ?> pos = objTable.getFocusModel().getFocusedCell();
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


        int formsCount = CSVHandler.getFormsCount();
        //add Weight columns
        if (formsCount == 1) {
            objTable.getColumns().add(createColumn(1,"Correct%"));
            objTable.getColumns().add(createColumn(2, "Weight"));
        } else {

            for (int i = 0; i < formsCount; i++) {

                TableColumn parentCol=new TableColumn("Form "+(i+1));
                objTable.getColumns().add(parentCol);
                parentCol.getColumns().add(createColumn(i*2+1,"Correct%"));
                parentCol.getColumns().add(createColumn(i*2+2, "Weight"));

            }
        }


    }

    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();

        boolean isWeightsCol=columnTitle.toLowerCase().contains("weight");


        column.setEditable(isWeightsCol);


        column.setCellFactory((t) -> EditCell.createStringEditCell(this));
        column.setText(columnTitle);

        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
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


        if(isWeightsCol) { //only weights columns are editable
            column.setOnEditCommit(t -> {
                        String w;
                        int row = t.getTablePosition().getRow();
                        int col = t.getTablePosition().getColumn();
                        if ((w = tryDouble(t.getNewValue())) != null) {
                            t.getTableView().getItems().get(row).set(col, new SimpleStringProperty(w));
                            objTable.refresh();
                            System.out.println(objTable.getItems());
                        } else { //return to old text before editing
                            t.getTableView().getItems().get(row).set(col, new SimpleStringProperty(t.getOldValue()));
                            objTable.refresh();
                        }
                    }
            );
        }


        return column;
    }

    private void initializeObjQuestions() {

        for (int i = 0; i < CSVHandler.getDetectedQHeaders().size(); i++) {
            ObservableList<StringProperty> row = FXCollections.observableArrayList();
            row.add(new SimpleStringProperty(CSVHandler.getDetectedQHeaders().get(i)));
            for (int j = 0; j < CSVHandler.getFormsCount(); j++) {
                String correctPercent=String.format("%.1f",Statistics.getCorrectAnswersPercents().get(j).get(i)*100);
                row.add(new SimpleStringProperty(correctPercent));
                String weight = isOpenMode && !isIgnoreSavedObjectiveWeights ? String.format("%.1f", Statistics.getQuestionWeights().get(j).get(i)) : "1.0";
                row.add(new SimpleStringProperty(weight));
            }
            objQuestions.add(row);
        }

    }

    private double getObjTableColumnsWidth() {
        int totalWidth = 0;
        for (TableColumn column : objTable.getColumns())
            totalWidth += column.getWidth();
        return totalWidth;
    }


    private void loadGradeScale() {


        if ((gradeScalesJsonObj = loadJsonObj(GRADE_SCALE_FILE_NAME)) == null) {
            showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Grade Configurations Error",
                    "Error in loading Grade Scale Configurations");
            return;
        }

    }

    private void loadGradesFreqData(){

        JSONArray scales = (JSONArray) gradeScalesJsonObj.get("scales");
        JSONObject obj=isOpenMode?currentOpenedProjectJson:gradeScalesJsonObj;
        int index=Integer.parseInt((String) obj.get(SELECTED_SCALE_JSON_KEY));


        JSONObject scale=(JSONObject)scales.get(index);

        JSONArray grades=(JSONArray) scale.values().iterator().next();
        ArrayList<Grade> tempScale=new ArrayList<>();

        for(int i=0;i<grades.size();i++){
            JSONObject grade = (JSONObject) grades.get(i);
            tempScale.add(new Grade((String) grade.keySet().iterator().next(),(String) grade.values().iterator().next()));
        }

        Collections.sort(tempScale,new GradeSorter());

        if(Double.parseDouble(tempScale.get(0).getMinPercentScore())!=0) //last grade must have min =0
            tempScale.add(0,new Grade("F","0.0"));

        ArrayList<String> gradeNames=new ArrayList<>();

        ArrayList<Double> gradeMins=new ArrayList<>();

        for(Grade grade : tempScale){
            gradeNames.add(grade.getGradeName());
            gradeMins.add(Double.parseDouble(grade.getMinPercentScore()) / 100);
        }

        gradeMins.add(1.0);
        Statistics.setGrades(gradeNames);
        Statistics.setGradesLowerRange(gradeMins);

        Statistics.initScores();

        ArrayList<ArrayList<String>> report1Table=Statistics.report1Stats();

        report1Table = Utils.transposeStringList(report1Table);

        gradesFreqData.clear();

        for(int i=0;i<report1Table.get(0).size();i++)
            gradesFreqData.add(new Grade(report1Table.get(0).get(i),"0.0",report1Table.get(3).get(i))); //0.0 is just a dummy

    }

    public void refreshGradesDistribution(){

        refreshGradeFreqTable();
        refreshBarChart();
        double maxScore=Statistics.getUserMaxScore()==-1?Statistics.getMaxScore():Statistics.getUserMaxScore();
        fullMarksTextField.setText(Double.toString(maxScore));

    }

    private void refreshGradeFreqTable(){
        saveWeights();
        if(gradeScalesJsonObj==null)
            loadGradeScale();
        loadGradesFreqData();
    }

    private void refreshBarChart(){

        int max = 0  ;

        barChartSeries.getData().clear();
        for(Grade grade:gradesFreqData) {
            int freq = Integer.parseInt(grade.getFrequency());
            max=Math.max(max,freq);
            barChartSeries.getData().add(new XYChart.Data(grade.getGradeName(), freq));
        }

    }

    class GradeSorter implements Comparator<Grade> {

        @Override
        public int compare(Grade o1,Grade o2) {
            return (int) (double) (Double.parseDouble(o1.getMinPercentScore()) - Double.parseDouble(o2.getMinPercentScore()));
        }
    }

}





