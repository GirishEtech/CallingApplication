package com.example.testapp.Adapter

import android.os.Build
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.callingapp.Utils.Utils
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Models.CallModel
import com.example.testapp.databinding.ItemCallBinding

class CallAdapter(
    val list: MutableList<CallModel>,
    val listner: itemListner,
    val currentCall: Call
) :
    RecyclerView.Adapter<CallAdapter.callViewHolder>() {
    private var callManager: CallManager? = null


    interface itemListner {
        fun count(size: Int)
        fun setCallback(call: Call, position: Int)
        fun callEnd(call: Call, position: Int)
    }

    fun itemRemove(position: Int) {
        if (list.size > 0) {
            if (list[position].callData == currentCall) {
                Log.i("CURRENT", "itemRemove: Current Call ")
            } else {
                list.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }
    }

    inner class callViewHolder(val binding: ItemCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun setData(item: CallModel, position: Int) {
            binding.txtCallerName.text = Utils.getCallerName(
                binding.root.context,
                item.callData.details.handle.schemeSpecificPart
            )
            listner.setCallback(item.callData, position)
            binding.btnCallEndItem.setOnClickListener {
                list.remove(item)
                listner.callEnd(item.callData, position)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): callViewHolder {
        val binding = ItemCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        callManager = CallManager(parent.context)
        return callViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBindViewHolder(holder: callViewHolder, position: Int) {
        val item = list[position]
        holder.setData(item, position)
    }
}