package dev.mad.ussd4etecsa.Receiber;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;



import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.Model.DatUssdModel;
import dev.mad.ussd4etecsa.Notification.NotificationHelper;
import dev.mad.ussd4etecsa.Util;

/**
 * Created by Daymel on 02/08/2017.
 */

public class SendSmsObserver extends ContentObserver {
    private Context mContext;
    private String lastSmsId;
    SharedPreferences sp;
    private DatUssdModel ussdModel;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SendSmsObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
        ussdModel = new DatUssdModel();
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        sp = mContext.getSharedPreferences("ussdPreferences", mContext.MODE_PRIVATE);
        Uri uriSms = Uri.parse("content://sms/");

        Cursor cur = mContext.getContentResolver().query(uriSms, null, null, null, null);
        // this will make it point to the first record, which is the last
        // SMS sent
        //Todo revisar porque vulve a incrementar
        cur.moveToNext();
        String id = cur.getString(cur.getColumnIndex("_id"));
        final String body = cur.getString(cur.getColumnIndex("body"));



        boolean chxStateSms = sp.getBoolean("cSMS", true);
        if (smsChecker(id) && chxStateSms) {
            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    try {
                        accionDemorada(mContext, Util.convertirCadena(body));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }.start();


        }

    }

    /**
     * Prevent duplicate results without overlooking legitimate duplicates
     * @param smsId
     * @return
     */

    public boolean smsChecker(String smsId) {
        boolean flagSMS = true;

        if (smsId.equals(lastSmsId)) {
            flagSMS = false;
        } else {
            lastSmsId = smsId;
        }
        return flagSMS;
    }

    /**
     *
     * @param context
     * @throws SQLException
     */
    private void accionDemorada(Context context, List<String> cadena) throws SQLException {
        String ussdCod = "222";
        if (!getValorSaldos("SMS", context).equals("0")) {
            ussdCod = "222*767";
        }
        //todo verificar mensages
        /*if(cadena.get(2).equals("transferido")){

        }*/
        marcarNumero(ussdCod, context);
        final NotificationHelper notificationHelper = new NotificationHelper(context);
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                notificationHelper.sendUpdateNotificacion();
            }

        }.start();
    }

    /**
     *
     * @param accion
     * @param context
     * @return
     * @throws SQLException
     */
    private String getValorSaldos(String accion, Context context) throws SQLException {
        String saldoCall = "";
        saldoCall = ussdModel.getValor(accion, context);
        return saldoCall;
    }

    /**
     * @param codigo
     * @param context
     */
    private void marcarNumero(String codigo, Context context) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
