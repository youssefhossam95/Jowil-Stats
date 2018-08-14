package Jowil;

import com.lowagie.text.Table;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static Jowil.ManualModeController.cellsCount;

public class ManualModeCell<S, T> extends TableCell<S, T> {

    int columnIndex;
    TableView myTable;
    ManualModeController controller;
    String origColor;

    ManualModeCell(int columnIndex,TableView myTable,ManualModeController controller){
        itemProperty().addListener((obx, oldItem, newItem) -> {
            if (newItem == null) {
                setText(null);
            } else {
                setText(newItem.toString());
            }
        });

        this.columnIndex=columnIndex;
        this.myTable=myTable;
        this.setPrefHeight(myTable.getPrefHeight()/(ManualModeController.MAX_ROWS_COUNT+2));
        this.setAlignment(Pos.CENTER);
        this.controller=controller;


        this.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            if(event.isShortcutDown() || event.isShiftDown() || event.isControlDown())
                event.consume();

            if(controller.colClicksCount==2){
                controller.resetTable();
                event.consume();
            }


        });


        controller.colColors.get(columnIndex).addListener((obs,oldValue,newValue)->{
            this.setStyle("-fx-background-color:"+newValue);
        });

    }

    public static <S> ManualModeCell<S, String> createManualModeCell(int columnIndex, TableView myTable,ManualModeController controller) {
        return new ManualModeCell<S, String>(columnIndex,myTable,controller);
    }



}
