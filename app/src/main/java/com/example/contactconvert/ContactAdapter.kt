package com.example.contactconvert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val contacts: MutableList<Contact>,
    private val onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(contact: Contact)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ContactViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int,
    ) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(contact: Contact) {
            tvName.text = contact.name
            tvPhoneNumber.text = contact.phoneNumber
            checkBox.isChecked = contact.isSelected
            itemView.setOnClickListener {
                contact.isSelected = !contact.isSelected
                checkBox.isChecked = contact.isSelected
                onItemClickListener.onItemClick(contact)
            }
        }
    }

    fun deleteSelectedContacts() {
        contacts.removeAll { it.isSelected }
        notifyDataSetChanged()
    }
}
