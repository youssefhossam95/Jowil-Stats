//package Jowil;
//
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.TextArea;
//import javafx.util.Callback;
//
//public class SimpleEditingTextTableCell extends TableCell {
//    private TextArea textArea;
//    Callback commitChange;
//
//    public SimpleEditingTextTableCell(Callback commitChange) {
//        this.commitChange = commitChange;
//        textArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
//                if (!arg2) {
//                    //commitEdit is replaced with own callback
//                    //commitEdit(getTextArea().getText());
//
//                    //Update item now since otherwise, it won't get refreshed
//                    setItem(textArea.getText());
//                    //Example, provide TableRow and index to get Object of TableView in callback implementation
//                    commitChange.call(new TableCellChangeInfo(getTableRow(), getTableRow().getIndex(), textArea.getText()));
//                }
//            }
//        });
//    }
//
//    @Override
//    public void startEdit() {
//
//
//
//    }
//
//}
