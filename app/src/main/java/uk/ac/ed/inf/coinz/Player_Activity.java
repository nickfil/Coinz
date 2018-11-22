package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Player_Activity extends AppCompatActivity {

    private Bank bank;
    private String username;
    private Double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_);
        bank = new Bank(MainActivity.todaysRates, SaveSharedPreference.getBankCoin(getApplicationContext()));
        distance = SaveSharedPreference.getDistanceWalked(getApplicationContext());
        username = SaveSharedPreference.getUsername(getApplicationContext());

        TextView name = findViewById(R.id.nameBox);
        name.setText(username);

        TextView dst = findViewById(R.id.distanceTextBox);
        dst.setText(String.valueOf(Math.round(distance))+"m");

        TextView gold = findViewById(R.id.goldAmount);
        gold.setText(String.valueOf(Math.round(bank.getCoinAmount("Gold", MainActivity.todaysRates))));

        TextView shil = findViewById(R.id.shilAmount);
        shil.setText(String.valueOf(Math.round(bank.getCoinAmount("SHIL", MainActivity.todaysRates))));

        TextView dolr = findViewById(R.id.dolrAmount);
        dolr.setText(String.valueOf(Math.round(bank.getCoinAmount("DOLR", MainActivity.todaysRates))));

        TextView quid = findViewById(R.id.quidAmount);
        quid.setText(String.valueOf(Math.round(bank.getCoinAmount("QUID", MainActivity.todaysRates))));

        TextView peny = findViewById(R.id.penyAmount);
        peny.setText(String.valueOf(Math.round(bank.getCoinAmount("PENY", MainActivity.todaysRates))));

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
                // User chose the "Settings" item, show the app settings UI...
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
