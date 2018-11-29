package br.com.spacebox.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import br.com.spacebox.api.model.request.UserRequest;
import br.com.spacebox.api.model.response.UserResponse;
import br.com.spacebox.utils.observer.Observer;

public class SessionManager implements Observer<UserRequest> {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static SessionManager instance;
    private static final String PREF_NAME = "SpaceBoxPreference";
    private static final String IS_LOGIN = "IS_LOGIN";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_TOKEN_TYPE = "KEY_TOKEN_TYPE";
    private static final String KEY_BEGIN_UPDATE_DATE = "KEY_BEGIN_UPDATE_DATE";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_EMAIL = "KEY_EMAIL";

    private SessionManager(Context context) {
        final int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    @Override
    public void update(UserRequest entity) {
        setUserName(entity.getName());
        setUserLogin(entity.getUsername());
        setUserEmail(entity.getEmail());
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null)
            instance = new SessionManager(context);

        return instance;
    }

    public UserResponse getUser() {
        UserResponse user = new UserResponse();
        user.setId(pref.getLong(KEY_ID, 0));
        user.setName(pref.getString(KEY_NAME, null));
        user.setUsername(pref.getString(KEY_USERNAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        return user;
    }

    public void setUser(UserResponse userDetail) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putLong(KEY_ID, userDetail.getId());
        editor.putString(KEY_NAME, userDetail.getName());
        editor.putString(KEY_EMAIL, userDetail.getEmail());
        editor.putString(KEY_USERNAME, userDetail.getUsername());
        editor.commit();
    }

    public void setUserName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void setUserLogin(String login) {
        editor.putString(KEY_USERNAME, login);
        editor.commit();
    }

    public void setUserEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getFullToken() {
        return String.format("%s %s", pref.getString(KEY_TOKEN_TYPE, null), getToken());
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void setToken(String type, String token) {
        editor.putString(KEY_TOKEN_TYPE, type);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void setBeginUpdateDate(Date beginUpdateDate) {
        editor.putLong(KEY_BEGIN_UPDATE_DATE, beginUpdateDate.getTime());
        editor.commit();
    }

    public Date getBeginUpdateDate() {
        Date result = null;
        Long time = pref.getLong(KEY_BEGIN_UPDATE_DATE, 0);
        if (time > 0)
            result = new Date(time);

        return result;
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}