package uk.ac.ed.inf.coinz;

import android.location.Location;
import android.util.Log;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class CollectingCoinz {

    private ArrayList<MarkerViewOptions> markers = new ArrayList<MarkerViewOptions>();
    private FeatureCollection fc;

    public CollectingCoinz(FeatureCollection fc){
        this.fc = fc;
    }

    public void initializeMap(MapboxMap map){
        for(Feature feat : fc.features()){
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


            map.addMarker(marker);
            markers.add(marker);

        }
    }

    public void checkCoinCollection(MapboxMap map, Location location, my_wallet wallet){
        Log.d("location has changed", "Location has changed");
        //whenever the location changes, we are checking to see if the user is within 25 metres of any coin
        for(MarkerViewOptions tempMarker:markers){
            float[] distance = new float[1];

            Location.distanceBetween(location.getLatitude(), //calculating distance between current location
                                    location.getLongitude(), //and each marker
                                    tempMarker.getPosition().getLatitude(),
                                    tempMarker.getPosition().getLongitude(), distance);
            Log.d(String.valueOf(distance[0]),"distance with coin");

            if(distance[0]<25 &&  !wallet.getCoinIDs().contains(tempMarker.getSnippet())){

                String[] currency_value = tempMarker.getTitle().split(":");
                wallet.addCoinz(currency_value[0], Double.valueOf(currency_value[1]), tempMarker.getSnippet());
                Log.d(String.valueOf(wallet.getCoinAmount(currency_value[0])), "coin is collected");

                //after a marker is collected by a player, it must be removed from the map
                MarkerView currentmarker = tempMarker.getMarker();
                map.removeMarker(currentmarker);
                markers.remove(currentmarker); //it must also be removed from our marker array list so it is not plotted again
                fc.features().remove(currentmarker);
            }
        }
    }


}
