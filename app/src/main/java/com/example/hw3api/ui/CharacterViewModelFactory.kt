package com.example.hw3api.ui

import com.example.hw3api.data.CharacterRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CharacterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterViewModel(CharacterRepository()) as T
    }
}