package dev.mad.ussd4etecsa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import dev.mad.ussd4etecsa.AboutUS.AboutUsFragment;
import dev.mad.ussd4etecsa.Ajustes.ConfigActivity;
import dev.mad.ussd4etecsa.Config_BD.DatabaseHelper;
import dev.mad.ussd4etecsa.Model.Tables.DatUssd;
import dev.mad.ussd4etecsa.Notification.NotificationHelper;
import dev.mad.ussd4etecsa.Services.Accesibilidad;
import dev.mad.ussd4etecsa.Services.GeneralService;
import dev.mad.ussd4etecsa.Services.UssdService;
import dev.mad.ussd4etecsa.Transferencia.TransferenciaFragment;

public class Nav_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    CollapsingToolbarLayout collapsingToolbarLayout;
    NotificationHelper notificationHelper;
    DatabaseHelper dbHelper;
    ScrollView scrollView;
    Intent intentMemoryService;

    private static final String TAG_ABOUT = "about";
    private static final String TAG_TRANFERENCIA = "transferencia";

    private static final String[] ARRAY_VOZ = {"5 Minutos / $1.50", "10 Minutos / $2.90", "15 Minutos / $4.20", "25 Minutos / $6.50", "40 Minutos / $10.00"};
    private static final String[] ARRAY_SMS = {"10 Mensajes / $0.70", "20 Mensajes / $1.30", "35 Mensajes / $2.10", "45 Mensajes / $2.45"};
    private static final String[] ARRAY_DATOS = {"Bolsa Nauta", "Tarifa por Consumo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notificationHelper = new NotificationHelper(getApplicationContext());
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.maincollapsing);
        cv_bono = (CardView) findViewById(R.id.cv_bono);
        refresh = (ImageButton) findViewById(R.id.btn_refresh);
        saldo = (TextView) findViewById(R.id.tv_valor_saldo);
        venceSaldo = (TextView) findViewById(R.id.tv_valor_vence);
        bono = (TextView) findViewById(R.id.tv_bono_saldo);
        scrollView = (ScrollView) findViewById(R.id.sv_contenedor);
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
        FloatingActionButton recargarSaldo = (FloatingActionButton) findViewById(R.id.btn_transferir_contacto);
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

        recargarSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText alertText = (EditText) findViewById(R.id.et_numtelf);
                Editable YouEditTextValue = alertText.getText();
                marcarNumero("662*" + String.valueOf(YouEditTextValue));
            }
        });
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
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY ==0 && !collapsingToolbarLayout.isShown())

                        collapsingToolbarLayout.setVisibility(View.VISIBLE);
                    else if (scrollY > 5 && collapsingToolbarLayout.isShown())
                        collapsingToolbarLayout.setVisibility(View.GONE);



                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(intentMemoryService);
        notificationHelper.sendUpdateNotificacion();
    }

    protected void onStart() {
        super.onStart();
        startService(intentMemoryService);
        notificationHelper.sendUpdateNotificacion();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_help: {
                fragmentGestor(new AboutUsFragment(), TAG_ABOUT);
                return true;
            }
            case R.id.action_config: {
                getConfig();
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Función para pintar los fragmentos en la vista principal
     *
     * @param fragment
     */
    private void fragmentGestor(Fragment fragment, String tag) {

        Fragment containerFragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (containerFragment == null) {
            fragmentTransaction.add(R.id.rl_contenedor, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
            collapsingToolbarLayout.setVisibility(View.GONE);
        } else if (!tag.equals(containerFragment.getTag())) {
            fragmentTransaction.replace(R.id.rl_contenedor, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
            collapsingToolbarLayout.setVisibility(View.GONE);
        }

    }


    /**
     *
     */
    private void getConfig() {
        Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_configuracion) {
            getConfig();

        } else if (id == R.id.nav_transferir) {
            fragmentGestor(new TransferenciaFragment(),TAG_TRANFERENCIA);

        } else if (id == R.id.nav_share) {
            fragmentGestor(new AboutUsFragment(), TAG_ABOUT);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Mostrar mensajes al usuarios
     *
     * @param msg
     */
    private void mostrarMensaje(String msg) {
        Snackbar.make(findViewById(R.id.rl_contenedor), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Manejador de dialogos
     */
    public void mostrarDialogos(View v) {
        final String[] valor = {"0"};
        final AlertDialog.Builder winDialog = new AlertDialog.Builder(this);
        switch (v.getId()) {
            case R.id.iv_config_voz: {

                winDialog.setTitle(getString(R.string.Principal_voz)).setSingleChoiceItems(ARRAY_VOZ, -1, new DialogInterface.OnClickListener() {
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
                            mostrarMensaje(getString(R.string.Principal_seleccionar));
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

                winDialog.setTitle(getString(R.string.Principal_sms)).setSingleChoiceItems(ARRAY_SMS, -1, new DialogInterface.OnClickListener() {
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
                            mostrarMensaje(getString(R.string.Principal_seleccionar));

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

                winDialog.setTitle(getString(R.string.Principal_datos)).setSingleChoiceItems(ARRAY_DATOS, -1, new DialogInterface.OnClickListener() {
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
                            mostrarMensaje(getString(R.string.Principal_seleccionar));

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
                descripcion.setText(getString(R.string.no_conection));
                tipoRed.setText(getString(R.string.no_conection));

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
            } else {
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
