package com.example.vipin.inclass09;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.media.session.MediaSessionCompat;
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

import com.example.vipin.inclass09.ChatRoomMessageAdapter;
import com.example.vipin.inclass09.ChatRoomThreadResponse;
import com.example.vipin.inclass09.ChatRoomThreadUtil;
import com.example.vipin.inclass09.ThreadClass;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatroomActivity extends AppCompatActivity {
    ArrayList<ChatRoomThreadResponse> responseList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    LinearLayoutManager layoutManager;
    String token = "";
    String user_id;
    Button button;
    EditText editTextmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewChatroom);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        final String id = getIntent().getStringExtra("chatRoomObject");

        String name = getIntent().getStringExtra("Threadname");
        editTextmessage = (EditText) findViewById(R.id.editTextMessage);
        SharedPreferences settings = this.getSharedPreferences("MyPref",0);
        token = settings.getString("token",null);
        user_id = settings.getString("user_id",null);

        TextView chatroomname = (TextView) findViewById(R.id.textViewChatroomThreadName);
        chatroomname.setText(name);
        button = (Button) findViewById(R.id.imageViewSendMessage);
        new GetAsyncTask().execute();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("message", editTextmessage.getText().toString())
                        .add("thread_id",id)
                        .build();
                editTextmessage.setText("");
                Request request = new Request.Builder().url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/add").addHeader("Authorization", "BEARER " + token).post(formBody).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        try {
                            ChatRoomThreadResponse thread = new Json4.RecipeJSONParser().parseRecipes(response.body().string());
                            Log.d("demo2", thread.toString());
                            responseList.add(thread);
                            new GetAsyncTask().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }


    public void updateData(ArrayList<ChatRoomThreadResponse> responseList) {
        Log.d("responseData",responseList.size()+"");
        for(int i=0;i<responseList.size();i++){
            if(responseList.get(i).getUser_id().matches(user_id)){
                responseList.get(i).setImage("0");
            }
            else{
                responseList.get(i).setImage("4");

            }
        }
        Collections.reverse(responseList);
        mAdapter = new ChatRoomMessageAdapter(responseList, ChatroomActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void removemessage(View view) {
        LinearLayout parentrow = (LinearLayout) view.getParent();
        ImageView favImage = (ImageView) parentrow.getChildAt(1);
        int position = (int) favImage.getTag();
        ChatRoomThreadResponse track = responseList.get(position);
        Toast.makeText(getApplicationContext(),"Deleted message: " + responseList.get(position).getMessage(),Toast.LENGTH_SHORT).show();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/delete/" + responseList.get(position).getId()).addHeader("Authorization", "BEARER " + token).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                new GetAsyncTask().execute();
            }
        });

    }


    public class GetAsyncTask extends AsyncTask<String, Void, String> {
        BufferedReader reader = null;
        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/messages/" +getIntent().getStringExtra("chatRoomObject"))
                    .addHeader("Authorization","BEARER "+ token)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();  //its synchronous task
                responseList  =  ChatRoomThreadUtil.MusicJSONParser.parseTracks(response.body().string());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateData(responseList);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
