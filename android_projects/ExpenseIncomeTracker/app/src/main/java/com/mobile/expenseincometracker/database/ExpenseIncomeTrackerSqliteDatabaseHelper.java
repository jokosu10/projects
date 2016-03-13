package com.mobile.expenseincometracker.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import com.mobile.expenseincometracker.model.ExpenseIncomeModel;
import android.database.Cursor;
import android.content.ContentValues;

public class ExpenseIncomeTrackerSqliteDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExpenseIncomeTrackerDatabase";
    private static final String DATABASE_TABLE_EXPENSE = "ExpenseIncomeTrackerTable";
    
    public ExpenseIncomeTrackerSqliteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    
    public void onCreate(SQLiteDatabase db) {
        String create_table = "CREATE TABLE " + DATABASE_TABLE_EXPENSE +" (date text not null, quantity integer not null, expenses text not null, income text not null);";
        db.execSQL(create_table);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EXPENSE);
        onCreate(db);
    }
    
    public void insertToTable(ExpenseIncomeModel ei) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", ei.getDate());
        contentValues.put("quantity", ei.getQuantity());
        contentValues.put("expenses", String.valueOf(ei.getExpense()));
        contentValues.put("income", String.valueOf(ei.getIncome()));
        db.insert("ExpenseIncomeTrackerTable", null, contentValues);
    }
    
    public ArrayList<ExpenseIncomeModel> getHistoricalData() {
    	
        ArrayList<ExpenseIncomeModel> expenseIncomeList = new ArrayList<ExpenseIncomeModel>();
        SQLiteDatabase db = getReadableDatabase();
        
        String query = "SELECT * FROM " + DATABASE_TABLE_EXPENSE;
        
        Cursor cursor = db.rawQuery(query, null);
        if((cursor != null) && (cursor.moveToFirst())) {
            while(cursor.moveToNext()) {
            	 ExpenseIncomeModel ei = new ExpenseIncomeModel();
                 ei.setDate(cursor.getString(0));
                 ei.setQuantity(Integer.valueOf(cursor.getInt(1)));
                 ei.setExpense(Double.valueOf(cursor.getString(2)));
                 ei.setIncome(Double.valueOf(cursor.getString(3)));
                 expenseIncomeList.add(ei);
            }
           
        }
        cursor.close();
        db.close();
        return expenseIncomeList;
    }
}
