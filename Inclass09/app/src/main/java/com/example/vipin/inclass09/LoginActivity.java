package com.example.vipin.inclass09;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signup = (Button) findViewById(R.id.button2);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

            Button login = (Button) findViewById(R.id.button);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView email = (TextView) findViewById(R.id.email);
                    TextView password = (TextView) findViewById(R.id.password);

                    if (!email.getText().toString().matches("") && !password.getText().toString().matches("")) {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("email", email.getText().toString())
                                .add("password", password.getText().toString())
                                .build();
                        Request request = new Request.Builder().url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/login").post(formBody).build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final Parseclass user_data = new Json.RecipeJSONParser().parseRecipes(response.body().string());
                                    Log.d("demo1", user_data.toString());
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();
                                    if (user_data.getStatus().matches("error")) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                                            @Override
                                            public void run() {
                                            Toast.makeText(getApplicationContext(), user_data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        editor.putString("token", user_data.getToken());
                                        editor.putString("user_id", user_data.getUser_id());
                                        editor.commit();
                                        Intent intent = new Intent(LoginActivity.this, MessageThread.class);
                                        intent.putExtra("name", user_data.getUser_fname() + " " + user_data.getUser_lname());
                                        startActivity(intent);
                                        finish();

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Fields should not be left empty", Toast.LENGTH_SHORT).show();
                    }


                }


            });
    }
}
