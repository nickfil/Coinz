package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.SafeBrowsingResponse;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

import java.text.CollationElementIterator;

public class Modes_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes_);

        Switch backgroundModeSwitch = (Switch) findViewById(R.id.BackgroundModeSwitch);
        Switch recordDistanceSwitch = (Switch) findViewById(R.id.recordDistanceSwitch);
        backgroundModeSwitch.setChecked(SaveSharedPreference.getBackgroundSwitch(getApplicationContext()));
        backgroundModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if(isChecked) {
                    MainActivity.mode = "Background";
                    SaveSharedPreference.setBackgroundSwitch(getApplicationContext(), true);
                }
                else{
                    MainActivity.mode = "Classic";
                    SaveSharedPreference.setBackgroundSwitch(getApplicationContext(), false);
                }
                Log.d(MainActivity.mode, "Mode has changed");
            }

        });

        recordDistanceSwitch.setChecked(SaveSharedPreference.getRecordDistanceSwitch(getApplicationContext()));
        recordDistanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if(isChecked) {
                    CollectingCoinz.recordDistance = true;
                    SaveSharedPreference.setRecordDistanceSwitch(getApplicationContext(), true);
                }
                else{
                    CollectingCoinz.recordDistance = false;
                    SaveSharedPreference.setRecordDistanceSwitch(getApplicationContext(), false);
                }
                Log.d(CollectingCoinz.recordDistance.toString(), "Distance Recording has Changed");
            }

        });

        Button setNickname = findViewById(R.id.setNickname);
        TextInputEditText usrnm = findViewById(R.id.EditNickname);
        setNickname.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveSharedPreference.setUsername(getApplicationContext(), usrnm.getEditableText().toString());
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
