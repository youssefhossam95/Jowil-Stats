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
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Border;
import javafx.stage.Stage;

import java.util.ArrayList;

public class HeadersConfigController extends Controller{


    JFXTreeTableColumn<Question, String> headerColumn= new JFXTreeTableColumn<>("Header");
    JFXTreeTableColumn<Question, String> weightColumn= new JFXTreeTableColumn<>("Weight");
    ObservableList<Question> questions = FXCollections.observableArrayList();
    ArrayList<String> headers=CSVHandler.getDetectedQHeaders();
    TreeItem<Question> root;
    JFXTreeTableView<Question> treeView;


    private class Question extends RecursiveTreeObject<Question> {
        public SimpleStringProperty header;
        public SimpleStringProperty weight;
        Question(String header, String weight){
            this.header=new SimpleStringProperty(header);
            this.weight=new SimpleStringProperty(weight);
        }
    }


    HeadersConfigController(){
        super("HeadersConfig.fxml","Headers and Weights",1.3,1.3,true);
    }


    protected void updateSizes() {
        super.updateSizes();
        treeView.setPrefWidth(rootWidth/1.5);
        treeView.setPrefHeight(rootHeight/1.5);
    }

    protected void initActions() {
        treeViewInitActions();
    }


    public void treeViewInitActions(){

        headerColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Question, String> param) -> {
            if (headerColumn.validateValue(param)) {
                return param.getValue().getValue().header;
            } else {
                return headerColumn.getComputedValue(param);
            }
        });



        weightColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Question, String> param) -> {
            if (weightColumn.validateValue(param)) {
                return  param.getValue().getValue().weight;
            } else {
                return weightColumn.getComputedValue(param);
            }
        });


        headerColumn.setCellFactory((TreeTableColumn<Question, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        headerColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<Question, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().header.set(t.getNewValue()));

        weightColumn.setCellFactory((TreeTableColumn<Question, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        headerColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<Question, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().weight.set(t.getNewValue()));


        for(int i=0;i<headers.size();i++)
            questions.add(new Question(headers.get(i),"1.0"));

        root = new RecursiveTreeItem<>(questions, RecursiveTreeObject::getChildren);
        treeView = new JFXTreeTableView<>(root);
        treeView.setShowRoot(false);
        treeView.setEditable(true);
        treeView.getColumns().setAll(headerColumn,weightColumn);
        treeView.setStyle("-fx-border-width:1;-fx-border-color:#949797");
        rootPane.getChildren().add(treeView);
    }
}
