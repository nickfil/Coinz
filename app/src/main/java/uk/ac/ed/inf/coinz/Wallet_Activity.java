package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Wallet_Activity extends AppCompatActivity {

    ListView listView;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;
    private ArrayList<Coin> walletCoinz;
    private my_wallet wallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_);
        listView = findViewById(R.id.listView);
        walletCoinz = new ArrayList<>();

        LoginActivity.firestore_wallet.get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                    @Override
                    public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                        List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                        for (DocumentSnapshot ds : task.getResult()) {
                            Coin c = new Coin((String) ds.get("coinCurrency"),
                                    (Double) ds.get("coinValue"),
                                    (String) ds.get("coinId"));
                            walletCoinz.add(c);

                        }

                        wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);

                        currencies = new ArrayList<>();
                        values = new ArrayList<>();
                        icon = new ArrayList<>();
                        id = new ArrayList<>();

                        if(!wallet.getCoinz().isEmpty()) {
                            for (Coin c : wallet.getCoinz()) {
                                currencies.add(c.getCoinCurrency());
                                values.add(c.getCoinValue().toString()); //parallel arrays with each coin in wallet and its value
                                icon.add(c.getIcon());
                                id.add(c.getCoinId());
                            }

                            ListAdapter listAdapter = new ListAdapter(Wallet_Activity.this, currencies, values, icon, id,1);
                            listView.setAdapter(listAdapter);
                        }
                        return Tasks.whenAllSuccess(tasks);
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.Main_Activity:

                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.Modes_Option:

                intent = new Intent(this, Modes_Activity.class);
                startActivity(intent);
                return true;

            case R.id.Wallet_Option:

                intent = new Intent(this, Wallet_Activity.class);
                startActivity(intent);
                return true;

            case R.id.Bank_Option:

                intent = new Intent(this, Bank_Activity.class);
                startActivity(intent);
                return true;

            case R.id.Chat_Option:

                intent = new Intent(this, KommunicatorActivity.class);
                startActivity(intent);
                return true;

            case R.id.Rates_Option:

                intent = new Intent(this, Rates_Activity.class);
                startActivity(intent);
                return true;

            case R.id.Profile_Option:

                intent = new Intent(this, Player_Activity.class);
                startActivity(intent);
                return true;

            case R.id.LogOut_Option:

                FirebaseAuth.getInstance().signOut();

                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
