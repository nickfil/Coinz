package uk.ac.ed.inf.coinz;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.stats.WakeLockEvent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter{

    private int resourceLayout;
    private final Activity context;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;
    private ArrayList<Coin> bankCoinz;
    private ArrayList<Coin> walletCoinz = new ArrayList<>();
    private int activity;
    private my_wallet wallet;
    private Bank bank;
    private String coinToRemove;



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

                                    String numOfCoinzTodayPlayer = SaveSharedPreference.getNumofBankedToday(getContext(), LoginActivity.mEmailView.toString());
                                    Log.d(numOfCoinzTodayPlayer, "heyyyyyyy");
                                    int numOfCoinzToday = Integer.valueOf(numOfCoinzTodayPlayer);
                                    //keeping the number of banked coins in shared preferences to have it when the task ends
                                    if(numOfCoinzToday<25) {
                                        numOfCoinzToday++;
                                        wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);
                                        wallet.Delete(c);
                                        LoginActivity.firestore_bank.document(c.getCoinId()).set(c);
                                        LoginActivity.firestore_user.update("numOfCoinzToday", numOfCoinzToday);
                                        SaveSharedPreference.setNumofBankedToday(getContext(),numOfCoinzToday ,LoginActivity.mEmailView.toString() );
                                        Toast.makeText(getContext(), "Coin Deposited", Toast.LENGTH_SHORT).show();
                                        removeCoin(position);
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Bank Limit of 25 Deposits Reached for Today", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.Send:
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Send Coin\n\n");
                                    alertDialog.setMessage("Enter email:");
                                    final EditText input = new EditText(context);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    alertDialog.setView(input);
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send",
                                            (dialog, i) -> {


                                                LoginActivity.firestore.collection("Users ").get()
                                                        .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                                                            @Override
                                                            public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                                                                List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                                                                Boolean flag=false;
                                                                for (DocumentSnapshot ds : task.getResult()) {
                                                                    Log.d(String.valueOf(ds.getData().get("email")), "UID here");

                                                                    if(ds.getData().get("email").equals(input.getText().toString())){
                                                                        Log.d(ds.getId(),"UID here");
                                                                        String UID = ds.getId();
                                                                        SaveSharedPreference.setUIDtoSend(getContext(), UID);

                                                                        LoginActivity.firestore.collection("Users ").document(UID).collection("Bank").document(c.getCoinId()).set(c);
                                                                        wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);
                                                                        wallet.Delete(c);
                                                                        removeCoin(position);
                                                                        flag=true;
                                                                        Toast.makeText(getContext(), "Coin Sent", Toast.LENGTH_SHORT).show();
                                                                        notifyDataSetChanged();

                                                                    }
                                                                }
                                                                if(flag.equals(false)){
                                                                    SaveSharedPreference.setUIDtoSend(getContext(), "");
                                                                    Toast.makeText(getContext(), "Coin Was Not Sent", Toast.LENGTH_SHORT).show();
                                                                }

                                                                return Tasks.whenAllSuccess(tasks);
                                                            }
                                                        });

                                                dialog.dismiss();
                                            });
                                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, i) -> dialog.dismiss());
                                    alertDialog.show();
                                    break;

                                case R.id.Delete:
                                    wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);
                                    wallet.Delete(c);
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
