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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Bank_Activity extends AppCompatActivity {

    ListView listView;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;
    private Bank bank;
    private ArrayList<Coin> bankCoinz;

    public Bank_Activity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_);
        listView = findViewById(R.id.listView);
        bankCoinz = new ArrayList<>();


        LoginActivity.firestore_bank.get()
                .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    for (DocumentSnapshot ds : task.getResult()) {
                        Coin c = new Coin((String) ds.get("coinCurrency"),
                                          (Double) ds.get("coinValue"),
                                          (String) ds.get("coinId"));
                        bankCoinz.add(c);

                    }

                    bank = new Bank(MainActivity.todaysRates, bankCoinz);

                    currencies = new ArrayList<>();
                    values = new ArrayList<>();
                    icon = new ArrayList<>();
                    id = new ArrayList<>();

                    if(!bank.getCoinz().isEmpty()) {
                        for (Coin c : bank.getCoinz()) {
                            currencies.add(c.getCoinCurrency());
                            values.add(c.getCoinValue().toString()); //parallel arrays with each coin in wallet and its value
                            icon.add(c.getIcon());
                            id.add(c.getCoinId());
                        }

                        ListAdapter listAdapter = new ListAdapter(Bank_Activity.this, currencies, values, icon, id,2);
                        listView.setAdapter(listAdapter);
                    }
                    return Tasks.whenAllSuccess(tasks);
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
