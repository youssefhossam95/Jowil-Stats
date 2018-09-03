package Jowil;

import Jowil.Reports.Report;
import Jowil.Reports.Report1;
import Jowil.Reports.ReportsHandler;
import com.lowagie.text.DocumentException;


import javafx.scene.chart.StackedAreaChart;
import javafx.util.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation ;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.StatUtils.* ;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static org.apache.commons.math3.stat.StatUtils.max;


public class Test {

    /**
     * function to get the slope and the average error of the trend for question hardness data sequence
     * @param hardness the ordered hardness of questions at index 0 question 1 ... etc
     * @return Pair with key = slope and value = error
     */
    public static Pair<Double , Double> getTrendData (ArrayList<Double> hardness) {
        double[] hardnessArray = hardness.stream().mapToDouble(d -> d).toArray();
        double [][] X = new double[hardnessArray.length][1] ;

        double maxHardness = max(hardnessArray);
        for (int i = 0 ; i< hardnessArray.length ; i++) { // x axis = 0 --> 1
            X[i][0] = (double)i / (double)(hardnessArray.length - 1);
            hardnessArray[i]/=maxHardness ;  // to make hardness 0 --> 1
        }
        OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
        ols.newSampleData(hardnessArray , X) ;
        RealMatrix coff = MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters());
        double slope = coff.getColumnVector(0).getEntry(1);
        double Rss = ols.estimateRegressionStandardError();
        System.out.println("Resedual sum squared: "+ols.calculateResidualSumOfSquares());
//        ols.
        return new Pair<Double, Double>(slope , Rss) ;
        }

        public static char getReplacement (char char1 , char char2 ){
            int shiftedDiff = Math.abs(char1 - char2) ;
            if(shiftedDiff == 2)
                return 'b' ;
            else if(Math.min(char1 , char2)-'a' == 0)
                return 'c';
            else
                return 'a';
        }
    public static void main(String [] args) throws IOException, DocumentException {

//        ArrayList<Double> hardness = new ArrayList<>( );
//        for(int i = 0 ; i < 40 ; i ++)
//            hardness.add(0.0);hardness.add(10.0);
////       hardness.add(0.0);hardness.add(10.0);
////        hardness.add(0.0); hardness.add(10.0) ;  hardness.add(0.0) ;  hardness.add(10.0) ;
////        hardness.add(0.0) ; hardness.add(10.0) ; hardness.add(0.0); hardness.add(10.0);
//        Pair<Double , Double> pair = getTrendData(hardness ) ;
//        double slope = pair.getKey();
//        double Rss = pair.getValue() ;
//        System.out.println("slope: " + slope);
//        System.out.println("standard Error: " + Rss)

        double number=  5.236;
        System.out.println(Statistics.formatNumber(number , 2) );


//        System.out.println(URLDecoder.decode(getResource("/reports/report1/report1template.html").getFile(),"utf-8"));



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



        //////////////////////// to create pdf from html //////////////////////////////////

//         final String reportsPath=  "E:\\work\\Jowil\\Jowil-Stats\\src\\main\\resources\\reports\\";
//        ReportsHandler reportsHandler = new ReportsHandler();
//        reportsHandler.generatePDF(reportsPath + "report8\\Report8.html", reportsPath + "report8\\test.pdf");
//



    }
}

