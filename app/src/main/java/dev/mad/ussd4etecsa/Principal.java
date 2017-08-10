package dev.mad.ussd4etecsa;

import android.annotation.TargetApi;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;

import android.support.v4.app.NotificationCompat;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;


import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.Model.AuxConfigModel;
import dev.mad.ussd4etecsa.Model.DatUssd;
import dev.mad.ussd4etecsa.Services.Accesibilidad;
import dev.mad.ussd4etecsa.Services.GeneralService;
import dev.mad.ussd4etecsa.Services.UssdService;
import dev.mad.ussd4etecsa.Config_BD.DatabaseHelper;

public class Principal extends AppCompatActivity {


    // Constante con ID de la notificacion
    private static final int NOTIFICATION_ID = 1;

    //    Variables de la sendNotifacation
    NotificationManager adminNotifications;

    static String servNotifications = Context.NOTIFICATION_SERVICE;

    //Defino los iconos de la sendNotifacation en la barra de sendNotifacation
    int iconNotificactionApp = R.drawable.ic_notification;


    ImageButton refresh;
    ImageView bolsa;
    ImageView sms;
    ImageView voz;

    TextView descripcion;
    TextView tipoRed;
    TextView proveedor;
    TextView saldo;
    TextView venceSaldo;
    TextView bono;
    TextView venceBono;
    TextView tv_voz;
    TextView tv_vozVence;
    TextView tv_sms;
    TextView tv_smsVence;
    TextView tv_bolsa;
    TextView tv_bolsaVence;
    TextView tv_activo_Datos;
    TextView tv_activo_Voz;
    TextView tv_activo_Sms;
    CardView cv_bono;

    DatabaseHelper dbHelper;
    Intent intentMemoryService;
    AuxConfigModel modelConfig = new AuxConfigModel();


    private static final String[] ARRAY_VOZ = {"5 Minutos / $1.50", "10 Minutos / $2.90", "15 Minutos / $4.20", "25 Minutos / $6.50", "40 Minutos / $10.00"};
    private static final String[] ARRAY_SMS = {"10 Mensajes / $0.70", "20 Mensajes / $1.30", "35 Mensajes / $2.10", "45 Mensajes / $2.45"};
    private static final String[] ARRAY_DATOS = {"Bolsa Nauta", "Tarifa por Consumo"};


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adminNotifications = (NotificationManager) getSystemService(servNotifications);

        cv_bono = (CardView) findViewById(R.id.cv_bono);
        refresh = (ImageButton) findViewById(R.id.btn_refresh);
        saldo = (TextView) findViewById(R.id.tv_valor_saldo);
        venceSaldo = (TextView) findViewById(R.id.tv_valor_vence);
        bono = (TextView) findViewById(R.id.tv_bono_saldo);
        venceBono = (TextView) findViewById(R.id.tv_bono_vence);
        tv_voz = (TextView) findViewById(R.id.tv_voz_vaue);
        tv_vozVence = (TextView) findViewById(R.id.tv_vtime_val);

        tv_activo_Datos = (TextView) findViewById(R.id.tv_act_datos);
        tv_activo_Sms = (TextView) findViewById(R.id.tv_act_sms);
        tv_activo_Voz = (TextView) findViewById(R.id.tv_act_voz);

        tv_sms = (TextView) findViewById(R.id.tv_sms_val);
        tv_smsVence = (TextView) findViewById(R.id.tv_stime_val);

        tv_bolsa = (TextView) findViewById(R.id.tv_bolsa_value);
        tv_bolsaVence = (TextView) findViewById(R.id.tv_btime_val);

