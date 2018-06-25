package pe.edu.sda.sdaasistencia.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    public static void AddStringSharedPreference(Context ctx, String key, String value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("SdaAttendanceSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String GetStringSharedPreference(Context ctx, String key) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("SdaAttendanceSP", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void CleanSharedPreferences(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("SdaAttendanceSP", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
