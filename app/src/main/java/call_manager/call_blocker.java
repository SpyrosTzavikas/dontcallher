package call_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class call_blocker extends BroadcastReceiver {
    private static final String OUTGOING_CALL_ACTION = "android.intent.action.NEW_OUTGOING_CALL";
    private static final String INTENT_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
    private static final String LOGTAG = "call_blocker";
    public String phone_number = "111";

    public void setPhoneNumber(String number) {
        phone_number = number;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.v(LOGTAG, "OutgoingCallReceiver onReceive");
        Log.v(LOGTAG, "Action : " + intent.getAction());
        if (intent.getAction().equals(call_blocker.OUTGOING_CALL_ACTION)) {
            Log.v(LOGTAG, "OutgoingCallReceiver NEW_OUTGOING_CALL received");

            // get phone number from bundle
            String phoneNumber = intent.getExtras().getString(call_blocker.INTENT_PHONE_NUMBER);
            if ((phoneNumber != null) && phoneNumber.equals(phone_number)) {
                Log.v(LOGTAG, "Phone number matched");
                Toast.makeText(context, "NEW_OUTGOING_CALL intercepted to number " + phone_number + " - aborting call",
                        Toast.LENGTH_LONG).show();

                try {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    Class c = Class.forName(tm.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
                    c = Class.forName(telephonyService.getClass().getName()); // Get its class
                    m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
                    m.setAccessible(true); // Make it accessible
                    m.invoke(telephonyService); // invoke endCall()
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                this.abortBroadcast();
                if (getResultData() != null) {
                    setResultData(null);
                }
            }
        }
    }
}
