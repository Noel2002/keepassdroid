package com.keepassdroid.sync.utilities;

import java.util.Date;

public class UnixTimeConverter {
    static public long DateTimeToUnixSeconds(Date date){
        return date.getTime() / 1000;
    }
    public static Date UnixSecondsToDateTime(long unixSeconds) {
        return new Date(unixSeconds * 1000);
    }
}
