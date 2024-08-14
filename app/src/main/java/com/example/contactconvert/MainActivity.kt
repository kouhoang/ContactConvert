package com.example.contactconvert

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
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
        private const val REQUEST_CODE_PERMISSIONS = 1
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
            startActivityForResult(intent, 1)
        }

        btnDeleteSelected.setOnClickListener {
            deleteSelectedContacts()
        }

        btnConvertNumbers.setOnClickListener {
            convertPhoneNumbers()
        }

        if (checkPermissions()) {
            loadContacts()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS,
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS,
            ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
            REQUEST_CODE_PERMISSIONS,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadContacts() {
        contacts.clear()
        val contentResolver: ContentResolver = contentResolver
        val cursor =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null,
            )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                contacts.add(Contact(id, name, number))
            }
        }

        contactAdapter.notifyDataSetChanged()
    }

    private fun addContact(
        name: String,
        phoneNumber: String,
    ) {
        val contentResolver: ContentResolver = contentResolver
        val contentValues =
            ContentValues().apply {
                put(ContactsContract.RawContacts.ACCOUNT_TYPE, "")
                put(ContactsContract.RawContacts.ACCOUNT_NAME, "")
            }

        val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
        val rawContactId = ContentUris.parseId(rawContactUri!!)

        contentValues.clear()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)

        contentValues.clear()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)

        loadContacts()
    }

    private fun deleteSelectedContacts() {
        val contentResolver: ContentResolver = contentResolver
        for (contact in contacts.filter { it.isSelected }) {
            val uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, contact.id.toLong())
            contentResolver.delete(uri, null, null)
        }
        contactAdapter.deleteSelectedContacts()
        loadContacts()
    }

    private fun convertPhoneNumbers() {
        for (contact in contacts) {
            if (contact.phoneNumber.startsWith("0167") || contact.phoneNumber.startsWith("84167")) {
                val newNumber = contact.phoneNumber.replaceFirst("0167", "037").replaceFirst("84167", "037")
                editContact(contact.id, contact.name, newNumber)
            }
        }
        loadContacts()
    }

    private fun editContact(
        id: String,
        name: String,
        newNumber: String,
    ) {
        val contentResolver: ContentResolver = contentResolver

        val where =
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ? AND " +
                "${ContactsContract.CommonDataKinds.Phone.MIMETYPE} = ?"
        val params = arrayOf(id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

        val contentValues =
            ContentValues().apply {
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
            }

        contentResolver.update(ContactsContract.Data.CONTENT_URI, contentValues, where, params)
    }

    override fun onItemClick(contact: Contact) {
        Toast.makeText(this, "Clicked on ${contact.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val name = data?.getStringExtra("contact_name") ?: ""
            val phoneNumber = data?.getStringExtra("contact_phone_number") ?: ""
            addContact(name, phoneNumber)
        }
    }
}
