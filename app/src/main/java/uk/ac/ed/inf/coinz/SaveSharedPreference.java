package uk.ac.ed.inf.coinz;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    private static final String tag = "SaveSharedPreference";
    static final String player_username = "username";

    static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUsername(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(player_username, "username");
        editor.apply();
    }

    public static String getUsername(Context context){
        return getSharedPreferences(context).getString(player_username, null);
    }
}
