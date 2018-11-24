package uk.ac.ed.inf.coinz;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setDate(Context context, String dt){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("DateToday", dt);
        editor.apply();
    }

    public static void setUIDtoSend(Context context, String UID){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("UID", UID);
        editor.apply();
    }

    public static String getLastSaveDate(Context context){
        return getSharedPreferences(context).getString("DateToday", "");
    }

    public static void setNumofBankedToday(Context context, int num, String email){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(email+":"+"numofBankedToday", String.valueOf(num));
        editor.apply();
    }

    public static String getNumofBankedToday(Context context, String email){
        return getSharedPreferences(context).getString(email+":"+"numofBankedToday", "0");
    }


}
