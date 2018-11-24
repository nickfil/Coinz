package uk.ac.ed.inf.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class KommunicatorActivity extends AppCompatActivity {

    private static final String TAG="JKommunicatorActivity";
    private static final String COLLECTION_KEY="Chat ";
    private static final String DOCUMENT_KEY="Message ";
    private static final String NAME_FIELD="Name ";
    private static final String TEXT_FIELD="Text ";

    private EditText name_text;
    private EditText outgoing_message_text;
    private TextView incoming_message_text;

    private FirebaseFirestore firestore;
    private DocumentReference firestoreChat;

    private void firestoreChat(){
        FirebaseFirestore.getInstance().collection(COLLECTION_KEY);
        }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jkommunicator);
        name_text= findViewById(R.id.jname_text);
        outgoing_message_text=findViewById(R.id.joutgoing_message_text);
        incoming_message_text=findViewById(R.id.jincoming_message_text);

        FloatingActionButton send_message_button=findViewById(R.id.jsend_message_button);
        send_message_button.setOnClickListener(view->sendMessage());

        firestore = FirebaseFirestore.getInstance();
        // Use com.google.firebase.Timestamp objects instead of java.util.Date objects
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();

        firestoreChat =
                firestore.collection(COLLECTION_KEY) // ”Chat”
                         .document(DOCUMENT_KEY); // ”Message”
        // Set a listener for changes to the /Chat/Message document
        realtimeUpdateListener();
    }

    private void sendMessage() {
        // create a message of the form { ”Name”: str1, ”Text”: str2 }
        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(NAME_FIELD, name_text.getText().toString());
        newMessage.put(TEXT_FIELD, outgoing_message_text.getText().toString());
        // send the message and listen for success or failure
        firestoreChat.set(newMessage)
                .addOnSuccessListener(v -> Toast.makeText(getApplicationContext(),
                "Message sent!",
                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }

    private void realtimeUpdateListener() {
        firestoreChat.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                String incoming = (documentSnapshot.getData().get(NAME_FIELD))
                        + ": "
                        + (documentSnapshot.getData().get(TEXT_FIELD));
                incoming_message_text.setText(incoming);
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
