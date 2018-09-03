package Jowil;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.DefaultProperty;
import javafx.scene.Node;

import javafx.scene.control.TextInputControl;


import java.io.File;
import java.io.IOException;



@DefaultProperty(value = "icon")
public class CSVFileValidator extends ValidatorBase {



    JFXTextField myTextField;
    int textFieldID;
    final public static int MAINFILETEXTFIELD=0, ANSWERSFILETEXTFIELD=1;
    private final static Node mainErrorIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.TIMES_CIRCLE).size("1em").build();
    private final static Node answersErrorIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.TIMES_CIRCLE).size("1em").build();
    private final static Node mainWarningIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.WARNING).size("1em").build();
    private final static Node answersWarningIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.WARNING).size("1em").build();
    private final static Node mainSuccessIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.3em").build();
    private final static Node answersSuccessIcon=GlyphsBuilder.create(FontAwesomeIconView.class).glyph(FontAwesomeIcon.CHECK).size("1.3em").build();

    public final static String EMPTY_CSV_MESSAGE="Empty CSV file.",DOES_NOT_EXIST_MESSAGE="File doesn't exist.",
            REQUIRED_FIELD_MESSAGE="Required field.",CSV_EXTENSION_MESSAGE="File must have a \".csv\" extension.",
            ERROR_READING_MESSAGE="Error in reading file.",NO_HEADERS_MESSAGE="No headers detected.",
            INCONSISTENT_ANSWER_KEY_MESSAGE="Blank answers positions are inconsistent.",
            ILLFORMED_CSV_MESSAGE="Invalid number of columns at row %d",HEADERS_ONLY_MESSAGE="File contains headers only.";




    public CSVFileValidator(JFXTextField myTextField,int textFieldID){
        this.myTextField=myTextField;
        this.textFieldID=textFieldID;

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
        File csvFile=null;
        if(!Controller.isOpenMode)
            csvFile= new File(text);

        messageType=ERROR;
        switch (textFieldID){
            case MAINFILETEXTFIELD:
                if(!Controller.isOpenMode)
                    validateMainCSV(csvFile);
                setSuccessIcon(mainSuccessIcon);
                break;
            case ANSWERSFILETEXTFIELD:
                if(!Controller.isOpenMode)
                    validateAnswersCSV(csvFile);
                setSuccessIcon(answersSuccessIcon);
                break;
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

        if(file.getPath().length()==0){
            setMessage(REQUIRED_FIELD_MESSAGE);
            hasErrors.set(true);
            return;
        }

        if (!file.exists()) {
            setMessage(DOES_NOT_EXIST_MESSAGE);
            hasErrors.set(true);
            return;
        }
        if (!file.getPath().toLowerCase().endsWith(".csv")) {
            setMessage(CSV_EXTENSION_MESSAGE);
            hasErrors.set(true);
            return;
        }
        try{
            if(CSVHandler.isCSVFileEmpty(file)){
                setMessage(EMPTY_CSV_MESSAGE);
                hasErrors.set(true);
                return;
            }
        } catch (IOException e) {
            setMessage(ERROR_READING_MESSAGE);
            hasErrors.set(true);
            return;
        }

        try{
            if(CSVHandler.isFileContainsNoRows(file)){
                setMessage(HEADERS_ONLY_MESSAGE);
                hasErrors.set(true);
                return;
            }
        } catch (IOException e) {
            setMessage(ERROR_READING_MESSAGE);
            hasErrors.set(true);
            return;
        }



    }

    private void validateMainCSV(File file){

        setIcon(mainErrorIcon);
        validateCSV(file);
        if(hasErrors.get())
            return;

        CSVHandler.setResponsesFilePath(file.getPath());
        try {
            if (!CSVHandler.processHeaders(false)) {
                setMessage(NO_HEADERS_MESSAGE);
                myTextField.getMySkin().getErrorContainer().updateErrorLabelStyle("warning-label");
                setIcon(mainWarningIcon);
                messageType=WARNING;
                hasErrors.set(true);
                return;
            }
        } catch (IOException e) {
            setMessage(ERROR_READING_MESSAGE);
            hasErrors.set(true);
            return;

        } catch (CSVHandler.EmptyCSVException e) {
            setMessage(EMPTY_CSV_MESSAGE);
            hasErrors.set(true);
            return;
        }

    }

    private void validateAnswersCSV(File file){

        boolean isHeadersExist=false;

        setIcon(answersErrorIcon);

        validateCSV(file);
        if(hasErrors.get())
            return;

        try {
            isHeadersExist=CSVHandler.loadAnswerKeys(file.getPath(),false);
        }
        catch(CSVHandler.IllFormedCSVException e){
            setMessage(String.format(ILLFORMED_CSV_MESSAGE,e.getRowNumber()));
            hasErrors.set(true);
            return;
        }  catch(IOException e) {
            setMessage(ERROR_READING_MESSAGE);
            hasErrors.set(true);
            return;
        } catch (CSVHandler.InConsistentAnswerKeyException e) {
            setMessage(INCONSISTENT_ANSWER_KEY_MESSAGE);
            hasErrors.set(true);
            return;
        }

        if(!isHeadersExist){
            setMessage(NO_HEADERS_MESSAGE);
            myTextField.getMySkin().getErrorContainer().updateErrorLabelStyle("warning-label");
            setIcon(answersWarningIcon);
            messageType=WARNING;
            hasErrors.set(true);
        }


    }

    private void setSuccessIcon(Node successIcon){

        if(!hasErrors.get()) {
            setIcon(successIcon);
            messageType = SUCCESS;
            setMessage("");
            hasErrors.set(true);
        }
    }

}

