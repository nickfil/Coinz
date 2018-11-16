package uk.ac.ed.inf.coinz;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveSharedPreference {
    private static final String tag = "SaveSharedPreference";
    static final String player_username = "username";
    static final String coin1 = "coin";

    static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUsername(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(player_username, username);
        editor.apply();
    }

    public static void setWalletCoin(Context context, String tempId, String coin){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(tempId,coin);
        editor.apply();
    }

    public static String getUsername(Context context){
        return getSharedPreferences(context).getString(player_username, "");
    }

    public static ArrayList<Coin> getWalletCoin(Context context){
        ArrayList<Coin> returns = new ArrayList<>();
        HashMap<String, String> coins = (HashMap<String, String>) getSharedPreferences(context).getAll();

        for(String a : coins.keySet()){
            if(a.split(":")[0].equals("walletcoin")){
                Log.d(String.valueOf(a), "Size of saved coins");

                Coin tempcoin = new Coin(coins.get(a).split(":")[0],
                                         Double.valueOf(coins.get(a).split(":")[1]),
                                         coins.get(a).split(":")[2]);
                returns.add(tempcoin);
                Log.d(String.valueOf(returns.size()),"coin is added");
            }
        }
        return returns;
    }
}
