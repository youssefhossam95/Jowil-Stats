package Jowil;

import Jowil.Reports.Report;
import Jowil.Reports.Report1;
import com.lowagie.text.DocumentException;


import org.apache.commons.math3.stat.correlation.PearsonsCorrelation ;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class Test {

    public static void main(String [] args) throws IOException, DocumentException, Docx4JException {



//        System.out.println(URLDecoder.decode(getResource("/reports/report1/report1template.html").getFile(),"utf-8"));


        XWPFDocument document = new XWPFDocument();

        //Write the Document in file system
        FileOutputStream out = new FileOutputStream( new File("E:\\work\\Jowil\\temp\\createdocument.docx"));
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("At tutorialspoint.com, we strive hard to " +
                        "provide quality tutorials for self-learning " +
                        "purpose in the domains of Academics, Information " +
                        "Technology, Management and Computer Programming Languages.");
        document.write(out);
        out.close();
        System.out.println("createdocument.docx written successully");


        //        ////////////////////////////  conversion code ///////////////////////////////////////
//        String html = "<html><head><title>Import me</title></head><body><p>Hello World!</p></body></html>";
//
//        File file = new File("E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\htmlReports\\report2.html");
//        Document doc = Jsoup.parse(file, "UTF-8");
//
//        String html = doc.outerHtml();
//
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
//        afiPart.setBinaryData(html.getBytes());
//        afiPart.setContentType(new ContentType("text/html"));
//        Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);
//
//// .. the bit in document body
//        CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
//        ac.setId(altChunkRel.getId() );
//        wordMLPackage.getMainDocumentPart().addObject(ac);
//
//// .. content type
//        wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");
//        wordMLPackage.save(new java.io.File("E:\\work\\Jowil\\temp\\test.docx"));

//        final String reportsPath=  "E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
//
//
//        final String report2TemplatePath = reportsPath + "report5\\test.html";
//
//        File file = new File(report2TemplatePath);
//        Document doc = Jsoup.parse(file, "UTF-8");
//
////        doc.select("div#footer").remove();
//        doc.select("p.group-name").last().select("span").last().text("hi");
//        String templateBodyHtml = doc.select("p.group-name").last().select("span").last().text();
//
//        System.out.println(templateBodyHtml);


//        String filePath = ".\\src\\test\\TestCSVs\\welloAnswerKeys.csv" ;
//        BufferedReader input = new BufferedReader(new FileReader(filePath));
//        String line ;
//        ArrayList<ArrayList<String>> csvRows = new ArrayList<>();
//
//        while( (line = input.readLine()) != null ) {
//            String[] row = line.split(",") ;
//            ArrayList<String> rowList = new ArrayList<String>() ;
//            for(int i = 0 ; i < row.length ; i++)
//                rowList.add(row[i]);
//            csvRows.add(rowList);
//        }
//        System.out.println(csvRows);


//        DecimalFormat format = new DecimalFormat("0.##");
//
//        System.out.println(format.format(0));

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
//        ReportsHandler reportsHandler = new ReportsHandler();
//        reportsHandler.generatePDF(reportsPath + "report2\\test.html", reportsPath + "report2\\test2.pdf");

    }
}
