package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.a.b.mileagetracker.DataAccess.DataProvider;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.opencsv.CSVWriter;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Andrew on 11/4/2015.
 */
public class EmailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private MySQLiteHelper mDBHelper;
    String TAG="EmailFragment";
    Workbook workBook;
    Sheet sheet;
    CreationHelper createHelper;
    FileOutputStream fileOut;
    File exportDir;
    ArrayList<String> vehicles=new ArrayList<>();

    public EmailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        ArrayList<String> vehicles=new ArrayList<String>(Arrays.asList(TextUtils.split(sharedPrefs.getString("vehicle_list", ""), "‚‗‚")));
//        for(String v:vehicles){
//            Log.e(TAG,"vehicles in shared prefs: "+v);
//        }

        workBook=new HSSFWorkbook();
        createHelper=workBook.getCreationHelper();

        Log.e(TAG, "processors available: " + Runtime.getRuntime().availableProcessors());
//        ExecutorService executor= Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
//            }
//        });

        int num=1;
        for(String v:vehicles) {
            Bundle extra=new Bundle();
            extra.putString("vehicle",v);
            getLoaderManager().initLoader(num, extra, (LoaderManager.LoaderCallbacks) this);
            num++;
        }

        File dbFile = getActivity().getApplicationContext().getDatabasePath(MySQLiteHelper.getInstance(getActivity().getApplicationContext()).getDatabaseName());
        Log.e(TAG, "DbFile path is: " + dbFile); // get the path of db
        exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + "exportFillupTable");
        Log.e(TAG, "exportDB path is: " + exportDir.getAbsolutePath()); // get the path of db

        long freeBytesInternal = new File(getActivity().getApplicationContext().getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
        long megAvailable = freeBytesInternal / 1048576;

        if (megAvailable < 0.1) {
            System.out.println("Please check"+megAvailable);
//            memoryErr = true;
        }else {
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file;

            try {
                file =new File(exportDir, "tracker" + ".csv");
                if(!file.exists()){
                    Boolean result =file.createNewFile();
                    Log.e("bool","bool createFile: "+result);
                }
//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
////                String arrStr1[] = { "SR.No", "CUTSOMER NAME", "PROSPECT", "PORT OF LOAD", "PORT OF DISCHARGE" };
////                String[] record = "4,cows,moo,sheeps".split(",");
//                data.moveToFirst();
//
//                List<String> list=new ArrayList<String>();
//                for(String c:data.getColumnNames()){
//                    list.add(c);
//                }
//
//                String[] names=list.toArray(new String[list.size()]);
//
////                String arrStr1[]={"columns in cursor: ",data.getString(0),data.getString(2),data.getString(3)};
////                Log.e(TAG, "columns in cursor: "+data.getString(0)+", "+data.getString(1)+", "+data.getString(2)+", "+data.getString(3));
//
//                csvWrite.writeNext(names);
//                csvWrite.close();


//                NPOIFSFileSystem fs=new NPOIFSFileSystem(new File(exportDir, "AdobeTracker" + ".csv"));
//                HSSFWorkbook workBook=new HSSFWorkbook(fs.getRoot(),true);
                fileOut = new FileOutputStream(exportDir+"/AdobeXLS.xls");
                workBook.write(fileOut);
                fileOut.close();
//                fs.close();

//                CSVReader read = new CSVReader(new FileReader(exportDir+"/hello1" + ".csv"),',','"',1);
//                String location=exportDir+File.separator+"hello1" + ".csv";
//                CSVReader read = new CSVReader(new FileReader(location));
//
//                Log.e(TAG,"read location: "+location);
//                Log.e(TAG,"final results: "+read.readAll());
//
//                String[] nextLine;
//                while((nextLine=read.readNext())!=null){
//                    if(nextLine !=null){
//                        Log.e(TAG,"results: "+ Arrays.toString(nextLine));
//                    }
//                }
//                List<String[]>allRows=read.readAll();
//                for(String[] row:allRows){
//                    Log.e(TAG,"results all: "+Arrays.toString(row)+", "+row.toString());
//                }


//                // this is the Column of the table and same for Header of CSV
//                // file
//                if (SyncStateContract.Constants.Common.OCEAN_LOB.equals(lob)) {
//                    csvWrite.writeNext(SyncStateContract.Constants.FileNm.CSV_O_HEADER);
//                }else{
//                    csvWrite.writeNext(SyncStateContract.Constants.FileNm.CSV_A_HEADER);
//                }
//                String arrStr1[] = { "SR.No", "CUTSOMER NAME", "PROSPECT", "PORT OF LOAD", "PORT OF DISCHARGE" };
//                csvWrite.writeNext(arrStr1);
//
//                if (listdata.size() > 0) {
//                    for (int index = 0; index < listdata.size(); index++) {
//                        sa = listdata.get(index);
//                        String pol;
//                        String pod;
//                        if (SyncStateContract.Constants.Common.OCEAN_LOB.equals(sa.getLob())) {
//                            pol = sa.getPortOfLoadingOENm();
//                            pod = sa.getPortOfDischargeOENm();
//                        } else {
//                            pol = sa.getAirportOfLoadNm();
//                            pod = sa.getAirportOfDischargeNm();
//                        }
//                        int srNo = index;
//                        String arrStr[] = { String.valueOf(srNo + 1), sa.getCustomerNm(), sa.getProspectNm(), pol, pod };
//                        csvWrite.writeNext(arrStr);
//                    }
//                    success = true;
//                }
//                csvWrite.close();
                File f=new File(exportDir,"AdobeXLS.xls");

                Intent email = new Intent(Intent.ACTION_SEND);
//                  email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
//                  email.putExtra(Intent.EXTRA_SUBJECT, "test");
//                  email.putExtra(Intent.EXTRA_TEXT, message);
                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                // need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client"));

            } catch (IOException e) {
                Log.e("SearchResultActivity", e.getMessage(), e);
            }
        }
//        mDBHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());

//        Cursor c=mDBHelper.getAllData();

//        DataProvider dp=new DataProvider();
//        Cursor c=dp.query(Uri.parse("content://com.a.b.mileagetracker/vehicle_key_table"),null,null,null,null);
////        Cursor c=dp.query(Uri.parse("content://"),null,null,null,null);
//        Log.e(TAG,"cursor: "+c);

//        ExportDatabaseAsyncTask exportDb=new ExportDatabaseAsyncTask(getActivity().getApplicationContext(), new ExportDatabaseAsyncTask.AsyncResponse() {
//            @Override
//            public void processFinish() {
//                String uriLocation= Environment.getExternalStorageDirectory()+ File.separator + "exportFillupTable"+File.separator +"hello1" + ".csv";
//                File file=new File(uriLocation);
//                Log.e("uriLocation", "URILocation: " + file.getAbsolutePath());
//
//                Intent email = new Intent(Intent.ACTION_SEND);
////                  email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
////                  email.putExtra(Intent.EXTRA_SUBJECT, "test");
////                  email.putExtra(Intent.EXTRA_TEXT, message);
//                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//
//                // need this to prompts email client only
//                email.setType("message/rfc822");
//
//                startActivity(Intent.createChooser(email, "Choose an Email client"));
//            }
//        });
//        exportDb.execute(c);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated thread1: " + Thread.currentThread().getName());
        getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
    }

