package pt.ipt.dam2025.vetconnect.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Modelos de Ddos de UTILIZADORES e AUTENTICAÇÃO
 */
@Entity(tableName = "users")
data class Usuario(
    @PrimaryKey val id: Int,
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
    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto") val dataRegisto: String?,
    val verificado: Boolean?
)

/*
 * modelo de dados para o pedido de criação de um novo utilizador
 */
data class NovoUsuario(
    val nome: String,
    val email: String,
    val telemovel: String,
    val tipo: String
)

/*
 * modelo para o pedido de atualização dos dados de um utilizador
 */
data class UpdateUserRequest(
    val nome: String,
    val email: String,
    val tipo: String
)

/*
 * resposta da API após o registo de um novo utilizador
 */
data class RegistrationResponse(
    val user: Usuario,
    val message: String,
    val verificationCode: String
)

/*
 * modelo para o pedido de verificação de um código enviado por email
 */
data class VerificationRequest(
    val email: String,
    @SerializedName("codigoVerificacao") val codigo: String
)

/*
 * resposta da API após uma verificação de código bem-sucedida
 */
data class VerificationResponse(
    val message: String,
    val userId: Int
)

/*
 * modelo para o pedido de criação de um PIN de acesso
 */
data class CreatePinRequest(
    val email: String,
    val pin: String
)

/*
 * resposta da API após a criação de um PIN
 */
data class CreatePinResponse(
    val message: String,
    val userId: Int
)

/*
 * modelo para o pedido de login de um utilizador
 */
data class LoginRequest(
    val email: String,
    val pin: String
)

/*
 * resposta da API após um login bem-sucedido
 */
data class LoginResponse(
    val message: String,
    val token: String,
    val user: Usuario
)

/*
 * modelo para o pedido de recuperação de PIN
 */
data class RecuperarPinRequest(
    val email: String
)

/*
 * resposta da API ao pedido de recuperação de PIN
 */
data class RecuperarPinResponse(
    val message: String,
    val codigoRecuperacao: String
)

/*
 * modelo para o pedido de redefinição de um novo PIN usando um código de recuperação
 */
data class RedefinirPinRequest(
    val email: String,
    val codigoRecuperacao: String,
    val novoPin: String
)

/*
 * resposta da API após uma redefinição de PIN bem-sucedida
 */
data class RedefinirPinResponse(
    val message: String
)

/*
 * modelo para o pedido de alteração do PIN atual (requer autenticação)
 */
data class AlterarPinRequest(
    val pinAtual: String,
    val novoPin: String
)

/*
 * resposta da API após uma alteração de PIN bem-sucedida
 */
data class ChangePinResponse(
    val success: Boolean,
    val message: String
)

/*
 * resposta da API após um pedido de logout
 */
data class LogoutResponse(
    val success: Boolean,
    val message: String
)

/*
 * modelo de resposta genérico da API contendo apenas uma mensagem
 */
data class GenericMessageResponse(val message: String)

/*
 * modelo de resposta genérico da API contendo um estado de sucesso e uma mensagem
 */
data class GenericSuccessResponse(val success: Boolean, val message: String)


/**
 * Modelos de Dados de ANIMAIS
 * Representa um animal de estimação
 * Usado como entidade da base de dados (tabela 'animais')
 * e como modelo em respostas da API
 */

/*
 * modelo para o pedido de criação de um novo animal
 */

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
    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto") val dataRegisto: String?,
    @SerializedName("tutornome") @ColumnInfo(name = "tutornome") val tutorNome: String?,
    @SerializedName("tutoremail") @ColumnInfo(name = "tutoremail") val tutorEmail: String?
)

data class CreateAnimalRequest(
    val nome: String,
    val especie: String,
    val raca: String?,
    val dataNascimento: String?,
    val numeroChip: String?
)

/*
 * representa uma versão resumida de um animal usada em respostas de outras operações
 */
data class AnimalResumo(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String
)

/*
 * resposta da API após o upload bem-sucedido da foto de um animal
 */
data class UploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("fotoUrl") val fotoUrl: String,
    @SerializedName("filename") val filename: String,
    @SerializedName("animal") val animal: AnimalResumo? = null
)

/**
 * Modelos de Dados de HISTÓRICO (EXAMES)
 * Representa um exame médico do histórico de um animal
 * Pode ser criado manualmente na app ou sincronizado da API
 * Usado como entidade da base de dados (tabela 'exames')
 */