        bolsa = (ImageView) findViewById(R.id.iv_bolsa);
        sms = (ImageView) findViewById(R.id.iv_sms);
        voz = (ImageView) findViewById(R.id.iv_mic);
        descripcion = (TextView) findViewById(R.id.tv_descrip);
        tipoRed = (TextView) findViewById(R.id.tv_tipodred);
        proveedor = (TextView) findViewById(R.id.tv_proveedor_name);
        intentMemoryService = new Intent(getApplicationContext(), UssdService.class);
        startService(new Intent(getApplicationContext(), GeneralService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constantes.ACTION_RUN_SERVICE);
        filter.addAction(Constantes.ACTION_USSD_EXIT);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        //Boton refrescar
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222");

            }
        });

        //Imagen de la bolsa
        bolsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*328");

            }
        });

        //imagen voz
        voz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*869");

            }
        });

        //imagen png_sms
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(v.getContext(), Accesibilidad.class));
                marcarNumero("222*767");

            }
        });


    }


    /**
     * Control sobre la aplicacion
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Control de la aplicacion
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStop() {
        super.onStop();
        stopService(intentMemoryService);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (verificarNotifications()) {
                resetNotication();
            }
        } else {
            try {
                if (modelConfig.existeConfig("NOTIFICATION", getApplicationContext())) {
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
     * Control de aplicacion
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        startService(intentMemoryService);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (verificarNotifications()) {
                resetNotication();
            }
        } else {
            try {
                if (modelConfig.existeConfig("NOTIFICATION", getApplicationContext())) {
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
     * Crear menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    /**
     * Acciones del menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment newFragment = MyDialogFragment.newInstance(0);

                newFragment.show(ft, "dialog");

                return true;
            }

            case R.id.action_config: {

                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(intent);
                return true;
            }

//            case R.id.action_favorite:
//                showSnackBar("Añadir a favoritos");
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean verificarNotifications() {
        final StatusBarNotification[] activeNotifications = adminNotifications.getActiveNotifications();
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
     * Resetear Notificaciones
     */
    public void resetNotication() {
        adminNotifications.cancel(NOTIFICATION_ID);
        sendNotifacation();
    }

    /**
     * Crear notificaciones en la barra de estado con el saldo
     */
    public void sendNotifacation() {
        // Capturo la hora del evento
        long hora = System.currentTimeMillis();
        // Conexion a la BD
        dbHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
        List<DatUssd> ussdObjetctSaldo = ussdDao.queryForEq("name", "SALDO");
        List<DatUssd> ussdObjetctVoz = ussdDao.queryForEq("name", "VOZ");
        List<DatUssd> ussdObjetctSms = ussdDao.queryForEq("name", "SMS");
        List<DatUssd> ussdObjetctBolsa = ussdDao.queryForEq("name", "BOLSA");

        String saldos = "Saldo principal: " + ussdObjetctSaldo.get(0).getValor();
        // Definimos la accion de la pulsacion sobre la sendNotifacation (esto es opcional)
        Context context = getApplicationContext();
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        Intent notificationIntent = null;
        if (this.getIntent() != null) {
            notificationIntent = this.getIntent();
        } else {
            notificationIntent = new Intent(this, Principal.class);

        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        nBuilder.setContentIntent(contentIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setSmallIcon(iconNotificactionApp).setWhen(hora)
                .setContentTitle("Saldo Disponible")
                .setContentText(saldos);

        if (tv_activo_Voz.getText().equals("Servicio activo.") || tv_activo_Sms.getText().equals("Servicio activo.") || tv_activo_Datos.getText().equals("Servicio activo.")) {
            String[] events = new String[5];

            events[0] = new String(saldos);
            events[1] = new String("SERVICIOS ACTIVOS:");
            if (tv_activo_Voz.getText().equals("Servicio activo.")) {
                events[2] = new String("Voz: " + ussdObjetctVoz.get(0).getValor() + " Min por " + ussdObjetctVoz.get(0).getFechavencimiento() + " días");
            }
            if (tv_activo_Sms.getText().equals("Servicio activo.")) {
                events[3] = new String("SMS: " + ussdObjetctSms.get(0).getValor() + " SMS por " + ussdObjetctSms.get(0).getFechavencimiento() + " días");
            }
            if (tv_activo_Datos.getText().equals("Servicio activo.")) {
                events[4] = new String("Datos: " + ussdObjetctBolsa.get(0).getValor() + " por " + ussdObjetctBolsa.get(0).getFechavencimiento() + " días");
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


    /**
     * Mostrar mensajes al usuarios
     *
     * @param msg
     */
    private void mostrarMensaje(String msg) {
        Snackbar.make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    @Nullable


    /**
     * Manejador de dialogos
     */
    public void mostrarDialogos(View v) {
        final String[] valor = {"0"};
        final AlertDialog.Builder winDialog = new AlertDialog.Builder(this);
        switch (v.getId()) {
            case R.id.iv_config_voz: {

                winDialog.setTitle("Contratar servicio de Voz").setSingleChoiceItems(ARRAY_VOZ, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                valor[0] = "133*3*1";
                                break;
                            }
                            case 1: {
                                valor[0] = "133*3*2";
                                break;
                            }
                            case 2: {
                                valor[0] = "133*3*3";
                                break;
                            }
                            case 3: {
                                valor[0] = "133*3*4";
                                break;
                            }
                            case 4: {
                                valor[0] = "133*3*5";
                                break;
                            }
                        }
                    }
                });
                winDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (valor[0].equals("0")) {
                            mostrarMensaje("Debe seleccionar una opción.");
                        } else {
                            marcarNumero(valor[0]);
                            dialog.dismiss();
                        }

                    }
                });
                winDialog.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        marcarNumero("222*869");
                        dialog.dismiss();
                    }
                });

                break;
            }

            case R.id.iv_config_sms: {

                winDialog.setTitle("Contratar servicio de Mensajes").setSingleChoiceItems(ARRAY_SMS, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                valor[0] = "133*2*1";
                                break;
                            }
                            case 1: {
                                valor[0] = "133*2*2";
                                break;
                            }
                            case 2: {
                                valor[0] = "133*2*3";
                                break;
                            }
                            case 3: {
                                valor[0] = "133*2*4";
                                break;
                            }

                        }
                    }
                });
                winDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (valor[0].equals("0")) {
                            mostrarMensaje("Debe seleccionar una opción.");

                        } else {
                            marcarNumero(valor[0]);
                            dialog.dismiss();
                        }
                    }
                });
                winDialog.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        marcarNumero("222*767");
                        dialog.dismiss();
                    }
                });

                break;
            }

            case R.id.iv_config_datos: {

                winDialog.setTitle("Contratar servicio de Mensajes").setSingleChoiceItems(ARRAY_DATOS, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                valor[0] = "133*1*2";
                                break;
                            }
                            case 1: {
                                valor[0] = "133*1*1";
                                break;
                            }


                        }
                    }
                });
                winDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (valor[0].equals("0")) {
                            mostrarMensaje("Debe seleccionar una opción.");

                        } else {
                            marcarNumero(valor[0]);
                            dialog.dismiss();
                        }
                    }
                });
                winDialog.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        marcarNumero("222*328");
                        dialog.dismiss();
                    }
                });

                break;
            }

        }
        AlertDialog dialog = winDialog.create();
        dialog.show();
    }


    /**
     * Marcar Codigo USSD
     *
     * @param codigo
     */
    private void marcarNumero(String codigo) {

        String ussdCodigo = "*" + codigo + Uri.encode("#");
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCodigo)));
    }

    // Broadcast receiver que recibe las emisiones desde los servicios
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constantes.ACTION_RUN_SERVICE:
                    refrescarValores(context);
                    getDatosRed(context);

                    break;

                case Constantes.ACTION_USSD_EXIT:
                    refrescarValores(context);
                    break;

            }
        }

        /**
         * Obtiene los png_datos de la red movil
         *
         * @param context
         */
        private void getDatosRed(Context context) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            proveedor.setText(manager.getNetworkOperatorName());
            if (info == null || !info.isConnected() || !info.isAvailable() || !info.getTypeName().equals("MOBILE")) {
                descripcion.setText("Sin conexión.");
                tipoRed.setText("Sin conexión.");

            } else {
                descripcion.setText(info.getExtraInfo());


                if (info.getSubtypeName().equals("HSDPA") || info.getSubtypeName().equals("UMTS")) {

                    tipoRed.setText(info.getTypeName() + " " + "3G");
                } else if (info.getSubtypeName().equals("HSPA+")) {
                    tipoRed.setText(info.getTypeName() + " " + "3G Plus");
                } else if (info.getSubtypeName().equals("EDGE")) {
                    tipoRed.setText(info.getTypeName() + " " + "2G");
                } else {
                    tipoRed.setText(info.getTypeName() + " " + info.getSubtypeName());
                }
            }

        }

        /**
         * Refresaca los valores en la actividad
         *
         * @param context
         */
        public void refrescarValores(Context context) {
            dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<DatUssd, Integer> ussdDao = dbHelper.getUssdRuntimeDao();
            List<DatUssd> ussdObjetctSaldo = ussdDao.queryForEq("name", "SALDO");
            List<DatUssd> ussdObjetctBono = ussdDao.queryForEq("name", "BONO");
            saldo.setText(ussdObjetctSaldo.get(0).getValor());
            venceSaldo.setText(ussdObjetctSaldo.get(0).getFechavencimiento() + " ");
            bono.setText(ussdObjetctBono.get(0).getValor());
            venceBono.setText(ussdObjetctBono.get(0).getFechavencimiento() + " ");

            if (ussdObjetctBono.get(0).getValor().equals("0.00")) {
                cv_bono.setVisibility(View.INVISIBLE);
            }else {
                cv_bono.setVisibility(View.VISIBLE);
            }
            List<DatUssd> ussdObjetctVoz = ussdDao.queryForEq("name", "VOZ");
            if (ussdObjetctVoz.get(0).getFechavencimiento().equals("0")) {
                tv_activo_Voz.setText("Sin servicio.");
                tv_activo_Voz.setTextColor(getResources().getColor(R.color.rojo));
                tv_voz.setText(ussdObjetctVoz.get(0).getValor() + " MIN");
                tv_vozVence.setText(ussdObjetctVoz.get(0).getFechavencimiento() + " días");
            } else {
                tv_activo_Voz.setText("Servicio activo.");
                tv_activo_Voz.setTextColor(getResources().getColor(R.color.verde));
                tv_voz.setText(ussdObjetctVoz.get(0).getValor() + " MIN");
                tv_vozVence.setText(ussdObjetctVoz.get(0).getFechavencimiento() + " días");
            }
            List<DatUssd> ussdObjetctSms = ussdDao.queryForEq("name", "SMS");
            if (ussdObjetctSms.get(0).getFechavencimiento().equals("0")) {
                tv_activo_Sms.setText("Sin servicio.");
                tv_activo_Sms.setTextColor(getResources().getColor(R.color.rojo));
                tv_sms.setText(ussdObjetctSms.get(0).getValor() + " SMS");
                tv_smsVence.setText(ussdObjetctSms.get(0).getFechavencimiento() + " días");
            } else {
                tv_activo_Sms.setText("Servicio activo.");
                tv_activo_Sms.setTextColor(getResources().getColor(R.color.verde));
                tv_sms.setText(ussdObjetctSms.get(0).getValor() + " SMS");
                tv_smsVence.setText(ussdObjetctSms.get(0).getFechavencimiento() + " días");
            }
            List<DatUssd> ussdObjetctBolsa = ussdDao.queryForEq("name", "BOLSA");
            if (ussdObjetctBolsa.get(0).getFechavencimiento().equals("0")) {
                tv_activo_Datos.setText("Sin servicio.");
                tv_activo_Datos.setTextColor(getResources().getColor(R.color.rojo));
                tv_bolsa.setText(ussdObjetctBolsa.get(0).getValor());
                tv_bolsaVence.setText(ussdObjetctBolsa.get(0).getFechavencimiento() + " días");
            } else {
                tv_activo_Datos.setText("Servicio activo.");
                tv_activo_Datos.setTextColor(getResources().getColor(R.color.verde));
                tv_bolsa.setText(ussdObjetctBolsa.get(0).getValor());
                tv_bolsaVence.setText(ussdObjetctBolsa.get(0).getFechavencimiento() + " días");
            }

        }
    }


}
