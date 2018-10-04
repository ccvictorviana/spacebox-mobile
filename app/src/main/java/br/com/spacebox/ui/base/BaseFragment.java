package br.com.spacebox.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.modelmapper.ModelMapper;

import br.com.spacebox.R;
import br.com.spacebox.repository.utils.SpaceBoxDatabase;
import br.com.spacebox.repository.utils.SpaceBoxDatabaseCreate;
import br.com.spacebox.sessions.SessionManager;

public class BaseFragment extends Fragment implements IBaseCommon {
    protected ModelMapper modelMapper;
    protected SpaceBoxDatabase database;
    protected SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelMapper = new ModelMapper();
        database = SpaceBoxDatabaseCreate.getInstance(getContext()).getDataBase();
        sessionManager = SessionManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return mContentView;
    }

    @Override
    public Activity getCommonActivity() {
        return getActivity();
    }

    @Override
    public Context getCommonContext() {
        return getContext();
    }
}
