package br.com.spacebox.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import br.com.spacebox.R;
import br.com.spacebox.ui.DashboardFragment;

public class MasterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        addListenerNavigation();
    }

    private void addListenerNavigation() {
        goToFragment(R.string.dashboardTitle, DashboardFragment.newInstance());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (!item.isChecked()) {
                switch (item.getItemId()) {
                    case R.id.dashboardMI:
                        goToFragment(R.string.dashboardTitle, DashboardFragment.newInstance());
                        break;
                    case R.id.userDataMI:
                        goToFragment(R.string.userDataTitle, UserDataFragment.newInstance());
                        break;
                    case R.id.notificationsMI:
                        goToFragment(R.string.notificationsTitle, NotificationFragment.newInstance());
                        break;
                    case R.id.exitMI:
                        exit();
                        break;
                }
            }

            return true;
        });
    }

    private void goToFragment(int title, Fragment fragment) {
        setTitle(getString(title));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void exit() {
        callAPI((cli) -> cli.auth().logout(sessionManager.getFullToken()), (voids) -> {
            startActivity(LoginActivity.class);
        });
    }
}