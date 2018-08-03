package Jowil;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;



public class gradeHBox extends HBox {

    private int index;

    JFXTextField nameTextField=new JFXTextField();
    JFXTextField percentScoreTextField=new JFXTextField();
    JFXTextField rawScoreTextField=new JFXTextField();
    JFXSlider scoreSlider=new JFXSlider();
    private final  Node addIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.PLUS_CIRCLE).size(Double.toString(Controller.resX/80)).styleClass("gradesPlusIcon").build();
    private final  Node removeIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.MINUS_CIRCLE).size(Double.toString(Controller.resX/80)).styleClass("gradesMinusIcon").build();
    StackPane addButton=new StackPane();
    StackPane removeButton=new StackPane();






    gradeHBox(int index,String name,String percentScore,GradeBoundariesController parentController){

        this.index=index;

        this.nameTextField.setText(name);
        nameTextField.setStyle("-jfx-focus-color:#3184c9;-fx-font-size:"+Double.toString(Controller.resX/100));
        nameTextField.setAlignment(Pos.CENTER);


        this.percentScoreTextField.setText(percentScore);
        percentScoreTextField.setStyle("-jfx-focus-color:#3184c9;-fx-font-size:"+Double.toString(Controller.resX/100));
        percentScoreTextField.setAlignment(Pos.CENTER);
        percentScoreTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            if(Controller.tryDouble(newValue)==null)
                return;
            if(!newValue.equals(oldValue)) {
                this.rawScoreTextField.setText(Double.toString(Double.parseDouble(newValue) / 100 * Statistics.getMaxScore()));
                this.scoreSlider.setValue(Double.parseDouble(newValue));
            }

        });

        percentScoreTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue){
                this.rawScoreTextField.setText(String.format(".1f",Double.parseDouble(this.rawScoreTextField.getText())));
            }

        });





        this.rawScoreTextField.setText(Double.toString(Double.parseDouble(percentScore)/100*Statistics.getMaxScore()));
        this.rawScoreTextField.setStyle("-jfx-focus-color:#3184c9;-fx-font-size:"+Double.toString(Controller.resX/100));
        rawScoreTextField.setAlignment(Pos.CENTER);
        rawScoreTextField.textProperty().addListener((observable,oldValue,newValue)-> {
            if(Controller.tryDouble(newValue)==null)
                return;
            if(!newValue.equals(oldValue)){
                this.percentScoreTextField.setText(Double.toString(Double.parseDouble(newValue) / Statistics.getMaxScore()*100));

            }


        });

        rawScoreTextField.focusedProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue){
                this.percentScoreTextField.setText(String.format(".1f",Double.parseDouble(this.percentScoreTextField.getText())));
            }

        });




        this.scoreSlider.setValue(Double.parseDouble(percentScore));
        scoreSlider.valueProperty().addListener((observable,oldValue,newValue)-> {
            if(!newValue.equals(oldValue)) {
                double d=(Double)newValue;
                this.percentScoreTextField.setText(String.format("%.1f",d));
            }
        });



        addButton.getChildren().add(addIcon);
        addButton.setOnMouseClicked(t->
                parentController.addNextGrade(this.index));

        removeButton.getChildren().add(removeIcon);
        removeButton.setOnMouseClicked(t->
                parentController.deleteGrade(this.index));

        this.getChildren().addAll(nameTextField,percentScoreTextField,rawScoreTextField,scoreSlider,addButton,removeButton);


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
        System.out.println(nameTextField.getPrefWidth());
        System.out.println(rawScoreTextField.getPrefWidth());
        System.out.println(percentScoreTextField.getPrefWidth());


    }


    public void incrementIndex() {
        index++;
    }


    public void decrementIndex() {
        index--;
    }

}
