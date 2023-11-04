package com.example.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_SECOND_ACTIVITY = 1
    lateinit var textView: TextView  // lateinit으로 선언
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        textView = findViewById(R.id.memoTextView)  // 뷰 초기화

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener(View.OnClickListener {
            // 두 번째 액티비티로 이동하는 인텐트를 생성하고 시작합니다.
            val intent = Intent(this@MainActivity, SecondActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY)
        })


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis


        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            val db = dbHelper.readableDatabase

            val projection = arrayOf(DatabaseHelper.COLUMN_TYPE, DatabaseHelper.COLUMN_AMOUNT)
            val selection = "${DatabaseHelper.COLUMN_DATE} = ?"
            val selectionArgs = arrayOf(selectedDate)

            val cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            val transactionInfo = StringBuilder()

            while (cursor.moveToNext()) {
                val type =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
                val amount =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                transactionInfo.append("유형: $type, 금액: $amount\n")
            }

            cursor.close()
            db.close()

            textView.text = "선택된 날짜: $selectedDate\n$transactionInfo"
        }
    }

    // onActivityResult 메서드에서 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val selectedDateLong = data?.getLongExtra("date", 0L) ?: 0L
            val selectedDate = selectedDateLong.toInt()

            val type = data?.getStringExtra("type") ?: ""
            val amount = data?.getDoubleExtra("amount", 0.0) ?: 0.0

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDateLong

            val formattedAmount = String.format("%.2f", amount)
            textView.text = "선택된 날짜: ${
                formatDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }\n유형: $type\n금액: $formattedAmount"
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun updateTextView() {
        val selectedDate = getSelectedDateFromCalendarView()
        val transactionInfoList = dbHelper.getTransactionsForDate(selectedDate)

        val transactionsStringBuilder = StringBuilder()
        transactionsStringBuilder.append("선택된 날짜: $selectedDate\n")

        for (transactionInfo in transactionInfoList) {
            transactionsStringBuilder.append("$transactionInfo\n")
        }

        textView.text = transactionsStringBuilder.toString()
    }

    private fun getSelectedDateFromCalendarView(): String{
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        var selectedDate = ""  // 날짜를 저장할 변수 초기화

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 월은 0부터 시작하므로 실제 월 값에 +1을 해줍니다.
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            // 이제 selectedDate에 선택된 날짜가 "dd/MM/yyyy" 포맷으로 들어 있습니다.
        }

        return selectedDate  // 선택된 날짜를 반환합니다.
    }


}
