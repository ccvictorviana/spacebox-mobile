package br.com.spacebox.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.LoginRequest;

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().hasExtra(RegisterActivity.EXTRA_LOGIN)) {
            EditText username = findViewById(R.id.loginText);
            username.setText(getIntent().getStringExtra(RegisterActivity.EXTRA_LOGIN));
        }
    }

    public void onLoginClickBtn(View v) {
        EditText username = findViewById(R.id.loginText);
        EditText password = findViewById(R.id.passwordText);

        LoginRequest request = new LoginRequest();
        request.setUsername(username.getText().toString());
        request.setPassword(password.getText().toString());
        callAPI((cli) -> cli.auth().login(request), (response) -> {
            sessionManager.setToken(response.getType(), response.getToken());
            callAPI((cli) -> cli.auth().detail(sessionManager.getFullToken()), (userData) -> {
                sessionManager.setUser(userData);
                startActivity(MasterActivity.class);
            });
        });
    }

    public void onClickRegisterBtn(View v) {
        startActivity(RegisterActivity.class);
    }
}
