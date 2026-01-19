package pt.ipt.dam2025.trabalho.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.Clinica
import pt.ipt.dam2025.trabalho.model.Consulta
import pt.ipt.dam2025.trabalho.model.NovaConsulta
import pt.ipt.dam2025.trabalho.model.Veterinario
import java.io.IOException

class MarcarConsultaViewModel : ViewModel() {

    private val _clinicas = MutableLiveData<List<Clinica>>()
    val clinicas: LiveData<List<Clinica>> = _clinicas

    private val _veterinarios = MutableLiveData<List<Veterinario>>()
    val veterinarios: LiveData<List<Veterinario>> = _veterinarios

    private val _consultaResult = MutableLiveData<Result<Consulta>>()
    val consultaResult: LiveData<Result<Consulta>> = _consultaResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        fetchClinicas()
    }

    /**
     * Busca a lista de todas as clínicas na API.
     */
    private fun fetchClinicas() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getClinicas()
                if (response.isSuccessful) {
                    _clinicas.postValue(response.body())
                } else {
                    throw IOException("Erro ao carregar clínicas: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MarcarConsultaViewModel", "Falha em fetchClinicas", e)
                _errorMessage.postValue("Erro ao carregar as clínicas: ${e.message}")
            }
        }
    }

    /**
     * Busca os veterinários de uma clínica específica.
     */
    fun fetchVeterinariosPorClinica(clinicaId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getVeterinariosPorClinica(clinicaId)
                if (response.isSuccessful) {
                    _veterinarios.postValue(response.body())
                } else {
                    throw IOException("Erro ao carregar veterinários: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MarcarConsultaViewModel", "Falha em fetchVeterinariosPorClinica", e)
                _errorMessage.postValue("Erro ao carregar os veterinários: ${e.message}")
            }
        }
    }

    /**
     * Envia um pedido para marcar uma nova consulta.
     * O token é necessário para autorizar a operação.
     */
    fun marcarConsulta(token: String, novaConsulta: NovaConsulta) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.marcarConsulta("Bearer $token", novaConsulta)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _consultaResult.postValue(Result.success(it))
                    } ?: throw IOException("Resposta da API nula ao marcar consulta.")
                } else {
                    // Lida com erros específicos da API, como conflito de horário
                    val errorMsg = when(response.code()) {
                        409 -> "Já existe uma consulta neste horário."
                        400 -> "Dados da consulta inválidos."
                        else -> "Ocorreu um erro no servidor."
                    }
                    throw IOException(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("MarcarConsultaViewModel", "Falha em marcarConsulta", e)
                _consultaResult.postValue(Result.failure(e))
            }
        }
    }
}