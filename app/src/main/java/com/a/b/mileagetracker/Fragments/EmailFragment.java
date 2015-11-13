package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.Loader;
import android.util.Log;

import com.a.b.mileagetracker.DataAccess.DataProvider;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andrew on 11/4/2015.
 */
public class EmailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private MySQLiteHelper mDBHelper;
    String TAG="EmailFragment";

    public EmailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mDBHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());

//        Cursor c=mDBHelper.getAllData();

        DataProvider dp=new DataProvider();
        Cursor c=dp.query(Uri.parse("content://com.a.b.mileagetracker/vehicle_key_table"),null,null,null,null);
//        Cursor c=dp.query(Uri.parse("content://"),null,null,null,null);
        Log.e(TAG,"cursor: "+c);

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
        getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks) this);
        Log.e(TAG, TAG + " Loader thread1: " + Thread.currentThread().getName());
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG,TAG+" Loader thread2: "+Thread.currentThread().getName());
        CursorLoader CL= new CursorLoader(getActivity().getApplicationContext(),Uri.parse("content://com.a.b.mileagetracker/vehicle_key_table"),null,null,null,null);
        return CL;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, TAG + " loader: " + loader.toString() + ", Cursor data: " + data);

//        DateFormat currentDateString =DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
//        Log.e("export data", "current date string: " + currentDateString);

        File dbFile = getActivity().getApplicationContext().getDatabasePath(MySQLiteHelper.getInstance(getActivity().getApplicationContext()).getDatabaseName());
        Log.e(TAG, "DbFile path is: " + dbFile); // get the path of db
        File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + "exportFillupTable");
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
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

//                String arrStr1[] = { "SR.No", "CUTSOMER NAME", "PROSPECT", "PORT OF LOAD", "PORT OF DISCHARGE" };
//                String[] record = "4,cows,moo,sheeps".split(",");
                data.moveToFirst();

                List<String> list=new ArrayList<String>();
                for(String c:data.getColumnNames()){
                    list.add(c);
                }

                String[] names=list.toArray(new String[list.size()]);

//                String arrStr1[]={"columns in cursor: ",data.getString(0),data.getString(2),data.getString(3)};
//                Log.e(TAG, "columns in cursor: "+data.getString(0)+", "+data.getString(1)+", "+data.getString(2)+", "+data.getString(3));

                csvWrite.writeNext(names);
                csvWrite.close();


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


                Intent email = new Intent(Intent.ACTION_SEND);
//                  email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
//                  email.putExtra(Intent.EXTRA_SUBJECT, "test");
//                  email.putExtra(Intent.EXTRA_TEXT, message);
                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                // need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client"));

            } catch (IOException e) {
                Log.e("SearchResultActivity", e.getMessage(), e);
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        Log.e(TAG,TAG+" Loader thread4: "+Thread.currentThread().getName());
    }
}