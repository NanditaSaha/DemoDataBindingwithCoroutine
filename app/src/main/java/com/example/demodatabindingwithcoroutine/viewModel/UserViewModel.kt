package com.example.demodatabindingwithcoroutine.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demodatabindingwithcoroutine.data.api.UserApi
import com.example.demodatabindingwithcoroutine.data.model.LoadingState
import com.example.demodatabindingwithcoroutine.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val userApi: UserApi): ViewModel() {
    private val _loading = MutableLiveData<LoadingState>()
    val loading: LiveData<LoadingState>
        get() = _loading

    private val _data = MutableLiveData<List<User>>()
    val data: LiveData<List<User>>
        get() = _data

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loading.postValue(LoadingState.LOADING)
                val response = userApi.getUserAsync()
                if (response.isSuccessful) {
                    _data.postValue(response.body())
                    _loading.postValue(LoadingState.LOADED)
                } else {
                    _loading.postValue(LoadingState.error(response.message()))
                }

            } catch (e: Exception) {
                _loading.postValue(LoadingState.error(e.message))
            }
        }
    }
}