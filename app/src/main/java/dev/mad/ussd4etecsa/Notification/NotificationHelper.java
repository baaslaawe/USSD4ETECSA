package dev.mad.ussd4etecsa.Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.Config_BD.DatabaseHelper;
import dev.mad.ussd4etecsa.Model.AuxConfigModel;
import dev.mad.ussd4etecsa.Model.DatUssd;
import dev.mad.ussd4etecsa.Principal;
import dev.mad.ussd4etecsa.R;

/**
 * Created by Daymel on 9/3/2017.
 */

public class NotificationHelper {

    private static final int NOTIFICATION_ID = 1;
    NotificationManager adminNotifications;
    static String servNotifications = Context.NOTIFICATION_SERVICE;
    int iconNotificactionApp = R.drawable.ic_notification;


    Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        this.adminNotifications = (NotificationManager) this.context.getSystemService(servNotifications);
    }



    public void sendUpdateNotificacion(){
        AuxConfigModel modelConfig = new AuxConfigModel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (verificarNotifications()) {
                resetNotication();
            }
        } else {
            try {
                if (modelConfig.existeConfig("NOTIFICATION",this.context)) {
                    if (modelConfig.getValorConfig("NOTIFICATION").equals("1")) {
                        resetNotication();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verificar q la notificacion este activa
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean verificarNotifications() {


        final StatusBarNotification[] activeNotifications = this.adminNotifications.getActiveNotifications();
        boolean flag = false;
        for (StatusBarNotification notification : activeNotifications) {
            if (notification.getId() == NOTIFICATION_ID) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Volver a crear la notificación
     */
    private void resetNotication() {
        adminNotifications.cancel(NOTIFICATION_ID);
        sendNotifacation();
    }

    private void sendNotifacation() {
        // Capturo la hora del evento
        long hora = System.currentTimeMillis();
        // Conexion a la BD
        DatabaseHelper dbHelper = OpenHelperManager.getHelper(this.context, DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        List<DatUssd> ussdObjetctSaldo = ussdDao.queryForEq("name", "SALDO");
        List<DatUssd> ussdObjetctVoz = ussdDao.queryForEq("name", "VOZ");
        List<DatUssd> ussdObjetctSms = ussdDao.queryForEq("name", "SMS");
        List<DatUssd> ussdObjetctBolsa = ussdDao.queryForEq("name", "BOLSA");

        String saldos = "Saldo principal: " + ussdObjetctSaldo.get(0).getValor();
        // Definimos la accion de la pulsacion sobre la sendNotifacation (esto es opcional)

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        Intent notificationIntent = null;

            notificationIntent = new Intent(this.context, Principal.class);


        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, notificationIntent, 0);

        nBuilder.setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setSmallIcon(iconNotificactionApp).setWhen(hora)
                .setContentTitle("Saldo Disponible")
                .setContentText(saldos);

        if (!ussdObjetctVoz.get(0).getValor().equals("0") || !ussdObjetctSms.get(0).getValor().equals("0") ||  !ussdObjetctBolsa.get(0).getValor().equals("0")) {
            String[] events = new String[4];

            events[0] = new String(saldos);
            if (!ussdObjetctVoz.get(0).getValor().equals("0") && !ussdObjetctVoz.get(0).getValor().equals("0:00:00")) {
                events[1] = new String("Voz: " + ussdObjetctVoz.get(0).getValor() + " Min por " + ussdObjetctVoz.get(0).getFechavencimiento() + " días");
            }
            if (!ussdObjetctSms.get(0).getValor().equals("0")) {
                events[2] = new String("SMS: " + ussdObjetctSms.get(0).getValor() + " SMS por " + ussdObjetctSms.get(0).getFechavencimiento() + " días");
            }
            if (!ussdObjetctBolsa.get(0).getValor().equals("0") && !ussdObjetctBolsa.get(0).getValor().equals("0.00") ) {
                events[3] = new String("Datos: " + ussdObjetctBolsa.get(0).getValor() + " por " + ussdObjetctBolsa.get(0).getFechavencimiento() + " días");
            }


            inboxStyle.setBigContentTitle("Saldos Disponibles:");

            // Moves events into the big view
            for (int i = 0; i < events.length; i++) {
                inboxStyle.addLine(events[i]);
            }

            nBuilder.setStyle(inboxStyle);
        }


        Notification notification = nBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        adminNotifications.notify(NOTIFICATION_ID, notification);

    }
}
