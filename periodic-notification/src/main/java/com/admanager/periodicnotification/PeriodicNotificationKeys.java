package com.admanager.periodicnotification;

public class PeriodicNotificationKeys {
    public String enabled;
    public String days;
    public String title;
    public String ticker;
    public String content;

    public PeriodicNotificationKeys() {
        this.enabled = "notif_enabled";
        this.days = "notif_days";
        this.title = "notif_title";
        this.ticker = "notif_ticker";
        this.content = "notif_content";
    }

    public PeriodicNotificationKeys(String enabled, String days, String title, String ticker, String content) {
        this.enabled = enabled;
        this.days = days;
        this.title = title;
        this.ticker = ticker;
        this.content = content;
    }

    @Override
    public String toString() {
        return "PeriodicNotificationKeys{" +
                "enabled='" + enabled + '\'' +
                ", days='" + days + '\'' +
                ", title='" + title + '\'' +
                ", ticker='" + ticker + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}