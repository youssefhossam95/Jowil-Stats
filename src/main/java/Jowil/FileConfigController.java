package Jowil;
import com.jfoenix.controls.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public class FileConfigController extends Controller{

 //components

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
        fileTextField.setPrefWidth(rootWidthToPixels(0.7));
        fileTextField.setPrefHeight(resYToPixels(0.017));
        fileTextField.setLayoutX(rootWidthToPixels(0.11));
        fileTextField.setLayoutY(rootHeightToPixels(0.4));
        fileChooserButton.setPrefWidth(resYToPixels(0.04));
        fileChooserButton.setPrefHeight(resYToPixels(0.04));
        fileChooserButton.setLayoutX(rootWidthToPixels(0.83));
        fileChooserButton.setLayoutY(rootHeightToPixels(0.4));
        chooserButtonImage.setFitWidth(fileChooserButton.getPrefWidth());
        chooserButtonImage.setFitHeight(fileChooserButton.getPrefHeight());
        nextButton.setPrefWidth(resXToPixels(0.07));
        nextButton.setPrefHeight(resXToPixels(0.004));
        nextButton.setLayoutX(rootWidthToPixels(0.78));
        nextButton.setLayoutY(rootHeightToPixels(0.77));
        subjVBox.setLayoutX(rootWidthToPixels(0.11));
        subjVBox.setLayoutY(rootHeightToPixels(0.5));

    }

    protected void initComponents(){
        initFileChooserButton();
        initNextButton();
        initToggleButton();
        initFileTextField();

    }

    @Override
    protected Controller getNextController() {
        return null;
    }

    @Override
    protected void saveChanges(){

    }

    @Override
    protected void buildComponentsGraph(){

//        subjVBox.getChildren().add(toggleButton);
//        subjVBox.getChildren().add(slider);
//        rootPane.getChildren().add(subjVBox);


    }

    //private methods
    private void initNextButton(){

//        nextButton.setOnMouseEntered(new EventHandler<MouseEvent>
//                () {
//            public void handle(MouseEvent t) {
//                nextButton.setStyle("-fx-background-color:#878a8a;");
//            }
//        });
//
//        nextButton.setOnMouseExited(new EventHandler<MouseEvent>
//                () {
//
//
//            public void handle(MouseEvent t) {
//                nextButton.setStyle("-fx-background-color:transparent;");
//            }
//        });

        nextButton.setOnMouseClicked(t->{
            //nextButton.setStyle("-fx-background-color:transparent;");

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
                        ViewGroupsAndSubjsController controller;
                        if(next==null || isContentEdited) {
                            next = controller = new ViewGroupsAndSubjsController(this);
                            controller.startWindow();
                        }
                        else {
                            controller = (ViewGroupsAndSubjsController) next;
                            controller.showWindow();
                        }
                        isContentEdited=false;
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
                    fileTextField.requestFocus();
                    fileTextField.deselect();
                }
            }
        });
        fileChooserButton.setStyle("-fx-border-width:0;fx-background-color:transparent");


    }

    private void initToggleButton(){

        toggleButton.setText("Subjective Questions");
        toggleButton.setStyle("-fx-font-weight: bold;-jfx-toggle-color: #00BFFF");
        slider.setMax(20);
        slider.setMin(0);
    }

    private void initFileTextField(){
        fileTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            isContentEdited=true;
        });
        fileTextField.setPromptText("Answers File Path");
        fileTextField.setLabelFloat(true);

    }

}
