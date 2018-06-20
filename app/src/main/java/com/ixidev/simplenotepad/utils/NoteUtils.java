package com.ixidev.simplenotepad.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ixi.Dv on 13/05/2018.
 */
public class NoteUtils {
    /**
     * @param time that will be convert  and formatted to string
     * @return string
     */
    public static String dateFromLong(long time) {
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy 'at' hh:mm aaa", Locale.US);
        return format.format(new Date(time));
    }
}
