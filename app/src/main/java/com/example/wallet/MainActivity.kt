package com.example.wallet

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val textView = findViewById<TextView>(R.id.memoTextView)

        // 달력에서 날짜를 선택했을 때의 이벤트 처리
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            // 여기에서 선택된 날짜에 대한 정보를 가져오거나 표시할 수 있습니다.
            // 이 예제에서는 간단하게 선택된 날짜를 TextView에 표시합니다.
            textView.text = "선택된 날짜: $selectedDate"
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
