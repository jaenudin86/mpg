//package com.radicaldroids.mileage.Fragments;
//
//
//import android.support.v4.app.LoaderManager;
//import android.content.Context;
//import android.support.v4.content.CursorLoader;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.Loader;
//import android.support.v7.app.AlertDialog;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.radicaldroids.mileage.Constants;
//import com.radicaldroids.mileage.DataAccess.DataProvider;
//import com.radicaldroids.mileage.DataAccess.SQLiteHelper;
//import com.radicaldroids.mileage.R;
//
//import org.apache.commons.lang3.text.WordUtils;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.CreationHelper;
//import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.util.WorkbookUtil;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//
///**
// * Created by Andrew on 11/4/2015.
// */
//public class EmailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
//    String TAG="EmailFragment";
//    Workbook workBook;
//    Sheet sheet;
//    CreationHelper createHelper;
//    FileOutputStream fileOut;
//    File exportFile;
//    ArrayList<String> vehicles=new ArrayList<>();
//    String mFileName="mpgxls.xls";
//    int mNumberOfVehicles=0;
//    int mNumberOfProcessedVehicles=0;
//
//    public EmailFragment() {
//        super();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        long freeBytesInternal = new File(getActivity().getApplicationContext().getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
//        long megAvailable = freeBytesInternal / 1048576;
////        Log.e(TAG, "processors available: " + Runtime.getRuntime().availableProcessors());
//        if (megAvailable < 0.1) {
//            new AlertDialog.Builder(getActivity()).setTitle(R.string.not_enough_memory)
//                .setPositiveButton("OK", null).setIcon(R.drawable.alert_48x48).show();
//
//        } else {
//            SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
//            ArrayList<String> vehicles = new ArrayList<String>(Arrays.asList(TextUtils.split(sharedPrefs.getString("vehicle_list", ""), "‚‗‚")));
//            mNumberOfVehicles=vehicles.size();
//
//            if (vehicles.size() > 0) {
//                workBook = new HSSFWorkbook();
//                createHelper = workBook.getCreationHelper();
//
////                File dbFile = getActivity().getApplicationContext().getDatabasePath(SQLiteHelper.getInstance(getActivity().getApplicationContext()).getDatabaseName());
////                Log.e(TAG, "DbFile path is: " + dbFile); // get the path of db
////                exportFile = new File(Environment.getExternalStorageDirectory(), mFileFolder);
////                exportFile = new File(getActivity().getFilesDir()+File.separator, mFileName);
//                exportFile =new File(getActivity().getCacheDir(),mFileName);
//                try {
//                    if(exportFile.exists()){
//                        Boolean del=exportFile.delete();
////                        Log.e(TAG,"delete old file stuck in cache: "+del);
//                    }
//                    exportFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                Log.e(TAG, "export spreadsheet path is: " + exportFile); // get the path of speadsheet
////                File folder=getActivity().getCacheDir();
////                Log.e(TAG,"getCacheDir: "+folder);
//
////                if (!exportFile.exists()) {
////                    Boolean createDirResults=exportFile.mkdirs();
////                    Log.e(TAG,"make directory: "+createDirResults);
////                }else{
////                    Log.e(TAG,"directory exists: "+exportFile.exists());
////                }
////                Log.e(TAG,"directory: "+exportFile.exists());
//
////                try {
////                    File file =new File(exportFile,mFileName);
////                    Log.e(TAG,"new File: "+file);
////
////                    if(!file.exists()){
////                        Boolean fileCreated=file.createNewFile();
////                        Log.e(TAG,"File created?: "+fileCreated);
////                    }
//////                    fileOut = new FileOutputStream(exportFile+File.separator+mFileName);
//////                    workBook.write(fileOut);
//////                    fileOut.close();
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//
//                int num = 1;
//                for (String v : vehicles) {
//                    Log.e(TAG, "vehicles: "+v);
//                    Bundle extra = new Bundle();
//                    extra.putString("vehicle", v);
//                    //create a new Loader for each vehicle in the database. Each Loader will create it's own page in the spreadsheet.
//                    getLoaderManager().initLoader(num, extra, (LoaderManager.LoaderCallbacks) this);
//                    Cursor cursor=getActivity().getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI+"/vehicle"),null,v,null,null);
//                    num++;
//                }
//            } else {
//                showNoDataAlert();
//            }
//        }
//    }
//    public void showNoDataAlert(){
//        new AlertDialog.Builder(getActivity())
//                .setTitle("No Data to Export!")
////                .setMessage()
//                .setPositiveButton("OK", null)
//                .setIcon(R.drawable.alert_48x48)
//                .show();
//
//    }
//    public void sendEmail(){
////        try {
//            File f=new File(getActivity().getCacheDir(),mFileName);
//            f.setReadable(true,false);
//
////            Log.e(TAG, "email path: " + f.getAbsolutePath() + ",   canonical: " + f.getCanonicalPath() + ",   file: " + f);
//
//            Intent email = new Intent(Intent.ACTION_SEND);
////              email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
////              email.putExtra(Intent.EXTRA_SUBJECT, "test");
////              email.putExtra(Intent.EXTRA_TEXT, message);
////              email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
//
//            email.setType("message/rfc822");
////            email.setType("application/zip");
//            email.putExtra(Intent.EXTRA_STREAM, Uri.parse(DataProvider.BASE_CONTENT_URI +"/spreadsheet.xls"));
//
//            email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            startActivityForResult(Intent.createChooser(email, "Choose an Email client"), 1);
//            startActivity(Intent.createChooser(email, "Choose an Email client"));
//
////        } catch (IOException e) {
////            Log.e("SearchResultActivity", e.getMessage(), e);
////        }
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        CursorLoader CL=null;
//        CL= new CursorLoader(getActivity().getApplicationContext(),Uri.parse(DataProvider.BASE_CONTENT_URI +"/vehicle"),null,args.getString("vehicle"),null,null);
//        return CL;
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
//        Log.e(TAG, "Loader cursor: "+loader);
//        Row row;
//        if(c!=null){
//            try {
//                c.moveToFirst();
//                if (c.getCount() > 0) {
////                    do {
////                        for (int i = 0; i < c.getColumnCount(); i++) {
////                            Log.e(TAG, "values: " + c.getString(i));
////                        }
////                    } while (c.moveToNext());
//
//                    c.moveToFirst();
//                    String vehicle = c.getString(c.getColumnIndex(SQLiteHelper.COLUMN_VEHICLE));
//                    String sheetName = WorkbookUtil.createSafeSheetName(vehicle);
//                    Log.e(TAG,"sheets: "+sheetName+", vehicle from column: "+vehicle);
//
//                    CellStyle styleBold = workBook.createCellStyle();
//                    Font font = workBook.createFont();
//                    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
//                    styleBold.setFont(font);
//
//                    sheet = workBook.createSheet(sheetName);
//                    row = sheet.createRow(0);
//                    Cell titleCell = row.createCell(0);
//                    titleCell.setCellValue("Data for " + vehicle);
//                    titleCell.setCellStyle(styleBold);
//
//                    int rowNumber = 5;
//                    row = sheet.createRow(rowNumber - 2);
//
//                    c.moveToFirst();
//                    for (int i = 2; i < c.getColumnCount(); i++) {
//                        Cell cell = row.createCell(i - 2);
//                        cell.setCellValue(WordUtils.capitalizeFully(c.getColumnName(i)));
//                        cell.setCellStyle(styleBold);
//                    }
//                    c.moveToFirst();
//                    do {
//                        row = sheet.createRow(rowNumber);
//                        for (int i = 2; i < c.getColumnCount(); i++) {
////                                    row.createCell(i - 2).setCellValue(c.getString(i));
//                            switch (i) {
//                                case 2: //mileage column
////                                            NumberFormat.getIntegerInstance().format(
//                                    row.createCell(0).setCellValue(c.getInt(2));
//                                    break;
//                                case 3: //quantity column
//                                    row.createCell(1).setCellValue(c.getDouble(3));
//                                    break;
//                                case 4: //price column
//                                    row.createCell(2).setCellValue(NumberFormat.getCurrencyInstance().format(Double.parseDouble(c.getString(4))));
//                                    break;
//                                case 5: //date column
//                                    long l = c.getLong(5);
//                                    Date date = new Date(l * 1000);
//                                    SimpleDateFormat sdfFormat = new SimpleDateFormat("MMM/dd/yyyy");
//                                    row.createCell(3).setCellValue(sdfFormat.format(date));
//                                    break;
//                                case 6: //location column
//                                    row.createCell(4).setCellValue(c.getString(6));
//                                    break;
//                                case 7: //mpg column
//                                    row.createCell(5).setCellValue(c.getDouble(7));
//                                    break;
//                            }
//                        }
//                        rowNumber++;
//                    } while (c.moveToNext());
//
////                    fileOut = new FileOutputStream(mFileName);
//
////                    fileOut = new FileOutputStream(exportFile +File.separator+mFileName);
////                    fileOut=new FileOutputStream(mFileName);
//                    fileOut=new FileOutputStream(getActivity().getCacheDir()+File.separator+mFileName);
//                    workBook.write(fileOut);
////                    Log.e(TAG,"workbook.write location: "+fileOut);
//                }
////                c.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else{
////            Log.e(TAG,"nothing to export from this vehicle");
//        }
//        mNumberOfProcessedVehicles++;
//        if(mNumberOfProcessedVehicles==mNumberOfVehicles){
////        if(loader.getId()==mNumberOfVehicles){
//            try {
//                if(fileOut!=null) {
//                    fileOut.close();
//                    workBook.close();
//                    sendEmail();
//                }else{
//                    showNoDataAlert();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
////    @Override
////    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor c) {
////        Row row;
////        switch (loader.getId()) {
////            case 0:
////                c.moveToFirst();
////                do{
////                    vehicles.add(c.getString(c.getColumnIndex(SQLiteHelper.KEY_COLUMN_TABLE)));
////                }while(c.moveToNext());
////
////                Log.e(TAG,"onloadFinished 0: ");
////                break;
////            case 1:
////                try {
////                    String sheetName=WorkbookUtil.createSafeSheetName("other car");
////                    sheet= workBook.createSheet(sheetName);
////                    row=sheet.createRow(0);
////                    row.createCell(0).setCellValue("hello");
////                    row.createCell(1).setCellValue(c.getColumnName(2));
////
////                    int rowNumber=5;
////                    row=sheet.createRow(rowNumber-2);
////                    c.moveToFirst();
////                    for(int i=1; i<c.getColumnCount();i++) {
////                        row.createCell(i).setCellValue(c.getColumnName(i));
////                    }
////                    c.moveToFirst();
////                    do{
////                        row=sheet.createRow(rowNumber);
////                        for(int i=0; i<c.getColumnCount();i++) {
////                            row.createCell(i).setCellValue(c.getString(i));
////                        }
////                        rowNumber++;
////                   }while(c.moveToNext());
////                    c.close();
////
////                    fileOut = new FileOutputStream(exportFile+"/AdobeXLS.xls");
////                    workBook.write(fileOut);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                Log.e(TAG,"onloadFinished 1: ");
////                break;
////            case 2:
////                try {
////                    String sheetName=WorkbookUtil.createSafeSheetName("HondaAccord2008");
////                    sheet= workBook.createSheet(sheetName);
////                    row=sheet.createRow(0);
////                    row.createCell(0).setCellValue("hello");
////                    row.createCell(1).setCellValue(c.getColumnName(2));
////
////                    int rowNumber=5;
////                    row=sheet.createRow(rowNumber-2);
////                    c.moveToFirst();
////                    for(int i=1; i<c.getColumnCount();i++) {
////                        row.createCell(i).setCellValue(c.getColumnName(i));
////                    }
////                    c.moveToFirst();
////                    do{
////                        row=sheet.createRow(rowNumber);
////                        for(int i=0; i<c.getColumnCount();i++) {
////                            row.createCell(i).setCellValue(c.getString(i));
////                        }
////                        rowNumber++;
////                    }while(c.moveToNext());
////                    c.close();
////
////                    fileOut = new FileOutputStream(exportFile+"/AdobeXLS.xls");
////                    workBook.write(fileOut);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////                Log.e(TAG,"onloadFinished 2: ");
////                break;
////            case 3:
////                try {
////                    String safeName= WorkbookUtil.createSafeSheetName("car name");
////                    sheet=workBook.createSheet(safeName);
////
////                    row= sheet.createRow((short) 0);
////                    row.createCell(0).setCellValue(12.95);
////                    row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
////                    row.createCell(3).setCellValue(true);
////                    row.createCell(4).setCellValue(new Date());
////
////                    CellStyle cellStyle=workBook.createCellStyle();
////                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
////
////                    Cell cell = row.createCell(5);
////                    cell.setCellValue(new Date());
////                    cell.setCellStyle(cellStyle);
////
////                    row=sheet.createRow((short)1);
////                    row.createCell(0).setCellValue(12.95);
////                    row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
////                    row.createCell(3).setCellValue(true);
////                    row.createCell(4).setCellValue(new Date());
////
////                    int rowNumber=5;
////                    row=sheet.createRow(rowNumber-2);
////                    c.moveToFirst();
////                    //TODO create bold column headings
////                    for(int i=1; i<c.getColumnCount();i++) {
////                        row.createCell(i).setCellValue(c.getColumnName(i));
////                    }
////                    c.moveToFirst();
////                    do{
////                        row=sheet.createRow(rowNumber);
////                        //TODO format $X.XX, quantity X.XXX, and date 11/29/15
////                        for(int i=0; i<c.getColumnCount();i++) {
////                            row.createCell(i).setCellValue(c.getString(i));
////                        }
////                        rowNumber++;
////                    }while(c.moveToNext());
////                    c.close();
////
////                    fileOut = new FileOutputStream(exportFile+"/AdobeXLS.xls");
////                    workBook.write(fileOut);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                Log.e(TAG,"onloadFinished 3:");
////                break;
////            case 4:
////                Log.e(TAG,"onloadFinished 4:");
////                break;
////            default:
////                break;
////        }
////
////    Log.e(TAG,"onLoadFinished loader: " + loader.toString() + ", Cursor data: " + c);
////
//////        DateFormat currentDateString =DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
//////        Log.e("export data", "current date string: " + currentDateString);
//    }
//}