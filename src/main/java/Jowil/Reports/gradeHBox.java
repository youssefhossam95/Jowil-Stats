package Jowil.Reports;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class gradeHBox extends HBox {




    private int index;

    JFXTextField nameTextField=new JFXTextField();
    JFXTextField scoreTextField=new JFXTextField();
    JFXSlider scoreSlider=new JFXSlider();
    private final static Node addIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.1em").styleClass("gradesPlusIcon").build();
    private final static Node removeIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.1em").styleClass("gradesMinusIcon").build();




    gradeHBox(int index,String name,String score){
        this.getChildren().addAll(nameTextField,scoreTextField,scoreSlider,addIcon,removeIcon);
        this.index=index;
        this.nameTextField.setText(name);
        this.scoreTextField.setText(score);
        this.scoreSlider.setValue(Double.parseDouble(score));
        nameTextField.setStyle("focusColor:#3184c9");
        scoreTextField.setStyle("focusColor:#3184c9");

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



}
