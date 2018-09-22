package br.com.spacebox.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.LoginRequest;
import br.com.spacebox.api.model.request.UserRequest;

public class RegisterActivity extends BaseActivity {
    public static final String EXTRA_LOGIN = "EXTRA_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_register);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void registerUser(View v) {
        EditText name = findViewById(R.id.nameText);
        EditText login = findViewById(R.id.loginText);
        EditText email = findViewById(R.id.emailText);
        EditText password = findViewById(R.id.passwordText);

        UserRequest request = new UserRequest();
        request.setUsername(login.getText().toString());
        request.setName(name.getText().toString());
        request.setEmail(email.getText().toString());
        request.setPassword(password.getText().toString());

        callAPI((cli) -> cli.auth().signup(request), (response) -> {
            toastMessage(R.string.userCreatedSuccess);
            startActivity(LoginActivity.class, EXTRA_LOGIN, request.getUsername());
        });
    }
}
