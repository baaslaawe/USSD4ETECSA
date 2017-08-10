package dev.mad.ussd4etecsa.Services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import dev.mad.ussd4etecsa.Receiber.SendSmsObserver;

public class GeneralService extends Service {
    public GeneralService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("generalservice", "Servicio creado...");
        SendSmsObserver myObserver = new SendSmsObserver(new Handler(),GeneralService.this);
        ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, myObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("generalservice", "Servicio iniciado...");

        return START_NOT_STICKY;
    }
}
