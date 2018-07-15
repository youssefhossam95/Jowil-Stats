package Jowil;

import com.jfoenix.controls.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConfigController extends Controller{

 //controls

    @FXML
    private JFXTextField fileTextField;
    @FXML
    private JFXButton fileChooserButton;

    @FXML
    private ImageView chooserButtonImage;

    @FXML
    private JFXButton nextButton;

    JFXToggleButton toggleButton = new JFXToggleButton();
    VBox subjVBox = new VBox();
    JFXSlider slider= new JFXSlider();

 //data fields
    private String lastDir;
    File csvFile;



//methods
    FileConfigController(){
        super("FileConfig.fxml","File configuration",1.8,1.8,true,null);
    }


    protected void updateSizes(){
        super.updateSizes();
        fileTextField.setPrefWidth(rootWidth/1.4);
        fileTextField.setPrefHeight(resY/60);
        fileTextField.setLayoutX(rootWidth/9);
        fileTextField.setLayoutY(rootHeight/2.5);
        fileChooserButton.setPrefWidth(resY/25);
        fileChooserButton.setPrefHeight(resY/25);
        fileChooserButton.setLayoutX(rootWidth/1.2);
        fileChooserButton.setLayoutY(rootHeight/2.5);
        chooserButtonImage.setFitWidth(fileChooserButton.getPrefWidth());
        chooserButtonImage.setFitHeight(fileChooserButton.getPrefHeight());
        nextButton.setPrefWidth(resX/15);
        nextButton.setPrefHeight(resX/250);
        nextButton.setLayoutX(rootWidth/1.275);
        nextButton.setLayoutY(rootHeight/1.3);
        subjVBox.setLayoutX(rootWidth/9);
        subjVBox.setLayoutY(rootHeight/2);

    }

    protected void initComponents(){
        initFileChooserButton();
        initNextButton();
        initToggleButton();
    }


    private void initNextButton(){

        nextButton.setOnMouseEntered(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                nextButton.setStyle("-fx-background-color:#878a8a;");
            }
        });

        nextButton.setOnMouseExited(new EventHandler<MouseEvent>
                () {


            public void handle(MouseEvent t) {
                nextButton.setStyle("-fx-background-color:transparent;-fx-border-color:#949797");
            }
        });

        nextButton.setOnMouseClicked(t->{
                csvFile=new File(fileTextField.getText());
                if(!csvFile.exists()){
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "The file entered doesn't exist.");
                    return ;
                }
                if(!csvFile.getPath().endsWith(".csv")) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "Wrong file type: file must have a \".csv\" extension.");
                    return;
                }
                CSVHandler.setFilePath(csvFile.getPath());
                try {
                    if(CSVHandler.processHeaders()){
                        ViewGroupsAndSubjsController controller = new ViewGroupsAndSubjsController(this);
                        controller.startWindow();
                    }
                    else{
                        HeadersCreateController controller=new HeadersCreateController(this);
                        controller.startWindow();
                    }
                    stage.close();

                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "Error reading file: "+e.getMessage()+".");
                } catch (CSVHandler.EmptyCSVException e) {
                    showAlert(Alert.AlertType.ERROR, stage.getOwner(), "CSV file Error",
                            "CSV file empty.");
                }

            });

    }

    private void initFileChooserButton(){

        fileChooserButton.setOnMouseEntered(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                fileChooserButton.setStyle("-fx-background-color:#878a8a;");
            }
        });

        fileChooserButton.setOnMouseExited(new EventHandler<MouseEvent>
                () {


            public void handle(MouseEvent t) {
                fileChooserButton.setStyle("-fx-background-color:transparent;");
            }
        });

        fileChooserButton.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                fileChooserButton.setStyle("-fx-background-color:transparent;");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open CSV file");
                fileChooser.setInitialDirectory(new File((lastDir==null?System.getProperty("user.home"):lastDir)));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                csvFile =fileChooser.showOpenDialog(stage);
                if(csvFile!=null) {
                    lastDir = csvFile.getParent();
                    fileTextField.setText(csvFile.getPath());
                }
            }
        });


    }

    private void initToggleButton(){

        toggleButton.setText("Subjective Questions");
        toggleButton.setStyle("-fx-font-weight: bold;-jfx-toggle-color: #00BFFF");
        subjVBox.getChildren().add(toggleButton);
        subjVBox.getChildren().add(slider);
        slider.setMax(20);
        slider.setMin(0);
        rootPane.getChildren().add(subjVBox);
    }


    






}
