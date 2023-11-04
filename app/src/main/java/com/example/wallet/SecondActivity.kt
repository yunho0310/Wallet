package com.example.wallet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SecondActivity : AppCompatActivity() {


    private lateinit var datePickerButton: Button
    private lateinit var typeSpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var saveButton: Button
    private val calendar = Calendar.getInstance()
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btn_2 = findViewById<Button>(R.id.btn_2)

        btn_2.setOnClickListener(View.OnClickListener {
            // 두 번째 액티비티로 이동하는 인텐트를 생성하고 시작합니다.
            val intent = Intent(this@SecondActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        })



            // 뷰를 수동으로 연결
        datePickerButton = findViewById(R.id.datePickerButton)
        typeSpinner = findViewById(R.id.typeSpinner)
        amountEditText = findViewById(R.id.amountEditText)
        saveButton = findViewById(R.id.saveButton)

        // 현재 날짜를 기본으로 설정
        updateDate()

        dbHelper = DatabaseHelper(this)

        // 날짜 선택 버튼 클릭 시 DatePickerDialog를 보여줌
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        // 저장 버튼 클릭 시 수입 또는 지출을 처리할 함수 호출
        saveButton.setOnClickListener {
            saveTransaction()
            saveTransactionToDatabase()
        }

    }

    private fun updateDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        datePickerButton.text = dateFormat.format(calendar.time)
    }


    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveTransaction() {
        val selectedType = typeSpinner.selectedItem.toString()
        val amountText = amountEditText.text.toString()

        if (amountText.isBlank()) {
            // 사용자가 금액을 입력하지 않은 경우
            amountEditText.error = "금액을 입력해주세요!"
            return
        }

        val amount = amountText.toDoubleOrNull()

        if (amount != null) {
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            dbHelper.insertTransaction(currentDate, selectedType, amount)
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            // 올바르지 않은 금액을 입력한 경우 사용자에게 알림을 표시할 수 있습니다.
            amountEditText.error = "올바른 금액을 입력해주세요!"
        }
    }

    private fun saveTransactionToDatabase() {
        val selectedType = typeSpinner.selectedItem.toString()
        val amountText = amountEditText.text.toString()

        if (amountText.isBlank()) {
            amountEditText.error = "금액을 입력해주세요!"
            return
        }

        val amount = amountText.toDoubleOrNull()

        if (amount != null) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_DATE, calendar.timeInMillis.toString())
                put(DatabaseHelper.COLUMN_TYPE, selectedType)
                put(DatabaseHelper.COLUMN_AMOUNT, amount)
            }

            val newRowId = db?.insert(DatabaseHelper.TABLE_NAME, null, values)
            db?.close()

            setResult(Activity.RESULT_OK)
            finish()
        } else {
            amountEditText.error = "올바른 금액을 입력해주세요!"
        }
    }
}