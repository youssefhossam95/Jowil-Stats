package Jowil;

import com.lowagie.text.DocumentException;


import org.apache.commons.math3.stat.correlation.PearsonsCorrelation ;


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




        ReportsHandler reportsHandler = new ReportsHandler() ;
        reportsHandler.generateReport2();

        /////////////////// test point biserial ////////////////////////////////////
//        double[] a = {1.0 , 1.0 , 0 , 0,  0} ;
//        double [] b = {1.0 , 1.0 , 0 , 1.0,  0} ;
//        double [] c = {1.0 , 1.0 , 1.0 , 0,  1.0 } ;
//        double [] x = {0 , 0 , 1.0 , 1.0 , 1.0 ,} ;
//        double[] total = {3.0 , 3.0 , 1.0, 1.0 , 1.0} ;
//
//        double [] zeros = {1,1,1,1,0};
//
//        ArrayList<Double> hi = new ArrayList<>();
//        hi.add(1.0);hi.add(1.0);hi.add(1.0);hi.add(1.0);hi.add(1.0);
//
//        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation() ;
//        System.out.println(Double.isNaN(pearsonsCorrelation.correlation( total , zeros))?0:pearsonsCorrelation.correlation( total , zeros)) ;



//        //////////////////////// to create pdf from html //////////////////////////////////

//         final String reportsPath=  "E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
//
//         final String report4TemplatePath = reportsPath + "report1\\report1Template.html";
//
//        File file = new File(report4TemplatePath);
//
//        Document doc =  Jsoup.parse(file , "UTF-8");
//
////        System.out.println("hi"+doc.select("tr.headerRow").last().outerHtml());
//
//        ReportsHandler reportsHandler = new ReportsHandler();
//        reportsHandler.generatePDF(reportsPath + "report1\\report1Template.html", reportsPath + "report1\\test.pdf");

    }
}
