package com.example.vipin.inclass09;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Json2 {
    static public class RecipeJSONParser {
        static ArrayList<ThreadClass> parseRecipes(String in) throws JSONException {
            JSONObject parse = new JSONObject(in);
            ArrayList<ThreadClass> threadArrayList = new ArrayList<>();

            JSONArray threadsarray = parse.getJSONArray("threads");
            for(int i =0 ; i< threadsarray.length();i++){
                JSONObject threadsobject = (JSONObject) threadsarray.get(i);
                ThreadClass threadClass = new ThreadClass();
                threadClass.setUser_id(threadsobject.getString("user_id"));
                threadClass.setUser_lname(threadsobject.getString("user_lname"));
                threadClass.setUser_fname(threadsobject.getString("user_fname"));
                threadClass.setCreated_at(threadsobject.getString("created_at"));
                threadClass.setTitle(threadsobject.getString("title"));
                threadClass.setId(threadsobject.getString("id"));

                threadArrayList.add(threadClass);
            }
            return threadArrayList;
        }
    }
}
