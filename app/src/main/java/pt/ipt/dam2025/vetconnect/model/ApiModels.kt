package pt.ipt.dam2025.vetconnect.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.io.Serializable

// representa um utilizador
@Entity(tableName = "users")
data class Usuario(
    //Propriedades da entidade
    @PrimaryKey val id: Int, // Chave primária
    val nome: String,
    val email: String,
    val telemovel: String?,
    val nacionalidade: String?,
    val sexo: String?,
    val cc: String?,
    val dataNascimento: String?,
    val morada: String?,
    val tipo: String,
    val token: String?,
    @SerializedName("dataregisto") val dataRegisto: String?,
    val verificado: Boolean?
)

// representa um novo utilizador
data class NovoUsuario(
    val nome: String,
    val email: String,
    val telemovel: String,
    val tipo: String
)

// update de um utilziador
data class UpdateUserRequest(
    val nome: String,
    val email: String,
    val tipo: String
)

// dados de login
data class RegistrationResponse(
    val user: Usuario,
    val message: String,
    val verificationCode: String
)

// verificação
data class VerificationRequest(
    val email: String,
    @SerializedName("codigoVerificacao") val codigo: String
)

// resposta de verificação
data class VerificationResponse(
    val message: String,
    val userId: Int
)

// criação de PIN
data class CreatePinRequest(
    val email: String,
    val pin: String
)

// resposta de criação de PIN
data class CreatePinResponse(
    val message: String,
    val userId: Int
)

// login
data class LoginRequest(
    val email: String,
    val pin: String
)

// recuperar pin
data class RecuperarPinRequest(
    val email: String
)

// recuperação de PIN
data class RecuperarPinResponse(
    val message: String,
    val codigoRecuperacao: String
)

// alteração de PIN
data class RedefinirPinRequest(
    val email: String,
    val codigoRecuperacao: String,
    val novoPin: String
)

// resposta de alteração de PIN
data class RedefinirPinResponse(
    val message: String
)

// logout
data class AlterarPinRequest(
    val pinAtual: String,
    val novoPin: String
)

// Wrapper para o corpo da resposta de login
data class LoginResponse(
    val message: String,
    val token: String,
    val user: Usuario
)

// Wrapper para o corpo da resposta de logout
data class LogoutResponse(
    val success: Boolean,
    val message: String
)

// Wrapper para o corpo da resposta de alteração de PIN
data class ChangePinResponse(
    val success: Boolean,
    val message: String
)

// Respostas genéricas
data class GenericMessageResponse(val message: String)
data class GenericSuccessResponse(val success: Boolean, val message: String)


// ANIMAIS
data class CreateAnimalRequest(
    val nome: String,
    val especie: String,
    val raca: String?,
    val dataNascimento: String?,
    val numeroChip: String?
)

// resposta de criação de animal
@Entity(tableName = "animais")
data class AnimalResponse(
    @PrimaryKey val id: Int,
    val tutorId: Int,
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String?,
    val fotoUrl: String?,
    val numeroChip: String?,
    val codigoUnico: String,
    @SerializedName("dataregisto") val dataRegisto: String?,
    @SerializedName("tutornome") val tutorNome: String?,
    @SerializedName("tutoremail") val tutorEmail: String?
)

// resposta de atualização de animal
data class AnimalResumo(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String
)

// resposta de upload de foto
data class UploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("fotoUrl") val fotoUrl: String,
    @SerializedName("filename") val filename: String,
    @SerializedName("animal") val animal: AnimalResumo? = null
)


// HISTÓRICO - EXAME
@Entity(tableName = "exames")
data class Exame(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @SerializedName("animalid") val animalId: Int,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("dataexame") val dataExame: String,
    @SerializedName("resultado") val resultado: String?,
    @SerializedName("laboratorio") val laboratorio: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("ficheirourl") val ficheiroUrl: String?,
    @SerializedName("observacoes") val observacoes: String?,
    @SerializedName("dataregisto") val dataRegisto: String?
) : Serializable

@Entity(tableName = "tipos_exame")
data class TipoExame(
    @PrimaryKey val id: Int,
    val nome: String,
    val descricao: String?
)

// Resposta da API para os tipos de exame
data class TiposExameResponse(
    val success: Boolean,
    val tipos: List<TipoExame>,
    val count: Int
)

// Resposta da API para o histórico de um animal
data class DocumentosResponse(
    val exames: List<Exame>
)

// CRIAR DOCUMENTO (agora apenas para exames)
data class CreateDocumentRequest(
    val tipo: String, // "exame"
    val animalId: Int,
    val dados: JsonElement
)

// RESPOSTA CRIAR DOCUMENTO
data class CreateDocumentResponse(
    val message: String,
    val tipo: String,
    val documento: JsonElement
)

// DELETAR DOCUMENTO
data class DeletedDocumentInfo(
    val id: Int,
    val tipo: String
)

