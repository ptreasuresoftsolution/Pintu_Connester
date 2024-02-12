package com.connester.job.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kishan on 09-Aug-17.
 */

public class SessionPref {

    public static final String USER_PREFS = "Connester_PREFS";
    public static SharedPreferences appSharedPref;
    public static SharedPreferences.Editor prefEditor;
    public final String FIREBASE_ID = "FIREBASE_ID";
    public final String DEVICE_TOKEN = "TOKEN";
    public final String DEVICE_ID = "DEVICE_ID";

    public SessionPref(Context context) {
        appSharedPref = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
    }

    public  String IsLogin = "IsLogin";
    public  final String LoginTime = "LoginTime";

    public  final String UserMasterRow = "UserMasterRow";
    public  final String UserMasterId = "UserMasterId";
    public  final String UserFullName = "UserFullName";
    public  final String UserName = "UserName";
    public  final String UserEmail = "UserEmail";
    public  final String UserPassword = "UserPassword";
    public  final String UserProfilePic = "UserProfilePic";

    public static String INTRO_VIEW = "intro_view";
    public static final String AC_LINK = "ac_link";

    public String getString(String key) {
        return appSharedPref.getString(key, "");
    }

    public void setString(String key, String value) {
        prefEditor = appSharedPref.edit();
        prefEditor.putString(key, value).apply();
    }
    public void removeKey(String key) {
        prefEditor = appSharedPref.edit();
        prefEditor.remove(key).apply();
    }

    public boolean getBoolean(String key) {
        return appSharedPref.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean val) {
        prefEditor = appSharedPref.edit();
        prefEditor.putBoolean(key, val);
        prefEditor.commit();
    }
    public void setJson(String key, ArrayList<String> value) {
        prefEditor = appSharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefEditor.putString(key, json);
        prefEditor.commit();
    }
    public ArrayList<String> getJson(String key) {
        Gson gson = new Gson();
        String json = appSharedPref.getString(key, "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public void setHashMapJson(String key, String assocJson) {
        prefEditor = appSharedPref.edit();
        prefEditor.putString(key, assocJson);
        prefEditor.commit();
    }
    public void setHashMapJson(String key, HashMap<String,String> value) {
        prefEditor = appSharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefEditor.putString(key, json);
        prefEditor.commit();
    }
    public HashMap<String,String> getHashMapJson(String key) {
        Gson gson = new Gson();
        String json = appSharedPref.getString(key, "");
        Type type = new TypeToken<HashMap<String,String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
    public boolean isIntroView() {
        return appSharedPref.getBoolean(INTRO_VIEW, false);
    }

    public void setIntroView(boolean isView) {
        prefEditor = appSharedPref.edit();
        prefEditor.putBoolean(INTRO_VIEW, isView).apply();
    }

    public void setAcLink(String link) {
        prefEditor = appSharedPref.edit();
        prefEditor.putString(AC_LINK, link).apply();
    }

    public String getAcLink() {
        return appSharedPref.getString(AC_LINK,"https://play.google.com/store/apps");
    }

    public boolean isLogin() {
        return getBoolean(IsLogin);
    }

    public void setIsLogin(boolean isLogin) {
        setBoolean(IsLogin,isLogin);
    }

    public void setLoginTime(String dateTime) {
        setString(LoginTime,dateTime);
    }
    public String getLoginTime() {
        return getString(LoginTime);
    }

    public void setUserMasterId(String userMasterId) {
        setString(UserMasterId,userMasterId);
    }
    public String getUserMasterId() {
        return getString(UserMasterId);
    }

    public void setUserFullName(String userFullName) {
        setString(UserFullName,userFullName);
    }
    public String getUserFullName() {
        return getString(UserFullName);
    }

    public void setUserName(String userName) {
        setString(UserName,userName);
    }
    public String getUserName() {
        return getString(UserName);
    }

    public void setUserEmail(String userEmail){
        setString(UserEmail,userEmail);
    }
    public String getUserEmail() {
        return getString(UserEmail);
    }

    public void setUserPassword(String userPassword){//encrypted store
        setString(UserPassword,userPassword);
    }
    public String getUserPassword() {
        return getString(UserPassword);
    }
    public String getTextUserPassword(){ //decrypt password base64_decode(base64_decode($val));
        String encPass = getString(UserPassword);
        return new String(Base64.decode(Base64.decode(encPass,Base64.DEFAULT),Base64.DEFAULT));
    }

    public void setUserProfilePic(String userProfilePic){
        setString(UserProfilePic,userProfilePic);
    }
    public String getUserProfilePic() {
        return getString(UserProfilePic);
    }

    public void setFIREBASE_ID(String firebaseId){
        setString(FIREBASE_ID,firebaseId);
    }
    public String getFIREBASE_ID() {
        return getString(FIREBASE_ID);
    }

    public void setDEVICE_TOKEN(String deviceToken){
        setString(DEVICE_TOKEN,deviceToken);
    }
    public String getDEVICE_TOKEN() {
        return getString(DEVICE_TOKEN);
    }

    public void setDEVICE_ID(String deviceId){
        setString(DEVICE_ID,deviceId);
    }
    public String getDEVICE_ID() {
        return getString(DEVICE_ID);
    }

    public void setUserMasterRow(String assocJsonUserMasterRow){
        setHashMapJson(UserMasterRow,assocJsonUserMasterRow);
    }
    public HashMap<String,String> getUserMasterRow(){
        return getHashMapJson(UserMasterRow);
    }
    public void logOutPref() {
        removeKey(IsLogin);
        removeKey(UserMasterId);
        removeKey(UserName);
        removeKey(UserFullName);
        removeKey(UserEmail);
        removeKey(UserPassword);
        removeKey(UserProfilePic);
        removeKey(UserMasterRow);
    }

}
