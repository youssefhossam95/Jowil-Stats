package Jowil;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


public class GradeBoundariesController extends Controller{


    GradeBoundariesController(Controller back) {
        super("grades.fxml","GradeBoundaries",1.25,1.25,true,back);
    }

    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox vbox;





    @Override
    protected void initComponents() {

        scrollPane.setContent(vbox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @Override
    protected void saveChanges() {

    }

    @Override
    protected Controller getNextController() {
        return null;
    }
}
