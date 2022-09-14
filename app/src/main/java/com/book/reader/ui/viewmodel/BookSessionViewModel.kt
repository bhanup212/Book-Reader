package com.book.reader.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.book.reader.data.IDispatchers
import com.book.reader.data.ISchedulers
import com.book.reader.data.model.BookDetailsModel
import com.book.reader.data.model.request.EndBookSessionRequestModel
import com.book.reader.data.repository.BookRepository
import com.book.reader.data.repository.SharedPreferenceRepository
import com.book.reader.utils.TimeUtils.toMinutes
import com.google.gson.JsonObject
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookSessionViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val schedulers: ISchedulers,
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val dispatchers: IDispatchers
): ViewModel() {
    private val _isSessionActive = MutableLiveData(false)
    val isSessionActive: LiveData<Boolean> = _isSessionActive

    private val _isProgressbarVisible = MutableLiveData<Boolean>()
    val isProgressbarVisible: LiveData<Boolean> = _isProgressbarVisible

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isEndSessionSuccess = MutableLiveData<Boolean>()
    val isEndSessionSuccess: LiveData<Boolean> = _isEndSessionSuccess

    private val _bookDetails = MutableLiveData<BookDetailsModel>()
    val bookDetails: LiveData<BookDetailsModel> = _bookDetails

    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        viewModelScope.launch(dispatchers.io()) {
            _bookDetails.postValue(sharedPreferenceRepository.getSessionDetails())
        }
    }

    fun updateSession(isActive: Boolean){
        _isSessionActive.value = isActive
    }

    fun getEndBookSessionRequestModel(): EndBookSessionRequestModel {
        val model = sharedPreferenceRepository.getSessionDetails()
        val endTime = System.currentTimeMillis()
        val minutes = (endTime - sharedPreferenceRepository.getSessionStartTime()).toMinutes()
        return EndBookSessionRequestModel(model.locationId, minutes.toInt(), endTime)
    }

    fun endSession(model: EndBookSessionRequestModel){
        bookRepository.endBookSession(model)
            .observeOn(schedulers.getIo())
            .map {
                if (isSuccess(it)){
                    sharedPreferenceRepository.removeSession()
                }
                it
            }
            .observeOn(schedulers.getMainThread())
            .doOnSubscribe {
                _isProgressbarVisible.postValue(true)
                disposable.add(it)
            }
            .subscribe({
                _isProgressbarVisible.value = false
                _isEndSessionSuccess.value = isSuccess(it)
                updateSession(false)
            }, {
                _errorMessage.value = it.localizedMessage
                _isProgressbarVisible.value = false
            })
    }

    private fun isSuccess(jsonObject: JsonObject): Boolean {
        return jsonObject.get("success").asBoolean
    }

    override fun onCleared() {
        super.onCleared()
        if (disposable.isDisposed.not()){
            disposable.dispose()
        }
    }

    fun setSession(model: BookDetailsModel) {
        viewModelScope.launch(dispatchers.io()) {
            sharedPreferenceRepository.setSession(model)
            _bookDetails.postValue(model)
        }
    }
}