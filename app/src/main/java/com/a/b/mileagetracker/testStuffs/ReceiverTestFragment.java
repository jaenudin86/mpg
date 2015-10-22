package com.a.b.mileagetracker.testStuffs;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.MyCursorAdapter;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.R;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/14/2015.
 */
public class ReceiverTestFragment extends Fragment {

    private MySQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private ListView listview;
    private ListView listview2;
    public static SimpleCursorAdapter cursorAdapter;
    public static MyCursorAdapter advancedCursorAdapter;

    public ReceiverTestFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dbHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        db=dbHelper.getWritableDatabase();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.receiver_test_fragment, container, false);
        listview=(ListView) view.findViewById(R.id.listview);
        listview2=(ListView) view.findViewById(R.id.listview2);
        Cursor cursor = db.rawQuery("SELECT _id, location FROM fillupTable", null);
        Log.e("output","output: "+cursor.getColumnName(0)+", "+cursor.getColumnName(1));

        String[] columns={cursor.getColumnName(1)};
        int[] displayViews = {R.id.list_text};
        cursorAdapter=new SimpleCursorAdapter(getActivity(),R.layout.list_item,cursor,columns,displayViews,0);
        advancedCursorAdapter = new MyCursorAdapter(getActivity(), cursor,0);


        listview2.setAdapter(advancedCursorAdapter);
        listview.setAdapter(cursorAdapter);
//        listview.setOnItemClickListener();


        return view;
//        return inflater.inflate(R.layout.receiver_test_fragment, container, false);
    }



    public void onEvent(MessageEvent event){
        Log.e("onEvent", "onEvent received");
        Toast.makeText(getActivity(),event.message,Toast.LENGTH_LONG).show();
        String insert_query="INSERT INTO fillupTable (location) "+"VALUES ('bp')";
        db.execSQL(insert_query);
        Cursor c =db.rawQuery("SELECT _id, location FROM fillupTable",null);
        cursorAdapter.changeCursor(c);

    }


//    public void onEvent(SomeOtherEvent event){
//        Log.e("","something else received");
//    }
//    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}
