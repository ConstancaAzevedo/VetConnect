package pt.ipt.dam2025.trabalho.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pt.ipt.dam2025.trabalho.api.ApiService
import pt.ipt.dam2025.trabalho.data.AnimalDao
import pt.ipt.dam2025.trabalho.model.AnimalResponse
import pt.ipt.dam2025.trabalho.model.CreateAnimalRequest
import pt.ipt.dam2025.trabalho.model.GenericMessageResponse
import pt.ipt.dam2025.trabalho.model.UploadResponse
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

// Classe de repositório para animais

class AnimalRepository(
    private val apiService: ApiService,
    private val animalDao: AnimalDao,
    private val context: Context
) {

    /**
     * Obtém os animais de um tutor, retornando um Flow da base de dados local.
     * Inicia uma atualização em background para buscar os dados mais recentes da API.
     */
    fun getAnimaisDoTutor(token: String, tutorId: Int): Flow<List<AnimalResponse>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAnimaisDoTutor(token, tutorId)
        }
        return animalDao.getAnimalsByTutorId(tutorId)
    }

    /**
     * Força a atualização da lista de animais de um tutor a partir da API e guarda na base de dados local.
     */
    suspend fun refreshAnimaisDoTutor(token: String, tutorId: Int) {
        try {
            val response = apiService.getAnimaisDoTutor("Bearer $token", tutorId)
            if (response.isSuccessful) {
                response.body()?.let { animaisDaApi ->
                    animaisDaApi.forEach { animal ->
                        animalDao.insertOrUpdate(animal)
                    }
                }
            } else {
                Log.e("AnimalRepository", "Erro ao refrescar animais: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AnimalRepository", "Falha ao refrescar animais", e)
        }
    }

    /**
     * Obtém um animal específico, retornando um Flow da base de dados local.
     * Inicia uma atualização em background para buscar os dados mais recentes da API.
     */
    fun getAnimal(token: String, animalId: Int): Flow<AnimalResponse?> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAnimal(token, animalId)
        }
        return animalDao.getById(animalId)
    }

    /**
     * Força a atualização de um animal a partir da API e guarda na base de dados local.
     */
    suspend fun refreshAnimal(token: String, animalId: Int) {
        try {
            val response = apiService.getAnimal("Bearer $token", animalId)
            if (response.isSuccessful) {
                response.body()?.let { animalDaApi ->
                    animalDao.insertOrUpdate(animalDaApi)
                }
            } else {
                Log.e("AnimalRepository", "Erro ao refrescar animal: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AnimalRepository", "Falha ao refrescar animal", e)
        }
    }

    /**
     * Cria um novo animal enviando os dados para a API.
     * Em caso de sucesso, o novo animal (com ID e outros dados gerados pelo servidor)
     * é inserido na base de dados local.
     */
    suspend fun createAnimal(token: String, request: CreateAnimalRequest): Result<AnimalResponse> {
        return try {
            val response = apiService.createAnimal("Bearer $token", request)
            if (response.isSuccessful) {
                response.body()?.let { novoAnimal ->
                    animalDao.insertOrUpdate(novoAnimal) // Insere o animal retornado pela API na BD
                    Result.success(novoAnimal)
                } ?: Result.failure(IOException("A API não retornou dados ao criar o animal."))
            } else {
                Result.failure(IOException("Erro da API ao criar animal: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apaga um animal, tanto na API como na base de dados local.
     */
    suspend fun deleteAnimal(token: String, animalId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteAnimal("Bearer $token", animalId)
            if (response.isSuccessful) {
                animalDao.deleteById(animalId)
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao apagar animal: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Faz o upload da foto de um animal para a API.
     * Em caso de sucesso, atualiza o URL da foto do animal na base de dados local.
     */
    suspend fun uploadFotoAnimal(token: String, animalId: Int, imageUri: Uri): Result<UploadResponse> {
        return try {
            val file = createTempFileFromUri(context, imageUri)
                ?: return Result.failure(FileNotFoundException("Não foi possível criar um ficheiro a partir do URI: $imageUri"))

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", file.name, requestFile)

            val response = apiService.uploadFoto("Bearer $token", animalId, body)

            file.delete() // Limpa o ficheiro temporário

            if (response.isSuccessful) {
                response.body()?.let { uploadResponse ->
                    // Após o upload, atualiza o animal na base de dados local com o novo fotoUrl
                    refreshAnimal("Bearer $token", animalId)
                    Result.success(uploadResponse)
                } ?: Result.failure(IOException("A API não retornou dados no upload da foto."))
            } else {
                Result.failure(IOException("Erro da API no upload da foto: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cria um ficheiro temporário a partir de um URI, copiando o seu conteúdo.
     */
    private fun createTempFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("AnimalRepository", "Erro ao criar ficheiro temporário do URI", e)
            null
        }
    }
}
