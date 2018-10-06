package Jowil.Reports.Utils;

import Jowil.Reports.ReportsHandler;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.util.IOUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class XlsUtils {


    public static HSSFWorkbook workbook ;
    public static HSSFSheet sheet ;
    public static int pageWidth ;
    final static int TABLE_ROW_HEIGHT = 400 ;
    final static String REPORTS_COLOR = "095c90" ;
    public static int nextColorIndex =40;
    public static int lastRowIndex = 0 ;
    public static final int  NUMBER_OF_LINES_AFTER_TABLE = 2 ;

    public static void createXls (int width) {
        workbook = new HSSFWorkbook() ;
        sheet = workbook.createSheet();

        pageWidth = width ;
//        sHSSFCellStyle defaultColumnStyle = getDefaltColumnStyle();
//        for(int i = 0 ; i < pageWidth ; i ++  ) {
//            sheet.setDefaultColumnStyle(i ,defaultColumnStyle );
//        }
    }


    public static short getColor(String color) {

        byte r =(byte) Integer.parseInt(color.substring(0 , 2) , 16) ;
        byte g =(byte)Integer.parseInt(color.substring(2, 4) , 16) ;
        byte b = (byte)Integer.parseInt(color.substring(4 , 6) , 16) ;

        HSSFPalette palette = workbook.getCustomPalette();


        HSSFColor myColor  = palette.findColor(r,g,b) ;
        if(myColor == null) {
            // get the color which most closely matches the color you want to use
            palette.setColorAtIndex((short) nextColorIndex, r, g, b);
            myColor= palette.getColor(nextColorIndex++) ;
        }
        // get the palette index of that color
        return  myColor.getIndex() ;
    }

    public static void addTitle(String title  , int numberOfLinesBelow) {

        // merge cells
        sheet.addMergedRegion(
                new CellRangeAddress(
                        lastRowIndex, //first row (0-based)
                        lastRowIndex, //last row (0-based)
                        0, //first column (0-based)
                        pageWidth-1 //last column (0-based)
                )
        );

        HSSFRow row = sheet.createRow(lastRowIndex);
        row.setHeight((short) 500);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(title);

//        HSSFRow row2 = sheet.createRow(10) ;
//        HSSFCell cell2 = row2.createCell(0);


        HSSFCellStyle titleStyle = getDefaltColumnStyle();

        //set background color
        titleStyle.setFillForegroundColor(getColor(REPORTS_COLOR));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(titleStyle);

        //set font
        HSSFFont font= workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short)18);
        font.setColor((short) 9 ); // white color index in palette
        titleStyle.setFont(font);

        lastRowIndex += numberOfLinesBelow + 1 ;
    }

    public static HSSFCellStyle getDefaltTableCellStyle(){
        HSSFCellStyle defaultTableCellStyle = workbook.createCellStyle();

        defaultTableCellStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultTableCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        defaultTableCellStyle.setBorderTop(BorderStyle.THIN);
        defaultTableCellStyle.setBorderBottom(BorderStyle.THIN);
        defaultTableCellStyle.setBorderRight(BorderStyle.THIN);
        defaultTableCellStyle.setBorderLeft(BorderStyle.THIN);
        return  defaultTableCellStyle ;
    }

    public static HSSFCellStyle getLRTableCellStyle(){
        HSSFCellStyle defaultTableCellStyle = workbook.createCellStyle();

        defaultTableCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        defaultTableCellStyle.setBorderTop(BorderStyle.THIN);
//        defaultTableCellStyle.setBorderBottom(BorderStyle.THIN);
//        defaultTableCellStyle.setBorderRight(BorderStyle.THIN);
//        defaultTableCellStyle.setBorderLeft(BorderStyle.THIN);
        return  defaultTableCellStyle ;
    }

    public static HSSFCellStyle getDefaltColumnStyle(){
        HSSFCellStyle defaultColStyle = workbook.createCellStyle();
        defaultColStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultColStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return defaultColStyle;
    }

    public static void addTableAlignCenter( ArrayList<ArrayList<String>> table , int colStartIndex ) {
        for (int rowIndex = 0 ; rowIndex < table.size() ; rowIndex++) {
            ArrayList<String> tableRow = table.get(rowIndex);
            HSSFRow row  =sheet.createRow(lastRowIndex +rowIndex);
            row.setHeight((short)TABLE_ROW_HEIGHT);
            for(int colIndex = 0 ; colIndex < tableRow.size(); colIndex++) {
                HSSFCellStyle cellStyle = getDefaltTableCellStyle() ;
                HSSFCell cell = row.createCell(colStartIndex+colIndex);
                cell.setCellValue(tableRow.get(colIndex)) ;
                if(rowIndex==0) {
                    HSSFFont font= workbook.createFont();
                    font.setBold(true);
                    cellStyle.setFont(font);

                }

                cell.setCellStyle(cellStyle);

            }

        }
        lastRowIndex += table.size() + NUMBER_OF_LINES_AFTER_TABLE ;
    }


    public static void addTableAlignLR( ArrayList<ArrayList<String>> table , String title ) {
        int colStartIndex = 1 ;
        HSSFRow row = sheet.createRow(lastRowIndex++) ;
        row.createCell(colStartIndex).setCellValue(title);
        HSSFCellStyle cellStyle = getLRTableCellStyle() ;

//        HSSFCellStyle style = getLRTableCellStyle() ;
//        style.setAlignment(HorizontalAlignment.RIGHT);
        for (int rowIndex = 0 ; rowIndex < table.size() ; rowIndex++) {

            ArrayList<String> tableRow = table.get(rowIndex);
            row  =sheet.createRow(lastRowIndex +rowIndex);
            row.setHeight((short)TABLE_ROW_HEIGHT);
            for(int colIndex = 0 ; colIndex < tableRow.size(); colIndex++) {

                HSSFCell cell = row.createCell(colStartIndex+colIndex);
                cell.setCellValue(tableRow.get(colIndex)) ;
                cell.setCellStyle(cellStyle);

                if(rowIndex==0) {
                    CellUtil.setCellStyleProperty(cell , CellUtil.BORDER_TOP ,BorderStyle.MEDIUM );
                }


                if(colIndex%2==0)
                    CellUtil.setAlignment(cell , HorizontalAlignment.LEFT);
                else
                    CellUtil.setAlignment(cell , HorizontalAlignment.RIGHT);


            }

        }
        lastRowIndex += table.size() + NUMBER_OF_LINES_AFTER_TABLE ;
    }

    public static void addPictureToCell(String imgPath , int rowIndex  , int colIndex , int width , int height ) throws IOException {
        InputStream inputStream = new FileInputStream(imgPath);

        byte[] imageBytes = IOUtils.toByteArray(inputStream);

        int pictureureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

        inputStream.close();

        CreationHelper helper = workbook.getCreationHelper();

        HSSFPatriarch  drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = helper.createClientAnchor();

        anchor.setCol1(colIndex);
        anchor.setRow1(rowIndex);

        HSSFPicture pic = drawing.createPicture(anchor, pictureureIdx);
        pic.resize(width , height );
        lastRowIndex += height + 2  ;
    }
    public static void postProcessSheet() {
        for(int i =0 ; i < lastRowIndex ; i ++) {
            HSSFRow row = sheet.getRow(i) ;
            if(row == null)
               row =  sheet.createRow(i);
            row.setHeight((short)TABLE_ROW_HEIGHT);
        }
        for( int i = 0 ; i < pageWidth ; i++ )
            sheet.autoSizeColumn(i);
    }
    public static void writeXlsFile(String filePath) throws IOException {
        postProcessSheet();
        FileOutputStream out = new FileOutputStream(new File(filePath));
        workbook.write(out);
        out.close();
    }


}
