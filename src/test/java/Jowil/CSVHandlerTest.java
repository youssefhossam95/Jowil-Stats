package Jowil;

import javafx.stage.Stage;
import junit.framework.TestCase;

import java.io.IOException;

public class CSVHandlerTest extends TestCase {

    public void testDetectHeaders() throws IOException, CSVHandler.EmptyCSVException {

        CSVHandler.setResponsesFilePath(".\\src\\test\\TestCSVs\\testAll2.csv");
        CSVHandler.processHeaders(false);
        System.out.println("Info Headers: "+CSVHandler.getDetectedInfoHeaders().toString());
        System.out.println("Question Headers: "+CSVHandler.getDetectedQHeaders().toString());
    }

}