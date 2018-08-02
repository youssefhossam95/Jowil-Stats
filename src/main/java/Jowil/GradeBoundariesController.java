package Jowil;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;


public class GradeBoundariesController extends Controller{


    GradeBoundariesController(Controller back) {
        super("gradeBoundaries.fxml","Grade Boundaries and Report Generation",1.25,1.25,true,back);
    }

    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox gradesVBox;

    @FXML
    JFXComboBox gradesConfigCombo;

    @FXML
    HBox comboHBox;

    @FXML
    StackPane deleteConfigButton;

    @FXML
    Label reportsConfigTitle;

    @FXML
    Label gradeBoundariesTitle;

    @FXML
    Separator midSeparator;




    int gradesConfigComboSelectedIndex;




    @Override
    protected void initComponents() {
        
        initScrollPane();
        initGradesConfigCombo();
        initTitles();

        
    }
    
    @Override
    protected void updateSizes(){

        super.updateSizes();
        //left half
        comboHBox.setLayoutX(rootWidthToPixels(0.05));
        comboHBox.setLayoutY(rootHeightToPixels(0.15));
        comboHBox.setSpacing(resXToPixels(0.005));
        gradesConfigCombo.setPrefWidth(rootWidthToPixels(0.25));
        scrollPane.setLayoutY(rootHeightToPixels(0.25));
        scrollPane.setLayoutX(rootWidthToPixels(comboHBox.getLayoutX()));
        gradeBoundariesTitle.setLayoutX(comboHBox.getLayoutX());
        gradeBoundariesTitle.setLayoutY(rootHeightToPixels(0.05));

        //right half

        midSeparator.setLayoutX(rootWidthToPixels(0.5));
        midSeparator.setLayoutY(rootHeightToPixels(0.03));
        midSeparator.setPrefHeight(rootHeightToPixels(0.8));
        reportsConfigTitle.setLayoutX(midSeparator.getLayoutX()+rootWidthToPixels(0.03));
        reportsConfigTitle.setLayoutY(gradeBoundariesTitle.getLayoutY());





    }


    @Override
    protected void saveChanges() {

    }

    @Override
    protected Controller getNextController() {
        return null;
    }
    
    //////helper methods
    private void initScrollPane(){
        scrollPane.setContent(gradesVBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        
    }
    
    private void initGradesConfigCombo(){
        
        gradesConfigCombo.setVisibleRowCount(3);
        gradesConfigCombo.setOnShown(t->gradesConfigCombo.getSelectionModel().clearSelection());
        gradesConfigCombo.setOnHidden(t->{gradesConfigCombo.getSelectionModel().select(gradesConfigComboSelectedIndex); System.out.println("easy"+gradesConfigComboSelectedIndex);});
        gradesConfigCombo.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)-> {
            if((Integer)newValue!=-1)
                gradesConfigComboSelectedIndex=(Integer)newValue;
        });

        gradesConfigCombo.setCellFactory(t->{
                        final ListCell<String> cell = new ListCell<String>() {
                            {
                                //super.setPrefHeight(gradesConfigCombo.getPrefHeight());
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

    private void initDeleteConfigButton(){
        deleteConfigButton.setOnMouseClicked(t->deleteCurrentConfig());

    }

    private void initTitles(){
        gradeBoundariesTitle.setFont(new Font("Arial", headersFontSize));
        reportsConfigTitle.setFont(new Font("Arial", headersFontSize));
    }

    private void deleteCurrentConfig(){

    }


}
