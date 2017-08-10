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

import dev.mad.ussd4etecsa.Model.DatUssdModel;

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

        boolean chxStateSms = sp.getBoolean("cSMS", true);
        if (smsChecker(id) && chxStateSms) {
            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    try {
                        accionDemorada(mContext);

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
    private void accionDemorada(Context context) throws SQLException {
        String ussdCod = "222";
        if (!getValorSaldos("SMS", context).equals("0")) {
            ussdCod = "222*767";
        }
        marcarNumero(ussdCod, context);
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
        context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }
}
