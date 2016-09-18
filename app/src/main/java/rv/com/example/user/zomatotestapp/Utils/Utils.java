package rv.com.example.user.zomatotestapp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rv.com.example.user.zomatotestapp.R;

/**
 * Created by User on 9/18/2016.
 */
public class Utils
{

    public static boolean is_Connected_To_Internet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {

                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isConnectedToGps(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static String getFullAddress(double lat, double lnt, Activity activity) {
        String fullAddress = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

        String address = " Enable to find Address", city = "", state = "", country = "", zip = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lnt, 1);
            if (addresses != null) {

                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                zip = addresses.get(0).getPostalCode();
                country = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fullAddress = address + " " + city + " " + state + " " + country + " " + zip;
        return fullAddress;
    }

    public static void showSettingsAlert(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS  settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(activity, "Need GPS to work properly!", Toast.LENGTH_LONG).show();
                activity.finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

}