@Parcelize
@Entity(tableName = "exames")
data class Exame(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("animalid") @ColumnInfo(name = "animalid") val animalId: Int,
    @SerializedName("tipo_exame_id") @ColumnInfo(name = "tipo_exame_id") val tipoExameId: Int?,
    @SerializedName("tipo_nome") @ColumnInfo(name = "tipo_nome") val tipo: String?,
    @SerializedName("dataexame") @ColumnInfo(name = "dataexame") val dataExame: String?,
    @SerializedName("clinicaid") @ColumnInfo(name = "clinicaid") val clinicaId: Int?,
    @SerializedName("clinicanome") @ColumnInfo(name = "clinicanome") val clinicaNome: String?,
    @SerializedName("veterinarioid") @ColumnInfo(name = "veterinarioid") val veterinarioId: Int?,
    @SerializedName("veterinarionome") @ColumnInfo(name = "veterinarionome") val veterinarioNome: String?,
    @SerializedName("resultado") val resultado: String?,
    @SerializedName("observacoes") val observacoes: String?,
    @SerializedName("fotourl") @ColumnInfo(name = "fotourl") val ficheiroUrl: String?,
    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto") val dataRegisto: String?
) : Parcelable

/*
 * representa um tipo de exame (ex: Raio-X, Análise de Sangue)
 * usado como entidade da base de dados (tabela 'tipos_exame')
 */
@Entity(tableName = "tipos_exame")
data class TipoExame(
    @PrimaryKey val id: Int,
    val nome: String,
    val descricao: String?
)

/*
 * resposta da API contendo uma lista de tipos de exame disponíveis
 */
data class TiposExameResponse(
    val success: Boolean,
    val tipos: List<TipoExame>,
    val count: Int
)

/*
 * resposta da API que agrega os exames de histórico de um animal
 */
data class ExamesResponse(
    val success: Boolean,
    val count: Int,
    val exames: List<Exame>
)

/*
 * modelo para o pedido de criação de um novo exame
 */
data class CreateExameRequest(
    @SerializedName("animalId") val animalId: Int,
    @SerializedName("tipo_exame_id") val tipoExameId: Int,
    @SerializedName("dataExame") val dataExame: String,
    @SerializedName("clinicaId") val clinicaId: Int,
    @SerializedName("veterinarioId") val veterinarioId: Int,
    @SerializedName("resultado") val resultado: String?,
    @SerializedName("observacoes") val observacoes: String?
)

/*
 * modelo para o pedido de edição de um exame existente
 */
data class UpdateExameRequest(
    @SerializedName("tipo_exame_id") val tipoExameId: Int?,
    @SerializedName("dataExame") val dataExame: String?,
    @SerializedName("clinicaId") val clinicaId: Int?,
    @SerializedName("veterinarioId") val veterinarioId: Int?,
    @SerializedName("resultado") val resultado: String?,
    @SerializedName("observacoes") val observacoes: String?
)

/*
 * resposta da API após a criação de um novo exame
 */
data class CreateExameResponse(
    val success: Boolean,
    val message: String,
    val exame: Exame
)

/*
* Resposta da API após o upload da foto de um exame
*/
data class AddExameFotoResponse(
    val success: Boolean,
    val message: String,
    val fotoUrl: String
)


/**
 * Modelos de Dados de CONSULTAS
 * Representa uma clínica veterinária
 * Usado como entidade da base de dados (tabela 'clinicas')
 */
@Entity(tableName = "clinicas")
data class Clinica(
    @PrimaryKey @SerializedName("id") val id: Int,
    val nome: String
)

/**
 * Representa um médico veterinário
 * Usado como entidade da base de dados (tabela 'veterinarios')
 */
@Entity(tableName = "veterinarios")
data class Veterinario(
    @PrimaryKey @SerializedName("id") val id: Int,
    val nome: String,
    @SerializedName("clinicaid") @ColumnInfo(name = "clinicaid") val clinicaId: Int
)

/**
 * Representa uma consulta
 * Usado como entidade da base de dados (tabela 'consultas')
 * e como modelo em respostas da API
 */
@Entity(tableName = "consultas")
data class Consulta(
    @PrimaryKey val id: Int,
    @SerializedName("userid") @ColumnInfo(name = "userid") val userId: Int,
    @SerializedName("animalid") @ColumnInfo(name = "animalid") val animalId: Int,
    @SerializedName("clinicaid") @ColumnInfo(name = "clinicaid") val clinicaId: Int,
    @SerializedName("veterinarioid") @ColumnInfo(name = "veterinarioid") val veterinarioId: Int,
    val data: String,
    val hora: String,
    val motivo: String?,
    val estado: String,
    @SerializedName("datamarcacao") @ColumnInfo(name = "datamarcacao") val dataMarcacao: String,
    // Campos extra obtidos por junção de tabelas na API
    @SerializedName("animalnome") @ColumnInfo(name = "animalnome") val animalNome: String?,
    @SerializedName("clinicanome") @ColumnInfo(name = "clinicanome") val clinicaNome: String?,
    @SerializedName("veterinarionome") @ColumnInfo(name = "veterinarionome") val veterinarioNome: String?
)

/*
 * modelo para o pedido de marcação de uma nova consulta
 */
