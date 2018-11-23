package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Player_Activity extends AppCompatActivity {

    private Bank bank;
    private ArrayList<Coin> bankCoinz = new ArrayList<>();
    private String username;
    private Double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_);
        username="";
        distance=0.0;

        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("num of coinz", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                distance = (Double) (documentSnapshot.getData().get("totalDistanceWalked"));
                Log.d(String.valueOf(distance), "fetched correctly");

                TextView dst = findViewById(R.id.distanceTextBox);
                dst.setText(String.valueOf(Math.round(distance))+"m");
            }
        });

        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("num of coinz", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                username = (String) (documentSnapshot.getData().get("nickname"));
                Log.d(String.valueOf(username), "fetched correctly");

                TextView name = findViewById(R.id.nameBox);
                name.setText(username);
            }
        });

        LoginActivity.firestore_bank.get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                    @Override
                    public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                        List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                        for (DocumentSnapshot ds : task.getResult()) {
                            Log.d(String.valueOf(ds.get("coinId")), "yoooooooooo");
                            Coin c = new Coin((String) ds.get("coinCurrency"),
                                    (Double) ds.get("coinValue"),
                                    (String) ds.get("coinId"));
                            bankCoinz.add(c);

                        }

                        bank = new Bank(MainActivity.todaysRates, bankCoinz);

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
