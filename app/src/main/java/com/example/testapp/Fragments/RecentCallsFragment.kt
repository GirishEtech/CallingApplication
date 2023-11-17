package com.example.testapp.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.callingapp.Utils.Messages
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.ContactCallLogsAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.RoomDatabase.ContactLogs
import com.example.testapp.databinding.FragmentRecentCallsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentCallsFragment : Fragment(), ContactCallLogsAdapter.number {

    lateinit var items: List<ContactLogs>
    lateinit var CallManager: CallManager
    lateinit var adapter: ContactCallLogsAdapter
    lateinit var _binding: FragmentRecentCallsBinding
    val binding: FragmentRecentCallsBinding
        get() = _binding


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentCallsBinding.inflate(inflater, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            displayData()
        }
        items = listOf()
        CallManager = CallManager(requireContext())
        return binding.root
    }

    companion object {
        val TAG = "FragmentRecentCalls"
    }

    suspend fun displayData() {
        val items = Utils.getContactLogsList(requireContext())
        if (items.isEmpty()) {
            Log.e(ContactFragment.TAG, "displayData: item is Empty")
            val data = Utils.getContactLogsList(requireContext())
            requireActivity().runOnUiThread {
                setData(data)
            }
        } else {
            requireActivity().runOnUiThread { setData(items) }
        }

    }


    private fun setData(items: List<ContactLogs>) {
        if (items.isEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            adapter = ContactCallLogsAdapter(
                items, this
            )

            binding.contactList.adapter = adapter
            binding.contactList.visibility = View.VISIBLE

        }
    }

    @RequiresApi(34)
    override fun passdata(data: ContactLogs) {
        Messages.showAlertForNextStep(requireContext(), "CALL", "are you sure you want to Call?") {
            CallManager.startOutgoingCall(data.number)
        }
    }
}