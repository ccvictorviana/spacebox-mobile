package br.com.spacebox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.UserRequest;
import br.com.spacebox.api.model.response.UserResponse;
import br.com.spacebox.ui.base.BaseFragment;
import br.com.spacebox.utils.observer.Observer;
import br.com.spacebox.utils.observer.Subject;

public class UserDataFragment extends BaseFragment implements Subject {
    private List<Observer> observers;
    private UserRequest request;

    public UserDataFragment() {
        observers = new ArrayList<>();
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

    private void onSaveBtn(View v) {
        EditText name = getView().findViewById(R.id.nameText);
        EditText login = getView().findViewById(R.id.loginText);
        EditText email = getView().findViewById(R.id.emailText);

        UserRequest.Builder builder = new UserRequest.Builder();
        builder.withName(name.getText().toString());
        builder.withUserName(login.getText().toString());
        builder.withEmail(email.getText().toString());

        request = builder.build();
        callAPI((cli) -> cli.auth().update(sessionManager.getFullToken(), request), (response) -> {
            toastMessage(R.string.successMessage);
            notifyObservers();
        });
    }

    @Override
    public void register(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (final Observer observer : observers) {
            observer.update(request);
        }
    }
}
