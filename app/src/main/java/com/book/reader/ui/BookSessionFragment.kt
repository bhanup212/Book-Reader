package com.book.reader.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.book.reader.BookApp
import com.book.reader.data.IDispatchers
import com.book.reader.data.model.BookDetailsModel
import com.book.reader.data.repository.SharedPreferenceRepository
import com.book.reader.databinding.FragmentHomeBinding
import com.book.reader.service.BookService
import com.book.reader.ui.viewmodel.BookSessionViewModel
import com.book.reader.ui.viewmodel.ViewModelFactory
import com.book.reader.utils.openAppSettings
import com.book.reader.utils.showLongToast
import com.book.reader.utils.showToast
import com.google.gson.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BookSessionFragment : Fragment() {

    companion object {
        const val TAG = "BookSessionFragment"
        fun newInstance() = BookSessionFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: BookSessionViewModel by viewModels { viewModelFactory }
    @Inject lateinit var sharedPreferenceRepository: SharedPreferenceRepository
    @Inject lateinit var gson: Gson
    @Inject lateinit var dispatchers: IDispatchers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BookApp.bookComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        viewModel.updateSession(sharedPreferenceRepository.getBookSessionStatus())
    }

    private fun observeData() {
        viewModel.errorMessage.observe(viewLifecycleOwner, { msg ->
            requireContext().showToast(msg)
        })
        viewModel.isEndSessionSuccess.observe(viewLifecycleOwner, {
            if (it) {
                stopService()
            } else {
                requireContext().showToast("Some error occurred while closing book session")
            }
        })

        if (activity is MainActivity) {
            (activity as MainActivity).isBookServiceConnected.observe(
                viewLifecycleOwner,
                { isConnected ->
                    if (isConnected) {
                        observeTimerState()
                    }
                })
        }
    }

    private fun observeTimerState() {
        (activity as MainActivity).getBookService()?.timerText?.observe(
            viewLifecycleOwner,
            Observer { timerTxt ->
                binding.timerTxt.text = timerTxt
            })
    }

    fun onScanNowButtonClick(view: View) {
        if (isCameraPermissionGranted()) {
            scanBarCode()
        } else {
            requestCameraPermission()
        }
    }

    fun onEndSessionClick(view: View) {
        scanBarCode()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private val permissionContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                scanBarCode()
            } else {
                requireContext().showLongToast("Please Grant Camera permission in app settings to scan qrcode")
                requireContext().openAppSettings()
            }
        }

    private fun scanBarCode() {
        IntentIntegrator.forSupportFragment(this).setBarcodeImageEnabled(true).initiateScan()
    }

    private fun requestCameraPermission() {
        permissionContract.launch(Manifest.permission.CAMERA)
    }

    private fun startBookService() {
        sharedPreferenceRepository.setSessionStartTime(System.currentTimeMillis())
        sharedPreferenceRepository.updateBookSession(true)
        viewModel.updateSession(true)
        if (activity is MainActivity) (activity as MainActivity).bindService()
    }

    private fun stopService() {
        if (activity is MainActivity) (activity as MainActivity).undBindService()
        val intent = Intent(requireContext(), BookService::class.java)
        activity?.stopService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                requireContext().showToast("Cancelled")
            } else {
                val formattedResult =
                    result.contents.toString().replace("\\", "").replaceFirst("\"", "")
                convertResultToJson(formattedResult.dropLast(1))
            }
        }
    }

    private fun convertResultToJson(result: String) {
        lifecycleScope.launch(dispatchers.io()) {
            try {
                val model = parseJson(result)
                withContext(dispatchers.main()) {
                    when {
                        model.locationId == sharedPreferenceRepository.getSessionDetails().locationId -> showConfirmFragment()
                        sharedPreferenceRepository.getBookSessionStatus().not() -> {
                            viewModel.setSession(model)
                            startBookService()
                        }
                        else -> requireContext().showToast("Please scan valid barcode")
                    }
                }
            } catch (e: Exception) {
                withContext(dispatchers.main()) {
                    requireContext().showToast("Please scan valid barcode")
                }
            }
        }
    }

    private fun parseJson(result: String): BookDetailsModel {
        val json = gson.fromJson(result, JsonObject::class.java)
        val locationId = json.get("location_id").asString
        val locationDetails = when {
            json.has("location_detail") -> json.get("location_detail").asString
            json.has("location_details") -> json.get("location_details").asString
            else -> ""
        }
        val pricePerMin = json.get("price_per_min").asFloat
        return BookDetailsModel(locationId, locationDetails, pricePerMin)
    }

    private fun showConfirmFragment() {
        val requestModel = viewModel.getEndBookSessionRequestModel()
        val fragment = SessionConfirmFragment()
        viewModel.bookDetails.value?.let {
            fragment.show(childFragmentManager, it, requestModel) {
                viewModel.endSession(requestModel)
            }
        }
    }

}