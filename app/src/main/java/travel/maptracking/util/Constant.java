package travel.maptracking.util;
/**
 * Created by Feriyal on 11/22/2016.
 */

public interface Constant {
    String default_longitude = "112.619020";
    String default_latitude = "-7.938375";
    String default_simpledateFormat = "dd-M-yyyy HH:mm:ss";
    String default_simpledate2= "yyyy-MM-dd";
    String PREFS_IS_LOGIN="PREFS_IS_LOGIN";
    String PREFS_IS_USER_ID="PREFS_IS_USER_ID";
    String PREFS_IS_USER_AKSES="PREFS_IS_USER_AKSES";
    String PREFS_IS_USER_NAME="PREFS_IS_USER_NAME";
    String PREFS_IS_USER_LEVEL="PREFS_IS_USER_LEVEL";
    String PREFS_IS_USER_TOKEN="PREFS_IS_TOKEN";
    String PREFS_LAST_SINKRONISASI="PREFS_LAST_SINKRONISASI";
    String PREFS_IS_latlong="PREFS_IS_latlong";

    public static final String ACTION_START_OUTBOX = "barcode.checklist.com.checklist.outbox.ACTION_START_OUTBOX";
    public static final String ACTION_STOP_OUTBOX = "barcode.checklist.com.checklistoutbox.ACTION_STOP_OUTBOX";
    public static final String ACTION_STOP_OUTBOX_NOW = "barcode.checklist.com.checklist.outbox.ACTION_STOP_OUTBOX_NOW";
    public static final String ACTION_EXECUTE_OUTBOX = "barcode.checklist.com.checklist.outbox.ACTION_EXECUTE_OUTBOX";
    public static final String ACTION_CANCEL_OUTBOX = "barcode.checklist.com.checklist.outbox.ACTION_CANCEL_OUTBOX";
    public static final String ACTION_ALARM_RING = "barcode.checklist.com.checklist.outbox.ACTION_ALARM_RING";

    String RECORD_ID = "ID_TABLE";
    int NO_REPEAT = 1;
    int DAILY =2 ;
    int WEEKLY = 3;
    int MONTHLY = 4;
    int MONTHLY_2 = 5;
    int YEARLY = 6;

    static final String[] Months = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };


    long LOCATION_INTERVAL =1000;
    long FASTEST_LOCATION_INTERVAL =500;
}
