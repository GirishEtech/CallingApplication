package com.example.testapp.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.RoomDatabase.ContactLogs
import com.example.testapp.Utils.CallType
import com.example.testapp.databinding.ItemCalllogsBinding

class ContactCallLogsAdapter(
    val items: List<ContactLogs>,
    val datapass: number
) : RecyclerView.Adapter<ContactCallLogsAdapter.Holder>() {
    private var filteredDataList: List<ContactLogs> = items

    interface number {
        fun passdata(data: ContactLogs)
    }


    inner class Holder(binding: ItemCalllogsBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemName = binding.txtCallerName
        val itemType = binding.btnCalltype
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemCalllogsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {

        Log.i("TAG", "getItemCount: ${filteredDataList.size}")
        if (filteredDataList.isEmpty()) {
            ///Log.i("TAG", "getItemCount: ${filteredDataList.size}")
        } else {
            return filteredDataList.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = filteredDataList[position]
        holder.itemName.text = item.name
        holder.itemType.imageTintList = ColorStateList.valueOf(Color.WHITE)
        when (item.type) {
            CallType.MISSED.toString() -> {
                holder.itemType.setImageResource(R.drawable.ic_missed)
                holder.itemType.imageTintList =
                    ColorStateList.valueOf(holder.itemView.context.getColor(R.color.SpeakerOn))
            }

            CallType.OUTGOING.toString() -> {
                holder.itemType.setImageResource(R.drawable.ic_made)
                holder.itemType.imageTintList =
                    ColorStateList.valueOf(holder.itemView.context.getColor(android.R.color.holo_green_light))
            }

            CallType.INCOMING.toString() -> {
                holder.itemType.setImageResource(R.drawable.ic_recieved)

            }

            else -> {
                Log.i("TAG", "onBindViewHolder: no match ")
            }
        }
        holder.itemView.setOnClickListener {
            datapass.passdata(item)
        }
    }

}