package br.com.spacebox.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import br.com.spacebox.R;
import br.com.spacebox.ui.base.BaseActivity;
import br.com.spacebox.ui.utils.EFragmentType;
import br.com.spacebox.ui.utils.FragmentFactory;
import br.com.spacebox.utils.observer.Subject;

public class MasterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        addListenerNavigation();
    }

    private void addListenerNavigation() {
        goToFragment(R.string.dashboardTitle, FragmentFactory.getFragment(EFragmentType.DASHBOARD_FRAGMENT));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (!item.isChecked()) {
                switch (item.getItemId()) {
                    case R.id.dashboardMI:
                        goToFragment(R.string.dashboardTitle, FragmentFactory.getFragment(EFragmentType.DASHBOARD_FRAGMENT));
                        break;
                    case R.id.notificationsMI:
                        goToFragment(R.string.notificationsTitle, FragmentFactory.getFragment(EFragmentType.NOTIFICATION_FRAGMENT));
                        break;
                    case R.id.userDataMI:
                        Fragment fragment = FragmentFactory.getFragment(EFragmentType.USERDATA_FRAGMENT);
                        ((Subject) fragment).register(sessionManager);
                        goToFragment(R.string.userDataTitle, fragment);
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
            finish();
        });
    }
}