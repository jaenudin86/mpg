//package com.a.b.mileagetracker;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.EditText;
//
//import com.a.b.mileagetracker.testStuffs.ExportDatabaseAsyncTask;
//
//import java.io.File;
//
///**
// * Created by Andrew on 11/3/2015.
// */
//public class SendEmailActivity  extends AppCompatActivity implements ExportDatabaseAsyncTask.AsyncResponse{
//
//        private EditText toEmail = null;
//        private EditText emailSubject = null;
//        private EditText emailBody = null;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.email_layout_main);
//
//            final Toolbar toolbar = (Toolbar) findViewById(R.id.email_toolbar);
//            setSupportActionBar(toolbar);
//
//
//            toEmail = (EditText) findViewById(R.id.toEmail);
//            emailSubject = (EditText) findViewById(R.id.subject);
//            emailBody = (EditText) findViewById(R.id.emailBody);
//
//            ExportDatabaseAsyncTask exportDb=new ExportDatabaseAsyncTask(getApplicationContext());
//            exportDb.execute();
//
//        }
//
//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            getMenuInflater().inflate(R.menu.email_menu, menu);
//            return true;
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_clear:
//                    toEmail.setText("");
//                    emailBody.setText("");
//                    emailSubject.setText("");
//                    break;
//                case R.id.menu_send:
//                    String to = toEmail.getText().toString();
//                    String subject = emailSubject.getText().toString();
//                    String message = emailBody.getText().toString();
//
//
//                    String uriLocation=Environment.getExternalStorageDirectory()+ File.separator + "exportFillupTable"+File.separator +"hello1" + ".csv";
//                    File file=new File(uriLocation);
//                    Log.e("uriLocation","URILocation: "+file.getAbsolutePath());
//
//                    Intent email = new Intent(Intent.ACTION_SEND);
//                    email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
//                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
//                    email.putExtra(Intent.EXTRA_TEXT, message);
//                    email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//
//                    // need this to prompts email client only
//                    email.setType("message/rfc822");
//
//                    startActivity(Intent.createChooser(email, "Choose an Email client"));
//
//                    break;
//            }
//            return true;
//        }
//
//    @Override
//    public void processFinish() {
//
//    }
//}
