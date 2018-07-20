package Jowil;
import com.jfoenix.controls.*;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

//import de.jensd.fx.glyphs.GlyphsBuilder;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;


public class FileConfigController extends Controller{

 //components

    @FXML
    private JFXTextField mainFileTextField;
    @FXML
    private JFXButton mainFileChooserButton;

    @FXML
    private ImageView mainChooserButtonImage;

    @FXML
    private JFXButton nextButton;

    @FXML
    private JFXTextField answersFileTextField;

    @FXML
    private JFXComboBox identifierCombo;

    @FXML
    private JFXComboBox formCombo;

    @FXML
    private JFXToggleButton manualModeToggle;


    @FXML
    private JFXButton answersFileChooserButton;

    @FXML
    private ImageView answersChooserButtonImage;

    @FXML
    private HBox mainHBox;

    @FXML
    private HBox answersHBox;

    @FXML
    private HBox combosHBox;


    @FXML
    private AnchorPane combosAnchor;

    @FXML
    private VBox contentVbox;



    JFXToggleButton toggleButton = new JFXToggleButton();
    VBox subjVBox = new VBox();
    JFXSlider slider= new JFXSlider();
    final Popup popup = new Popup();



 //data fields
    private String lastDir;
    File csvFile;



//methods
    FileConfigController(){
        super("FileConfig.fxml","File configuration",1.6,1.45,true,null);
    }


    protected void updateSizes(){
        super.updateSizes();

        contentVbox.setLayoutX(rootWidthToPixels(0.072));
        contentVbox.setLayoutY(rootHeightToPixels(0.13));
        contentVbox.setSpacing(rootHeightToPixels(0.1));
        contentVbox.setAlignment(Pos.BASELINE_LEFT);

        mainHBox.setSpacing(resXToPixels(0.005));
        answersHBox.setSpacing(resXToPixels(0.005));
        //combosHBox.setSpacing(rootWidthToPixels(0.15));
        //formCombo.setLayoutX(rootWidthToPixels(0.7));
        mainFileTextField.setPrefWidth(rootWidthToPixels(0.5));
        mainFileTextField.setPrefHeight(resYToPixels(0.04));
        mainFileTextField.setPadding(Insets.EMPTY);



        answersFileTextField.setPrefWidth(rootWidthToPixels(0.5));
        answersFileTextField.setPrefHeight(resYToPixels(0.04));
        answersFileTextField.setPadding(Insets.EMPTY);


        mainFileChooserButton.setPrefWidth(resYToPixels(0.04));
        mainFileChooserButton.setPrefHeight(resYToPixels(0.04));
        mainChooserButtonImage.setFitWidth(mainFileChooserButton.getPrefWidth());
        mainChooserButtonImage.setFitHeight(mainFileChooserButton.getPrefHeight());

        answersFileChooserButton.setPrefWidth(resYToPixels(0.04));
        answersFileChooserButton.setPrefHeight(resYToPixels(0.04));
        answersChooserButtonImage.setFitWidth(mainFileChooserButton.getPrefWidth());
        answersChooserButtonImage.setFitHeight(mainFileChooserButton.getPrefHeight());


        formCombo.setPadding(Insets.EMPTY);
        formCombo.setPrefWidth(rootWidthToPixels(0.227));
        identifierCombo.setPrefWidth(rootWidthToPixels(0.227));
        manualModeToggle.setPadding(Insets.EMPTY);


        nextButton.setPrefWidth(resXToPixels(0.07));
        nextButton.setPrefHeight(resXToPixels(0.004));
        nextButton.setLayoutX(rootWidthToPixels(0.78));
        nextButton.setLayoutY(rootHeightToPixels(0.77));


        combosAnchor.setPrefWidth(rootWidthToPixels(0.4));
        AnchorPane.setLeftAnchor(identifierCombo,0.0);
        AnchorPane.setRightAnchor(formCombo,0.0);
        combosAnchor.setPadding(new Insets(0,answersFileChooserButton.getLayoutBounds().getWidth()+answersHBox.getSpacing(),0,0));

        subjVBox.setLayoutX(rootWidthToPixels(0.11));
        subjVBox.setLayoutY(rootHeightToPixels(0.5));

    }

    protected void initComponents(){
        initFileChooserButton();
        initNextButton();
        initToggleButton();
        initMainFileTextField();
        initAnswersFileTextField();

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

        Label label=new Label();
        label.setText("This is an error message");
        label.getStyleClass().add("chat-bubble");
        popup.getContent().add(label);

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

            csvFile=new File(mainFileTextField.getText());
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
                        GroupsController controller;
                        if(next==null || isContentEdited) {
                            next = controller = new GroupsController(this);
                            controller.startWindow();
                        }
                        else {
                            controller = (GroupsController) next;
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

        mainFileChooserButton.setOnMouseClicked(new EventHandler<MouseEvent>
                () {
            public void handle(MouseEvent t) {
                mainFileChooserButton.setStyle("-fx-background-color:transparent;");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open CSV file");
                fileChooser.setInitialDirectory(new File((lastDir==null?System.getProperty("user.home"):lastDir)));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
                csvFile =fileChooser.showOpenDialog(stage);
                if(csvFile!=null) {
                    lastDir = csvFile.getParent();
                    mainFileTextField.setText(csvFile.getPath());
                    mainFileTextField.requestFocus();
                    mainFileTextField.deselect();
                }
            }
        });
        mainFileChooserButton.setStyle("-fx-border-width:0;fx-background-color:transparent");


    }

    private void initToggleButton(){

        toggleButton.setText("Subjective Questions");
        toggleButton.setStyle("-fx-font-weight: bold;-jfx-toggle-color: #00BFFF");
        slider.setMax(20);
        slider.setMin(0);
    }

    private void initMainFileTextField(){

        CSVFileValidator validator= new CSVFileValidator();
        mainFileTextField.getValidators().add(validator);
//        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
//                .glyph(FontAwesomeIcon.WARNING)
//                .size(EM1)
//                .styleClass(ERROR)
//                .build());

        mainFileTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            isContentEdited=true;
        });

        mainFileTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {
            if(!newValue){
                mainFileTextField.validate();
            }

        });

    }


    private void initAnswersFileTextField(){

        CSVFileValidator validator= new CSVFileValidator();
        answersFileTextField.getValidators().add(validator);
//        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
//                .glyph(FontAwesomeIcon.WARNING)
//                .size(EM1)
//                .styleClass(ERROR)
//                .build());

        answersFileTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            isContentEdited=true;
        });

        answersFileTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {
            if(!newValue){
                answersFileTextField.validate();
            }

        });

    }

}
