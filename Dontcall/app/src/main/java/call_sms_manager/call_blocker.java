package call_sms_manager;

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
        Log.e(LOGTAG, "OutgoingCallReceiver onReceive");
        if (intent.getAction().equals(call_blocker.OUTGOING_CALL_ACTION)) {
            Log.e(LOGTAG, "OutgoingCallReceiver NEW_OUTGOING_CALL received");

            // get phone number from bundle
            String phoneNumber = intent.getExtras().getString(call_blocker.INTENT_PHONE_NUMBER);
            if ((phoneNumber != null) && phoneNumber.equals(phone_number)) {
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
            }
        }
    }
}

// This is the broadcast receiver which will receive any event of the phone state.
//public class call_blocker extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        TelephonyManager telephony = (TelephonyManager)  context.getSystemService(Context.TELEPHONY_SERVICE);
//        MyPhoneStateListener listener = new MyPhoneStateListener (context);
//        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//    }
//}
//
//// Finally the PhoneStateListener extended class which will block the events.
//class MyPhoneStateListener extends PhoneStateListener {
//    Context context;
//
//    public MyPhoneStateListener(Context context) {
//        super();
//        this.context = context;
//    }
//
//    @Override
//    public void onCallStateChanged(int state, String callingNumber) {
//        super.onCallStateChanged(state, callingNumber);
//        switch (state) {
//            case TelephonyManager.CALL_STATE_IDLE:
//                break;
//
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                //handle out going call
//                endCallIfBlocked(callingNumber);
//                break;
//
//            case TelephonyManager.CALL_STATE_RINGING:
//                //handle in coming call
//                endCallIfBlocked(callingNumber);
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    private void endCallIfBlocked(String callingNumber) {
//        try {
//            if (callingNumber == "111") {
//                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                Class c = Class.forName(tm.getClass().getName());
//                Method m = c.getDeclaredMethod("getITelephony");
//                m.setAccessible(true);
//                Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
//                c = Class.forName(telephonyService.getClass().getName()); // Get its class
//                m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
//                m.setAccessible(true); // Make it accessible
//                m.invoke(telephonyService); // invoke endCall()
//            }
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            c = Class.forName(tm.getClass().getName());
//            Method m = c.getDeclaredMethod("getITelephony");
//            m.setAccessible(true);
//            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m.invoke(tm);
//            telephonyService = (ITelephony) m.invoke(tm);
//
//            telephonyService.silenceRinger();
//            telephonyService.endCall();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}