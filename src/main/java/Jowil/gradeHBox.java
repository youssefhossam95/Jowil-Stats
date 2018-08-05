package Jowil;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import static Jowil.Controller.resX;


public class GradeHBox extends HBox {

    private int index;

    JFXTextField nameTextField=new JFXTextField();
    JFXTextField percentScoreTextField=new JFXTextField();
    JFXTextField rawScoreTextField=new JFXTextField();
    JFXSlider scoreSlider=new JFXSlider();
    private final  Node addIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.PLUS_CIRCLE).size(Double.toString(resX/80)).styleClass("gradesPlusIcon").build();
    private final  Node removeIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.MINUS_CIRCLE).size(Double.toString(resX/80)).styleClass("gradesMinusIcon").build();
    StackPane addButton=new StackPane();
    StackPane removeButton=new StackPane();
    boolean isIgnorePercentScoreLosingFocus=false;

    GradeBoundariesController parentController;




    GradeHBox(int index,String name,String percentScore,GradeBoundariesController parentController){

        this.index=index;
        this.parentController=parentController;



        this.nameTextField.setText(name);
        nameTextField.setStyle("-jfx-focus-color:#3184c9;-fx-font-size:"+Double.toString(resX/100));
        nameTextField.setAlignment(Pos.CENTER);


<<<<<<< HEAD
        this.percentScoreTextField.setText(percentScore);
        this.percentScoreTextField.setPromptText("Percent Score");
        this.percentScoreTextField.setLabelFloat(true);
        percentScoreTextField.setStyle("focusColor:#3184c9");
        percentScoreTextField.textProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue.equals(oldValue)) {
                this.rawScoreTextField.setText(String.format("%.1f", Double.parseDouble(newValue) / 100 * Statistics.getMaxScore()));
                this.scoreSlider.setValue(Double.parseDouble(newValue));
            }
=======
        initPercentScoreTextField(percentScore);
        initRawScoreTextField(percentScore);


        this.scoreSlider.setValue(Double.parseDouble(percentScore));
        scoreSlider.valueProperty().addListener((observable,oldValue,newValue)-> {

            double d=(Double)newValue;
            this.percentScoreTextField.setText(String.format("%.1f",d));
            this.rawScoreTextField.setText(String.format("%.1f",d/ 100 * Statistics.getMaxScore()));
>>>>>>> c82be3483172781a98b0c34f38485d8a8049452f

        });


