package uk.ac.ed.inf.coinz;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.geojson.Point;

import android.graphics.Bitmap;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.android.core.location.LocationEngineProvider;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationEngineListener, PermissionsListener{

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    public static TextView data;
    private my_wallet wallet;
    private Bank bank;
    private HashMap<String, Double> todaysRates = new HashMap<>();
    private CollectingCoinz collectingCoinz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance (this, "pk.eyJ1Ijoibmlja2ZpbCIsImEiOiJjam55bGRjZHEwZTh1M2xwOWpqdjRjcDhwIn0.FhMCVf5LAlvD7Im8-Xpsvw");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();


        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
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


        wallet = new my_wallet(todaysRates, SaveSharedPreference.getWalletCoin(getApplicationContext()));
        bank = new Bank(todaysRates,SaveSharedPreference.getBankCoin(getApplicationContext()));

        //if the date today is different than the last save, then the wallet is wiped clean since coinz have expired
        if(!SaveSharedPreference.getLastSaveDate(getApplicationContext()).equals(dt)) {

            Log.d(SaveSharedPreference.getLastSaveDate(getApplicationContext()),"date 1");
            Log.d(dt, "date 2");

            SaveSharedPreference.setNumofBankedToday(getApplicationContext(), 0);
            wallet.wipeWallet();

        }

        //extracting the properties and geometry from each feature to create markers
        FeatureCollection fc = FeatureCollection.fromJson(json);
        collectingCoinz = new CollectingCoinz(fc);
        collectingCoinz.initializeMap(map, wallet);
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
            collectingCoinz.checkCoinCollection(map, location, wallet);
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

}

