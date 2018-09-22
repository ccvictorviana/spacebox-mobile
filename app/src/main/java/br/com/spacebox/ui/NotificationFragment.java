package br.com.spacebox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.NotificationFilterRequest;
import br.com.spacebox.api.model.response.NotificationResponse;
import br.com.spacebox.api.model.response.NotificationsResponse;
import br.com.spacebox.utils.Util;

public class NotificationFragment extends BaseFragment {
    private ListView myListView;
    private List<Long> openedFolders;
    private View mContentView;

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openedFolders = new ArrayList<>();
        synchronize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_notifications, container, false);
        myListView = mContentView.findViewById(R.id.notificationListView);
        return mContentView;
    }

    private void synchronize() {
        NotificationFilterRequest request = new NotificationFilterRequest();
        callAPI((cli) -> cli.file().notifications(sessionManager.getFullToken(), request), (response) -> {
            myListView.setAdapter(createListData(response));
        });
    }

    private SimpleAdapter createListData(NotificationsResponse response) {
        String TITLE_TAG = "TITLE_TAG";
        String DATE_TAG = "DATE_TAG";
        String ICON_TAG = "ICON_TAG";

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> datum;

        if (response.getNotifications() != null && response.getNotifications().length > 0) {
            for (NotificationResponse file : response.getNotifications()) {
                datum = new HashMap<>(2);
                datum.put(TITLE_TAG, file.getnFileName());
                datum.put(DATE_TAG, Util.formatToDate(file.getCreated()));
                datum.put(ICON_TAG, Util.getNotificationTypeIcon(file.getType()));
                data.add(datum);
            }
        }

        return new SimpleAdapter(getContext(), data, R.layout.item_notification,
                new String[]{TITLE_TAG, DATE_TAG, ICON_TAG},
                new int[]{R.id.notificationTitleTV, R.id.notificationLastModifiedDateTV, R.id.notificationTypeIV});
    }
}
