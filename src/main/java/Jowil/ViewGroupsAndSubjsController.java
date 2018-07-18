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
    private TableView groupsTable = new TableView();
    TableColumn groupNamesCol = new TableColumn("Group");
    TableColumn qCountCol = new TableColumn("Number of Questions");
    final VBox groupsTableVbox = new VBox();
    private VBox subjTableVbox=new VBox();
    private TableView subjTable= new TableView();
    TableColumn subjNamesCol = new TableColumn("Question");
    TableColumn maxScoreCol = new TableColumn("Max Score");
    private HBox tablesHbox= new HBox();
    private JFXButton manualButton= new JFXButton("Edit Manually");

    //data fields
    ObservableList<Group> tableGroups = FXCollections.observableArrayList();
    ArrayList<Group> detectedGroups=CSVHandler.getDetectedGroups();
    ObservableList<SubjQuestion> subjQuestions = FXCollections.observableArrayList();

    //main methods
    ViewGroupsAndSubjsController(Controller back){
        super("ViewGroupsAndSubjs.fxml","Groups and Subjective Questions",1.25,1.25,true,back);
    }


    @Override
    protected void initComponents() {
        initGroupsTableVBox();
        initManualButton();
        initSubjTableVBox();
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
        subjTable.setPrefHeight(rootHeight/1.5);
        subjTable.setPrefWidth(rootWidth/3.2);
        subjTableVbox.setSpacing(resY/50);
        subjTableVbox.setPadding(new Insets(rootHeight/20, 0, 0, rootWidth-2*subjTable.getPrefWidth()-2*rootWidth/20));
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
                    else
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setNameProp(t.getNewValue());

                }
        );


        qCountCol.setCellValueFactory(
                new PropertyValueFactory<Group,String>("qCountProp")
        );

        qCountCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        final Label label = new Label("Groups");
        label.setFont(new Font("Arial", 20));

        for(int i=0;i<detectedGroups.size();i++)
            tableGroups.add(detectedGroups.get(i));

        
        groupsTable.setEditable(true);
        groupsTable.setItems(tableGroups);
        groupsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        groupsTable.getColumns().addAll(groupNamesCol,qCountCol);
        //groupsTable.setStyle("-fx-border-color:#1E90FF");
        groupsTableVbox.getChildren().addAll(label, groupsTable);
        tablesHbox.getChildren().add(groupsTableVbox);
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
        rootPane.getChildren().add(manualButton);

    }


    private void initSubjTableVBox(){


        subjNamesCol.setCellValueFactory(
                new PropertyValueFactory<SubjQuestion,String>("name")
        );


        subjNamesCol.setCellFactory(TextFieldTableCell.forTableColumn());




        maxScoreCol.setCellValueFactory(
                new PropertyValueFactory<SubjQuestion,String>("maxScore")
        );

        maxScoreCol.setCellFactory(TextFieldTableCell.forTableColumn());

        maxScoreCol.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<SubjQuestion,String>>)t-> {

            if(!isValidDouble(t.getNewValue().trim())){
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(t.getOldValue());
                subjTable.refresh();
            }
            else
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setMaxScore(t.getNewValue());

        });

        final Label label = new Label("Subjective Questions");
        label.setFont(new Font("Arial", 20));

        for(int i=0;i<CSVHandler.getSubjQuestionsCount();i++)
            subjQuestions.add(new SubjQuestion(Integer.toString(i+1),"10"));


        subjTable.setEditable(true);
        subjTable.setItems(subjQuestions);
        subjTable.setPlaceholder(new Label("No subjective questions detected"));
        //subjTable.setStyle("-fx-border-color:#1E90FF");

        subjTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        subjTable.getColumns().addAll(subjNamesCol,maxScoreCol);
        subjTableVbox.getChildren().addAll(label, subjTable);
        tablesHbox.getChildren().add(subjTableVbox);
        subjNamesCol.setSortable(false);
        maxScoreCol.setSortable(false);
        subjNamesCol.setEditable(false);
    }

    private void initTablesHBox(){
        rootPane.getChildren().add(tablesHbox);
    }
    
    


    protected void saveChanges() {
        ArrayList<Double> maxSubjScores = new ArrayList<>();
        for (SubjQuestion question : subjQuestions)
            maxSubjScores.add(Double.parseDouble(question.getMaxScore()));

        Statistics.setSubjMaxScores(maxSubjScores);

        ArrayList<String> newHeaders = new ArrayList<>();

        for (Group group : tableGroups) {

            for (int i = 0; i < Integer.parseInt(group.getqCountProp()); i++)
                newHeaders.add(group.getNameProp() + (i + 1));

            CSVHandler.setDetectedQHeaders(newHeaders);
        }

    }
}
