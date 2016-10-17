package org.kevoree.brain.oven.util;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by assaad on 19/02/15.
 */
public class XLXLoader {

    public static MWGAttribute loadOneCol(String excelfile, int sheetNbr, int column) {
        try {
            FileInputStream file = new FileInputStream(excelfile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(sheetNbr);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNum = 0;
            MWGAttribute result = null;

            Date timestamp;
            double value;
            int errcounter = 0;
            int normcounter = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (rowNum == 0) {
                    result = new MWGAttribute(row.getCell(column).getStringCellValue());
                    row = rowIterator.next();
                }
                try {
                    timestamp = row.getCell(0).getDateCellValue();
                    value = row.getCell(column).getNumericCellValue();
                    result.setValue(timestamp.getTime(), value);
                    normcounter++;
                } catch (Exception ex) {
                    errcounter++;
                }
                rowNum++;
            }
            System.out.println("Loaded " + normcounter + " values of " + result.getName() + " with " + errcounter + " errors");
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static MWGObject loadFile(String excelfile) {
        MWGObject model = new MWGObject();
        try {
            long timestampinit = System.currentTimeMillis();
            FileInputStream file = new FileInputStream(excelfile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            HashMap<String, MWGAttribute> dictionary=new HashMap<String, MWGAttribute>();

            int totalelem = 0;
            int totalerr = 0;
            int totalvar = 0;
            int totalnull = 0;

            for (int sheetNbr = 1; sheetNbr < workbook.getNumberOfSheets(); sheetNbr++) {
                XSSFSheet sheet = workbook.getSheetAt(sheetNbr);
                int numberOfCells = 0;
                Iterator rowIteratorhead = sheet.rowIterator();
                if (rowIteratorhead.hasNext()) {
                    Row headerRow = (Row) rowIteratorhead.next();
                    numberOfCells = headerRow.getPhysicalNumberOfCells();
                }

                for (int column = 1; column < numberOfCells; column++) {
                    //Iterate through each rows one by one
                    if (sheet.getRow(0).getCell(column) == null) {
                        continue;
                    }


                    Iterator<Row> rowIterator = sheet.iterator();
                    int rowNum = 0;
                    MWGAttribute result = null;
                    Date timestamp = null;
                    double value = 0;
                    int errcounter = 0;
                    int normcounter = 0;
                    int nullval = 0;
                    String att="";

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        if (rowNum == 0) {
                            att=row.getCell(column).getStringCellValue();
                            if(att.equals("CastNb")){
                                att="CAST_NUMBER";
                            }
                            if(dictionary.get(att)==null) {
                                result = new MWGAttribute(row.getCell(column).getStringCellValue());
                                dictionary.put(att,result);
                            }
                            else{
                                result=dictionary.get(att);
                            }
                            row = rowIterator.next();

                        }
                        try {
                            timestamp = row.getCell(0).getDateCellValue();
                            value = row.getCell(column).getNumericCellValue();
                            result.setValue(timestamp.getTime(), value);
                            normcounter++;
                        } catch (Exception ex) {
                            if (row.getCell(column) == null || row.getCell(column).getStringCellValue().toLowerCase().trim().equals("null") || row.getCell(column).getStringCellValue().toLowerCase().trim().equals("")) {
                                nullval++;
                            } else {
                                if (row.getCell(column).getStringCellValue().toLowerCase().trim().equals("pci1")) {
                                    result.setValue(timestamp.getTime(), 0);
                                    normcounter++;
                                }
                                else if (row.getCell(column).getStringCellValue().toLowerCase().trim().equals("ore")) {
                                    result.setValue(timestamp.getTime(), 0);
                                    normcounter++;
                                } else if (row.getCell(column).getStringCellValue().toLowerCase().trim().equals("coke")) {
                                    result.setValue(timestamp.getTime(), 1);
                                    normcounter++;
                                } else {
                                    System.out.println("ERROR: " + row.getCell(column).getStringCellValue());
                                    errcounter++;
                                }
                            }
                        }
                        rowNum++;
                    }
                    totalelem += normcounter;
                    totalerr += errcounter;
                    totalnull += nullval;
                    totalvar++;
                    System.out.println("Loaded " + normcounter + " values of " + result.getName() + " with " + errcounter + " errors and " + nullval + " nulls");
                    model.add(result);
                    totalelem++;
                }
                System.out.println();
            }
            System.out.println("total values: " + totalelem + ", total error:" + totalerr + ", total null: " + totalnull + ", total variables: " + totalvar);
            long timestampfinal = (System.currentTimeMillis()-timestampinit);
            System.out.println("File loaded in: "+timestampfinal+" ms");


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return model;
    }

}