//    @Override
//    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        CursorLoader CL=null;
//        switch (id){
//            case 0:
//                CL= new CursorLoader(getActivity().getApplicationContext(),Uri.parse("content://com.a.b.mileagetracker/key_table"),null,null,null,null);
//                Log.e(TAG,"onCreateLoader 0:");
//            break;
//            case 1:
//                for(int i=0;i<vehicles.size();i++) {
//                    CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/vehicle"), null, vehicles.get(i), null, null);
//                }
//                Log.e(TAG,"onCreateLoader 1:");
//            break;
//            case 2:
//                CL= new CursorLoader(getActivity().getApplicationContext(),Uri.parse("content://com.a.b.mileagetracker/vehicle"),null,"HondaAccord2008",null,null);
//                Log.e(TAG,"onCreateLoader 2:");
//            break;
//            case 3:
//            Log.e(TAG,"onCreateLoader 3:");
//            break;
//            case 4:
//            Log.e(TAG,"onCreateLoader 4:");
//            break;
//            default:
//            break;
//        }
//        return CL;
//    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        switch (id) {
            case 0:
                CL = new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/key_table"), null, null, null, null);
                Log.e(TAG, "onCreateLoader 0:");
                break;
            default:
                CL= new CursorLoader(getActivity().getApplicationContext(),Uri.parse("content://com.a.b.mileagetracker/vehicle"),null,args.getString("vehicle"),null,null);
                break;
        }
        return CL;
    }
    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor c) {
        Row row;
        if(c.getCount()>0){
        switch (loader.getId()) {
            case 0:
                c.moveToFirst();
                do {
                    vehicles.add(c.getString(c.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));
                } while (c.moveToNext());

                Log.e(TAG, "onloadFinished 0: ");
                break;
            default:
                try {
                    c.moveToFirst();
                    if (c.getCount() > 0) {
                        do {
                            for (int i = 0; i < c.getColumnCount(); i++) {
                                Log.e(TAG, "values: " + c.getString(i));
                            }
                        } while (c.moveToNext());

                        c.moveToFirst();
                        String vehicle = c.getString(c.getColumnIndex(MySQLiteHelper.COLUMN_VEHICLE));
                        String sheetName = WorkbookUtil.createSafeSheetName(vehicle);

                        CellStyle styleBold = workBook.createCellStyle();
                        Font font = workBook.createFont();
                        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                        styleBold.setFont(font);

                        sheet = workBook.createSheet(sheetName);
                        row = sheet.createRow(0);
                        Cell titleCell = row.createCell(0);
                        titleCell.setCellValue("Data for " + vehicle);
                        titleCell.setCellStyle(styleBold);

                        int rowNumber = 5;
                        row = sheet.createRow(rowNumber - 2);

                        c.moveToFirst();
                        for (int i = 2; i < c.getColumnCount(); i++) {
                            Cell cell = row.createCell(i - 2);
                            cell.setCellValue(WordUtils.capitalizeFully(c.getColumnName(i)));
                            cell.setCellStyle(styleBold);
                        }
                        c.moveToFirst();
                        if (c.getCount() > 0) {
                            do {
                                row = sheet.createRow(rowNumber);
                                for (int i = 2; i < c.getColumnCount(); i++) {
//                                    row.createCell(i - 2).setCellValue(c.getString(i));
                                    switch (i) {
                                        case 2: //mileage column
//                                            NumberFormat.getIntegerInstance().format(
                                            row.createCell(0).setCellValue(c.getInt(2));
                                            break;
                                        case 3: //quantity column
                                            row.createCell(1).setCellValue(c.getDouble(3));
                                            break;
                                        case 4: //price column
                                            row.createCell(2).setCellValue(NumberFormat.getCurrencyInstance().format(Double.parseDouble(c.getString(4))));
                                            break;
                                        case 5: //date column
                                            long l = c.getLong(5);
                                            Date date = new Date(l * 1000);
                                            SimpleDateFormat sdfFormat = new SimpleDateFormat("MMM/dd/yyyy");
                                            row.createCell(3).setCellValue(sdfFormat.format(date));
                                            break;
                                        case 6: //location column
                                            row.createCell(4).setCellValue(c.getString(6));
                                            break;
                                        case 7: //mpg column
                                            row.createCell(5).setCellValue(c.getDouble(7));
                                            break;
                                    }
                                }
                                rowNumber++;
                            } while (c.moveToNext());
                        }

                        fileOut = new FileOutputStream(exportDir + "/AdobeXLS.xls");
                        workBook.write(fileOut);
                    }
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }


//    @Override
//    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor c) {
//        Row row;
//        switch (loader.getId()) {
//            case 0:
//                c.moveToFirst();
//                do{
//                    vehicles.add(c.getString(c.getColumnIndex(MySQLiteHelper.KEY_COLUMN_TABLE)));
//                }while(c.moveToNext());
//
//                Log.e(TAG,"onloadFinished 0: ");
//                break;
//            case 1:
//                try {
//                    String sheetName=WorkbookUtil.createSafeSheetName("other car");
//                    sheet= workBook.createSheet(sheetName);
//                    row=sheet.createRow(0);
//                    row.createCell(0).setCellValue("hello");
//                    row.createCell(1).setCellValue(c.getColumnName(2));
//
//                    int rowNumber=5;
//                    row=sheet.createRow(rowNumber-2);
//                    c.moveToFirst();
//                    for(int i=1; i<c.getColumnCount();i++) {
//                        row.createCell(i).setCellValue(c.getColumnName(i));
//                    }
//                    c.moveToFirst();
//                    do{
//                        row=sheet.createRow(rowNumber);
//                        for(int i=0; i<c.getColumnCount();i++) {
//                            row.createCell(i).setCellValue(c.getString(i));
//                        }
//                        rowNumber++;
//                   }while(c.moveToNext());
//                    c.close();
//
//                    fileOut = new FileOutputStream(exportDir+"/AdobeXLS.xls");
//                    workBook.write(fileOut);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.e(TAG,"onloadFinished 1: ");
//                break;
//            case 2:
//                try {
//                    String sheetName=WorkbookUtil.createSafeSheetName("HondaAccord2008");
//                    sheet= workBook.createSheet(sheetName);
//                    row=sheet.createRow(0);
//                    row.createCell(0).setCellValue("hello");
//                    row.createCell(1).setCellValue(c.getColumnName(2));
//
//                    int rowNumber=5;
//                    row=sheet.createRow(rowNumber-2);
//                    c.moveToFirst();
//                    for(int i=1; i<c.getColumnCount();i++) {
//                        row.createCell(i).setCellValue(c.getColumnName(i));
//                    }
//                    c.moveToFirst();
//                    do{
//                        row=sheet.createRow(rowNumber);
//                        for(int i=0; i<c.getColumnCount();i++) {
//                            row.createCell(i).setCellValue(c.getString(i));
//                        }
//                        rowNumber++;
//                    }while(c.moveToNext());
//                    c.close();
//
//                    fileOut = new FileOutputStream(exportDir+"/AdobeXLS.xls");
//                    workBook.write(fileOut);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Log.e(TAG,"onloadFinished 2: ");
//                break;
//            case 3:
//                try {
//                    String safeName= WorkbookUtil.createSafeSheetName("car name");
//                    sheet=workBook.createSheet(safeName);
//
//                    row= sheet.createRow((short) 0);
//                    row.createCell(0).setCellValue(12.95);
//                    row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
//                    row.createCell(3).setCellValue(true);
//                    row.createCell(4).setCellValue(new Date());
//
//                    CellStyle cellStyle=workBook.createCellStyle();
//                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
//
//                    Cell cell = row.createCell(5);
//                    cell.setCellValue(new Date());
//                    cell.setCellStyle(cellStyle);
//
//                    row=sheet.createRow((short)1);
//                    row.createCell(0).setCellValue(12.95);
//                    row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
//                    row.createCell(3).setCellValue(true);
//                    row.createCell(4).setCellValue(new Date());
//
//                    int rowNumber=5;
//                    row=sheet.createRow(rowNumber-2);
//                    c.moveToFirst();
//                    //TODO create bold column headings
//                    for(int i=1; i<c.getColumnCount();i++) {
//                        row.createCell(i).setCellValue(c.getColumnName(i));
//                    }
//                    c.moveToFirst();
//                    do{
//                        row=sheet.createRow(rowNumber);
//                        //TODO format $X.XX, quantity X.XXX, and date 11/29/15
//                        for(int i=0; i<c.getColumnCount();i++) {
//                            row.createCell(i).setCellValue(c.getString(i));
//                        }
//                        rowNumber++;
//                    }while(c.moveToNext());
//                    c.close();
//
//                    fileOut = new FileOutputStream(exportDir+"/AdobeXLS.xls");
//                    workBook.write(fileOut);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.e(TAG,"onloadFinished 3:");
//                break;
//            case 4:
//                Log.e(TAG,"onloadFinished 4:");
//                break;
//            default:
//                break;
//        }
//
//    Log.e(TAG,"onLoadFinished loader: " + loader.toString() + ", Cursor data: " + c);
//
////        DateFormat currentDateString =DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
////        Log.e("export data", "current date string: " + currentDateString);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
    }
}