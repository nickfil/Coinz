package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class Wallet_Activity extends AppCompatActivity {

    ListView listView;
    private ArrayList<String> currencies;
    private ArrayList<String> values;
    private ArrayList<Integer> icon;
    private ArrayList<String> id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_);
        listView = findViewById(R.id.listView);
        my_wallet wallet = MainActivity.wallet;

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

            ListAdapter listAdapter = new ListAdapter(Wallet_Activity.this, currencies, values, icon, id);
            listView.setAdapter(listAdapter);
        }
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
            case R.id.Modes_Option:

                intent = new Intent(this, MainActivity.class);
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
