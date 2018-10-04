package br.com.spacebox.ui.utils;

import br.com.spacebox.ui.DashboardFragment;
import br.com.spacebox.ui.NotificationFragment;
import br.com.spacebox.ui.UserDataFragment;
import br.com.spacebox.ui.base.BaseFragment;

public class FragmentFactory {
    public static BaseFragment getFragment(EFragmentType eFragmentFactory) {

        if (eFragmentFactory == EFragmentType.DASHBOARD_FRAGMENT) {
            return new DashboardFragment();
        } else if (eFragmentFactory == EFragmentType.NOTIFICATION_FRAGMENT) {
            return new NotificationFragment();
        } else if (eFragmentFactory == EFragmentType.USERDATA_FRAGMENT) {
            return new UserDataFragment();
        } else {
            return null;
        }
    }
}