data class NovaConsulta(
    val animalId: Int,
    val clinicaId: Int,
    val veterinarioId: Int,
    val data: String,
    val hora: String,
    val motivo: String?
)

/*
 * resposta da API após o cancelamento de uma consulta
 */
data class CancelConsultaResponse(
    val message: String,
    val consultaId: Int
)


/**
 * Modelos de Dados de VACINAS
 * Representa uma vacina (agendada ou administrada)
 * Usado como entidade da base de dados (tabela 'vacinas') e como modelo em respostas da API
 */
@Parcelize
@Entity(tableName = "vacinas")
data class Vacina(
    @PrimaryKey @SerializedName("id")
    val id: Int,

    @SerializedName("animalid") @ColumnInfo(name = "animalid")
    val animalId: Int,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("tipo_vacina_id") @ColumnInfo(name = "tipo_vacina_id")
    val tipoVacinaId: Int?,

    @SerializedName("data_agendada") @ColumnInfo(name = "data_agendada")
    val dataAgendada: String?,

    @SerializedName("dataaplicacao") @ColumnInfo(name = "dataaplicacao")
    val dataAplicacao: String?,

    @SerializedName("dataproxima") @ColumnInfo(name = "dataproxima")
    val dataProxima: String?,

    @SerializedName("veterinario")
    val veterinario: String?,

    @SerializedName("lote")
    val lote: String?,

    @SerializedName("observacoes")
    val observacoes: String?,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("notificado")
    val notificado: Boolean,

    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto")
    val dataRegisto: String?,

    @SerializedName("descricao")
    val descricao: String?,

    @SerializedName("periodicidade")
    val periodicidade: String?,

    @SerializedName("animal_nome") @ColumnInfo(name = "animal_nome")
    val animalNome: String?,

    @SerializedName("especie")
    val especie: String?,

    @SerializedName("categoria")
    val categoria: String?
) : Parcelable

/**
 * Representa um tipo de vacina (ex: Raiva, Leptospirose)
 * Usado como entidade da base de dados (tabela 'tipos_vacina')
 */
@Entity(tableName = "tipos_vacina")
data class TipoVacina(
    @PrimaryKey val id: Int,
    val nome: String,
    val descricao: String?,
    val especie: String?,
    val periodicidade: String?
)

/*
 * resposta da API contendo uma lista de tipos de vacina disponíveis
 */
data class TiposVacinaResponse(
    val success: Boolean,
    val tipos: List<TipoVacina>,
    val count: Int
)

/*
 * modelo para o pedido de agendamento de uma nova vacina
 */
data class AgendarVacinaRequest(
    @SerializedName("animalId") val animalId: Int,
    @SerializedName("tipo_vacina_id") val tipoVacinaId: Int,
    @SerializedName("clinicaId") val clinicaId: Int,
    @SerializedName("veterinarioId") val veterinarioId: Int,
    @SerializedName("data_agendada") val dataAgendada: String,
    @SerializedName("observacoes") val observacoes: String?
)

/*
 * resposta da API após o agendamento de uma vacina
 */
data class AgendarVacinaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("vacina") val vacina: Vacina,
    @SerializedName("animal") val animal: AnimalResumo,
    @SerializedName("tipo_vacina") val tipoVacina: TipoVacina
)

/*
 * resposta da API que contém uma lista de vacinas agendadas
 */
data class VacinasAgendadasResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("vacinas") val vacinas: List<Vacina>
)

/*
 * resposta da API que contém uma lista de vacinas com data próxima
 */
data class VacinasProximasResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("vacinas") val vacinas: List<Vacina>,
    @SerializedName("mensagem") val mensagem: String
)

/*
 * modelo para o pedido de atualização de uma vacina existente
 */
data class UpdateVacinaRequest(
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("dataProxima") val dataProxima: String?,
    @SerializedName("observacoes") val observacoes: String?
)

/*
 * resposta da API após a atualização de uma vacina
 */
data class UpdateVacinaResponse(
    val success: Boolean,
    @SerializedName("mensagem") val message: String,
    val vacina: Vacina
)

/*
 * contém informação sobre uma vacina que foi cancelada
 */
data class CanceledVacinaInfo(
    val id: Int,
    val tipo: String,
    @SerializedName("animalid") val animalId: Int
)

/*
 * resposta da API após o cancelamento de uma vacina
 */
data class CancelVacinaResponse(
    val success: Boolean,
    val message: String,
    val vacina: CanceledVacinaInfo
)

/*
 * modelo para o pedido de marcação de uma vacina como 'realizada'
 */
data class MarcarVacinaRealizadaRequest(
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("lote") val lote: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("observacoes") val observacoes: String?
)

/*
 * resposta da API após marcar uma vacina como 'realizada'
 */
data class MarkVacinaRealizadaResponse(
    val success: Boolean,
    @SerializedName("mensagem") val message: String,
    val vacina: Vacina
)
