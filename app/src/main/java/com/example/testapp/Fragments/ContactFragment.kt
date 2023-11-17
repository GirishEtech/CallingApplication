package com.example.testapp.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.callingapp.Utils.Messages
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.ContactAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.RoomDatabase.Contact
import com.example.testapp.databinding.FragmentContactBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactFragment : Fragment(), ContactAdapter.number {

    lateinit var CallManager: CallManager
    lateinit var items: List<Contact>
    lateinit var adapter: ContactAdapter
    lateinit var _binding: FragmentContactBinding
    val binding: FragmentContactBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            displayData()
        }
        CallManager = CallManager(requireContext())
        return binding.root
    }

    companion object {
        val TAG = "FragmentContact"
    }

    suspend fun displayData() {
        val items = Utils.getContactList(requireContext())
        if (items?.isEmpty() == true) {
            Log.e(TAG, "displayData: item is Empty")
            val data = Utils.getContactList(requireContext())
            requireActivity().runOnUiThread {
                data?.let { setData(it) }
            }
        } else {
            requireActivity().runOnUiThread { items?.let { setData(it) } }
        }

    }

    private fun setData(items: List<Contact>) {
        if (items.isEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            this.items = items
            binding.progressBar.visibility = View.INVISIBLE
            adapter = ContactAdapter(
                items, this
            )
            binding.contactList.adapter = adapter
            binding.searchView.setIconifiedByDefault(false)
            binding.contactList.visibility = View.VISIBLE
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }

    }

    @RequiresApi(34)
    override fun passdata(data: Contact) {
        Messages.showAlertForNextStep(requireContext(), "CALL", "are you sure you want to Call?") {
            CallManager.startOutgoingCall(data.number)
        }
    }
}