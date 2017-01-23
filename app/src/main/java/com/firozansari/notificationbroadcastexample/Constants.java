package com.firozansari.notificationbroadcastexample;

/**
 * Created by Firoz Ansari on 23/01/2017.
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.firozansari.foregroundservice.action.main";
        public static String INIT_ACTION = "com.firozansari.foregroundservice.action.init";
        public static String PREV_ACTION = "com.firozansari.foregroundservice.action.prev";
        public static String PLAY_ACTION = "com.firozansari.foregroundservice.action.play";
        public static String NEXT_ACTION = "com.firozansari.foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "com.firozansari.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.firozansari.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 111;
    }
}
