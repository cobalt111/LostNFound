//package com.example.tim.lostnfound;
//
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class CustomListViewAdapter extends ArrayAdapter<String> {
//
//    private final Context mContext;
//    ArrayList<String> lineOne;
//    ArrayList<String> lineTwo;
//
//    public CustomListViewAdapter(Context mContext, ArrayList<String> lineOne, ArrayList<String> lineTwo) {
//        super(mContext, R.layout.content_listings);
//        this.mContext = mContext;
//        this.lineOne = lineOne;
//        this.lineTwo = lineTwo;
//
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LinearLayout listingsView;
//
//        if (convertView == null) {
//            listingsView = new LinearLayout(getContext());
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.listings_list_view, parent, false);
////            mHolder = new ViewHolder();
////            mHolder.lineOneView = (TextView) convertView
////                    .findViewById(R.id.listingsLineOne);
////            mHolder.lineTwoView = (TextView) convertView
////                    .findViewById(R.id.listingsLineTwo);
////            convertView.setTag(mHolder);
//        } else {
//            listingsView = (LinearLayout) convertView;
//        }
//
//        .setText(lineOne.get(position));
//        mHolder.lineTwoView.setText(lineTwo.get(position));
//
//        return convertView;
//    }
//
//    private class ViewHolder {
//        private TextView lineOneView;
//        private TextView lineTwoView;
//    }
//}
//
////        String item = getItem(position);
////
////        View view = super.getView(position, convertView, parent);
////        ((TextView)view.findViewById(R.id.listingsLineOne)).setText(item.getName());
////        ((TextView)view.findViewById(R.id.listingsLineTwo)).setText(item.getNumber());
//
