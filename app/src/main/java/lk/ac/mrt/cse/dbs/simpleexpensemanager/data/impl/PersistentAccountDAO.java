package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "180524J";

    public PersistentAccountDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.onCreate(this.getReadableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS account ( "+
                        "accountNo TEXT PRIMARY KEY,"+
                        "bankName TEXT,"+
                        "accountHolderName TEXT,"+
                        "initialBalance REAL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS account");
        onCreate(db);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accountNumberList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select accountNo from account", null);
        res.moveToFirst();

        while (res.isAfterLast()==false){
            accountNumberList.add(res.getString(res.getColumnIndex("accountNo")));
            res.moveToNext();
        }

        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> accountList = new ArrayList<Account>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from account", null);
        res.moveToFirst();

        while (res.isAfterLast()==false){
            Account account = new Account();
            account.setAccountNo(res.getString(res.getColumnIndex("accountNo")));
            account.setBankName(res.getString(res.getColumnIndex("bankName")));
            account.setAccountHolderName(res.getString(res.getColumnIndex("accountHolderName")));
            account.setBalance(res.getFloat(res.getColumnIndex("initialBalance")));
            accountList.add(account);
            res.moveToNext();
        }

        return  accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from account where accountNo="+accountNo+"", null);
        if (res.moveToFirst()) {
            Account account = new Account();
            account.setAccountNo(res.getString(res.getColumnIndex("accountNo")));
            account.setBankName(res.getString(res.getColumnIndex("bankName")));
            account.setAccountHolderName(res.getString(res.getColumnIndex("accountHolderName")));
            account.setBalance(res.getFloat(res.getColumnIndex("initialBalance")));
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("initialBalance", account.getBalance());

        db.insert("account", null, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("account", "accountNo = ?", new String[]{accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = this.getAccount(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("initialBalance", account.getBalance());

        db.update("account",  contentValues, "accountNo = ?", new String[]{accountNo});
    }

}
