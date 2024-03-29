package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.provider.Telephony;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Modes_Activity extends AppCompatActivity {

    private Switch backgroundModeSwitch;
    private Switch recordDistanceSwitch;
    private Switch routeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes_);

        backgroundModeSwitch = findViewById(R.id.BackgroundModeSwitch);
        recordDistanceSwitch = findViewById(R.id.recordDistanceSwitch);
        routeSwitch = findViewById(R.id.routeSwitch);

        //setting the background switch from firestore
        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("hey", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                backgroundModeSwitch.setChecked((Boolean) Objects.requireNonNull(documentSnapshot.getData()).get("backgroundSwitch"));
                Log.d(String.valueOf(backgroundModeSwitch), "fetched correctly");
            }
        });

        //getting distance switch from firestore
        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("hey", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                recordDistanceSwitch.setChecked((Boolean) Objects.requireNonNull(documentSnapshot.getData()).get("distanceSwitch"));
                Log.d(String.valueOf(recordDistanceSwitch), "fetched correctly");
            }
        });

        //getting route switch from firestore
        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("hey", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                routeSwitch.setChecked((Boolean) Objects.requireNonNull(documentSnapshot.getData()).get("routeSwitch"));
                Log.d(String.valueOf(routeSwitch), "fetched correctly");
            }
        });


        backgroundModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.v("Switch State=", ""+isChecked);
            if(isChecked) {
                MainActivity.mode = "Background";
                LoginActivity.firestore_user.update("backgroundSwitch", true);
            }
            else{
                MainActivity.mode = "Classic";
                LoginActivity.firestore_user.update("backgroundSwitch", false);
            }
            Log.d(MainActivity.mode, "Mode has changed");
        });

        recordDistanceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.v("Switch State=", ""+isChecked);
            if(isChecked) {
                recordDistanceSwitch.setChecked(true);
                LoginActivity.firestore_user.update("distanceSwitch", true);
            }
            else{
                recordDistanceSwitch.setChecked(false);
                LoginActivity.firestore_user.update("distanceSwitch", false);
            }
            Log.d(MainActivity.mode, "Mode has changed");
        });

        routeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.v("Switch State=", ""+isChecked);
            if(isChecked) {
                routeSwitch.setChecked(true);
                LoginActivity.firestore_user.update("routeSwitch", true);
            }
            else{
                routeSwitch.setChecked(false);
                LoginActivity.firestore_user.update("routeSwitch", false);
            }
            Log.d(MainActivity.mode, "Mode has changed");
        });


        //updating the nickname in the online database when it is updated here

        Button setNickname = findViewById(R.id.setNickname);
        TextInputEditText usrnm = findViewById(R.id.EditNickname);

        setNickname.setOnClickListener(v -> LoginActivity.firestore_user.update("nickname", usrnm.getEditableText().toString()));

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
