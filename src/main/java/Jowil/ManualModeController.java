package Jowil;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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


    ObservableList<ObservableList<StringProperty>> tableContent= FXCollections.observableArrayList();
    ArrayList<String> tableHeaders;
    static final int MAX_ROWS_COUNT=10;

    ManualModeController(Controller back){
        super("ManualMode.fxml","Manual Mode",1.25,1.25,true,back);
    }


    @Override
    protected void updateSizes() {
        super.updateSizes();
        tableVBox.setLayoutX(rootWidthToPixels(0.05));
        tableVBox.setLayoutY(rootHeightToPixels(0.04));
        tableVBox.setSpacing(rootHeightToPixels(0.019));
        tableVBox.setPadding(new Insets(0, 0, 0, 0));
        tableVBox.setPrefWidth(rootWidthToPixels(0.6));
        table.setPrefHeight(rootHeightToPixels(0.5));
        columnSetHBox.setSpacing(rootWidthToPixels(0.00625));
        scrollPane.setLayoutX(tableVBox.getLayoutX()+tableVBox.getPrefWidth()+rootWidthToPixels(0.05));
        scrollPane.setLayoutY(tableVBox.getLayoutY()+tableVBox.getSpacing()+tableTitle.getHeight());
        scrollPane.setPrefWidth(buttonsHbox.getPrefWidth()+buttonsHbox.getLayoutX()-scrollPane.getLayoutX());

//        for(int i=table.getColumns().size()-1;i>=0;i--){
//            TableColumn<ObservableList<String>,String> column=(TableColumn<ObservableList<String>,String>)table.getColumns().get(i);
//            column.setPrefWidth(rootWidthToPixels(0.06));
//        }

    }

    @Override
    protected void initComponents() {
        tableTitle.setFont(new Font("Arial",headersFontSize));
        addButton.getStyleClass().add("BlueJFXButton");
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

        for(int i=0;i<colsCount;i++){
            table.getColumns().add(createColumn(i));
        }


        table.getSelectionModel().setCellSelectionEnabled(true);


        Platform.runLater(() -> table.refresh());



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



        column.setCellFactory((t) -> EditCell.createStringEditCell());
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
}
