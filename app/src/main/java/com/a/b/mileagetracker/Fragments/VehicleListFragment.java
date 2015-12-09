package com.a.b.mileagetracker.Fragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.a.b.mileagetracker.DataAccess.MySQLiteHelper;
import com.a.b.mileagetracker.DataAccess.VehicleCursorAdapter;
import com.a.b.mileagetracker.R;

/**
 * Created by Andrew on 12/8/2015.
 */
public class VehicleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView mListView;

    public static VehicleListFragment newInstance(){
        VehicleListFragment vehicleListFragment=new VehicleListFragment();
        return vehicleListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks) this );
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vehicle_listview_fragment, container, false);
        mListView=(ListView) view.findViewById(R.id.vehicle_listview);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader CL=null;
        switch (id){
            case 1:
                CL=new CursorLoader(getActivity().getApplicationContext(), Uri.parse("content://com.a.b.mileagetracker/key_table"), null, null, null, null);
            break;
        }
        return CL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        VehicleCursorAdapter vCA=new VehicleCursorAdapter(getActivity(),data,0);
        mListView.setAdapter(vCA);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
