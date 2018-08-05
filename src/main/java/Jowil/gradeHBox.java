package Jowil;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class gradeHBox extends HBox {




    private int index;

    JFXTextField nameTextField=new JFXTextField();
    JFXTextField percentScoreTextField=new JFXTextField();
    JFXTextField rawScoreTextField=new JFXTextField();
    JFXSlider scoreSlider=new JFXSlider();
    private final static Node addIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.1em").styleClass("gradesPlusIcon").build();
    private final static Node removeIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.1em").styleClass("gradesMinusIcon").build();




    gradeHBox(int index,String name,String percentScore){
        this.getChildren().addAll(nameTextField,percentScoreTextField,rawScoreTextField,scoreSlider,addIcon,removeIcon);
        this.index=index;

        this.nameTextField.setText(name);
        nameTextField.setStyle("focusColor:#3184c9");


        this.percentScoreTextField.setText(percentScore);
        this.percentScoreTextField.setPromptText("Percent Score");
        this.percentScoreTextField.setLabelFloat(true);
        percentScoreTextField.setStyle("focusColor:#3184c9");
        percentScoreTextField.textProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue.equals(oldValue)) {
                this.rawScoreTextField.setText(String.format("%.1f", Double.parseDouble(newValue) / 100 * Statistics.getMaxScore()));
                this.scoreSlider.setValue(Double.parseDouble(newValue));
            }

        });


        this.rawScoreTextField.setText(Double.toString(Double.parseDouble(percentScore)/100*Statistics.getMaxScore()));
        this.rawScoreTextField.setPromptText("Raw Score");
        this.rawScoreTextField.setLabelFloat(true);
        this.rawScoreTextField.setStyle("focusColor:#3184c9");
        rawScoreTextField.textProperty().addListener((observable,oldValue,newValue)-> {


            if(!newValue.equals(oldValue)) {
                this.percentScoreTextField.setText(String.format("%.1f", Double.parseDouble(newValue) / Statistics.getMaxScore()*100));
                this.scoreSlider.setValue(Double.parseDouble(newValue));
            }

        });




        this.scoreSlider.setValue(Double.parseDouble(percentScore));
        scoreSlider.valueProperty().addListener((observable,oldValue,newValue)-> {

            if(!newValue.equals(oldValue))
                this.percentScoreTextField.setText(Double.toString((Double)newValue));
        });








    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



}
