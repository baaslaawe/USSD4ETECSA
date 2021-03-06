package dev.mad.ussd4etecsa.Model;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import dev.mad.ussd4etecsa.Config_BD.DatabaseHelper;
import dev.mad.ussd4etecsa.Model.Tables.DatTranferencia;


/**
 * Created by Daymel on 30/10/2017.
 */

public class DatTransferenciaModel {
    DatabaseHelper dbHelper;
    Context mcontext;


    public DatTransferenciaModel(Context context){
        this.dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        this.mcontext = context;
    }
    /**
     * @param newTransfer     *
     * @throws SQLException
     */
    public void insertTransferencia(DatTranferencia newTransfer) throws SQLException {

        RuntimeExceptionDao<DatTranferencia, Integer> tranferenciaDao = this.dbHelper.getTransfereneciaRuntimeDao();
        tranferenciaDao.create(newTransfer);
        Log.i("Transferencia", "Transferencia realizada");
    }

    /**
     * @return
     * @throws SQLException
     */
    public List<DatTranferencia> getTranferencias() throws SQLException {
        RuntimeExceptionDao<DatTranferencia, Integer> transdao = this.dbHelper.getTransfereneciaRuntimeDao();
        QueryBuilder<DatTranferencia, Integer> builder = transdao.queryBuilder();
        builder.limit((long) 10);
        builder.orderBy("fechaTransferencia", false);
        List<DatTranferencia> listTranferencia = transdao.query(builder.prepare());

        return listTranferencia;
    }

}
