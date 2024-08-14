package com.example.contactconvert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {
    private lateinit var edtName: EditText
    private lateinit var edtPhoneNumber: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        edtName = findViewById(R.id.edtName)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = edtName.text.toString()
            val phoneNumber = edtPhoneNumber.text.toString()
            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                val resultIntent =
                    Intent().apply {
                        putExtra("CONTACT_NAME", name)
                        putExtra("CONTACT_PHONE_NUMBER", phoneNumber)
                    }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
