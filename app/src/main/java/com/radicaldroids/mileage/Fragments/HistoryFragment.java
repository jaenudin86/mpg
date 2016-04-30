package com.radicaldroids.mileage.Fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.radicaldroids.mileage.DataAccess.DataProvider;
import com.radicaldroids.mileage.DataAccess.DialogInterfaces;
import com.radicaldroids.mileage.DataAccess.HistoryCursorAdapter;
import com.radicaldroids.mileage.Events.EditHistoryEvent;
import com.radicaldroids.mileage.MyApplication;
import com.radicaldroids.mileage.R;
import com.radicaldroids.mileage.Events.RefreshHistoryListViewEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Andrew on 10/14/2015.
 */
public class HistoryFragment extends Fragment {

    private ListView mListView;
    private DialogInterfaces.DialogInterface mListener;
    public static HistoryCursorAdapter mHistoryCursorAdapter;
    private TextView mInitMessage;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        MyApplication application=(MyApplication) getActivity().getApplication();
        mTracker=application.getTracker();
        sendAnalyticName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_data_listview_fragment, container, false);
        mInitMessage=(TextView) view.findViewById(R.id.init_message);
        mListView = (ListView) view.findViewById(R.id.listview);

        final Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/current_vehicle"),null,null,null,null);

        if(cursor==null){
            mInitMessage.setText(R.string.init_message_no_vehicles);
            mInitMessage.setVisibility(View.VISIBLE);
        }else if (cursor.getCount()<1){
            mInitMessage.setText(R.string.init_message_no_data);
            mInitMessage.setVisibility(View.VISIBLE);
        }else{
            mInitMessage.setVisibility(View.GONE);
        }

        mHistoryCursorAdapter = new HistoryCursorAdapter(getActivity(), cursor, 0);
        mListView.setAdapter(mHistoryCursorAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/current_vehicle"),null,null,null,null);
            mListener.openEditVehicleEntryFragment();
            EventBus.getDefault().postSticky(new EditHistoryEvent(cursor, position, id));

//                Log.e("long click", "long clicked: " + view + ", " + position + ", " + id);
            return true;
            }
        });
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        return view;
    }

    public void onEvent(RefreshHistoryListViewEvent event){
        final Cursor cursor = getActivity().getContentResolver().query(Uri.parse(DataProvider.BASE_CONTENT_URI +"/current_vehicle"),null,null,null,null);
        mHistoryCursorAdapter.changeCursor(cursor);
        mHistoryCursorAdapter.notifyDataSetChanged();

        if(cursor==null){
            mInitMessage.setText(R.string.init_message_no_vehicles);
            mInitMessage.setVisibility(View.VISIBLE);
        }else if (cursor.getCount()<1){
            mInitMessage.setText(R.string.init_message_no_data);
            mInitMessage.setVisibility(View.VISIBLE);
        }else{
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
    public void onResume() {
        super.onResume();
        sendAnalyticName();
    }
    private void sendAnalyticName(){
        mTracker.setScreenName(getString(R.string.history_fragment_analytic_tag));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}