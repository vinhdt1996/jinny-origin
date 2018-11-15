//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.firebase.messaging;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.zzap;
import com.google.firebase.iid.zzb;
import com.google.firebase.iid.zzv;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CustomFirebaseMessagingService extends zzb {
    private static final Queue<String> zzdo = new ArrayDeque(10);

    public CustomFirebaseMessagingService() {
    }

    @WorkerThread
    public void onMessageReceived(RemoteMessage var1) {
    }

    @WorkerThread
    public void onDeletedMessages() {
    }

    @WorkerThread
    public void onMessageSent(String var1) {
    }

    @WorkerThread
    public void onSendError(String var1, Exception var2) {
    }

    protected final Intent zzb(Intent var1) {
        return zzap.zzac().zzad();
    }

    public final boolean zzc(Intent var1) {
        Log.d("FA", "zzc");
        if ("com.google.firebase.messaging.NOTIFICATION_OPEN".equals(var1.getAction())) {
            PendingIntent var4;
            if ((var4 = (PendingIntent)var1.getParcelableExtra("pending_intent")) != null) {
                try {
                    var4.send();
                } catch (CanceledException var5) {
                    Log.e("FirebaseMessaging", "Notification pending intent canceled");
                }
            }

            if (zzk(var1.getExtras())) {
                com.google.firebase.messaging.zzb.zzd(this, var1);
            }

            return true;
        } else {
            return false;
        }
    }

    public final void zzd(Intent var1) {
        String var2;
        if ((var2 = var1.getAction()) == null) {
            var2 = "";
        }
        Log.d("FA", "zzd");
        byte var4 = -1;
        switch(var2.hashCode()) {
            case 75300319:
                if (var2.equals("com.google.firebase.messaging.NOTIFICATION_DISMISS")) {
                    var4 = 1;
                }
                break;
            case 366519424:
                if (var2.equals("com.google.android.c2dm.intent.RECEIVE")) {
                    var4 = 0;
                }
        }
        Log.d("FirebaseMessaging", ""+var4);
        String var10001;
        String var10002;
        String var10003;
        switch(var4) {
            case 0:
                String var7 = var1.getStringExtra("google.message_id");
                Task var10000;
                if (TextUtils.isEmpty(var7)) {
                    var10000 = Tasks.forResult((Object)null);
                } else {
                    Bundle var13;
                    (var13 = new Bundle()).putString("google.message_id", var7);
                    var10000 = zzv.zzc(this).zza(2, var13);
                }

                Task var8 = var10000;
                boolean var21;
                if (TextUtils.isEmpty(var7)) {
                    var21 = false;
                } else if (zzdo.contains(var7)) {
                    if (Log.isLoggable("FirebaseMessaging", 3)) {
                        var10002 = String.valueOf(var7);
                        if (var10002.length() != 0) {
                            var10001 = "Received duplicate message: ".concat(var10002);
                        } else {
                            var10003 = new String();
                            var10001 = var10003;
                            var10003 = ("Received duplicate message: ");
                        }

                        Log.d("FirebaseMessaging", var10001);
                    }

                    var21 = true;
                } else {
                    if (zzdo.size() >= 10) {
                        zzdo.remove();
                    }

                    zzdo.add(var7);
                    var21 = false;
                }

                if (!var21) {
                    String var20;
                    if ((var20 = var1.getStringExtra("message_type")) == null) {
                        var20 = "gcm";
                    }
                    Log.d("FA", "142: "+ var20);

                    byte var15 = -1;
                    switch(var20.hashCode()) {
                        case -2062414158:
                            if (var20.equals("deleted_messages")) {
                                var15 = 1;
                            }Log.d("FA", "149");
                            break;
                        case 102161:
                            if (var20.equals("gcm")) {
                                var15 = 0;
                            }Log.d("FA", "154");
                            break;
                        case 814694033:
                            if (var20.equals("send_error")) {
                                var15 = 3;
                            }
                            break;
                        case 814800675:
                            if (var20.equals("send_event")) {
                                var15 = 2;
                            }
                    }
                    Log.d("FA", "166: "+var15);
                    switch(var15) {
                        case 0:
                            if (zzk(var1.getExtras())) {
                                com.google.firebase.messaging.zzb.zzc(this, var1);
                            }

                            Bundle var22;
                            if ((var22 = var1.getExtras()) == null) {
                                var22 = new Bundle();
                            }

                            var22.remove("android.support.content.wakelockid");
//                            if (zza.zzf(var22)) {
//                                if (zza.zzd(this).zzh(var22)) {
//                                    break;
//                                }
//                                Log.d("FA", "183");
//                                if (zzk(var22)) {
//                                    com.google.firebase.messaging.zzb.zzf(this, var1);
//                                }
//                            }
                            Log.d("FA", "188");
                            try {
                                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
                                firebaseAnalytics.logEvent("jinny_notification_foreground", null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            this.onMessageReceived(new RemoteMessage(var22));
                            break;
                        case 1:
                            Log.d("FA", "192");
                            this.onDeletedMessages();
                            break;
                        case 2:
                            Log.d("FA", "196");
                            this.onMessageSent(var1.getStringExtra("google.message_id"));
                            break;
                        case 3:
                            String var18;
                            if ((var18 = var1.getStringExtra("google.message_id")) == null) {
                                var18 = var1.getStringExtra("message_id");
                            }
                            Log.d("FA", "202");
                            this.onSendError(var18, new SendException(var1.getStringExtra("error")));
                            break;
                        default:
                            Log.d("FA", "206");
                            var10002 = String.valueOf(var20);
                            if (var10002.length() != 0) {
                                var10001 = "Received message with unknown type: ".concat(var10002);
                            } else {
                                var10003 = new String();
                                var10001 = var10003;
                                var10003 = ("Received message with unknown type: ");
                            }

                            Log.w("FirebaseMessaging", var10001);
                    }
                }

                try {
                    Tasks.await(var8, 1L, TimeUnit.SECONDS);
                    return;
                } catch (InterruptedException | TimeoutException | ExecutionException var19) {
                    String var10 = String.valueOf(var19);
                    Log.w("FirebaseMessaging", (new StringBuilder(20 + String.valueOf(var10).length())).append("Message ack failed: ").append(var10).toString());
                    return;
                }
            case 1:
                if (zzk(var1.getExtras())) {
                    com.google.firebase.messaging.zzb.zze(this, var1);
                    return;
                }
                break;
            default:
                var10002 = String.valueOf(var1.getAction());
                if (var10002.length() != 0) {
                    var10001 = "Unknown intent action: ".concat(var10002);
                } else {
                    var10003 = new String();
                    var10001 = var10003;
                    var10003 =("Unknown intent action: ");
                }

                Log.d("FirebaseMessaging", var10001);
        }

    }

    static void zzj(Bundle var0) {
        Iterator var1 = var0.keySet().iterator();
        Log.d("FA", "zzj");

        while(var1.hasNext()) {
            String var2;
            if ((var2 = (String)var1.next()) != null && var2.startsWith("google.c.")) {
                var1.remove();
            }
        }

    }

    static boolean zzk(Bundle var0) {
        Log.d("FA", "zzk");

        return var0 == null ? false : "1".equals(var0.getString("google.c.a.e"));
    }
}
