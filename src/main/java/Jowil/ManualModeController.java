package Jowil;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class ManualModeController extends Controller{



    @FXML
    TableView table;

    @FXML
    Label tableTitle;

    @FXML
    VBox tableVBox;

    @FXML
    JFXTextField columnSetTextField;

    @FXML
    JFXComboBox columnSetCombo;

    @FXML
    Button addButton;


    @FXML
    ScrollPane scrollPane;

    @FXML
    HBox columnSetHBox;

    @FXML
    Separator midSeparator;

    @FXML
    VBox rightSideVBox;

    @FXML
    Label rightTitleLabel;

    @FXML
    HBox scrollPaneTitlesHBox;

    @FXML
    VBox columnSetsVBox;

    @FXML
    VBox scrollPaneContentVBox;


    @FXML
    Label CSNameLabel;

    @FXML
    Label CSColorLabel;

    @FXML
    Label CSRangeLabel;

    @FXML
    Label CSTypeLabel;


    ObservableList<ObservableList<StringProperty>> tableContent= FXCollections.observableArrayList();
    ArrayList<ColumnSet> columnSets=new ArrayList<>();
    int colClicksCount=0;
    ArrayList<String> tableHeaders;
    static final int MAX_ROWS_COUNT=11;
    ArrayList<SimpleStringProperty> colColors;
    int prevClickedCol=-1;
    int firstColIndex=-1;
    int secondColIndex=-1;
    static final String CELL_DEFAULT_COLOR="transparent";
    ColorGenerator colorGen=new ColorGenerator();
    final static String OBJECTIVE_TYPE="Objective Questions Group", SUBJECTIVE_TYPE="Subjective Questions Group",
    ID_TYPE="Student ID",FORM_TYPE="Form Number";
    final static String[] comboOptions={OBJECTIVE_TYPE,SUBJECTIVE_TYPE,ID_TYPE ,FORM_TYPE};
//    int IDStartIndex,IDEndIndex,formIndex;
    int columnSetComboSelectedIndex;


    ManualModeController(Controller back){
        super("ManualMode.fxml","Manual Mode",1.25,1.25,true,back);
    }


    @Override
    protected void updateSizes() {
        super.updateSizes();


        //left
        tableVBox.setLayoutX(rootWidthToPixels(0.05));
        tableVBox.setLayoutY(rootHeightToPixels(0.04));
        tableVBox.setSpacing(rootHeightToPixels(0.05));
        tableVBox.setPadding(new Insets(0, 0, 0, 0));
        tableVBox.setPrefWidth(rootWidthToPixels(0.55));
        table.setPrefHeight(rootHeightToPixels(0.55));
        columnSetHBox.setSpacing(rootWidthToPixels(0.01));
        columnSetTextField.setPrefWidth(tableVBox.getPrefWidth()*0.25);
        columnSetCombo.setPrefWidth(tableVBox.getPrefWidth()*0.25);
        //columnSetHBox.setPadding(new Insets(rootHeightToPixels(0.05),0,0,0));

        midSeparator.setLayoutX(tableVBox.getLayoutY()+tableVBox.getPrefWidth()+rootWidthToPixels(0.05));
        midSeparator.setLayoutY(rootHeightToPixels(0.03));
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));


        //right
        rightSideVBox.setLayoutX(tableVBox.getLayoutX()+tableVBox.getPrefWidth()+rootWidthToPixels(0.05));
        rightSideVBox.setLayoutY(tableVBox.getLayoutY());
        rightSideVBox.setSpacing(tableVBox.getSpacing());
        rightSideVBox.setPadding(tableVBox.getPadding());
        double scrollPaneWidth=buttonsHbox.getPrefWidth()+buttonsHbox.getLayoutX()-rightSideVBox.getLayoutX();
        double scrollPaneHeight=table.getPrefHeight();
        scrollPane.setPrefWidth(scrollPaneWidth);
        scrollPane.setPrefHeight(scrollPaneHeight);

        scrollPaneContentVBox.setPadding(new Insets(0, 0, 0, (int)(scrollPaneWidth * 0.02)));
        scrollPaneContentVBox.setSpacing((int)(resYToPixels(0.03)));
        columnSetsVBox.setSpacing((int)(resYToPixels(0.025)));

        scrollPaneTitlesHBox.setPadding(new Insets((int)(scrollPaneHeight * 0.05), 0, 0, 0));
        scrollPaneTitlesHBox.setSpacing((int)(scrollPaneWidth * 0.03));


        CSNameLabel.setPrefWidth((int)(scrollPaneWidth*0.21));
        CSTypeLabel.setPrefWidth((int)(scrollPaneWidth*0.21));
        CSColorLabel.setPrefWidth((int)(scrollPaneWidth*0.15));
        CSRangeLabel.setPrefWidth((int)(scrollPaneWidth*0.2));


        for(ColumnSet col:columnSets)
            col.updateSizes(scrollPaneWidth,scrollPaneHeight);

    }

    @Override
    protected void initComponents() {
        tableTitle.setFont(new Font("Arial",headersFontSize));
        rightTitleLabel.setFont(tableTitle.getFont());
        

        scrollPaneTitlesHBox.setStyle("-fx-font-size:"+resX/100+";-fx-font-weight: bold;");
        scrollPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                rootPane.requestFocus();
        });

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        initTable();
        initColumnSetsVBox();
        initAddButton();
        initColumnSetCombo();
    }




    @Override
    protected Controller getNextController() {
        return null;
    }

    @Override
    protected void stabalizeTables(){
        disableTableDrag(table);
    }

    protected void saveChanges(){}

    


    //init functions
    private void initTable(){

        loadTableContents();
        table.setItems(tableContent);

        int colsCount=tableContent.get(0).size();

        colColors=new ArrayList<>();
        for(int i=0;i<colsCount;i++){
            table.getColumns().add(createColumn(i));
            colColors.add(new SimpleStringProperty(CELL_DEFAULT_COLOR));
        }


        table.getSelectionModel().setCellSelectionEnabled(true);


        Platform.runLater(() -> table.refresh());

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



    }


    private void initColumnSetCombo(){
        ObservableList<String> items=FXCollections.observableArrayList();

        for(String s:comboOptions)
            items.add(s);

        columnSetCombo.setItems(items);

        columnSetCombo.setVisibleRowCount(3);
        columnSetCombo.setOnShown(t -> columnSetCombo.getSelectionModel().clearSelection());
        columnSetCombo.setOnHidden(t -> columnSetCombo.getSelectionModel().select(columnSetComboSelectedIndex));

        columnSetCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {

            if((Integer)newValue!=-1)
                columnSetComboSelectedIndex=(Integer)newValue;

        });

        columnSetCombo.getSelectionModel().select(0);

        columnSetCombo.setCellFactory(param ->  {
            final ListCell<String> cell = new ListCell<String>() {
                {

                    this.setPrefHeight(rootHeight*0.05);
                }
                @Override public void updateItem(String item,
                                                 boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
                };
            return cell;
        });

    }

    private void initColumnSetsVBox() {

    }

    private void initAddButton(){
        addButton.getStyleClass().add("BlueJFXButton");
        addButton.setOnMouseClicked(t->{
            if(columnSetTextField.getText().isEmpty()) {
                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Column Set Error",
                        "Column Set Name cannot be empty.");
                return;
            }

            if(table.getSelectionModel().getSelectedCells().isEmpty()){

                showAlertAndWait(Alert.AlertType.ERROR, stage.getOwner(), "Column Set Error",
                        "No columns selected.");
                return;

            }

            if(isAllowColumnSetAddition())
                addColumnSet();




        });
    }




    //utility functions
    private void loadTableContents() {

        ArrayList<ArrayList<String>> content;

        try {
            content=CSVHandler.readResponsesFile(MAX_ROWS_COUNT+1); //+1 to include headers
        } catch (IOException e) {
            e.printStackTrace();
            showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"CSV Reloading Error","Error in " +
                    "reloading student responses file.");
            return;
        }

        int start=0,end;

        if(CSVHandler.isIsSkipRowInManual()) { //headers exist
            tableHeaders=content.get(0);
            start=1;
        }
        end=Math.min(content.size(),start+MAX_ROWS_COUNT);

        for(int i=start;i<end;i++){
            ObservableList<StringProperty> row= FXCollections.observableArrayList();
            for(int j=0;j<content.get(0).size();j++)
                row.add(new SimpleStringProperty(content.get(i).get(j)));

            tableContent.add(row);
        }
    }


    private TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        String title;

        title = tableHeaders==null?"Column "+Integer.toString(columnIndex+1):tableHeaders.get(columnIndex);



        column.setCellFactory((t) -> ManualModeCell.createManualModeCell(columnIndex,table,this));
        column.setSortable(false);
        column.setEditable(false);
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


        return column;
    }

    public boolean selectRequiredRange(int clickedCol){


        if(prevClickedCol!=-1){ //a column was selected before

            int minIndex=Math.min(clickedCol,prevClickedCol);
            int maxIndex=Math.max(clickedCol,prevClickedCol);


            TableColumn minCol=(TableColumn)table.getColumns().get(minIndex);
            TableColumn maxCol=(TableColumn)table.getColumns().get(maxIndex);


            if(isRangeInValid(minIndex,maxIndex)){
                resetTable(true);
                showInvalidSelectionMessage("range");
                return false;
            }
            else {
                table.getSelectionModel().selectRange(0, minCol, table.getItems().size(), maxCol);
                firstColIndex=prevClickedCol;
                secondColIndex=clickedCol;
            }
        }
        else {

            if(isColumnInvalid(clickedCol)){
                resetTable(true);
                return false;
            }
            else {
                TableColumn col =(TableColumn)table.getColumns().get(clickedCol);
                table.getSelectionModel().selectRange(0, col, table.getItems().size(), col);
                firstColIndex = secondColIndex =clickedCol;
            }
        }

        prevClickedCol=clickedCol;

        colClicksCount++;


        return true;


    }


    private boolean isAllowColumnSetAddition() {

        int type=columnSetComboSelectedIndex;
        String newGroupName=columnSetTextField.getText();


        for (ColumnSet columnSet : columnSets) {

            if(columnSet.getName().equals(newGroupName)){ //check for repeated name
                showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Column Set Addition Error",
                        "A column set with the name \""+newGroupName+"\" already exists.");
                return false;
            }

            if (!comboOptions[type].equals(OBJECTIVE_TYPE) && columnSet.getType().equals(comboOptions[type])) { //check for repeated type except if objective group
                boolean isReplaceExisting = showConfirmationDialog("Confirm Column Set Addition",
                        "A " + comboOptions[type] + " column set already exists. Do you want to replace the existing column set?"
                        , stage.getOwner());

                if (isReplaceExisting) {
                    deleteColumnSet(columnSet);
                    return true;
                } else
                    return false;
            }


        }

        return true;
    }


    private void addColumnSet(){

        int start=Math.min(firstColIndex,secondColIndex);
        int end=Math.max(firstColIndex,secondColIndex);

        if(start==-1)
            return;

        String nextColor=colorGen.getNextColor();

        for(int i=start;i<=end;i++)
            colColors.get(i).set(nextColor);

        resetTable(false);


        columnSets.add(new ColumnSet(columnSetTextField.getText(),nextColor,start,end-start+1,this,comboOptions[columnSetCombo.getSelectionModel().getSelectedIndex()]));
        columnSets.sort(new ColumnSetSorter());

        columnSetsVBox.getChildren().setAll(columnSets);
        updateSizes();

    }

    private boolean isRangeInValid(int minIndex, int maxIndex) {
        for(int i=minIndex;i<=maxIndex;i++){
            if(!colColors.get(i).get().equals(CELL_DEFAULT_COLOR))
                return true;
        }

        return false;

    }

    private boolean isColumnInvalid(int index){
        return !colColors.get(index).get().equals(CELL_DEFAULT_COLOR);
    }


    public void resetTable(boolean isIgnoreClear) {
        this.colClicksCount=0;
        prevClickedCol=-1;

        if(!isIgnoreClear) //avoid clear selection when nothing selected, otherwise ->index out of bounds exception
            table.getSelectionModel().clearSelection();
        this.firstColIndex=-1;
        this.secondColIndex=-1;
    }

    private void showInvalidSelectionMessage(String type){
        showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Invalid Selection","Selected " +
                type+" must not overlap with an existing column set.");
    }

    public void deleteColumnSet(ColumnSet deleted) {
        columnSets.remove(deleted);
        columnSetsVBox.getChildren().setAll(columnSets);
        String deletedColor=deleted.getColor();
        colorGen.addToAvailable(deletedColor);

        for(SimpleStringProperty colColor:colColors){
            if(colColor.get().equals(deletedColor))
                colColor.set("transparent");
        }
    }

    class ColumnSetSorter implements Comparator<ColumnSet> {

        @Override
        public int compare(ColumnSet o1, ColumnSet o2) {
            return o1.getStartIndex()-o2.getStartIndex();
        }
    }

}
