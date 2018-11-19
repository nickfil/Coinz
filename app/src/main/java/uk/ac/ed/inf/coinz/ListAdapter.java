package uk.ac.ed.inf.coinz;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter{

    private int resourceLayout;
    private final Activity context;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;

    public ListAdapter(Activity context, ArrayList<String> currencies, ArrayList<String> values, ArrayList<Integer> icon, ArrayList<String> id) {
        super(context, R.layout.list_row, currencies);
        this.context = context;
        this.currencies = currencies;
        this.values = values;
        this.icon = icon;
        this.id = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.list_row, null, true);

        TextView tt1 = v.findViewById(R.id.currency_title);
        TextView tt2 = v.findViewById(R.id.currency_value);
        ImageView tt3 = v.findViewById(R.id.currency_marker);

        tt1.setText(currencies.get(position));
        tt2.setText(values.get(position));
        tt3.setImageResource(icon.get(position));

        return v;
    }

}
