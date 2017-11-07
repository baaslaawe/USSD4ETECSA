package dev.mad.ussd4etecsa.Transferencia;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import dev.mad.ussd4etecsa.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferenciaFragment extends Fragment {

    FloatingActionButton btnContacto;
    FloatingActionButton btnCambiarPass;
    FloatingActionButton btnTransferir;
    EditText numTelf;
    EditText pass;
    EditText dineroTrans;

    final static int PICK_CONTACT = 456;

    public TransferenciaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_transferencia, container, false);

        btnContacto = (FloatingActionButton) v.findViewById(R.id.btn_transferir_contacto);
        btnCambiarPass = (FloatingActionButton) v.findViewById(R.id.btn_cambiar_contraseña);
        btnTransferir = (FloatingActionButton) v.findViewById(R.id.btn_tranfer);
        numTelf = (EditText) v.findViewById(R.id.et_numtelf);
        pass = (EditText) v.findViewById(R.id.et_contrasenna);
        dineroTrans = (EditText) v.findViewById(R.id.et_money);


        btnContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        return v;
    }

    /**
     * todo terminar lo de seleccionar el contacto
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT): {
                if (resultCode == getActivity().RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    cursor.moveToFirst();

                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    numTelf.setText(number);

                }
            }
        }
    }

}
