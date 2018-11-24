package uk.ac.ed.inf.coinz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationEngineListener, PermissionsListener{

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    public TextView data;
    public static my_wallet wallet;
    private ArrayList<Coin> walletCoinz = new ArrayList<>();
    public static HashMap<String, Double> todaysRates = new HashMap<>();
    private CollectingCoinz collectingCoinz;
    public static String mode = "Classic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance (this, "pk.eyJ1Ijoibmlja2ZpbCIsImEiOiJjam55bGRjZHEwZTh1M2xwOWpqdjRjcDhwIn0.FhMCVf5LAlvD7Im8-Xpsvw");
        setContentView(R.layout.activity_main);
        mapView = this.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();


        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dt = df.format(date);
        String json = "";
        Log.d(dt, "Date format");
        String mapURLstring = "http://homepages.inf.ed.ac.uk/stg/coinz/"+dt+"/coinzmap.geojson";

        DownloadFileTask getData = new DownloadFileTask();

        //getting json string from specific date url
        try {
            json = getData.execute(mapURLstring).get();
            Log.d("json file downloaded","completed");
        } catch (ExecutionException|InterruptedException e) {
            e.printStackTrace();
        }

        //getting rates of today's date currencies
        JsonParser parser = new JsonParser();
        JsonObject tempObj = (JsonObject) parser.parse(json);
        JsonObject rates = (JsonObject) tempObj.get("rates");

        todaysRates.put("SHIL", Double.valueOf(rates.get("SHIL").toString()));
        todaysRates.put("QUID", Double.valueOf(rates.get("QUID").toString()));
        todaysRates.put("DOLR", Double.valueOf(rates.get("DOLR").toString()));
        todaysRates.put("PENY", Double.valueOf(rates.get("PENY").toString()));



        //if the date today is different than the last save, then the wallet is wiped clean since coinz have expired
        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("num of coinz", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                String lastEntryDate = (String) (Objects.requireNonNull(documentSnapshot.getData()).get("lastEntryDate"));
                //saving the last entry date to shared preferences to perform check later
                SaveSharedPreference.setDate(getApplicationContext(), lastEntryDate);
            }
        });

        if(!SaveSharedPreference.getLastSaveDate(getApplicationContext()).equals(dt)) {

            LoginActivity.firestore_user.update("numOfCoinzToday", 0);
            LoginActivity.firestore_user.update("lastEntryDate", dt);
            LoginActivity.firestore_wallet.get()
                    .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                        for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                            Coin c = new Coin((String) ds.get("coinCurrency"),
                                    (Double) ds.get("coinValue"),
                                    (String) ds.get("coinId"));
                            walletCoinz.add(c);
                        }

                        wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);

                        for(Coin c : walletCoinz){
                            LoginActivity.firestore_wallet.document(c.getCoinId()).delete();
                        }

                        return Tasks.whenAllSuccess(tasks);
                    });
        SaveSharedPreference.setNumofBankedToday(getApplicationContext(),0 ,LoginActivity.mEmailView.toString());
        LoginActivity.firestore_user.update("numOfCoinzToday", 25);
        }

        //extracting the properties and geometry from each feature to create markers
        FeatureCollection fc = FeatureCollection.fromJson(json);
        collectingCoinz = new CollectingCoinz(fc,this);
        collectingCoinz.initializeMap(map);
    }

    private void enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            initializeLocationLayer();
        }
        else{
            permissionsManager=new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){
        locationEngine=new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.addLocationEngineListener(this);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if(lastLocation!=null){
            originLocation=lastLocation;
            setCameraPosition(lastLocation);
        }
        else{
            locationEngine.addLocationEngineListener(this);
        }

    }


    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){
        locationLayerPlugin=new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                            location.getLongitude()), 15.0));
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            collectingCoinz.checkCoinCollection(map, location);
            originLocation=location;
            setCameraPosition(location);
        }
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions,grantResults);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        if(locationEngine!=null){
            locationEngine.requestLocationUpdates();
        }
        if(locationLayerPlugin!=null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(locationEngine!=null){
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin!=null){
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null){
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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

