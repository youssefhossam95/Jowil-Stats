package Jowil.Reports.Utils;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;

public class WordUtils {

    final static int inch = 1440 ;
    final static int TITLE_FONT_SIZE = 24 ;
    public final static String REPORTS_COLOR = "157880" ;
    final static String GRAY_ROW_COLOR = "EFEFEF" ;
    public final static int  A4_PAGE_WIDTH = (int)(6.5 * inch) ; // used width of the A4 page
    public final static int LANDSCAPE_PAGE_WIDHT = 15840 ; // total width of the land scape page
    public final static int LANDSCAPE_PAGE_HEIGHT = 12240; // total height of the land scape page
    public final static int TABLE_ALIGN_CENTER = 0 ;
    public final static int TABLE_ALIGN_LR = 1 ;
    static int pageWidth = A4_PAGE_WIDTH ;



    public static XWPFDocument createDocument (){
      return  createDocument((int)(8.5*inch ) ,  (int)(11 * inch)) ;
    }

    public static void changeDocumentSize(XWPFDocument document , int width , int height) {

        CTBody body = document.getDocument().getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        if(!section.isSetPgSz()) {
            section.addNewPgSz();
        }
        CTPageSz pageSize = section.getPgSz();

        pageWidth = width - 2 *inch ;

        pageSize.setW(BigInteger.valueOf(width));
        pageSize.setH(BigInteger.valueOf(height));
    }
    public static XWPFDocument createDocument (int width , int height){
        XWPFDocument document = new XWPFDocument();

        changeDocumentSize(document , width , height) ;

        //set document margins
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(inch));
        pageMar.setTop(BigInteger.valueOf((int)(0.5*inch)));
        pageMar.setRight(BigInteger.valueOf(inch));
        pageMar.setBottom(BigInteger.valueOf((int)(0.5*inch)));


