package Jowil;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.DefaultProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * An example of Number field validation, that is applied on text input controls
 * such as {@link TextField} and {@link TextArea}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "icon")
public class CSVFileValidator extends ValidatorBase {



    JFXTextField myTextField;
    int textFieldID;
    final public static int MAINFILETEXTFIELD=0, ANSWERSFILETEXTFIELD=1;
    private final static Node mainErrorIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.TIMES_CIRCLE).size("1em").styleClass("error").build();
    private final static Node answersErrorIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.TIMES_CIRCLE).size("1em").styleClass("error").build();
    private final static Node warningIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.WARNING).size("1em").styleClass("error").build();
    private final static Node successIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.2em").styleClass("error").build();

    private boolean isHeadersFound=true;

    public CSVFileValidator() { }

    public CSVFileValidator(String message) {
        super(message);
    }


    public CSVFileValidator(JFXTextField myTextField,int textFieldID){
        this.myTextField=myTextField;
        this.textFieldID=textFieldID;

    }

    public boolean isHeadersFound() {
        return isHeadersFound;
    }

    public void setTextField(JFXTextField myTextField){
        this.myTextField=myTextField;
    }

//    public CSVFileValidator(String message, NumberStringConverter numberStringConverter) {
//        super(message);
//        this.numberStringConverter = numberStringConverter;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        String text = textField.getText();
        File csvFile = new File(text);
        messageType=ERROR;
        switch (textFieldID){
            case MAINFILETEXTFIELD:
                validateMainCSV(csvFile);
                break;
            case ANSWERSFILETEXTFIELD:
                validateAnswersCSV(csvFile);
                break;
        }


        if(!hasErrors.get()) {
            setIcon(successIcon);
            messageType = SUCCESS;
            hasErrors.set(true);
        }

        //textField.pseudoClassStateChanged( PseudoClass.getPseudoClass("focused"),true);

//        try {
//            hasErrors.set(false);
//            if (!text.isEmpty())
//
//        } catch (Exception e) {
//            hasErrors.set(true);
//        }
    }


    private void validateCSV(File file) {


        if(myTextField==null)
            return;

        //initialization
        myTextField.getMySkin().getErrorContainer().updateErrorLabelStyle("error-label");
        hasErrors.set(false);


        //basic csv checks
        if (!file.exists()) {
            setMessage("File doesn't exist.");
            hasErrors.set(true);
            return;
        }
        if (!file.getPath().endsWith(".csv")) {
            setMessage("File must have a \".csv\" extension.");
            hasErrors.set(true);
            return;
        }
        try{
            if(CSVHandler.isCSVFileEmpty(file)){
                setMessage("Empty CSV file.");
                hasErrors.set(true);
                return;
            }
        } catch (IOException e) {
            setMessage("Error in reading file.");
            hasErrors.set(true);
            return;
        }


    }

    private void validateMainCSV(File file){

        setIcon(mainErrorIcon);
        validateCSV(file);
        if(hasErrors.get())
            return;

        isHeadersFound = true;
        CSVHandler.setFilePath(file.getPath());
        try {
            if (!CSVHandler.processHeaders()) {
                setMessage("No headers detected.");
                isHeadersFound = false;
                myTextField.getMySkin().getErrorContainer().updateErrorLabelStyle("warning-label");
                setIcon(warningIcon);
                messageType=WARNING;
                hasErrors.set(true);
                return;
            }
        } catch (IOException e) {
            setMessage("Error in reading file.");
            hasErrors.set(true);
            return;

        } catch (CSVHandler.EmptyCSVException e) {
            setMessage("Empty CSV file.");
            hasErrors.set(true);
            return;
        }

    }

    private void validateAnswersCSV(File file){
        setIcon(answersErrorIcon);
        validateCSV(file);
        if(hasErrors.get())
            return;

    }

}

