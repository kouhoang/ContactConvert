package com.example.contactconvert

import android.Manifest
import android.app.Activity
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity :
    AppCompatActivity(),
    ContactAdapter.OnItemClickListener {
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contacts: MutableList<Contact>
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var btnAddContact: Button
    private lateinit var btnDeleteSelected: Button
    private lateinit var btnConvertNumbers: Button

    companion object {
        const val REQUEST_ADD_CONTACT = 1
        const val REQUEST_CONTACTS_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts)
        btnAddContact = findViewById(R.id.btnAddContact)
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected)
        btnConvertNumbers = findViewById(R.id.btnConvertNumbers)

        contacts = mutableListOf()
        contactAdapter = ContactAdapter(contacts, this)

        recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
        }

        btnAddContact.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_CONTACT)
        }

        btnDeleteSelected.setOnClickListener {
            deleteSelectedContacts()
        }

        btnConvertNumbers.setOnClickListener {
            convertPhoneNumbers()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                REQUEST_CONTACTS_PERMISSION,
            )
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadContacts()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_CONTACT && resultCode == Activity.RESULT_OK) {
            val name = data?.getStringExtra("CONTACT_NAME") ?: ""
            val phoneNumber = data?.getStringExtra("CONTACT_PHONE_NUMBER") ?: ""
            addContact(name, phoneNumber)
        }
    }

    private fun loadContacts() {
        contacts.clear()
        val cursor =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null,
            )
        cursor?.let {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val phoneNumber = cursor.getString(numberIndex)
                contacts.add(Contact(name, phoneNumber))
            }
            cursor.close()
        }
        contactAdapter.notifyDataSetChanged()
    }

    private fun addContact(
        name: String,
        phoneNumber: String,
    ) {
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build(),
        )

        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build(),
        )

        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build(),
        )

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            loadContacts()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to add contact", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteSelectedContacts() {
        val selectedContacts = contactAdapter.getSelectedContacts()
        if (selectedContacts.isNotEmpty()) {
            for (contact in selectedContacts) {
                val where = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ? AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
                val args = arrayOf(contact.name, contact.phoneNumber)
                contentResolver.delete(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, where, args)
            }
            loadContacts()
            Toast.makeText(this, "Selected contacts deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No contacts selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertPhoneNumbers() {
        for (contact in contacts) {
            val formattedNumber = formatPhoneNumber(contact.phoneNumber)
            contact.phoneNumber = formattedNumber
        }
        contactAdapter.notifyDataSetChanged()
    }

    private fun formatPhoneNumber(phoneNumber: String): String =
        if (phoneNumber.startsWith("0")) {
            "+84${phoneNumber.substring(1)}"
        } else {
            phoneNumber
        }

    override fun onItemClick(contact: Contact) {
        Toast.makeText(this, "Clicked on ${contact.name}", Toast.LENGTH_SHORT).show()
    }
}
