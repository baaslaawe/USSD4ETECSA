package dev.mad.ussd4etecsa.Receiber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.sql.SQLException;

import dev.mad.ussd4etecsa.Model.DatUssdModel;

/**
 * Created by Daymel on 11/7/2017.
 */

public class Reciver extends BroadcastReceiver {

    private String callState = "";
    boolean flag = false;
    private DatUssdModel ussdModel = new DatUssdModel();

    @Override
    public void onReceive(final Context context, Intent intent) {

        // verificacion para cuando se cuelga la llamada;
        callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        final SharedPreferences sp = context.getSharedPreferences("ussdPreferences", context.MODE_PRIVATE);
        boolean chxStateCall = sp.getBoolean("cCall", true);

        if (callState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            sp.edit().putBoolean("flag", true).commit();
        }
        flag = sp.getBoolean("flag", true);
        if (callState.equals(TelephonyManager.EXTRA_STATE_IDLE) && flag && chxStateCall) {
            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    try {
                        accionDemorada(context, "call");
                        sp.edit().putBoolean("flag", false).commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }.start();

        }

    }

    /**
     * @param codigo
     * @param context
     */
    private void marcarNumero(String codigo, Context context) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }


    /**
     * @param context
     * @param accion
     */
    private void accionDemorada(Context context, String accion) throws SQLException {
        String ussdCod = "222";
        Log.i("call", ussdModel.getValor("VOZ", context));
        if (!getValorSaldos("VOZ", context).equals("0:00:00") || !getValorSaldos("VOZ", context).equals("0")) {
            ussdCod = "222*869";

        }


        marcarNumero(ussdCod, context);
    }

    /**
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
}
