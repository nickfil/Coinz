package uk.ac.ed.inf.coinz;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.stats.WakeLockEvent;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter{

    private int resourceLayout;
    private final Activity context;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;
    private int activity;

    public ListAdapter(Activity context, ArrayList<String> currencies, ArrayList<String> values, ArrayList<Integer> icon, ArrayList<String> id, int activity) {//1=wallet, 2=bank
        super(context, R.layout.list_row, currencies);
        this.context = context;
        this.currencies = currencies;
        this.values = values;
        this.icon = icon;
        this.id = id;
        this.activity= activity;
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
        String id1 = id.get(position);

        if(activity==1) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(getContext(), v);

                    popup.getMenuInflater()
                            .inflate(R.menu.wallet_item_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            Log.d(tt1.getText().toString(), "popup currency");
                            Log.d(tt2.getText().toString(), "popup value");
                            Log.d(id1, "popup id");
                            Coin c = new Coin(tt1.getText().toString(), Double.valueOf(tt2.getText().toString()), id1);

                            switch (item.getItemId()) {

                                case R.id.Deposit:
                                    if(MainActivity.bank.addCoinz(c.getCoinCurrency(), c.getCoinValue(), c.getCoinId())) {
                                        MainActivity.wallet.Delete(c);
                                        removeCoin(position);
                                        Toast.makeText(getContext(),"Coin Deposited", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    Toast.makeText(getContext(),"Bank is full for today", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.Send:
                                    MainActivity.wallet.Delete(c);
                                    removeCoin(position);
                                    Toast.makeText(getContext(),"Coin Sent", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.Delete:
                                    MainActivity.wallet.Delete(c);
                                    removeCoin(position);
                                    Toast.makeText(getContext(),"Coin Deleted", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            notifyDataSetChanged();
                            return true;
                        }
                    });

                    popup.show();
                }
            });
            return v;
        }
        else{ return v;}
    }

    public void removeCoin(int position){
        currencies.remove(position);
        values.remove(position);
        icon.remove(position);
        id.remove(position);
    }
}
