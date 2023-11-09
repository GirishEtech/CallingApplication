package com.example.testapp.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.ContactAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Models.Contact
import com.example.testapp.PreferenceManager
import com.example.testapp.databinding.ButtomSheetDesignBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment(), ContactAdapter.number {


    lateinit var preferenceManager: PreferenceManager
    lateinit var _binding: ButtomSheetDesignBinding
    val binding: ButtomSheetDesignBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ButtomSheetDesignBinding.inflate(inflater)
        preferenceManager = PreferenceManager(requireContext())
        val list = Utils.getContactList(requireContext())
        val adapter = ContactAdapter(list, this)
        binding.lstButtomContact.adapter = adapter
        binding.seachView.setIconifiedByDefault(false)
        binding.seachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(value = 34)
    override fun passdata(data: Contact) {
        dismiss()
        val manager = CallManager(requireContext())
        manager.startOutgoingCall(data.number)
        preferenceManager.setConference(true)
    }
}