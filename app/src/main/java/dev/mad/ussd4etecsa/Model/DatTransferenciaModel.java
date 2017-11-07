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


    /**
     * @param newTransfer
     * @param context
     * @throws SQLException
     */
    public void insertTransferencia(DatTranferencia newTransfer, Context context) throws SQLException {
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        RuntimeExceptionDao<DatTranferencia, Integer> tranferenciaDao = dbHelper.getTransfereneciaRuntimeDao();
        tranferenciaDao.create(newTransfer);
        Log.i("Transferencia", "Transferencia realizada");
    }

    /**
     * @return
     * @throws SQLException
     */
    public List<DatTranferencia> getValorConfig() throws SQLException {
        RuntimeExceptionDao<DatTranferencia, Integer> transdao = dbHelper.getTransfereneciaRuntimeDao();
        QueryBuilder<DatTranferencia, Integer> builder = transdao.queryBuilder();
        builder.limit((long) 10);
        builder.orderBy("fechaTransferencia", true);
        List<DatTranferencia> listTranferencia = transdao.query(builder.prepare());

        return listTranferencia;
    }

}
