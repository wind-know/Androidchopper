package com.example.androidchopper.presentation.viewmodel
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidchopper.data.model.NavigationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class  NavigationViewModel : ViewModel() {
    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Idle)
    val navigationState: StateFlow<NavigationState> = _navigationState

    fun loadNavigationInfo(command: String = "") {
        viewModelScope.launch {
            _navigationState.value = NavigationState.Loading
            try {
                val infos = NavigationModel().getNavigationInfo(command)
                _navigationState.value = NavigationState.Success(infos)
            } catch (e: Exception) {
                _navigationState.value = NavigationState.Error(e.message ?: "加载失败")
            }
        }
    }
}

sealed class NavigationState {
    object Idle : NavigationState()
    object Loading : NavigationState()
    data class Success(val info: List<NavigationInfo>) : NavigationState()
    data class Error(val message: String) : NavigationState()
}

data class NavigationInfo(
    val fragmentName: String,
    val fragment: Fragment
)