package com.example.tripist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// FAVORİLER TASARIMI
public class Adapter extends ArrayAdapter<Yerler> {
    ArrayList<Yerler> yerlerList;
    Context context;
    public Adapter(@NonNull Context context, ArrayList<Yerler> yerlerList) {
        super(context, R.layout.favoriler,yerlerList);
        this.yerlerList = yerlerList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View favorilerView = layoutInflater.inflate(R.layout.favoriler,parent,false);
        TextView nameTextView = favorilerView.findViewById(R.id.nameTextView);
        nameTextView.setText(yerlerList.get(position).name);

        return favorilerView;
    }
}
