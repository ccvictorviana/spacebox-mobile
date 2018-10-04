package br.com.spacebox.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.UserRequest;
import br.com.spacebox.ui.base.BaseActivity;

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

        UserRequest.Builder builder = new UserRequest.Builder();
        UserRequest request =
                builder.withName(name.getText().toString())
                        .withUserName(login.getText().toString())
                        .withPassword(password.getText().toString())
                        .withEmail(email.getText().toString()).build();

        callAPI((cli) -> cli.auth().signup(request), (response) -> {
            toastMessage(R.string.userCreatedSuccess);
            startActivity(LoginActivity.class, EXTRA_LOGIN, request.getUsername());
            finish();
        });
    }
}
