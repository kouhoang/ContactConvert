package com.example.contactconvert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()

            val resultIntent =
                Intent().apply {
                    putExtra("contact_name", name)
                    putExtra("contact_phone_number", phoneNumber)
                }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
