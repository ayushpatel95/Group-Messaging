package com.example.vipin.inclass09;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Json {
    static public class RecipeJSONParser {
        static Parseclass parseRecipes(String in) throws JSONException {
            JSONObject parse = new JSONObject(in);
            Parseclass parseclass = new Parseclass();
            parseclass.setStatus(parse.getString("status"));

            if(parse.getString("status").matches("ok")){
                parseclass.setToken(parse.getString("token"));
                parseclass.setUser_email(parse.getString("user_email"));
                parseclass.setUser_fname(parse.getString("user_fname"));
                parseclass.setUser_lname(parse.getString("user_lname"));
                parseclass.setUser_role(parse.getString("user_role"));
                parseclass.setUser_id(parse.getString("user_id"));

            }
            else{
                parseclass.setMessage(parse.getString("message"));
            }
            /*if(parse.getString("token") != null) {
                }
            if(parse.getString("user_email") != null) {
            }
            if(parse.getString("user_fname") != null) {
            }
            if(parse.getString("user_lname") != null) {
            }
            if(parse.getString("user_role") != null) {
            }

            if(parse.getString("user_id") != null) {
            }
*/
            return parseclass;
        }
    }
}
