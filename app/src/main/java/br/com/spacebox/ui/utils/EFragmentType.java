package br.com.spacebox.ui.utils;

public enum EFragmentType {
    DASHBOARD_FRAGMENT(1),
    NOTIFICATION_FRAGMENT(2),
    USERDATA_FRAGMENT(3);

    private int type;

    EFragmentType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
