package rv.com.example.user.zomatotestapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import rv.com.example.user.zomatotestapp.CustomAdapters.Select_restaurant_name_popup_custom_adapter;
import rv.com.example.user.zomatotestapp.DatabaseHelper.DatabaseHelper;
import rv.com.example.user.zomatotestapp.R;
import rv.com.example.user.zomatotestapp.RowItems.Select_restaurant_name_popup_RowItem;
import rv.com.example.user.zomatotestapp.ServiceCalls.MakeServiceCall;

/**
 * Created by User on 9/18/2016.
 */
public class SelectSearchPopup extends Activity implements AdapterView.OnItemClickListener {
    ListView listView;
    List<Select_restaurant_name_popup_RowItem> rowItems;
    EditText et_search;
    Select_restaurant_name_popup_custom_adapter adapter;
    String searchtext;
    ProgressDialog pdailog;
    String[] res_id;
    boolean isOkay = false, insert_Status = false;
    String Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_restaurant_name_popup);
        this.setFinishOnTouchOutside(false);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            searchtext = bundle.getString("Searchtext");
        }
        new Fetch_Restaurant_for_Popup().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Select_restaurant_name_popup_RowItem rw = rowItems.get(position);
       /* Toast toast = Toast.makeText(getApplicationContext(),
                rw.getState_id(),
                Toast.LENGTH_SHORT);*/
        /*toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);*/
        //toast.show();
        Intent intent = new Intent();
        intent.putExtra("RestaurantName", rw.getRestaurant_name());
        //intent.putExtra("RestaurantId",rw.getState_id());
        setResult(1, intent);
        finish();//finishing activity

    }

    public class Fetch_Restaurant_for_Popup extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdailog = new ProgressDialog(SelectSearchPopup.this);
            pdailog.setMessage("Syncing Model! Please wait...");
            pdailog.setCancelable(false);
            pdailog.show();

        }

        @Override
        protected String doInBackground(String... params) {


            String url = null;
            try {
                url = "https://developers.zomato.com/api/v2.1/search?q=" + URLEncoder.encode(searchtext, "UTF-8");
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
            return Data;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("modeldata", Data);
            pdailog.dismiss();
            if (Data != null) {
                try {

                    rowItems = new ArrayList<Select_restaurant_name_popup_RowItem>();
                    JSONObject jsonObject1 = new JSONObject(Data);

                    JSONArray jsonArray = jsonObject1.getJSONArray("restaurants");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        String[] res_name = new String[jsonArray.length()];
                        String[] res_locality = new String[jsonArray.length()];
                        res_id = new String[jsonArray.length()];
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        JSONObject jsonObject3 = jsonObject2.getJSONObject("restaurant");
                        res_name[i] = jsonObject3.getString("name");
                        JSONObject jsonObject4 = jsonObject3.getJSONObject("location");
                        res_locality[i] = jsonObject4.getString("locality");

                        Select_restaurant_name_popup_RowItem item = new Select_restaurant_name_popup_RowItem(res_name[i],res_locality[i]);
                        rowItems.add(item);
                    }
                    listView = (ListView) findViewById(R.id.list);
                    adapter = new Select_restaurant_name_popup_custom_adapter(SelectSearchPopup.this,
                            R.layout.select_restauant_name_popup_item_list, rowItems);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            try {
                                DatabaseHelper helper = new DatabaseHelper(SelectSearchPopup.this);

                                SQLiteDatabase db = helper.getWritableDatabase();
                                JSONObject jsonObject1 = new JSONObject(Data);

                                JSONArray jsonArray = jsonObject1.getJSONArray("restaurants");


                                String res_name = "";
                                JSONObject jsonObject2 = jsonArray.getJSONObject(position);
                                JSONObject jsonObject3 = jsonObject2.getJSONObject("restaurant");
                                res_name = jsonObject3.getString("name");
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
                                Log.d("insert", "" + insert_Status);

                                Log.d("name", res_name);
                                Intent intent = new Intent();
                                setResult(1, intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (JSONException e) {
                    pdailog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(SelectSearchPopup.this, e.toString() + "", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    pdailog.dismiss();
                    Toast.makeText(SelectSearchPopup.this, e.toString() + "", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(SelectSearchPopup.this, "Data null", Toast.LENGTH_LONG).show();
            }
        }
    }
}

