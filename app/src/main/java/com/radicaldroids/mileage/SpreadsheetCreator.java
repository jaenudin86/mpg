package com.radicaldroids.mileage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.SQLiteHelper;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Andrew on 4/29/2016.
 */
public class SpreadsheetCreator extends AsyncTask {

    private String TAG="SpreadSheetCreator";
    FileOutputStream mFileOut;
    Workbook mWorkBook;
    Sheet mSheet;
    Context mContext;
    String mFileName="mpgxls.xls";
    CreationHelper createHelper;
    File exportFile;
    int mNumberOfNoDataVehicles=0;

    public SpreadsheetCreator(Context context) {
        super();
        mContext=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        //pull all data from keyTable
        Cursor cursor = mContext.getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI + "/key_table"), null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ArrayList<String> vehicles = new ArrayList<>();
            do {
                vehicles.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_COLUMN_TABLE)));
            } while (cursor.moveToNext());

            mWorkBook = new HSSFWorkbook();
            createHelper = mWorkBook.getCreationHelper();
            exportFile = new File(mContext.getCacheDir(), mFileName);

            try {
                if (exportFile.exists()) {
                    Boolean del = exportFile.delete();
                    Log.e(TAG,"delete old file stuck in cache: "+del);
                }
                exportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int mNumberOfVehicles = vehicles.size();
            Log.e(TAG, "vehicles size: "+mNumberOfVehicles);
            for (String vehicleName : vehicles) {
                //search for table with vehicleName and pull all contents
                Cursor c = mContext.getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI + "/vehicle"), null, vehicleName, null, null);

                Row row;
                if (c != null) {
                    try {
                        c.moveToFirst();
                        if (c.getCount() > 0) {

                            String sheetName = WorkbookUtil.createSafeSheetName(vehicleName);
//                            Log.e(TAG, "sheets: " + sheetName + ", vehicle from column: " + vehicleName);

                            CellStyle styleBold = mWorkBook.createCellStyle();
                            Font font = mWorkBook.createFont();
                            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                            styleBold.setFont(font);

                            mSheet = mWorkBook.createSheet(sheetName);
                            row = mSheet.createRow(0);
                            Cell titleCell = row.createCell(0);
                            titleCell.setCellValue("Data for " + vehicleName);
                            titleCell.setCellStyle(styleBold);

                            int rowNumber = 5;
                            row = mSheet.createRow(rowNumber - 2);

                            c.moveToFirst();
                            for (int i = 2; i < c.getColumnCount(); i++) {
                                Cell cell = row.createCell(i - 2);
                                cell.setCellValue(WordUtils.capitalizeFully(c.getColumnName(i)));
                                cell.setCellStyle(styleBold);
                            }

                            c.moveToFirst();
                            do {
                                row = mSheet.createRow(rowNumber);
                                for (int i = 2; i < c.getColumnCount(); i++) {

                                    switch (i) {
                                        case 2: //mileage column
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

                            mFileOut = new FileOutputStream(mContext.getCacheDir() + File.separator + mFileName);
                            mWorkBook.write(mFileOut);

                        }else {    //there is no data for this vehicle
                            mNumberOfNoDataVehicles++;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //close the workbook
                try {
                    if (mFileOut != null) {
                        mFileOut.close();
                        mWorkBook.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(mNumberOfNoDataVehicles==mNumberOfVehicles){
                return null;    //each vehicle had only a name but no data; nothing to export
            }

            return 1;

        } else {
            return null;    //if no data in keyTable; no vehicles created
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        if(o!=null) {
            sendEmail();
        }else{
            showNoDataAlert();
        }
    }
    public void sendEmail(){
        File f=new File(mContext.getCacheDir(),mFileName);
        if(f!=null) {

            f.setReadable(true, false);

            Intent email = new Intent(Intent.ACTION_SEND);

            email.setType("message/rfc822");
//            email.setType("application/zip");
            email.putExtra(Intent.EXTRA_STREAM, Uri.parse(DataProvider.BASE_CONTENT_URI + "/spreadsheet.xls"));

            email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivityForResult(Intent.createChooser(email, "Choose an Email client"), 1);
            Activity act = (Activity) mContext;
            act.startActivity(Intent.createChooser(email, "Choose an Email client"));
        }else{
            showNoDataAlert();
        }
    }

    public void showNoDataAlert(){
        new AlertDialog.Builder(mContext)
            .setTitle(R.string.no_data_to_export)
//            .setMessage()
            .setPositiveButton(R.string.ok, null)
            .setIcon(R.drawable.alert_48x48)
            .show();
    }
}