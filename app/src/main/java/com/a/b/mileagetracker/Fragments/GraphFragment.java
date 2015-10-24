package com.a.b.mileagetracker.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a.b.mileagetracker.R;

/**
 * Created by Andrew on 10/23/2015.
 */
public class GraphFragment extends Fragment {

    public static GraphFragment newInstance(String s){
        GraphFragment graphFrag=new GraphFragment();

        Bundle args = new Bundle();
        args.putString("someString", s);
        graphFrag.setArguments(args);
        return graphFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String s=getArguments().getString("someString", "notFound");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, container, false);
        TextView message=(TextView) view.findViewById(R.id.message_received);
        message.setText(getArguments().getString("someString", "not found"));
        return view;
    }
}
