package rv.com.example.user.zomatotestapp.ServiceCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import rv.com.example.user.zomatotestapp.DatabaseHelper.DatabaseHelper;
import rv.com.example.user.zomatotestapp.ServiceCalls.MakeServiceCall;

/**
 * Created by User on 9/17/2016.
 */
public class SearchApi extends AsyncTask<String, String, String> {
    Activity activity;
    String search, Data;
    ProgressDialog pdailog;
    boolean isOkay = false, insert_Status = false;

    SearchApi(Activity activity, String search) {
        this.activity = activity;
        this.search = search;
    }

    @Override
    protected String doInBackground(String... params) {

        String url = null;
        try {
            url = "https://developers.zomato.com/api/v2.1/search?q=" + URLEncoder.encode(search, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Data = new MakeServiceCall().makeServiceCall(url);
            isOkay = true;
        } catch (IOException e) {
            e.printStackTrace();
            isOkay = false;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdailog = new ProgressDialog(activity);
        pdailog.setMessage("Searching Restaurants! Please wait...");
        pdailog.setCancelable(false);
        pdailog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pdailog.dismiss();
        if (isOkay) {
            if (Data != null && !Data.equalsIgnoreCase("")) {
                DatabaseHelper helper = new DatabaseHelper(activity);

                SQLiteDatabase db = helper.getWritableDatabase();
                try {


                    JSONObject jsonObject1 = new JSONObject(Data);

                    JSONArray jsonArray = jsonObject1.getJSONArray("restaurants");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                        JSONObject jsonObject3 = jsonObject2.getJSONObject("restaurant");
                        String res_name = jsonObject3.getString("name");
                        String site_url = jsonObject3.getString("url");
                        String res_id = jsonObject3.getString("id");
                        JSONObject jsonObject4 = jsonObject3.getJSONObject("location");
                        String address = jsonObject4.getString("address");
                        String locality = jsonObject4.getString("locality");
                        String latitude = jsonObject4.getString("latitude");
                        String longitude = jsonObject4.getString("longitude");

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseHelper.RESTAURANT_ID, res_id);
                        contentValues.put(DatabaseHelper.RESTAURANT_NAME, res_name);
                        contentValues.put(DatabaseHelper.SITE_URL, site_url);
                        contentValues.put(DatabaseHelper.ADDRESS, address);
                        contentValues.put(DatabaseHelper.LOCALITY, locality);
                        contentValues.put(DatabaseHelper.LATITUDE, latitude);
                        contentValues.put(DatabaseHelper.LONGITUDE, longitude);

                        insert_Status = db.insert(DatabaseHelper.RESTAURANT_TABLE, null, contentValues) > 0;
                        Log.d("insert",""+insert_Status);
                        activity.recreate();
                    }
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    db.close();
                }
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

}



