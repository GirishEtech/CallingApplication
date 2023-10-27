package com.example.testapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.Models.Contact
import com.example.testapp.databinding.ContactItemBinding
import java.util.Locale

class ContactAdapter(
    val items: List<Contact>,
    val datapass: number
) : RecyclerView.Adapter<ContactAdapter.Holder>(), Filterable {
    private var filteredDataList: List<Contact> = items

    interface number {
        fun passdata(data: Contact)
    }


    inner class Holder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemName = binding.txtContactName
        val itemNumber = binding.txtContactNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        if (filteredDataList.isEmpty()) {
            Log.i("TAG", "getItemCount: ")
        } else {
            return filteredDataList.size
        }
        return 0
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = filteredDataList[position]
        holder.itemName.text = item.name
        holder.itemNumber.text = item.number
        holder.itemView.setOnClickListener {
            datapass.passdata(item)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = mutableListOf<Contact>()

                if (constraint.isNullOrEmpty()) {
                    filteredResults.addAll(items)
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault())
                    for (item in items) {
                        if (item.name.lowercase(Locale.getDefault()).contains(filterPattern)) {
                            filteredResults.add(item)
                        }
                    }
                }

                val filterResult = FilterResults()
                filterResult.values = filteredResults
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDataList = results?.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
    }
}