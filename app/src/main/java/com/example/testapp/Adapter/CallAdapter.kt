package com.example.testapp.Adapter

import android.annotation.SuppressLint
import android.os.Build
import android.telecom.Call
import android.telecom.VideoProfile
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.callingapp.Utils.Utils
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.CallProvides.MyInCallService
import com.example.testapp.Models.CallModel
import com.example.testapp.databinding.ItemCallBinding

class CallAdapter(val list: MutableList<CallModel>) :
    RecyclerView.Adapter<CallAdapter.callViewHolder>() {
    private var callManager: CallManager? = null

    inner class callViewHolder(val binding: ItemCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun setData(item: CallModel) {
            binding.txtCallerName.text = Utils.getCallerName(
                binding.root.context,
                item.callData.details.handle.schemeSpecificPart
            )
            setCallBack(item.callData)
            binding.btnCallEndItem.setOnClickListener {
                item.callData.disconnect()
                MyInCallService.INSTANCE!!.onCallRemoved(item.callData)
                list.remove(item)
                notifyDataSetChanged()
            }
        }

        @SuppressLint("NewApi", "SwitchIntDef")
        fun setCallBack(call: Call) {
            when (call.details.state) {
                Call.STATE_ACTIVE -> {
                    call.playDtmfTone('1')
                    call.answer(VideoProfile.STATE_AUDIO_ONLY)
                }

                Call.STATE_DIALING -> {
                    Toast.makeText(binding.root.context, "New Call is Dialing", Toast.LENGTH_SHORT)
                        .show()
                }

                Call.STATE_DISCONNECTED -> {
                    Toast.makeText(
                        binding.root.context,
                        "New Call is Disconnected",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                Call.STATE_HOLDING -> {
                    Toast.makeText(binding.root.context, "New Call is Holding", Toast.LENGTH_SHORT)
                        .show()
                }

                Call.STATE_RINGING -> {
                    Toast.makeText(binding.root.context, "New Call is Ringing", Toast.LENGTH_SHORT)
                        .show()
                }
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
        holder.setData(item)
    }
}