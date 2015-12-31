package com.a.b.mileagetracker.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.a.b.mileagetracker.DataAccess.DialogInterfaces;
import com.a.b.mileagetracker.DataAccess.HistoryCursorAdapter;
import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.Events.EditHistoryEvent;
import com.a.b.mileagetracker.R;
import com.a.b.mileagetracker.Events.RefreshHistoryListViewEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/14/2015.
 */
public class HistoryFragment extends Fragment implements View.OnClickListener{

    private MySQLiteHelper mDBHelper;
    private ListView mListView;
    private TextView header;
    private DialogInterfaces.DialogInterface mListener;
    public static HistoryCursorAdapter mHistoryCursorAdapter;
    private Button mEmptyButton;
    private TextView mInitMessage;

    public HistoryFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDBHelper=MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.all_data_listview_fragment, container, false);
        mEmptyButton = (Button) view.findViewById(R.id.empty_history_button);
        mInitMessage=(TextView) view.findViewById(R.id.init_message);
        mEmptyButton.setOnClickListener(this);
        mListView = (ListView) view.findViewById(R.id.listview);

        SharedPreferences mSharedPrefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        mDBHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        final Cursor cursor=mDBHelper.getAllData();

        if(cursor==null){
            mEmptyButton.setText(R.string.get_started);
            mEmptyButton.setVisibility(View.VISIBLE);
            mInitMessage.setText(R.string.init_message_no_vehicles);
            mInitMessage.setVisibility(View.VISIBLE);
        }else if (cursor.getCount()<1){
            mEmptyButton.setText(R.string.add_record_title);
            mEmptyButton.setVisibility(View.VISIBLE);
            mInitMessage.setText(R.string.init_message_no_data);
            mInitMessage.setVisibility(View.VISIBLE);
        }else{
            mEmptyButton.setVisibility(View.GONE);
            mInitMessage.setVisibility(View.GONE);
        }

        mHistoryCursorAdapter = new HistoryCursorAdapter(getActivity(), cursor, 0);
        mListView.setAdapter(mHistoryCursorAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = mDBHelper.getAllData();
            mListener.openEditVehicleEntryFragment();
            EventBus.getDefault().postSticky(new EditHistoryEvent(c, position, id));

//                Log.e("long click", "long clicked: " + view + ", " + position + ", " + id);
            return true;
            }
        });
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        return view;
    }

    public void onEvent(RefreshHistoryListViewEvent event){
        mDBHelper = MySQLiteHelper.getInstance(getActivity().getApplicationContext());
        Cursor cursor=mDBHelper.getAllData();
        mHistoryCursorAdapter.changeCursor(cursor);
        mHistoryCursorAdapter.notifyDataSetChanged();

        if(cursor==null){
            mEmptyButton.setText(R.string.get_started);
            mEmptyButton.setVisibility(View.VISIBLE);
            mInitMessage.setText(R.string.init_message_no_vehicles);
            mInitMessage.setVisibility(View.VISIBLE);
        }else if (cursor.getCount()<1){
            mEmptyButton.setText(R.string.add_record_title);
            mEmptyButton.setVisibility(View.VISIBLE);
            mInitMessage.setText(R.string.init_message_no_data);
            mInitMessage.setVisibility(View.VISIBLE);
        }else{
            mEmptyButton.setVisibility(View.GONE);
            mInitMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogInterfaces.DialogInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onClick(View v) {
        mListener.pressInitialButtonAction();
    }
}