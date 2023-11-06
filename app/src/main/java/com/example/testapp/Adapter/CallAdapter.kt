package com.example.testapp.Adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.callingapp.Utils.Utils
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Models.CallModel
import com.example.testapp.databinding.ItemCallBinding

class CallAdapter(val list: MutableList<CallModel>) :
    RecyclerView.Adapter<CallAdapter.callViewHolder>() {
    private var callManager: CallManager? = null

    inner class callViewHolder(val binding: ItemCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun setData(item: CallModel) {
            if (item.isConfereneActive) {
                binding.txtCallerName.text = Utils.getCallerName(
                    binding.root.context,
                    item.callData.details.handle.schemeSpecificPart
                )
                binding.btnMerge.setOnClickListener {
                    if (item.isActive) {
                        callManager!!.mergeConference()
                        binding.btnMerge.visibility = View.INVISIBLE
                    }
                }
            } else {
                binding.txtCallerName.text = Utils.getCallerName(
                    binding.root.context,
                    item.callData.details.handle.schemeSpecificPart
                )
                binding.btnMerge.visibility = View.INVISIBLE
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

    override fun onBindViewHolder(holder: callViewHolder, position: Int) {
        val item = list[position]
        holder.setData(item)
    }
}