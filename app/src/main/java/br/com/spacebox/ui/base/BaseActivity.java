package br.com.spacebox.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.modelmapper.ModelMapper;

import br.com.spacebox.repository.utils.SpaceBoxDatabase;
import br.com.spacebox.repository.utils.SpaceBoxDatabaseCreate;
import br.com.spacebox.sessions.SessionManager;

public abstract class BaseActivity extends AppCompatActivity implements IBaseCommon {
    protected ModelMapper modelMapper;
    protected SpaceBoxDatabase database;
    protected SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelMapper = new ModelMapper();
        database = SpaceBoxDatabaseCreate.getInstance(getApplicationContext()).getDataBase();
        sessionManager = SessionManager.getInstance(getApplicationContext());
    }

    protected void startActivity(Class<?> cls) {
        Intent myIntent = new Intent(this, cls);
        super.startActivity(myIntent);
    }

    protected void startActivity(Class<?> cls, String key, String value) {
        Intent myIntent = new Intent(this, cls);
        myIntent.putExtra(key, value);
        super.startActivity(myIntent);
    }

    @Override
    public Activity getCommonActivity() {
        return this;
    }

    @Override
    public Context getCommonContext() {
        return getApplicationContext();
    }
}
