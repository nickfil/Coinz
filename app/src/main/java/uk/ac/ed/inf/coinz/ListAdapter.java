package uk.ac.ed.inf.coinz;


import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListAdapter extends ArrayAdapter{

    private final Activity context;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;
    private ArrayList<Coin> walletCoinz = new ArrayList<>();
    private int activity;
    private my_wallet wallet;



    public ListAdapter(Activity context, ArrayList<String> currencies, ArrayList<String> values, ArrayList<Integer> icon, ArrayList<String> id, int activity) {//1=wallet, 2=bank
        super(context, R.layout.list_row, currencies);
        this.context = context;
        this.currencies = currencies;
        this.values = values;
        this.icon = icon;
        this.id = id;
        this.activity= activity;

    }

    @Override //implementation of the list adapter to view coinz as lists in the ui
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

        if(activity==1) { //activity is used to indicate whether we are in the bank or wallet, as only the wallet has extra functionalities
            v.setOnClickListener(v1 -> {

                PopupMenu popup = new PopupMenu(getContext(), v1);

                popup.getMenuInflater()
                        .inflate(R.menu.wallet_item_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {

                    Log.d(tt1.getText().toString(), "popup currency");
                    Log.d(tt2.getText().toString(), "popup value");
                    Log.d(id1, "popup id");
                    Coin c = new Coin(tt1.getText().toString(), Double.valueOf(tt2.getText().toString()), id1);

                    switch (item.getItemId()) {

                        //three options in the wallet: deposit, send, delete
                        case R.id.Deposit:

                            String numOfCoinzTodayPlayer = SaveSharedPreference.getNumofBankedToday(getContext(), LoginActivity.mEmailView.toString());
                            Log.d(numOfCoinzTodayPlayer, "Number of Coinz Today");
                            int numOfCoinzToday = Integer.valueOf(numOfCoinzTodayPlayer);
                            //keeping the number of banked coins in shared preferences to have it when the task ends
                            if(numOfCoinzToday<25) {
                                numOfCoinzToday++;
                                wallet = new my_wallet(MainActivity.todaysRates, walletCoinz); //when depositing, a coin is removed from the wallet and then added to the bank
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
                                                .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                                                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                                                    Boolean flag=false;
                                                    for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                                                        Log.d(String.valueOf(Objects.requireNonNull(ds.getData()).get("email")), "UID here");

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
                });

                popup.show();
            });
            return v;
        }
        else{ return v;}
    }

    private void removeCoin(int position){
        currencies.remove(position);
        values.remove(position);
        icon.remove(position);
        id.remove(position);
    }
}
