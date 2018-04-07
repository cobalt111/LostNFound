package com.example.tim.lostnfound;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {



    List<HashMap<String, String>> animalList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameView, typeView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.listingsNameView);
            typeView = (TextView) view.findViewById(R.id.listingsTypeView);
            imageView = (ImageView) view.findViewById(R.id.listingsImageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListingsAdapter(List<HashMap<String, String>> dataset) {
        animalList = dataset;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ListingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_animal_listing_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        HashMap<String, String> animal = animalList.get(position);

        String name = animal.get("name");
        String type = animal.get("type");
        String color = animal.get("color");
        String colType = color + " " + type;
        if (name != null && color != null) {
            holder.nameView.setText(animal.get("name"));
            holder.typeView.setText(animal.get(colType));
        } else if (color != null){
            holder.nameView.setText(colType);
        } else holder.nameView.setText(type);

        Picasso.get()
                .load(animal.get("thumbURL"))
                .resize(60,60)
                .centerCrop()
                .into(holder.imageView);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return animalList.size();
    }
}