        return document ;
    }
    public static void changeTableWidth(XWPFTable table){
        changeTableWidth(table , pageWidth);
    }

    public static void changeTableWidth(XWPFTable table , int tableWidth ){
        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        width.setType(STTblWidth.DXA);
        width.setW(BigInteger.valueOf(tableWidth));
    }

    public static void addPageBreak (XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setPageBreak(true);
        paragraph.removeRun(0) ;
    }


    public static void removeBorders (XWPFTable docTable ) {
        removeBorders(docTable, true);
    }

        public static void removeBorders (XWPFTable docTable , boolean addTopBorder ) {
        docTable.setInsideHBorder(XWPFTable.XWPFBorderType.NONE,0 , 0 , "FFFFFF");
        docTable.setInsideVBorder(XWPFTable.XWPFBorderType.NONE , 0 , 0 , "FFFFFF");


        CTTblPr tblpro = docTable.getCTTbl().getTblPr();

        CTTblBorders borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.NONE);
        borders.addNewLeft().setVal(STBorder.NONE);
        borders.addNewRight().setVal(STBorder.NONE);
        if(addTopBorder) {
            borders.addNewTop().setVal(STBorder.THICK);
            borders.getTop().setSz(BigInteger.valueOf(15));
            borders.getTop().setSpace(BigInteger.valueOf(0));
            borders.getTop().setColor(REPORTS_COLOR);
        }
        else
            borders.addNewTop().setVal(STBorder.NONE);
    }
    public static XWPFTable addTable(XWPFDocument document , ArrayList<ArrayList<String>> dataTable ) throws IOException, InvalidFormatException {
        return addTable(document,dataTable, TABLE_ALIGN_CENTER , "" , 12 , false );
    }

    public static XWPFTable addTable(XWPFDocument document , ArrayList<ArrayList<String>> dataTable , boolean hasBorders ) throws IOException, InvalidFormatException {
        return addTable(document, dataTable, TABLE_ALIGN_CENTER , "" , 12 , hasBorders );

    }
    public static XWPFTable addTable(XWPFDocument document , ArrayList<ArrayList<String>> dataTable , int alignment , String title , int titleFontSize) throws IOException, InvalidFormatException {
       return addTable(document ,  dataTable, alignment , title , titleFontSize , false );
    }


    public static XWPFTable addTable(XWPFDocument document , ArrayList<ArrayList<String>> dataTable , int alignment , String title , int titleFontSize , boolean hasBorders) throws IOException, InvalidFormatException {

        if(title != "") {
            XWPFParagraph titleParagraph = document.createParagraph() ;
            if(alignment==TABLE_ALIGN_LR)
                titleParagraph.setSpacingAfter(0);
            XWPFRun titleRun = titleParagraph.createRun() ;
            titleRun.setText(title);
            titleRun.setFontSize(titleFontSize);
        }

        int numberOfRows = dataTable.size();
        int numberOfCols = dataTable.get(0).size() ;
//        XWPFTable docTable = new XWPFTable() ;
        XWPFTable docTable =document.createTable(numberOfRows , numberOfCols);

        int cellRightMargin = 0 ;
        if(alignment== TABLE_ALIGN_LR && numberOfCols > 2)
            cellRightMargin = 200 ;


        docTable.setCellMargins(50 , 0 ,50 , cellRightMargin);
        setTableAlign(docTable,ParagraphAlignment.CENTER);
        changeTableWidth(docTable , pageWidth);
        if(!hasBorders)
            removeBorders(docTable);

        for(int rowIndex = 0 ; rowIndex< numberOfRows ; rowIndex++) {
            ArrayList<String> dataTableRow = dataTable.get(rowIndex) ;
            XWPFTableRow docTableRow = docTable.getRow(rowIndex);
            for(int colIndex = 0 ; colIndex < numberOfCols  ; colIndex++) {
                String data = dataTableRow.get(colIndex);
                XWPFTableCell cell = docTableRow.getCell(colIndex);
                XWPFParagraph par = cell.getParagraphArray(0) ;
                par.setSpacingAfter(0);
                if(rowIndex%2==0 && alignment!= TABLE_ALIGN_LR) {
                    if(!(rowIndex == 0 && hasBorders))
                        cell.setColor(GRAY_ROW_COLOR);
                }
                if(alignment == TABLE_ALIGN_LR) {
                    if(colIndex%2==0)
                        par.setAlignment(ParagraphAlignment.LEFT);
                    else
                        par.setAlignment(ParagraphAlignment.RIGHT);
                }else
                    par.setAlignment(ParagraphAlignment.CENTER);
              XWPFRun run = processCellData(cell , par , data );
              if(alignment == TABLE_ALIGN_CENTER && rowIndex==0)
                  run.setBold(true);

            }
        }
        document.createParagraph().createRun().addBreak();
        return docTable ;
    }


    public static XWPFTable createTableInCell (XWPFTableCell wrapperCell, ArrayList<ArrayList<String>> dataTable , int alignment , String title , int titleFontSize , boolean addHeaderRow ) throws IOException, InvalidFormatException {


        wrapperCell.removeParagraph(0);

        if(title != "") {
            XWPFParagraph titleParagraph = wrapperCell.addParagraph() ;
            if(alignment==TABLE_ALIGN_LR)
                titleParagraph.setSpacingAfter(0);
            XWPFRun titleRun = titleParagraph.createRun() ;
            titleRun.setText(title);
            titleRun.setFontSize(titleFontSize);
        }

        XWPFParagraph paragraph = wrapperCell.addParagraph();
        XWPFTable docTable = wrapperCell.insertNewTbl(paragraph.getCTP().newCursor());

        int numberOfRows = dataTable.size();
        int numberOfCols = dataTable.get(0).size() ;


        docTable.setCellMargins(50 , 0 ,50 , 0);
        setTableAlign(docTable,ParagraphAlignment.CENTER);
        changeTableWidth(docTable , (int)(pageWidth/2.8));

        removeBorders(docTable);

        if(addHeaderRow) {
            XWPFTableRow firstRow = docTable.createRow();
            for (int i = 0; i < numberOfCols; i++) {
                XWPFTableCell headerCell = firstRow.createCell();
//                headerCell.removeParagraph(0);
//                headerCell.getParagraphArray(0).setSpacingAfter(0);
                headerCell.setColor(GRAY_ROW_COLOR) ;
//                headerCell
                }
            firstRow.setHeight(250);
            firstRow.getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT); //set w:hRule="exact"

        }

        for(int rowIndex = 0 ; rowIndex< numberOfRows ; rowIndex++) {
            ArrayList<String> dataTableRow = dataTable.get(rowIndex) ;
            XWPFTableRow docTableRow = docTable.createRow();
            for(int colIndex = 0 ; colIndex < numberOfCols  ; colIndex++) {
                String data = dataTableRow.get(colIndex);
                XWPFTableCell cell ;
                if(rowIndex>0 || addHeaderRow)
                   cell= docTableRow.getCell(colIndex);
                else
                    cell=docTableRow.createCell() ;
                XWPFParagraph par = cell.getParagraphArray(0) ;
                par.setSpacingAfter(0);
                if(rowIndex%2==0 && alignment!= TABLE_ALIGN_LR) {
                    cell.setColor(GRAY_ROW_COLOR);
                }
                if(alignment == TABLE_ALIGN_LR) {
                    if(colIndex%2==0)
                        par.setAlignment(ParagraphAlignment.LEFT);
                    else
                        par.setAlignment(ParagraphAlignment.RIGHT);
                }else
                    par.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = processCellData(cell , par , data );
                if(alignment == TABLE_ALIGN_CENTER && rowIndex==0)
                    run.setBold(true);

            }
        }
        return docTable ;
    }

    private static void parseCellClass( XWPFTableCell cell ,  String cellClassesString) {
        String [] cellClasses = cellClassesString.split(" ") ;
        ArrayList<String> cellClassesList = new ArrayList<>();
        for(int i =0 ; i < cellClasses.length ; i++)
            cellClassesList.add(cellClasses[i]) ;

        if(cellClassesList.contains("red"))
            cell.setColor("f87878");
        if(cellClassesList.contains("gold"))
            cell.setColor("ffe44d");
        if(cellClassesList.contains("green"))
            cell.setColor("71e08d");
    }
    /**
     * function to take cell data and parse it then it puts the parsed data in the cell
     * @param cell cell object to be filled
     * @param cellData string containing class and text of the cell
     */
    private static XWPFRun processCellData (XWPFTableCell cell , XWPFParagraph cellParagraph , String cellData ) throws IOException, InvalidFormatException {

        String cellText = cellData ; // the normal case no classes
        if(cellData.length()>7 && cellData.substring(0 , 7).equals("<<img>>")){
            String imgPath = cellData.substring(7 , cellData.length());
            addImage(cellParagraph , imgPath , 100 , 10) ;
            return cellParagraph.createRun();
        }else if(cellData.contains(";")) {
            String [] parts = cellData.split(";");
            cellText = parts[0] ;
            String cellClass= parts[1] ;
            parseCellClass(cell , cellClass);
        }

        XWPFRun run = cellParagraph.createRun();
        run.setText(cellText);
        return run;

    }
    public static  void setTableAlign(XWPFTable table,ParagraphAlignment align) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
        STJc.Enum en = STJc.Enum.forInt(align.getValue());
        jc.setVal(en);
    }


    public static void addTitle (XWPFDocument document , String title ) {

        // create title table
        XWPFTable titleTable = document.createTable(1 , 3) ;
        titleTable.setCellMargins(30 , 100 , 30 , 100);
        changeTableWidth(titleTable);
        setTableAlign(titleTable , ParagraphAlignment.CENTER);
        removeBorders(titleTable);


        // add title text to the table and format it
        XWPFTableRow titleTableRow = titleTable.getRow(0);
        XWPFParagraph paragraph = titleTableRow.getCell(1).addParagraph() ;
        paragraph.setSpacingAfter(0);
        titleTableRow.getCell(1).removeParagraph(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(TITLE_FONT_SIZE);
        run.setText(title) ;
        run.setColor("FFFFFF");
        for (int i = 0 ; i < 3 ; i++)
            titleTableRow.getCell(i).setColor(REPORTS_COLOR);

        // add line breaks after the table
        int numberOfBreaks = 4 ;
        XWPFRun lineBreakRun = document.createParagraph().createRun();
        for(int i = 0 ; i <4 ; i++) {
            lineBreakRun.addBreak();
        }
    }

    public static XWPFParagraph addLegendImage(XWPFDocument document , String imgPath) throws IOException, InvalidFormatException {
        return  addImage( document ,null ,imgPath , ParagraphAlignment.LEFT,  10 , 10 , false);
    }

    public static XWPFParagraph addImage (XWPFDocument document , String imgPath) throws IOException, InvalidFormatException {
      return  addImage( document , null,  imgPath , ParagraphAlignment.CENTER , 400 , 300 , true );
    }
    public static XWPFParagraph addImage (XWPFParagraph paragraph , String imgPath , int width , int height) throws IOException, InvalidFormatException {
        return  addImage( null , paragraph,  imgPath , ParagraphAlignment.CENTER , width , height , false );
    }



    public static XWPFParagraph addImage (XWPFDocument document, XWPFParagraph p , String imgPath , ParagraphAlignment alignment , int width , int height , boolean addBreak ) throws IOException, InvalidFormatException {

        if(p == null)
            p =  document.createParagraph();
        XWPFRun run = p.createRun() ;
        p.setAlignment(alignment);
        FileInputStream is = new FileInputStream(imgPath);
        if(addBreak)
            run.addBreak();
        run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, imgPath, Units.toEMU(width), Units.toEMU(height));
        is.close();
        return p ;

    }

    public static void addLegend (XWPFDocument document , ArrayList<ArrayList<String>> legends) throws IOException, InvalidFormatException {
        for ( int i = 0  ; i < legends.size() ; i++) {
            String imgPath = legends.get(i).get(0) ;
            String legendTxt = legends.get(i).get(1) ;
            XWPFParagraph legendPar = WordUtils.addLegendImage(document , imgPath);
            XWPFRun legendRun = legendPar.createRun();
            legendRun.setText(" " + legendTxt);
            legendRun.setFontSize(13);
            legendRun.setBold(true);

            if(i != legends.size()-1)
                legendPar.setSpacingAfter(0);

        }

    }


    public static void writeWordDocument (XWPFDocument document , String filePath) throws IOException {

        FileOutputStream out = new FileOutputStream( new File(filePath));
        document.write(out);
        out.close();
    }
}
