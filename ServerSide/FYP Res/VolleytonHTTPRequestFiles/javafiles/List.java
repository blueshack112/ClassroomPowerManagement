package com.ascomp.database_prac;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class List extends AppCompatActivity {
    RecyclerView rec_list;
    ListAdapter adapter;
    ArrayList<Model> data;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        rec_list=findViewById(R.id.rec_list);
        data=new ArrayList<Model>();
        adapter =new ListAdapter(data, this);
        linearLayoutManager=new LinearLayoutManager(this);
        rec_list.setLayoutManager(linearLayoutManager);
        rec_list.setAdapter(adapter);

        String url="http://192.168.10.17/android/getlist.php";

        Response.Listener listener=new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject object= new JSONObject(response.toString());
                    boolean status=object.getBoolean("status");
                    if (status==true) {
                        JSONArray array = object.getJSONArray("data");
                        for (int i=0; i<array.length(); i++){
                            String id=array.getJSONObject(i).getString("id");
                            String title=array.getJSONObject(i).getString("title");
                            data.add( new Model(id,title));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("---", "Error");
            }
        };

        StringRequest request= new StringRequest(Request.Method.POST,url,listener,errorListener);
        Volleyton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
}
