package com.example.wallet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "wallet.db"
        const val TABLE_NAME = "transactions"
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_TYPE = "type"
        const val COLUMN_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DATE TEXT, $COLUMN_TYPE TEXT, $COLUMN_AMOUNT REAL)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun insertTransaction(date: String, type: String, amount: Double) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_TYPE, type)
            put(COLUMN_AMOUNT, amount)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getTransactionsForDate(selectedDate: String): List<String> {
        val transactionInfoList = mutableListOf<String>()
        val db = readableDatabase
        val selection = "${DatabaseHelper.COLUMN_DATE} = ?"
        val selectionArgs = arrayOf(selectedDate)

        val cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
            transactionInfoList.add("유형: $type, 금액: $amount")
        }

        cursor.close()
        db.close()

        return transactionInfoList
    }
}