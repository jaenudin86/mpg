package com.a.b.mileagetracker.testStuffs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        Log.e("export data", "current date string: "+currentDateString);

        File dbFile = mContext.getDatabasePath(MySQLiteHelper.getInstance(mContext).getDatabaseName());
            Log.e(TAG, "DbFile path is: " + dbFile); // get the path of db
        File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + "exportFillupTable");
            Log.e(TAG, "exportDB path is: " + exportDir.getAbsolutePath()); // get the path of db

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

                mContext.getFilesDir().getPath();
                Environment.getExternalStorageDirectory().getPath();
//
//                file = new File(exportDir, "/data/data/com.a.b.mileagetracker/databases/exportFillupTable" + currentDateString + ".csv");
//                file =new File(Environment.getExternalStorageDirectory(), mContext.getFilesDir().getAbsolutePath()+ "/hello" + ".csv");
//                file =new File(exportDir, mContext.getFilesDir().getAbsolutePath()+ "/hello1" + ".csv");
                file =new File(exportDir, "hello1" + ".csv");
                if(!file.exists()){
//                    file.mkdirs();
                    Boolean result =file.createNewFile();
                    Log.e("bool","bool createFile: "+result);
                }
                Log.e(TAG,"file: "+file.getAbsolutePath());

//                printWriter = new PrintWriter(new FileWriter(file));
//                } else {
//                    file = new File(exportDir, SyncStateContract.Constants.FileNm.FILE_AFS + currentDateString + ".csv");
//                }
//                file.createNewFile();


                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                String arrStr1[] = { "SR.No", "CUTSOMER NAME", "PROSPECT", "PORT OF LOAD", "PORT OF DISCHARGE" };
//                String[] record = "4,cows,moo,sheeps".split(",");
                csvWrite.writeNext(arrStr1);
                csvWrite.close();

//                CSVReader read = new CSVReader(new FileReader(exportDir+"/hello1" + ".csv"),',','"',1);
                String location=exportDir+File.separator+"hello1" + ".csv";
                CSVReader read = new CSVReader(new FileReader(location));

                Log.e(TAG,"read location: "+location);
                Log.e(TAG,"final results: "+read.readAll());

                String[] nextLine;
                while((nextLine=read.readNext())!=null){
                    if(nextLine !=null){
                        Log.e(TAG,"results: "+ Arrays.toString(nextLine));
                    }
                }
                List<String[]>allRows=read.readAll();
                for(String[] row:allRows){
                    Log.e(TAG,"results all: "+Arrays.toString(row)+", "+row.toString());

                }

                Log.e(TAG, "location: "+exportDir+"/hello1" + ".csv");



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