<<<<<<< HEAD
        this.rawScoreTextField.setText(Double.toString(Double.parseDouble(percentScore)/100*Statistics.getMaxScore()));
        this.rawScoreTextField.setPromptText("Raw Score");
        this.rawScoreTextField.setLabelFloat(true);
        this.rawScoreTextField.setStyle("focusColor:#3184c9");
        rawScoreTextField.textProperty().addListener((observable,oldValue,newValue)-> {
=======
>>>>>>> c82be3483172781a98b0c34f38485d8a8049452f

        addButton.getChildren().add(addIcon);
        addButton.setOnMouseClicked(t-> parentController.addNextGrade(this.index));

<<<<<<< HEAD
            if(!newValue.equals(oldValue)) {
                this.percentScoreTextField.setText(String.format("%.1f", Double.parseDouble(newValue) / Statistics.getMaxScore()*100));
                this.scoreSlider.setValue(Double.parseDouble(newValue));
            }
=======
        removeButton.getChildren().add(removeIcon);
        removeButton.setOnMouseClicked(t-> parentController.deleteGrade(this.index));
>>>>>>> c82be3483172781a98b0c34f38485d8a8049452f

        this.getChildren().addAll(nameTextField,percentScoreTextField,rawScoreTextField,scoreSlider,addButton,removeButton);


    }


    GradeHBox(GradeHBox orig){

        this(orig.index,orig.nameTextField.getText(),orig.percentScoreTextField.getText(),orig.parentController);
    }

    public void updateSizes(double scrollPaneWidth,double scrollPaneHeight){



        nameTextField.setPrefWidth(scrollPaneWidth*0.15);
        nameTextField.setPrefHeight(scrollPaneHeight/20);
        rawScoreTextField.setPrefWidth(scrollPaneWidth*0.13);
        rawScoreTextField.setPrefHeight(scrollPaneHeight/20);
        percentScoreTextField.setPrefWidth(scrollPaneWidth*0.13);
        percentScoreTextField.setPrefHeight(scrollPaneHeight/20);
        scoreSlider.setPrefWidth(scrollPaneWidth*0.3);
        scoreSlider.setPadding(new Insets(rawScoreTextField.getPrefHeight()/2,0,0,0));
        this.setSpacing(scrollPaneWidth*0.03);

    }


    public void incrementIndex() {
        index++;
    }


    public void decrementIndex() {
        index--;
    }

    private void initPercentScoreTextField(String percentScore){

        this.percentScoreTextField.setText(percentScore);
        percentScoreTextField.setStyle("-jfx-focus-color:#3184c9;-fx-font-size:"+Double.toString(resX/100));
        percentScoreTextField.setAlignment(Pos.CENTER);


        percentScoreTextField.setOnKeyPressed(event -> {

            if(event.getCode()==KeyCode.ENTER){

                if(isPercentScoreTextFieldInValid()){
                    isIgnorePercentScoreLosingFocus=true;
                    parentController.showAlertAndWait(Alert.AlertType.ERROR, parentController.stage.getOwner(), "Grade Scale Value Error",
                            "Grade scale value \""+percentScoreTextField.getText()+"\" is invalid.");
                    percentScoreTextField.setText(String.format("%.1f",scoreSlider.getValue()));
                }
                else {
                    parentController.rootPane.requestFocus();
                    scoreSlider.setValue(Double.parseDouble(percentScoreTextField.getText()));
                    rawScoreTextField.setText(String.format("%.1f", Double.parseDouble(percentScoreTextField.getText()) / 100 * Statistics.getMaxScore()));
                }

            }
        });

        percentScoreTextField.setOnKeyTyped(event-> {
            if(Character.isDigit(event.getCharacter().charAt(0)) || (event.getCharacter().equals(".") && !percentScoreTextField.getText().contains("."))) //invalid character
                return;

            event.consume();
        });

        percentScoreTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {

            if(isIgnorePercentScoreLosingFocus){
                isIgnorePercentScoreLosingFocus=false;
                return;
            }

            if(newValue)
                return;

            if(isPercentScoreTextFieldInValid()){
                parentController.showAlertAndWait(Alert.AlertType.ERROR, parentController.stage.getOwner(), "Grade Scale Value Error",
                        "Grade scale value \""+percentScoreTextField.getText()+"\" is invalid.");
                percentScoreTextField.setText(String.format("%.1f",scoreSlider.getValue()));
            }
            else {
                scoreSlider.setValue(Double.parseDouble(percentScoreTextField.getText()));
                rawScoreTextField.setText(String.format("%.1f", Double.parseDouble(percentScoreTextField.getText()) / 100 * Statistics.getMaxScore()));
            }

        });
    }



    private void initRawScoreTextField(String percentScore){

        this.rawScoreTextField.setText(Double.toString(Double.parseDouble(percentScore)/100*Statistics.getMaxScore()));
        this.rawScoreTextField.setStyle("-jfx-focus-color:#3184c9;-fx-font-size:"+Double.toString(resX/100));
        rawScoreTextField.setAlignment(Pos.CENTER);
        rawScoreTextField.setEditable(false);
        rawScoreTextField.focusedProperty().addListener((observable,oldValue,newValue)->{
            if(newValue)
                parentController.rootPane.requestFocus();
        });
    }


    private boolean isPercentScoreTextFieldInValid(){
        String s=percentScoreTextField.getText();
        return s.length()==0 ||  s.equals(".")||Double.parseDouble(s)>100;
    }


}
