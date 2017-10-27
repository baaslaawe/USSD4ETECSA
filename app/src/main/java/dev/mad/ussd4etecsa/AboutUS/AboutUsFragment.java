package dev.mad.ussd4etecsa.AboutUS;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import dev.mad.ussd4etecsa.BuildConfig;
import dev.mad.ussd4etecsa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    final static String VERSION = BuildConfig.VERSION_NAME;
    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_us, container, false);
        TextView version = (TextView) v.findViewById(R.id.tv_version_val);
        FloatingActionButton contact = (FloatingActionButton) v.findViewById(R.id.btn_contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });
        version.setText(VERSION);
        return v;
    }

    /**
     * Intent para enviar correo
     */
    public void openEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"daym3l@nauta.cu"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Reportando bug ");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Breve explicación del error...");
        startActivity(Intent.createChooser(emailIntent, "Seleccione aplicación de correo"));

    }

}
