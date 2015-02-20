package org.kevoree.brain.smartgrid;

import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by assaad on 19/02/15.
 */
public class ExcelLoader {

    public static HashMap<String,ArrayList<ElectricMeasure>> load(String directory){
        HashMap<String,ArrayList<ElectricMeasure>> result = new HashMap<String, ArrayList<ElectricMeasure>>();

        double apmax=0;
        double ammax=0;
        double rpmax=0;
        double rmmax=0;
        int errCounter=0;
        int globaltotal=0;

        String s="";
        try {
            File dir = new File(directory);
            File[] directoryListing = dir.listFiles();
            System.out.println("Found " + directoryListing.length + " files");
            if (directoryListing != null) {
                for (File file : directoryListing) {

                    s=file.getName();
                   if(file.getName().equals(".DS_Store")){
                       continue;
                   }
                    FileInputStream file2 = new FileInputStream(file);

                    //Create Workbook instance holding reference to .xlsx file
                    XSSFWorkbook workbook = new XSSFWorkbook(file2);

                    //Get first/desired sheet from the workbook
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    //Iterate through each rows one by one
                    Iterator<Row> rowIterator = sheet.iterator();
                    int rowNum=0;

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        if (rowNum == 0)
                            row = rowIterator.next();
                        String equipment = row.getCell(0).getStringCellValue();

                        if (!equipment.startsWith("ZIVS")) {
                            Date timestamp = row.getCell(1).getDateCellValue();
                            double aplus = row.getCell(2).getNumericCellValue();
                            double aminus = row.getCell(3).getNumericCellValue();
                            double rplus = row.getCell(4).getNumericCellValue();
                            double rminus = row.getCell(5).getNumericCellValue();

                            if(aplus<1e7&&aminus<1e7&&rplus<1e7&&rminus<1e7) {

                                if (aplus > apmax) {
                                    apmax = aplus;

                                }
                                if (aminus > ammax) {
                                    ammax = aminus;
                                }
                                if (rplus > rpmax) {
                                    rpmax = rplus;
                                }
                                if (rminus > rmmax) {
                                    rmmax = rminus;
                                }

                                ArrayList<ElectricMeasure> ad;
                                if (result.containsKey(equipment)) {
                                    ad = result.get(equipment);
                                    ElectricMeasure dp = new ElectricMeasure();
                                    dp.setTime(timestamp.getTime());
                                    dp.aplus = aplus;
                                    dp.aminus = aminus;
                                    dp.rplus = rplus;
                                    dp.rminus = rminus;
                                    ad.add(dp);
                                } else {
                                    ad = new ArrayList<ElectricMeasure>();
                                    ElectricMeasure dp = new ElectricMeasure();
                                    dp.setTime(timestamp.getTime());
                                    dp.aplus = aplus;
                                    dp.aminus = aminus;
                                    dp.rplus = rplus;
                                    dp.rminus = rminus;
                                    ad.add(dp);
                                    result.put(equipment, ad);
                                }

                                globaltotal++;
                                rowNum++;
                            }
                            else{
                                errCounter++;
                            }
                        }
                    }
                    System.out.println("file "+file.getName()+" read "+rowNum);
                    //fileInputStream.close();
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in file: "+s);
            e.printStackTrace();
        }
        System.out.println("Number of Error: "+errCounter);
        System.out.println("Read "+globaltotal+" power records!");
        System.out.println(apmax+","+ammax+","+rpmax+","+rmmax);





        return result;
    }
}