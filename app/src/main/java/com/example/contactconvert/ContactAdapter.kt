package com.example.contactconvert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val contacts: MutableList<Contact>,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    private val selectedContacts = mutableSetOf<Contact>()

    interface OnItemClickListener {
        fun onItemClick(contact: Contact)
    }

    inner class ContactViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtPhoneNumber: TextView = itemView.findViewById(R.id.txtPhoneNumber)

        fun bind(contact: Contact) {
            txtName.text = contact.name
            txtPhoneNumber.text = contact.phoneNumber
            checkBox.isChecked = selectedContacts.contains(contact)
            itemView.setOnClickListener {
                listener.onItemClick(contact)
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedContacts.add(contact)
                } else {
                    selectedContacts.remove(contact)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int,
    ) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun getSelectedContacts(): Set<Contact> = selectedContacts
}
