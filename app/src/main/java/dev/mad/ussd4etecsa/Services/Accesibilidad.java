package dev.mad.ussd4etecsa.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import dev.mad.ussd4etecsa.Model.Tables.DatUssd;
import dev.mad.ussd4etecsa.Config_BD.DatabaseHelper;
import dev.mad.ussd4etecsa.R;

/**
 * Created by Daymel on 25-Apr-17.
 */

public class Accesibilidad extends AccessibilityService {

    public static String TAG = Accesibilidad.class.getSimpleName();
    DatabaseHelper dbHelper;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "Estoy en Accesibilidad");


        AccessibilityNodeInfo source = event.getSource();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.getClassName().equals("android.app.AlertDialog")) {
            // android.app.AlertDialog is the standard but not for all phones

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !String.valueOf(event.getClassName()).contains("AlertDialog")) {
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.TextView"))) {
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(source.getText())) {
                return;
            }

            List<CharSequence> eventText;

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                eventText = event.getText();
            } else {
                eventText = Collections.singletonList(source.getText());
            }

            String text = processUSSDText(eventText);

            if (TextUtils.isEmpty(text)) return;

            // Close dialog
            if(!text.equals(getString(R.string.alert_dialog_title))){
                performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only
            }

            Log.d(TAG, text);

            try {
                procesarRespuesta(text);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Handle USSD response here

        }
    }

    private void procesarRespuesta(String respuesta) throws SQLException {

        List<String> valores = convertirCadena(respuesta);
        if (valores.size() > 6) {
            if (valores.get(0).equals("Saldo")) {
                updateSaldo(valores.get(1), valores.get(6), "SALDO");

            }
            if (valores.get(4).equals("MIN")) {
                updateSaldo(valores.get(3), valores.get(7), "VOZ");

            }
            if (valores.get(4).equals("SMS")) {
                updateSaldo(valores.get(3), valores.get(7), "SMS");

            }
            if ((valores.get(4).equals("KB")) || (valores.get(4).equals("MB"))) {
                updateSaldo(valores.get(3) + " " + valores.get(4), valores.get(7), "BOLSA");

            }
            if (valores.get(4).equals("MB.")) {
                updateSaldo("0" + " " + "MB", "0", "BOLSA");
            }
            if (valores.get(4).equals("Minutos")) {
                updateSaldo(valores.get(3), valores.get(7), "VOZ");
                Log.d("saldo", valores.get(3));
            }
            if (valores.get(6).equals("SMS.")) {
                updateSaldo("0", "0", "SMS");
            }
            if (valores.get(6).equals("Minutos.")) {
                updateSaldo("0:00:00", "0", "VOZ");
            }
            if (valores.get(4).equals("oferta.")) {
                updateSaldo("0.00", "0", "BOLSA");
            }
        }
        if (valores.size() > 9) {
            if (valores.get(9).equals("Bono:")) {
                updateSaldo(valores.get(10), valores.get(12), "BONO");
            }
        }
        if(!respuesta.equals(getString(R.string.alert_dialog_title))){
            mostrarToast(respuesta);
        }



    }

    public void updateSaldo(String valor, String vence, String opcion) throws SQLException {

        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        UpdateBuilder<DatUssd, Integer> updateBuilder = ussdDao.updateBuilder();
        valor = (valor.equals("de")) ? "0" : valor;
        vence = (vence.equals("plan.")) ? "0" : vence;
        vence = (vence.equals("hoy.")) ? "1" : vence;
        updateBuilder.where().eq("name", opcion);
        updateBuilder.updateColumnValue("valor", valor);
        updateBuilder.updateColumnValue("fechavencimiento", vence);
        updateBuilder.update();


    }

    private List<String> convertirCadena(String text) {
        List<String> valores = new ArrayList<>();
        StringTokenizer tProcesar = new StringTokenizer(text);
        while (tProcesar.hasMoreTokens()) {
            valores.add(tProcesar.nextToken());
        }
        return valores;
    }

    private String processUSSDText(List<CharSequence> eventText) {

        String s;
        Log.i("version SDK", String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT == 18) {
            s = (String) eventText.get(1);
        } else {
            s = (String) eventText.get(0);
        }
//        for (CharSequence s : eventText) {
//            String text = String.valueOf(s);
//            // Return text if text is the expected ussd response
////
//            if (true) {
//                return text;
//            }
//        }
        return s;
    }

    private void mostrarToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }


}


