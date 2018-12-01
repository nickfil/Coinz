package uk.ac.ed.inf.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class CollectingCoinz {

    private ArrayList<MarkerViewOptions> markers = new ArrayList<MarkerViewOptions>();
    private FeatureCollection fc;
    private Context context;
    private Location prevLocation;
    private Double totalDistanceWalked;
    public static Boolean recordDistance=false;
    public static Boolean routeSwitch=false;
    private my_wallet wallet;
    private NavigationMapRoute navigationMapRoute;
    ArrayList<Coin> walletCoinz = new ArrayList<>();


    public CollectingCoinz(FeatureCollection fc, Context context){
        this.fc = fc;
        this.context=context;


        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("CollectingCoinz", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                totalDistanceWalked = (Double) documentSnapshot.getData().get("totalDistanceWalked");       //getting the total distance walked from online db
                Log.d(String.valueOf(totalDistanceWalked), "fetched correctly");
            }
        });

        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("CollectingCoinz", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                recordDistance = (Boolean) (documentSnapshot.getData().get("distanceSwitch"));       //getting the the distance switch from online db - one can turn off the distance recording
                Log.d(String.valueOf(recordDistance), "fetched correctly");
            }
        });

        LoginActivity.firestore_user.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("CollectingCoinz", e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                routeSwitch = (Boolean) (documentSnapshot.getData().get("routeSwitch"));       //getting the the route switch from online db - one can turn off the route showing
                Log.d(String.valueOf(routeSwitch), "fetched correctly");
            }
        });

    }


    public void initializeMap(MapboxMap map){
        for(Feature feat : Objects.requireNonNull(fc.features())){
            Point pt = (Point) feat.geometry();
            JsonObject j = feat.properties();

            //getting each property of the specific coin
            Double longitude = pt.coordinates().get(0);
            Double latitude = pt.coordinates().get(1);
            String currency = String.valueOf(j.get("currency")).substring(1,5);
            String value = String.valueOf(j.get("value")).substring(1, String.valueOf(j.get("value")).length()-1);
            String markerTitle = currency + ": "  + value;
            IconFactory icon1= IconFactory.getInstance(getApplicationContext());
            Icon icon = null;

            //getting the colour of the marker depending on the currency of the coin
            if(currency.equals("SHIL")) {
                icon = icon1.fromResource(R.drawable.blue);
            }
            else if(currency.equals("DOLR")){
                icon = icon1.fromResource(R.drawable.green);
            }
            else if(currency.equals("QUID")){
                icon = icon1.fromResource(R.drawable.yellow);
            }
            else if(currency.equals("PENY")){
                icon = icon1.fromResource(R.drawable.red);
            }


            MarkerViewOptions marker = new MarkerViewOptions().position(new LatLng(latitude, longitude))
                    .title(markerTitle)
                    .icon(icon)
                    .snippet(String.valueOf(j.get("id")).substring(1, String.valueOf(j.get("id")).length()-1));


            //if a marker is in the wallet, it is not plotted
            LoginActivity.firestore_wallet.get()
                .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        Log.d(String.valueOf(ds.get("coinId")), "Coin ID - in CollectingCoinz");
                        Coin c = new Coin((String) ds.get("coinCurrency"),
                                (Double) ds.get("coinValue"),
                                (String) ds.get("coinId"));
                        walletCoinz.add(c);

                    }

                    wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);

                    if(!wallet.contains(marker.getSnippet())) { //if a coin is already in the wallet, it is not added to the map
                        map.addMarker(marker);
                        markers.add(marker);
                    }

                    return Tasks.whenAllSuccess(tasks);
                });

        }

    }

    public void checkCoinCollection(MapboxMap map, Location location){
        Log.d("location has changed", "Location has changed - in CollectingCoinz");
        //whenever the location changes, we are checking to see if the user is within 25 metres of any coin
        for(MarkerViewOptions tempMarker:markers) {
            float[] distance = new float[1];

            Location.distanceBetween(location.getLatitude(), //calculating distance between current location
                    location.getLongitude(), //and each marker
                    tempMarker.getPosition().getLatitude(),
                    tempMarker.getPosition().getLongitude(), distance);
            Log.d(String.valueOf(distance[0]), "distance with coin - in CollectingCoinz");
            Log.d(tempMarker.getSnippet(), "Marker ID");



             LoginActivity.firestore_wallet.get()
                .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        Log.d(String.valueOf(ds.get("coinId")), "Coin ID - in CollectingCoinz");
                        Coin c = new Coin((String) ds.get("coinCurrency"),
                                (Double) ds.get("coinValue"),
                                (String) ds.get("coinId"));
                        walletCoinz.add(c);

                    }

                    wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);


                    if (distance[0] < 25 && !wallet.contains(tempMarker.getSnippet())) {

                        if (MainActivity.mode.equals("Classic")) { //if the mode is classic, then we have a popup asking if the coin should be collected, otherwise it is collected automatically

                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Coin Collection Available\n\n");
                            alertDialog.setMessage("Do you want to collect this coin?" + "\n\n" + tempMarker.getTitle());
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                    (dialog, i) -> {

                                        String[] currency_value = tempMarker.getTitle().split(":");
                                        wallet.addCoinz(currency_value[0], Double.valueOf(currency_value[1]), tempMarker.getSnippet());
                                        Log.d(String.valueOf(wallet.getCoinAmount(currency_value[0])), "coin is collected");

                                        //after a marker is collected by a player, it must be removed from the map
                                        map.removeMarker(tempMarker.getMarker());
                                        markers.remove(tempMarker.getMarker()); //it must also be removed from our marker array list so it is not plotted again
                                        fc.features().remove(tempMarker.getMarker());

                                        dialog.dismiss();
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, i) -> dialog.dismiss());
                            alertDialog.show();
                        } else {


                            String[] currency_value = tempMarker.getTitle().split(":");
                            wallet.addCoinz(currency_value[0], Double.valueOf(currency_value[1]), tempMarker.getSnippet());
                            Log.d(String.valueOf(wallet.getCoinAmount(currency_value[0])), "coin is collected");

                            //after a marker is collected by a player, it must be removed from the map
                            map.removeMarker(tempMarker.getMarker());
                            markers.remove(tempMarker.getMarker()); //it must also be removed from our marker array list so it is not plotted again
                            fc.features().remove(tempMarker.getMarker());


                        }


                        if(recordDistance){
                            if(prevLocation==null){
                                prevLocation = location;
                                Log.d("location", "location created");
                            }
                            //add distance walked every time the location is updated
                            else{
                                Location.distanceBetween(prevLocation.getLatitude(),
                                        prevLocation.getLongitude(),
                                        location.getLatitude(),
                                        location.getLongitude(), distance);

                                totalDistanceWalked += distance[0];
                                Log.d("Distance Updated", totalDistanceWalked.toString());
                                LoginActivity.firestore_user.update("totalDistanceWalked", totalDistanceWalked);
                            }
                        }
                    }

                    return Tasks.whenAllSuccess(tasks);
                });
        }
    }

    public void routeCreator(MapboxMap map, Location location, MapView mapView) {

        //establishing async task connection, to be able to have access to the wallet and the routeSwitch
        LoginActivity.firestore_wallet.get()
                .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        Log.d(String.valueOf(ds.get("coinId")), "Coin ID - in CollectingCoinz");
                        Coin c = new Coin((String) ds.get("coinCurrency"),
                                (Double) ds.get("coinValue"),
                                (String) ds.get("coinId"));
                        walletCoinz.add(c);

                    }

                    if(routeSwitch) {//checking if the user wants the route to be shown

                        wallet = new my_wallet(MainActivity.todaysRates, walletCoinz);

                        float minDistance = 500;
                        Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                        MarkerViewOptions finalMarker = null;

                        for (MarkerViewOptions tempMarker : markers) {
                            float[] distance = new float[1];

                            Location.distanceBetween(location.getLatitude(), //calculating distance between current location
                                    location.getLongitude(), //and each marker
                                    tempMarker.getPosition().getLatitude(),
                                    tempMarker.getPosition().getLongitude(), distance);

                            if (distance[0] < minDistance && !wallet.contains(tempMarker.getSnippet())) {
                                minDistance = distance[0];
                                finalMarker = tempMarker; //finding the closest marker, in order to create route for that specific one
                            }
                        }

                        Point finalPoint = Point.fromLngLat(finalMarker.getPosition().getLongitude(), finalMarker.getPosition().getLatitude());

                        //creating navigation route
                        NavigationRoute.builder()
                                .accessToken("pk.eyJ1Ijoibmlja2ZpbCIsImEiOiJjam55bGRjZHEwZTh1M2xwOWpqdjRjcDhwIn0.FhMCVf5LAlvD7Im8-Xpsvw")
                                .origin(origin)
                                .destination(finalPoint)
                                .profile(DirectionsCriteria.PROFILE_WALKING)
                                .build()
                                .getRoute(new Callback<DirectionsResponse>() {
                                    @Override
                                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                        if (response.body() == null) {
                                            Log.e("NavigationRoute", "No Routes Found - no response");
                                            return;
                                        } else if (response.body().routes().size() == 0) {
                                            Log.e("NavigationRoute", "No Routes Found - size is 0");
                                            return;
                                        }

                                        DirectionsRoute currentRoute = response.body().routes().get(0);

                                        if(navigationMapRoute!=null){
                                            navigationMapRoute.removeRoute();
                                        } else {
                                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                                        }
                                        navigationMapRoute.addRoute(currentRoute);
                                    }

                                    @Override
                                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                                        Log.e("NavigationRoute", "Error:"+t.getMessage());
                                    }
                                });

                    }

                    return Tasks.whenAllSuccess(tasks);
                });
    }


}
