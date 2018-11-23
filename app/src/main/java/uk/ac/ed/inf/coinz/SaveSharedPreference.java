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

    public static String getLastSaveDate(Context context){
        return getSharedPreferences(context).getString("date", "");
    }

    public static void setNumofBankedToday(Context context, int num){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("numofBankedToday", String.valueOf(num));
        editor.apply();
    }

    public static int getNumofBankedToday(Context context){
        return Integer.valueOf(getSharedPreferences(context).getString("numofBankedToday", "0"));
    }


}
