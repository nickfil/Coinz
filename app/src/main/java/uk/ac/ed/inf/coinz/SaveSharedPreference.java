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

    public static void setBankCoin(Context context, String tempId, String coin){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(tempId,coin);
        editor.apply();
    }

    public static void lastSaveDate(Context context, String dt){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("date", dt);
        editor.apply();
    }

    public static void setNumofBankedToday(Context context, int num){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("numofBankedToday", String.valueOf(num));
        editor.apply();
    }

    public static int getNumofBankedToday(Context context){
        return Integer.valueOf(getSharedPreferences(context).getString("numofBankedToday", "0"));
    }

    public static String getLastSaveDate(Context context){
        return getSharedPreferences(context).getString("date", "");
    }

    public static String getUsername(Context context){
        return getSharedPreferences(context).getString(player_username, "");
    }

    public static ArrayList<Coin> getWalletCoin(Context context){
        ArrayList<Coin> savedCoinz = new ArrayList<>();
        HashMap<String, String> coins = (HashMap<String, String>) getSharedPreferences(context).getAll();

        for(String a : coins.keySet()){
            if(a.split(":")[0].equals("walletcoin")){
                Log.d(String.valueOf(a), "Size of saved coins");

                Coin tempcoin = new Coin(coins.get(a).split(":")[0],
                                         Double.valueOf(coins.get(a).split(":")[1]),
                                         coins.get(a).split(":")[2]);
                savedCoinz.add(tempcoin);
                Log.d(String.valueOf(savedCoinz.size()),"coin is added");
            }
        }
        return savedCoinz;
    }

    public static ArrayList<Coin> getBankCoin(Context context){
        ArrayList<Coin> savedCoinz = new ArrayList<>();
        HashMap<String, String> coins = (HashMap<String, String>) getSharedPreferences(context).getAll();

        for(String a : coins.keySet()){
            if(a.split(":")[0].equals("bankcoin")){
                Log.d(String.valueOf(a), "Size of saved coins");

                Coin tempcoin = new Coin(coins.get(a).split(":")[0],
                        Double.valueOf(coins.get(a).split(":")[1]),
                        coins.get(a).split(":")[2]);
                savedCoinz.add(tempcoin);
                Log.d(String.valueOf(savedCoinz.size()),"coin is added");
            }
        }
        return savedCoinz;
    }

    public static void wipeWallet(Context context){
        HashMap<String, String> coins = (HashMap<String, String>) getSharedPreferences(context).getAll();
        ArrayList<String> keystoRemove = new ArrayList<>();

        for(String a : coins.keySet()){
            if(a.split(":")[0].equals("walletcoin")){
                Log.d(String.valueOf(a), "Size of saved coins");
                keystoRemove.add(a);
            }
        }

        for(String key : keystoRemove){
            getSharedPreferences(context).edit().remove(key).commit();
        }
    }

}
