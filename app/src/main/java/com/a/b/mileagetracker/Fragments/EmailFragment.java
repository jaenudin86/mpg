package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.testStuffs.ExportDatabaseAsyncTask;

import java.io.File;

/**
 * Created by Andrew on 11/4/2015.
 */
public class EmailFragment extends Fragment {
    private MySQLiteHelper mDBHelper;

    public EmailFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Cursor c=mDBHelper.getAllData();

        ExportDatabaseAsyncTask exportDb=new ExportDatabaseAsyncTask(getActivity().getApplicationContext(), new ExportDatabaseAsyncTask.AsyncResponse() {
            @Override
            public void processFinish() {
                String uriLocation= Environment.getExternalStorageDirectory()+ File.separator + "exportFillupTable"+File.separator +"hello1" + ".csv";
                File file=new File(uriLocation);
                Log.e("uriLocation", "URILocation: " + file.getAbsolutePath());

                Intent email = new Intent(Intent.ACTION_SEND);
//                  email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
//                  email.putExtra(Intent.EXTRA_SUBJECT, "test");
//                  email.putExtra(Intent.EXTRA_TEXT, message);
                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                // need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client"));
            }
        });
        exportDb.execute(c);
    }


}
