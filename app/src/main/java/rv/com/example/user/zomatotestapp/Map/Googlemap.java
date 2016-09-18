package rv.com.example.user.zomatotestapp.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import rv.com.example.user.zomatotestapp.R;
import rv.com.example.user.zomatotestapp.Utils.Utils;

/**
 * Created by User on 9/18/2016.
 */
public class Googlemap extends ActionBarActivity {
    private GoogleMap googleMap;
    String userlat, userlng, res_name;
    LatLng userloc;

    boolean onPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        // calling init method to for initialization
        Intent i = getIntent();
        if (i != null) {
            userlat = i.getStringExtra("lat");
            userlng = i.getStringExtra("long");
            res_name = i.getStringExtra("res_name");
            if (Utils.is_Connected_To_Internet(Googlemap.this)) {
                init();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            }


        }
    }

    private void init() {
        if (Utils.isConnectedToGps(Googlemap.this)) {

            //initializing google maps & throwing error if failed to do so.
            if (googleMap == null) {

                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


                googleMap.setMyLocationEnabled(true);

                getuserlocation();

                if (googleMap == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't initialize google maps ! try again.", Toast.LENGTH_LONG).show();
                }
            }
        } else {

            Utils.showSettingsAlert(Googlemap.this);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onPause) {

            init();
            onPause = false;
        } else {
            onPause = false;
        }

    }

    protected void onPause() {
        super.onPause();
        onPause = true;
    }

    public void getuserlocation() {
        //if the user location is not available try to obtain it once we get it update it using main ui thread
        if (!userlat.equalsIgnoreCase("0.0") && !userlng.equalsIgnoreCase("0.0")) {
            userloc = new LatLng(Double.parseDouble(userlat), Double.parseDouble(userlng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userloc, 12));
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(userlat), Double.parseDouble(userlng)))
                    .title(res_name));
        }
    }


}