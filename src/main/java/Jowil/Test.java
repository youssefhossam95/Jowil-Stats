package Jowil;

import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String [] args) throws IOException, DocumentException {


        ArrayList<ArrayList<String>> statsTable = new ArrayList<ArrayList<String>>();

        Map<String, Integer> gradsCount = new HashMap<>();
        gradsCount.put("A" , 10) ;
        gradsCount.put("B" , 20) ;
        gradsCount.put("C" , 30) ;

        for(Map.Entry<String , Integer> entry : gradsCount.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        //////////////////////// to create pdf from html //////////////////////////////////

//         final String reportsPath=  "E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
//
//         final String report4TemplatePath = reportsPath + "report4\\report4Template.html";
//
//        File file = new File(report4TemplatePath);
//
//        Document doc =  Jsoup.parse(file , "UTF-8");
//
//        System.out.println("hi"+doc.select("tr.headerRow").last().outerHtml());
//
//        ReportsHandler reportsHandler = new ReportsHandler();
//        reportsHandler.generatePDF(reportsPath + "report4\\test.html", reportsPath + "report4\\test.pdf");

    }
}
