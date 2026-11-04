package com.example.aeroback.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aeroback.data.repository.ReservaRepository
import com.example.aeroback.data.repository.VooRepository

class ReservaViewModelFactory(
    private val reservaRepository: ReservaRepository,
    private val vooRepository: VooRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservaViewModel::class.java)) {
            return ReservaViewModel(reservaRepository, vooRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}
