package Jowil;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.css.PseudoClass;
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

    private boolean isHeadersFound=true;

    public CSVFileValidator() { }

    public CSVFileValidator(String message) {
        super(message);
    }


    public CSVFileValidator(JFXTextField myTextField){
        this.myTextField=myTextField;
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
        validateCSV(text);

        //textField.pseudoClassStateChanged( PseudoClass.getPseudoClass("focused"),true);

//        try {
//            hasErrors.set(false);
//            if (!text.isEmpty())
//
//        } catch (Exception e) {
//            hasErrors.set(true);
//        }
    }


    private void validateCSV(String text){

//        errorTooltip.getStyleClass().removeAll();
        //errorTooltip.getStyleClass().add("error-tooltip");

        myTextField.getMySkin().getErrorContainer().updateErrorLabelStyle("error-label");
        FileConfigController.setIsError(true);
        isHeadersFound=true;
        hasErrors.set(false);
        File csvFile=new File(text);


        if(!csvFile.exists()){
            setMessage("File doesn't exist.");
            hasErrors.set(true);
            return ;
        }
        if(!csvFile.getPath().endsWith(".csv")) {
            setMessage("file must have a \".csv\" extension.");
            hasErrors.set(true);
            return;
        }
        CSVHandler.setFilePath(csvFile.getPath());
        try {
            if (!CSVHandler.processHeaders()) {
                setMessage("No headers detected");
                isHeadersFound=false;
                myTextField.getMySkin().getErrorContainer().updateErrorLabelStyle("warning-label");
                FileConfigController.setIsError(false);
                hasErrors.set(true);
                return;
            }
        }
        catch (IOException e) {
            setMessage("Error reading file!");
            hasErrors.set(true);
            return;

        } catch (CSVHandler.EmptyCSVException e) {
            setMessage("File empty.");
            hasErrors.set(true);
            return;
        }



    }



}