// RESPOSTA DE DELETAR DOCUMENTO
data class DeleteDocumentResponse(
    val success: Boolean,
    val message: String,
    val documento: DeletedDocumentInfo
)

// CONSULTAS
@Entity(tableName = "clinicas")
data class Clinica(
    @PrimaryKey @SerializedName("id") val id: Int,
    val nome: String
)

//
@Entity(tableName = "veterinarios")
data class Veterinario(
    @PrimaryKey @SerializedName("id") val id: Int,
    val nome: String,
    @SerializedName("clinicaid") val clinicaId: Int
)


data class NovaConsulta(
    val animalId: Int,
    val clinicaId: Int,
    val veterinarioId: Int,
    val data: String,
    val hora: String,
    val motivo: String?
)

// resposta de criar consulta
@Entity(tableName = "consultas")
data class Consulta(
    @PrimaryKey val id: Int,
    @SerializedName("userid") val userId: Int,
    @SerializedName("animalid") val animalId: Int,
    @SerializedName("clinicaid") val clinicaId: Int,
    @SerializedName("veterinarioid") val veterinarioId: Int,
    val data: String,
    val hora: String,
    val motivo: String?,
    val estado: String,
    @SerializedName("datamarcacao") val dataMarcacao: String,
    // Joined fields
    @SerializedName("animalnome") val animalNome: String?,
    @SerializedName("clinicanome") val clinicaNome: String?,
    @SerializedName("veterinarionome") val veterinarioNome: String?
)

// resposta de listar consultas
data class CancelConsultaResponse(
    val message: String,
    val consultaId: Int
)

// VACINAS (Funcionalidade isolada)
@Entity(tableName = "vacinas")
data class Vacina(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("animalid") val animalId: Int,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("tipo_vacina_id") val tipoVacinaId: Int?,
    @SerializedName("data_agendada") val dataAgendada: String?,
    @SerializedName("dataaplicacao") val dataAplicacao: String?,
    @SerializedName("dataproxima") val dataProxima: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("lote") val lote: String?,
    @SerializedName("observacoes") val observacoes: String?,
    @SerializedName("estado") val estado: String,
    @SerializedName("notificado") val notificado: Boolean,
    @SerializedName("dataregisto") val dataRegisto: String?,
    // Fields from joined tables
    @SerializedName("descricao") val descricao: String?,
    @SerializedName("periodicidade") val periodicidade: String?,
    @SerializedName("animal_nome") val animalNome: String?,
    @SerializedName("especie") val especie: String?,
    @SerializedName("categoria") val categoria: String?
) : Serializable

// resposta de listar vacinas
@Entity(tableName = "tipos_vacina")
data class TipoVacina(
    @PrimaryKey val id: Int,
    val nome: String,
    val descricao: String?,
    val especie: String?,
    val periodicidade: String?
)

// resposta de listar tipos de vacinas
data class TiposVacinaResponse(
    val success: Boolean,
    val tipos: List<TipoVacina>,
    val count: Int
)

// resposta de agendar vacina
data class AgendarVacinaRequest(
    @SerializedName("animalId") val animalId: Int,
    @SerializedName("tipo_vacina_id") val tipoVacinaId: Int,
    @SerializedName("data_agendada") val dataAgendada: String,
    @SerializedName("observacoes") val observacoes: String?
)

// resposta de agendar vacina
data class AgendarVacinaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("vacina") val vacina: Vacina,
    @SerializedName("animal") val animal: AnimalResumo,
    @SerializedName("tipo_vacina") val tipoVacina: TipoVacina
)

// resposta de listar vacinas agendadas
data class VacinasAgendadasResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("vacinas") val vacinas: List<Vacina>
)

// resposta de listar vacinas próximas
data class VacinasProximasResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("vacinas") val vacinas: List<Vacina>,
    @SerializedName("mensagem") val mensagem: String
)

// resposta de atualizar vacina
data class UpdateVacinaRequest(
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("dataProxima") val dataProxima: String?,
    @SerializedName("observacoes") val observacoes: String?
)

// resposta de atualizar vacina
data class UpdateVacinaResponse(
    val success: Boolean,
    @SerializedName("mensagem") val message: String,
    val vacina: Vacina
)

// resposta de cancelar vacina
data class CanceledVacinaInfo(
    val id: Int,
    val tipo: String,
    @SerializedName("animalid") val animalId: Int
)

// resposta de cancelar vacina
data class CancelVacinaResponse(
    val success: Boolean,
    val message: String,
    val vacina: CanceledVacinaInfo
)

// resposta de marcar vacina realizada
data class MarcarVacinaRealizadaRequest(
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("lote") val lote: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("observacoes") val observacoes: String?
)

// resposta de marcar vacina realizada
data class MarkVacinaRealizadaResponse(
    val success: Boolean,
    @SerializedName("mensagem") val message: String,
    val vacina: Vacina
)
