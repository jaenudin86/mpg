//package com.a.b.mileagetracker.Model;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import R;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Andrew on 10/27/2015.
// */
//public class ToolbarSpinnerAdapter extends BaseAdapter {
//        private Context context;
//        private List<TestObject> mItems = new ArrayList<>();
//
//        public ToolbarSpinnerAdapter(Context context){
//            this.context=context;
//
//        }
//
//        public void clear() {
//            mItems.clear();
//        }
//
//        public void addItem(TestObject yourObject) {
//            mItems.add(yourObject);
//        }
//
//        public void addItems(List<TestObject> yourObjectList) {
//            mItems.addAll(yourObjectList);
//        }
//
//        @Override
//        public int getCount() {
//            return mItems.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mItems.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getDropDownView(int position, View view, ViewGroup parent) {
//            if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
//                view=LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
////                view = getLayoutInflater().inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
//                view.setTag("DROPDOWN");
//            }
//
//            TextView textView = (TextView) view.findViewById(android.R.id.text1);
//            textView.setText(getTitle(position));
//
//            return view;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup parent) {
//            if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
//                view = LayoutInflater.from(context).inflate(R.layout.
//                        toolbar_spinner_item_actionbar, parent, false);
//                view.setTag("NON_DROPDOWN");
//            }
//            TextView textView = (TextView) view.findViewById(android.R.id.text1);
//            textView.setText(getTitle(position));
//            return view;
//        }
//
//        private String getTitle(int position) {
//            return mItems.get(position).getName();
////            return position >= 0 && position < mItems.size() ? mItems.get(position).getName() : "";
//        }
//    }


