package pt.ipt.dam2025.vetconnect.repository

import android.content.Context // Importa o contexto da aplicação para operações de ficheiros
import android.net.Uri // Importa a classe Uri para lidar com caminhos de ficheiros
import android.util.Log // Importa a classe Log para registar mensagens de erro
import kotlinx.coroutines.CoroutineScope // Importa para criar um escopo de coroutines
import kotlinx.coroutines.Dispatchers // Importa os dispatchers para definir a thread (ex: IO para rede/disco)
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull // Importa para definir o tipo de media (MIME type)
import okhttp3.MultipartBody // Importa para construir pedidos de upload de ficheiros
import okhttp3.RequestBody.Companion.asRequestBody // Importa para criar um corpo de pedido a partir de um ficheiro
import pt.ipt.dam2025.vetconnect.api.ApiService // Importa a nossa interface de API Retrofit
import pt.ipt.dam2025.vetconnect.data.AnimalDao // Importa o nosso DAO para animais
import pt.ipt.dam2025.vetconnect.model.AnimalResponse // Importa o modelo de dados do animal
import pt.ipt.dam2025.vetconnect.model.CreateAnimalRequest // Importa o modelo para criar um animal
import pt.ipt.dam2025.vetconnect.model.UpdateAnimalResponse // Importa o modelo de resposta da atualização
import pt.ipt.dam2025.vetconnect.model.UploadResponse // Importa o modelo de resposta do upload
import java.io.File // Importa a classe File para manipulação de ficheiros
import java.io.FileNotFoundException // Importa a exceção para quando um ficheiro não é encontrado
import java.io.FileOutputStream // Importa para escrever em ficheiros
import java.io.IOException // Importa a exceção para erros de Input/Output

/**
 * Repositório para gerir os dados dos animais
 * É a única fonte de verdade para os dados dos animais, coordenando a API e a base de dados local
 */
class AnimalRepository(
    private val apiService: ApiService, // Dependência para aceder à API
    private val animalDao: AnimalDao, // Dependência para aceder à base de dados local
    private val context: Context // Contexto necessário para operações de ficheiros (upload)
) {

    fun getAnimaisDoTutor(token: String, tutorId: Int): Flow<List<AnimalResponse>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAnimaisDoTutor(token, tutorId)
        }
        return animalDao.getAnimalsByTutorId(tutorId)
    }

    private suspend fun refreshAnimaisDoTutor(token: String, tutorId: Int) {
        try {
            val response = apiService.getAnimaisDoTutor("Bearer $token", tutorId)
            if (response.isSuccessful) {
                response.body()?.let { animaisDaApi ->
                    animaisDaApi.forEach { animal ->
                        animalDao.insertOrUpdate(animal)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AnimalRepository", "Falha ao refrescar animais", e)
        }
    }

    /**
     * Obtém os dados de um animal específico
     * Segue o mesmo padrão: lança um refresh e retorna um Flow da BD local
     */
    fun getAnimal(token: String, animalId: Int): Flow<AnimalResponse?> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAnimal(token, animalId)
        }
        return animalDao.getById(animalId)
    }

    /**
     * Força a atualização dos dados de um único animal a partir da API
     */
    suspend fun refreshAnimal(token: String, animalId: Int) {
        try {
            val response = apiService.getAnimal("Bearer $token", animalId)
            if (response.isSuccessful) {
                response.body()?.let { animalDaApi ->
                    animalDao.insertOrUpdate(animalDaApi)
                }
            }
        } catch (e: Exception) {
            Log.e("AnimalRepository", "Falha ao refrescar animal", e)
        }
    }

    /**
     * Cria um novo animal enviando os dados para a API
     * Em caso de sucesso, insere o novo animal (com o ID gerado) na base de dados local
     */
    suspend fun createAnimal(token: String, request: CreateAnimalRequest): Result<AnimalResponse> {
        return try {
            val response = apiService.createAnimal("Bearer $token", request)
            if (response.isSuccessful) {
                response.body()?.let {
                    animalDao.insertOrUpdate(it)
                    Result.success(it)
                } ?: Result.failure(IOException("A API não retornou dados ao criar o animal."))
            } else {
                Result.failure(IOException("Erro da API ao criar animal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza os dados de um animal existente na API
     * Em caso de sucesso, chama o refreshAnimal para atualizar a base de dados local
     */
    suspend fun updateAnimal(token: String, animalId: Int, request: CreateAnimalRequest): Result<UpdateAnimalResponse> {
        return try {
            val response = apiService.updateAnimal("Bearer $token", animalId, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    // Após o sucesso da API, atualiza os dados na base de dados local
                    refreshAnimal(token, animalId)
                    Result.success(it)
                } ?: Result.failure(IOException("A API não retornou dados ao atualizar o animal."))
            } else {
                Result.failure(IOException("Erro da API ao atualizar animal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Faz o upload da foto de um animal para a API
     * Em caso de sucesso, atualiza o animal na BD local para que a UI mostre a nova foto
     */
    suspend fun uploadFotoAnimal(token: String, animalId: Int, imageUri: Uri): Result<UploadResponse> {
        return try {
            // Cria um ficheiro temporário a partir da imagem selecionada pelo utilizador
            val file = createTempFileFromUri(context, imageUri)
                ?: return Result.failure(FileNotFoundException("Não foi possível criar um ficheiro a partir do URI"))

            // Prepara o ficheiro para ser enviado como parte de um pedido multipart
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", file.name, requestFile)

            // Chama o endpoint da API para fazer o upload
            val response = apiService.uploadFoto("Bearer $token", animalId, body)

            // Apaga o ficheiro temporário após o upload
            file.delete()

            if (response.isSuccessful) {
                response.body()?.let {
                    // Força a atualização do animal na BD para obter a nova fotoUrl
                    refreshAnimal(token, animalId)
                    Result.success(it)
                } ?: Result.failure(IOException("A API não retornou dados no upload da foto."))
            } else {
                Result.failure(IOException("Erro da API no upload da foto: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Função de utilidade para criar um ficheiro temporário a partir de um URI
     * Necessário porque o Retrofit precisa de um File para fazer uploads
     */
    private fun createTempFileFromUri(context: Context, uri: Uri): File? {
        return try {
            // Abre um stream de input para ler os dados do URI
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            // Cria um ficheiro temporário na cache da aplicação
            val file = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.jpg")
            // Abre um stream de output para escrever os dados no ficheiro
            val outputStream = FileOutputStream(file)
            // Copia os dados do input stream para o output stream
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            // Retorna o ficheiro criado
            file
        } catch (e: Exception) {
            Log.e("AnimalRepository", "Erro ao criar ficheiro temporário do URI", e)
            null
        }
    }
}