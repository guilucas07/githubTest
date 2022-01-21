package com.example.aestudio.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.Serializable

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableSharedFlow<User>()
    val user: SharedFlow<User>
        get() = _user

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String>
        get() = _errorMessage

    fun onNextButtonClick(userName: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                if (userName.isEmpty()) {
                    _errorMessage.emit("Username can't be null")
                    return@launch
                }
                val user = withContext(Dispatchers.IO) { repository.searchUser(userName) }
                if (user != null)
                    _user.emit(user)
                else
                    _errorMessage.emit("This user doesn't exist")

            } catch (ex: HttpException) {
                if (ex.code() == 404) {
                    _errorMessage.emit("This user doesn't exist")
                }
            }
        }
    }
}

interface GithubService {
    @GET("/users/{username}")
    suspend fun getUser(@Path("username") userName: String): User?
}

data class User(@SerializedName("name") val name: String) : Serializable

class UserRepository(private val githubService: GithubService) {
    suspend fun searchUser(userName: String): User? {
        return githubService.getUser(userName)
    }
}