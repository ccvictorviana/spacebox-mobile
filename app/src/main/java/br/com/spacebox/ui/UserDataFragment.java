package br.com.spacebox.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.LoginRequest;
import br.com.spacebox.api.model.request.UserRequest;
import br.com.spacebox.api.model.response.UserResponse;

public class UserDataFragment extends BaseFragment {
    public static UserDataFragment newInstance() {
        return new UserDataFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userdata, container, false);
        view.findViewById(R.id.saveBtn).setOnClickListener(this::onSaveBtn);

        TextView name = view.findViewById(R.id.nameText);
        TextView login = view.findViewById(R.id.loginText);
        TextView email = view.findViewById(R.id.emailText);

        UserResponse userData = sessionManager.getUser();
        name.setText(userData.getName());
        login.setText(userData.getUsername());
        email.setText(userData.getEmail());

        return view;
    }

    public void onSaveBtn(View v) {
        EditText name = getView().findViewById(R.id.nameText);
        EditText login = getView().findViewById(R.id.loginText);
        EditText email = getView().findViewById(R.id.emailText);

        UserRequest request = new UserRequest();
        request.setName(name.getText().toString());
        request.setUsername(login.getText().toString());
        request.setEmail(email.getText().toString());

        callAPI((cli) -> cli.auth().update(sessionManager.getFullToken(), request), (response) -> {
            sessionManager.setUserName(request.getName());
            sessionManager.setUserLogin(request.getUsername());
            sessionManager.setUserEmail(request.getEmail());
            toastMessage(R.string.successMessage);
        });
    }
}
