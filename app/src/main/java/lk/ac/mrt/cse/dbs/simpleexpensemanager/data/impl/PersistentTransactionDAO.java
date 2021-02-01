package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.R;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "180524J";

    public PersistentTransactionDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.onCreate(this.getReadableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS transactionLog ( "+
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "accountNo TEXT,"+
                "expenseType TEXT CHECK( expenseType IN ('EXPENSE','INCOME') ),"+
                "date TEXT,"+
                "amount REAL," +
                "FOREIGN KEY(accountNo) REFERENCES account(accountNo) ON DELETE CASCADE)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactionLog");
        onCreate(db);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", String.valueOf(accountNo));
        contentValues.put("expenseType", String.valueOf(expenseType));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(date);
        contentValues.put("date", String.valueOf(formattedDate));
        contentValues.put("amount", amount);

        db.insert("transactionLog", null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from transactionLog", null);
        res.moveToFirst();

        while (res.isAfterLast()==false){
            Transaction transaction = new Transaction();
            transaction.setAccountNo(res.getString(res.getColumnIndex("accountNo")));
            String type = res.getString(res.getColumnIndex("expenseType"));
            ExpenseType expenseType;
            if (type.equals("EXPENSE")){
                expenseType = ExpenseType.EXPENSE;
            }else{
                expenseType = ExpenseType.INCOME;
            }
            transaction.setExpenseType(expenseType);

            String[] date = res.getString(res.getColumnIndex("date")).split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
            Date transactionDate = calendar.getTime();
            transaction.setDate(transactionDate);

            transaction.setAmount(res.getFloat(res.getColumnIndex("amount")));
            transactionList.add(transaction);
            res.moveToNext();
        }

        return  transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from transactionLog LIMIT "+limit+"", null);
        res.moveToFirst();

        while (res.isAfterLast()==false){
            Transaction transaction = new Transaction();
            transaction.setAccountNo(res.getString(res.getColumnIndex("accountNo")));
            String type = res.getString(res.getColumnIndex("expenseType"));
            ExpenseType expenseType;
            if (type.equals("EXPENSE")){
                expenseType = ExpenseType.EXPENSE;
            }else{
                expenseType = ExpenseType.INCOME;
            }
            transaction.setExpenseType(expenseType);

            String[] date = res.getString(res.getColumnIndex("date")).split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
            Date transactionDate = calendar.getTime();
            transaction.setDate(transactionDate);

            transaction.setAmount(res.getFloat(res.getColumnIndex("amount")));
            transactionList.add(transaction);
            res.moveToNext();
        }

        return  transactionList;
    }
}
