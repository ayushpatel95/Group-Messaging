package com.example.vipin.inclass09;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Json4 {
    static public class RecipeJSONParser {
        static ChatRoomThreadResponse parseRecipes(String in) throws JSONException {
            JSONObject parse = new JSONObject(in);
            ChatRoomThreadResponse threadClass = new ChatRoomThreadResponse();
            threadClass.setStatus(parse.getString("status"));
            JSONObject thread = parse.getJSONObject("message");
            if (parse.getString("status").matches("ok")) {
                threadClass.setUser_id(thread.getString("user_id"));
                threadClass.setUser_lname(thread.getString("user_lname"));
                threadClass.setUser_fname(thread.getString("user_fname"));
                threadClass.setCreated_at(thread.getString("created_at"));
                threadClass.setMessage(thread.getString("message"));
                threadClass.setId(thread.getString("id"));

            }
            return threadClass;
        }
    }
}
