package br.com.spacebox.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import br.com.spacebox.R;

public abstract class LoggedBaseActivity extends BaseActivity {

    protected BottomNavigationView navigation;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        drawerLayout = findViewById(R.id.drawerLayout);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addListenerNavigationView();
    }

//    protected View getViewHeader() {
//        View result = null;
//        NavigationView spaceBoxNV = findViewById(R.id.spaceBoxNV);
//        if (spaceBoxNV != null)
//            result = spaceBoxNV.getHeaderView(0);
//
//        return result;
//    }

    private void addListenerNavigationView() {
        final AppCompatActivity _this = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        navigation = findViewById(R.id.navigationView);
//        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                Intent i;
//                switch (id) {
//                    case R.id.dashboardMI:
//                        i = new Intent(_this, DashboardFragment.class);
//                        startActivity(i);
//                        break;
//                    case R.id.userDataMI:
//                        i = new Intent(_this, UserDataFragment.class);
//                        startActivity(i);
//                        break;
//                    case R.id.notificationsMI:
//                        i = new Intent(_this, NotificationFragment.class);
//                        startActivity(i);
//                        break;
//                    case R.id.uploadMI:
//                        i = new Intent(_this, UploadFragment.class);
//                        startActivity(i);
//                        break;
//                    case R.id.sigoutMI:
//                        i = new Intent(_this, LoginActivity.class);
//                        startActivity(i);
//                        break;
//                }
//                return false;
//            }
//        });

//        navigation.setOnNavigationItemSelectedListener
//                (new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        Fragment selectedFragment = null;
//                        switch (item.getItemId()) {
//                            case R.id.action_item1:
//                                selectedFragment = ItemOneFragment.newInstance();
//                                break;
//                            case R.id.action_item2:
//                                selectedFragment = ItemTwoFragment.newInstance();
//                                break;
//                            case R.id.action_item3:
//                                selectedFragment = ItemThreeFragment.newInstance();
//                                break;
//                        }
//                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame_layout, selectedFragment);
//                        transaction.commit();
//                        return true;
//                    }
//                });
//
//        //Manually displaying the first fragment - one time only
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, ItemOneFragment.newInstance());
//        transaction.commit();
    }
}
