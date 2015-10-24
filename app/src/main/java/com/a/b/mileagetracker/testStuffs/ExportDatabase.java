package com.a.b.mileagetracker.testStuffs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andrew on 10/23/2015.
 */
public class ExportDatabase extends AsyncTask<String, Integer, String> {
    private String TAG="AsyncTask message";
    private Context mContext;
    public ExportDatabase(Context c){
        this.mContext=c;
    }

    @Override
    protected String doInBackground(String... params) {
        boolean success = false;

//        String currentDateString = new SimpleDateFormat(SyncStateContract.Constants.SimpleDtFrmt_ddMMyyyy).format(new Date());
        DateFormat currentDateString =DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        File dbFile = mContext.getDatabasePath("fillupTable.db");
            Log.e(TAG, "Db path is: " + dbFile); // get the path of db
        File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + "exportFillupTable");
            Log.e(TAG, "Db path is: " + mContext.getDatabasePath("exportFillupTable")); // get the path of db

        long freeBytesInternal = new File(mContext.getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
        long megAvailable = freeBytesInternal / 1048576;

        if (megAvailable < 0.1) {
            System.out.println("Please check"+megAvailable);
//            memoryErr = true;
        }else {
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            PrintWriter printWriter=null;
            File file;

            try {
//                List<SalesActivity> listdata = salesLst;
//                SalesActivity sa = null;
//                String lob = null;
//                for (int index = 0; index < listdata.size();) {
//                    sa = listdata.get(index);
//                    lob = sa.getLob();
//                    break;
//                }
//                if (SyncStateContract.Constants.Common.OCEAN_LOB.equals(lob)) {
//
                file = new File(exportDir, "/data/data/com.a.b.mileagetracker/databases/exportFillupTable" + currentDateString + ".csv");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));





//                } else {
//                    file = new File(exportDir, SyncStateContract.Constants.FileNm.FILE_AFS + currentDateString + ".csv");
//                }
//                file.createNewFile();


//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//                Log.e(TAG,"results: "+csvWrite);


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
//
            } catch (IOException e) {
                Log.e("SearchResultActivity", e.getMessage(), e);
                return "fail";
            }
        }
        return "done";

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("asynctask finished", "Asynctask finished");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
