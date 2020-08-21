package com.ricki.test.ViewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ricki.test.Interface.APIInterface
import com.ricki.test.Model.UserItemModel
import kotlinx.coroutines.launch

class UserViewModel (application: Application) : BaseViewModel(application) {
    private val _listOfUser = MutableLiveData<ArrayList<UserItemModel>>()
    val listOfUser : LiveData<ArrayList<UserItemModel>>
        get() = _listOfUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean>
        get() = _isLoading

    private val _isStatus = MutableLiveData<Boolean>()
    val isStatus : LiveData<Boolean>
        get() = _isStatus

    private val _messageException = MutableLiveData<String>()
    val messageException : LiveData<String>
        get() = _messageException

    fun fetchUser(keyword: String, page: Int, pageSize: Int) {
        launch {
            try{
                _isLoading.value = true
                val result = APIInterface.APIAllServices.retrofitService.getAllUser(keyword, page, pageSize)
                if(result.items.size > 0){
                    _listOfUser.value = result.items
                    _isLoading.value = false
                    _isStatus.value = false
                }else{
                    _isLoading.value = false
                    _isStatus.value = true
                }
            }
            catch(t : Throwable){
                _isLoading.value = false
                _listOfUser.value = arrayListOf()
                _messageException.value = t.message.toString()
            }
        }
    }
}