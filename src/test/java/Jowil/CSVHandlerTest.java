package Jowil;

import junit.framework.TestCase;

import java.io.IOException;

public class CSVHandlerTest extends TestCase {

    public void testDetectHeaders() throws IOException, CSVHandler.EmptyCSVException {

        CSVHandler.setFilePath(".\\src\\test\\TestCSVs\\testAll2.csv");
        CSVHandler.detectHeaders();
        System.out.println("Info Headers: "+CSVHandler.getDetectedInfoHeaders().toString());
        System.out.println("Question Headers: "+CSVHandler.getDetectedQHeaders().toString());
    }
}