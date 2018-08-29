package Jowil;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Controller {

    ///components

    @FXML
    protected Pane rootPane;
    protected Scene scene;
    protected Stage stage;
    protected Controller back;
    protected Controller next;
    protected JFXButton backButton= new JFXButton("Back");
    protected JFXButton nextButton=new JFXButton("Next");


    //data fields
    static public double resX;
    static public double resY;
    protected double rootWidth;
    protected double rootHeight;
    protected static final Logger logger = Logger.getLogger( FileConfigController.class.getName());
    final String myFXML;
    final String myTitle;
    final double XSCALE,YSCALE;
    final boolean isResizable;
    double navWidth;
    double navHeight;
    boolean isHeightCalling;


    protected boolean isContentEdited=false;
    protected HBox buttonsHbox= new HBox();
    protected final double headersFontSize=resX/64;
    private boolean isMaximizedChanged=false;
    boolean isWait=false;
    BorderPane outerBorderPane=new BorderPane();
    HBox topBarHbox=new HBox();
    AnchorPane topWrapperPane=new AnchorPane();
    ImageView progressImage=new ImageView();
    boolean isBeginMaximised;
    boolean isStepWindow;
  


    //Main methods
    Controller(String fxmlName, String myTitle, double XSCALE , double YSCALE, boolean isResizable,Controller back,String progressImageName){
        this(fxmlName,myTitle,XSCALE,YSCALE,isResizable,back,true,false);
        progressImage=new ImageView(new Image("Images/"+progressImageName));
    }



    Controller(String fxmlName, String myTitle, double XSCALE , double YSCALE, boolean isResizable,Controller back, boolean isStepWindow,boolean isMaximised){

        this.myFXML="/FXML/"+fxmlName;
        this.myTitle=myTitle;
        this.XSCALE=XSCALE;
        this.YSCALE=YSCALE;
        this.isResizable = isResizable;
        if(back==null)
            backButton.setVisible(false);
        this.back=back;
        this.isBeginMaximised=isMaximised;
        this.isStepWindow=isStepWindow;

    }




    protected abstract void initComponents();
    protected abstract void saveChanges();

    public void initialize() {

        if(isStepWindow)
            outerBorderPane.prefHeightProperty().bind(stage.heightProperty());
        else
            rootPane.prefHeightProperty().bind(stage.heightProperty());

        rootPane.prefWidthProperty().bind(stage.widthProperty());
        topWrapperPane.getChildren().add(topBarHbox);
        outerBorderPane.setTop(topWrapperPane);
        initTopBarHBox();
        initBackButton();
        initNextButton();
        initButtonsHBox();
        initComponents();
        updateSizes();
        buildComponentsGraph();
        stabalizeTables();





    }


    public void showWindow(){
        stage.show();
        stage.setMaximized(false);
        rootPane.requestFocus();
        this.updateSizes();
    }

    public void startWindow(){
        try {

            // Hide this current window (if this is what you want)

            FXMLLoader loader = new FXMLLoader(getClass().getResource(myFXML));
            stage = new Stage();
            loader.setController(this);

            Pane root =loader.load();



            if(isStepWindow){
                outerBorderPane.setCenter(rootPane);
                scene = new Scene(outerBorderPane, resX / XSCALE, resY / YSCALE);
            }
            else
                scene=new Scene(root,resX / XSCALE, resY / YSCALE);




            stage.setTitle(myTitle);
            scene.getStylesheets().add(getClass().getResource("/FXML/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(isResizable);

            stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old, Boolean neww) {
                    isMaximizedChanged=true;


                }
            });


            stage.widthProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {

                    isHeightCalling=false;
                    updateSizes();

                }
            });



            stage.heightProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {

                    isHeightCalling=true;
                    updateSizes();
                }
            });


            rootPane.requestFocus();
            rootPane.setOnMouseClicked(t->rootPane.requestFocus());

            if(this instanceof StartController)
                stage.initStyle(StageStyle.UNDECORATED);
            else
                stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(event -> {
                if(!showConfirmationDialog("Cancel Project","Are you sure you want to cancel this project?",stage.getOwner()))
                    event.consume();
            });
            stage.show();
            stage.setMaximized(isBeginMaximised);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void updateSizes(){

        Insets navPadding=new Insets(0,0,0,0);
        navWidth=resX/15;
        navHeight=resX/47.5;




        if(!isHeightCalling) {
            if (scene != null && !isMaximizedChanged) {
                rootWidth = scene.getWidth();
            } else { //if maximised property was changed use scene width will not be properly set
                rootWidth = rootPane.getPrefWidth();
                System.out.println("maxim");
                isMaximizedChanged = false;
            }
        }

        topBarHbox.setPrefHeight(outerBorderPane.getPrefHeight()*0.1);
        topBarHbox.setPrefWidth(rootWidth*0.9);
        topBarHbox.setLayoutX(rootWidth*0.05);

        if(isStepWindow) {
            rootHeight = outerBorderPane.getPrefHeight() - topBarHbox.getPrefHeight();
            rootPane.setPrefHeight(rootHeight);
        }
        else
            rootHeight=rootPane.getPrefHeight();
        System.out.println("habebna"+rootHeight);



        progressImage.setFitHeight(topBarHbox.getPrefHeight());

        backButton.setPrefWidth(navWidth);
        backButton.setPrefHeight(navHeight);
        backButton.setPadding(navPadding);

        nextButton.setPrefWidth(navWidth);
        nextButton.setPrefHeight(navHeight);
        nextButton.setPadding(navPadding);
        buttonsHbox.setLayoutX(rootWidth*0.05);


        //AnchorPane.setBottomAnchor(buttonsHbox,0.0);
        //buttonsHbox.setPrefHeight(resY*0.12);
        //buttonsHbox.setPrefHeight(rootHeight*0.15);

        double hpos=0.877;
        if(rootHeight<=resY/1.34)
            buttonsHbox.setLayoutY(rootHeight*hpos);
        else
            buttonsHbox.setLayoutY(rootHeight-resY*(1-hpos));



        buttonsHbox.setSpacing(resX/100);
        buttonsHbox.setPrefWidth(rootWidth*0.9);
        buttonsHbox.setPadding(new Insets(resY/100, 0, 0, 0));



        buttonsHbox.setAlignment(Pos.TOP_RIGHT);
        System.out.println(rootWidth);
        System.out.println(rootHeight);

    }




    //helper methods
    protected static void showAlert(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    protected static void showAlertAndWait(Alert.AlertType alertType, javafx.stage.Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());

        alert.getButtonTypes().setAll(ButtonType.CLOSE);
        Button closeButt=(Button)alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButt.setText("OK");
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }


    /**
     *
     * @param s
     * @return null if the value is less than zero or is not Parsable to Double , otherwise returns a new string in a non-integer representation
     */
    public static String tryDouble(String s){

        s=s.trim();
        try
        {
            double x=Double.parseDouble(s);
            if(x>=0)
                return Double.toString(x);
            else
                return null;
        }
        catch(NumberFormatException e)
        {
            return null;
        }

    }

    private void initTopBarHBox() {
        topBarHbox.setAlignment(Pos.CENTER);
        //topBarHbox.setStyle("-fx-border-width:0 0 1 0;-fx-border-color:#A9A9A9");
        topBarHbox.getChildren().add(progressImage);
        //topBarHbox.setStyle("-fx-border-width:0 0 1 0;-fx-border-color:#A9A9A9");

    }


    private void initBackButton(){
        //backButton.setStyle("-fx-border-width:1;-fx-border-color:#949797");
        rootPane.getChildren().add(backButton);
        backButton.setOnMouseClicked(t->{
            rootPane.requestFocus();
            back.showWindow();
            stage.close();
        });

//        backButton.setOnMouseEntered(t->backButton.setStyle("-fx-background-color:#878a8a;"));
//        backButton.setOnMouseExited(t->backButton.setStyle("-fx-background-color:transparent"));
    }

    private void initButtonsHBox(){
        buttonsHbox.setStyle("-fx-border-width: 1 0 0 0;-fx-border-color:#A9A9A9");
        //buttonsHbox.setStyle("-fx-border-width: 1 0 0 0;-fx-border-color:#3184c9");
    }


    protected void initNextButton(){
        //nextButton.setStyle("-fx-border-width:1;-fx-border-color:#949797");

//        nextButton.setOnMouseEntered(t->nextButton.setStyle("-fx-background-color:#878a8a;"));
//        nextButton.setOnMouseExited(t->nextButton.setStyle("-fx-background-color:transparent;"));
        nextButton.setOnMouseClicked(t->{
            rootPane.requestFocus();
            goToNextWindow();});

    }

    protected abstract Controller getNextController();

    protected void goToNextWindow(){
        saveChanges();

        if(next==null || isContentEdited) { //if first time or edit manually has been pressed
            next = getNextController();
            next.startWindow();
        }
        else
            next.showWindow();

        isContentEdited=false;
        stage.close();
    }


    protected void buildComponentsGraph(){

        buttonsHbox.getChildren().add(backButton);
        buttonsHbox.getChildren().add(nextButton);
        rootPane.getChildren().addAll(buttonsHbox);
    }

    protected double resXToPixels(double relativeVal){

        return relativeVal*resX;
    }

    protected double resYToPixels(double relativeVal){

        return relativeVal*resY;
    }

    protected double rootWidthToPixels(double relativeVal){

        return relativeVal*rootWidth;
    }

    protected double rootHeightToPixels(double relativeVal){

        return relativeVal*rootHeight;
    }



    protected void stabalizeTables(){

    }

    protected static void disableTableDrag(TableView table){
        try {
            table.setOnMouseEntered(t -> {
                TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
                header.setMouseTransparent(true);
            });
        }catch(Exception e){

        }
    }

    public void setContentEdited(boolean contentEdited) {
        isContentEdited = contentEdited;
    }

    public static boolean showConfirmationDialog(String title,String content,Window owner) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Button okButt=(Button)alert.getDialogPane().lookupButton(ButtonType.OK);
        okButt.setText("Yes");

        Button cancelButt=(Button)alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButt.setText("No");

        alert.initOwner(owner);
        //alert.getDialogPane().getStylesheets().add(Controller.class.getResource("/FXML/application.css").toExternalForm());
        Optional<ButtonType> option = alert.showAndWait();

        return option.get() == ButtonType.OK;
    }


    protected JSONObject loadJsonObj(String path) {

        String file = "";
        JSONObject jsonObj = null;
        try {
            file = URLDecoder.decode(getClass().getResource("/" + path).getFile(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        try {
            jsonObj = (JSONObject) new JSONParser().parse(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return jsonObj;

    }

    protected void saveJsonObj(String path, JSONObject jsonObj) {

        PrintWriter pw = null;
        String file = "";
        try {
            file = URLDecoder.decode(getClass().getResource("/" + path).getFile(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pw.write(jsonObj.toJSONString());
        pw.flush();
        pw.close();
    }

//    protected static double setPrefWidth(Region element,relativeVal){
//
//        //element.setPrefWidth();
//
//    }






}
