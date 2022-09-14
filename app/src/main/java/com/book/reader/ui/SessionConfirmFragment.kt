package com.book.reader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.book.reader.BookApp
import com.book.reader.data.model.BookDetailsModel
import com.book.reader.data.model.request.EndBookSessionRequestModel
import com.book.reader.databinding.FragmentSessionConfirmBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SessionConfirmFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSessionConfirmBinding? = null
    private val binding get() = _binding!!

    private var bookDetails: BookDetailsModel? = null
    private var requestModel: EndBookSessionRequestModel? = null

    private var onConfirm:()-> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BookApp.bookComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSessionConfirmBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookDetails?.let {
            binding.model = bookDetails
            if (requestModel != null) {
                binding.requestModel = requestModel
                binding.totalPrice =  (it.pricePerMin * requestModel!!.timeSpent).toString()
            }
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.confirmBtn.setOnClickListener {
            dismiss()
            onConfirm()
        }
    }

    fun show(fragmentManager: FragmentManager, model: BookDetailsModel, requestModel: EndBookSessionRequestModel, onConfirm:()-> Unit){
        this.bookDetails = model
        this.requestModel = requestModel
        this.onConfirm = onConfirm
        show(fragmentManager, "SessionConfirmFragment")
    }
}