package com.example.vipin.inclass09;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.vipin.inclass09.R.id.email;

public class MessageThread extends AppCompatActivity {

    ArrayList<ThreadClass> threadArrayList;
    MyAdapter adapter;
    String token;
    String user_id;
    RecyclerView rcv;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);
        threadArrayList = new ArrayList<ThreadClass>();
        TextView textView = (TextView) findViewById(R.id.textViewUserName);
        textView.setText(getIntent().getStringExtra("name"));
        SharedPreferences settings = this.getSharedPreferences("MyPref",0);
        token = settings.getString("token",null);
        ImageView logout = (ImageView) findViewById(R.id.imageViewLogout);
        user_id = settings.getString("user_id",null);
        new async().execute();



        final EditText newthread = (EditText) findViewById(R.id.editTextNewThread);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                finish();
                Intent intent = new Intent(MessageThread.this,LoginActivity.class);
                startActivity(intent);

            }
        });
        ImageView imageview = (ImageView) findViewById(R.id.imageViewAdd);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("title", newthread.getText().toString())
                        .build();
                newthread.setText("");
                Request request = new Request.Builder().url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/add").addHeader("Authorization", "BEARER " + token).post(formBody).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        try {
                            ThreadClass thread = new Json3.RecipeJSONParser().parseRecipes(response.body().string());
                            Log.d("demo2", thread.toString());
                            threadArrayList.add(thread);
                            new async().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });


    }
    public void remove(View view){
        LinearLayout parentrow = (LinearLayout) view.getParent();
        ImageView favImage = (ImageView) parentrow.getChildAt(1);
        int position = (int) favImage.getTag();
        ThreadClass track = threadArrayList.get(position);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/delete/" + threadArrayList.get(position).getId()).addHeader("Authorization", "BEARER " + token).build();
        Toast.makeText(getApplicationContext(),"Deleted message thread: "+ threadArrayList.get(position).getTitle(),Toast.LENGTH_SHORT).show();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                new async().execute();
            }
        });

    }

    public void add(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        Log.d("yes","yes"+position);
        ThreadClass messageThreadResponse = threadArrayList.get(position);
        Intent i = new Intent(MessageThread.this, ChatroomActivity.class);
        i.putExtra("chatRoomObject",messageThreadResponse.getId());
        i.putExtra("Threadname",messageThreadResponse.getTitle());
        startActivity(i);

    }

    public class async extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            rcv = (RecyclerView) findViewById(R.id.recyclerViewThread);
            for(int i=0;i<threadArrayList.size();i++){
                if(threadArrayList.get(i).getUser_id().matches(user_id)){
                    threadArrayList.get(i).setImage("0");
                }
                else{
                    threadArrayList.get(i).setImage("4");

                }
            }
            final LinearLayoutManager mLayoutManager =  new LinearLayoutManager(MessageThread.this);
            rcv.setLayoutManager(mLayoutManager);
            adapter = new MyAdapter(threadArrayList,MessageThread.this,user_id,token);
            rcv.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread").addHeader("Authorization", "BEARER " + token).build();
            try {

                Response response = client.newCall(request).execute();
                threadArrayList = new Json2.RecipeJSONParser().parseRecipes(response.body().string());
                Log.d("demo1", threadArrayList.get(1).toString());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
        }
    }


