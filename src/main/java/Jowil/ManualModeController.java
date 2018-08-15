package Jowil;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

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
    VBox scrollPaneVBox;

    @FXML
    Label rightLabel;



    ObservableList<ObservableList<StringProperty>> tableContent= FXCollections.observableArrayList();
    int colClicksCount=0;
    ArrayList<String> tableHeaders;
    static final int MAX_ROWS_COUNT=11;
    static int cellsCount=0;
    ArrayList<SimpleStringProperty> colColors;
    int prevClickedCol=-1;
    int firstColIndex=-1;
    int secondColIndex=-1;
    static final String CELL_DEFAULT_COLOR="transparent";
    ColorGenerator colorGen=new ColorGenerator();


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
        tableVBox.setPrefWidth(rootWidthToPixels(0.6));
        table.setPrefHeight(rootHeightToPixels(0.55));
        columnSetHBox.setSpacing(rootWidthToPixels(0.01));
        columnSetTextField.setPrefWidth(tableVBox.getPrefWidth()*0.25);
        columnSetCombo.setPrefWidth(tableVBox.getPrefWidth()*0.25);
        //columnSetHBox.setPadding(new Insets(rootHeightToPixels(0.05),0,0,0));

        midSeparator.setLayoutX(tableVBox.getLayoutY()+tableVBox.getPrefWidth()+rootWidthToPixels(0.05));
        midSeparator.setLayoutY(rootHeightToPixels(0.03));
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));


        //right
        scrollPaneVBox.setLayoutX(tableVBox.getLayoutX()+tableVBox.getPrefWidth()+rootWidthToPixels(0.05));
        scrollPaneVBox.setLayoutY(tableVBox.getLayoutY());
        scrollPaneVBox.setSpacing(tableVBox.getSpacing());
        scrollPaneVBox.setPadding(tableVBox.getPadding());
        //scrollPane.setLayoutY(tableVBox.getLayoutY()+tableVBox.getSpacing()+tableTitle.getHeight());
        scrollPane.setPrefWidth(buttonsHbox.getPrefWidth()+buttonsHbox.getLayoutX()-scrollPaneVBox.getLayoutX());
        scrollPane.setPrefHeight(table.getPrefHeight());
//        for(int i=table.getColumns().size()-1;i>=0;i--){
//            TableColumn<ObservableList<String>,String> column=(TableColumn<ObservableList<String>,String>)table.getColumns().get(i);
//            column.setPrefWidth(rootWidthToPixels(0.06));
//        }

    }

    @Override
    protected void initComponents() {
        tableTitle.setFont(new Font("Arial",headersFontSize));
        rightLabel.setFont(tableTitle.getFont());
        addButton.getStyleClass().add("BlueJFXButton");
        addButton.setOnMouseClicked(t->updateSelectedRangeColor());
        initTable();
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

    //helper methods


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
                showIllegalClickMessage("range");
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


    ArrayList<TablePosition> obsListToArrayList(ObservableList<TablePosition> list){

        ArrayList<TablePosition> copy=new ArrayList<>();
        for(TablePosition pos:list)
            copy.add(pos);
        return copy;
    }

    private void updateSelectedRangeColor(){

        int start=Math.min(firstColIndex,secondColIndex);
        int end=Math.max(firstColIndex,secondColIndex);
        if(start==-1)
            return;


        String nextColor=colorGen.getNextColor();

        if(nextColor==null){
            colorGen.resetAvailable();
            nextColor=colorGen.getNextColor();
        }

        for(int i=start;i<=end;i++)
            colColors.get(i).set(nextColor);

        resetTable(false);

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
        if(!isIgnoreClear)
            table.getSelectionModel().clearSelection();
        this.firstColIndex=-1;
        this.secondColIndex=-1;
    }

    private void showIllegalClickMessage(String type){
        showAlertAndWait(Alert.AlertType.ERROR,stage.getOwner(),"Invalid Selection","Selected " +
                type+" must not overlap with an existing column set.");
    }

}
