package pt.ipt.dam2025.vetconnect.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Modelos de Dados de UTILIZADORES e AUTENTICAÇÃO
 * Representa um Utilizador na aplicação
 * É usado como entidade para a tabela 'users' da base de dados Room
 * e como modelo de dados para as respostas da API
 */
@Entity(tableName = "users") // Anotação do Room que marca esta classe como uma tabela da base de dados
data class Utilizador(
    @PrimaryKey val id: Int, // Chave primária da tabela
    val nome: String, // Nome do utilizador
    val email: String, // Email do utilizador
    val telemovel: String?, // Número de telemóvel (opcional)
    val nacionalidade: String?,
    val sexo: String?,
    val cc: String?,
    val dataNascimento: String?,
    val morada: String?,
    val tipo: String, // Tipo de utilizador (ex: 'tutor')
    val token: String?, // Token de autenticação (guardado localmente mas não vem da API de utilizador)
    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto") val dataRegisto: String?,
    val verificado: Boolean?
)

/**
 * Modelo de dados para o pedido de criação de um novo utilizador
 * Usado para enviar os dados no corpo de um pedido POST para a API
 */
data class NovoUtilizador(
    val nome: String,
    val email: String,
    val telemovel: String,
    val tipo: String
)

/**
 * Modelo para o pedido de atualização dos dados de um utilizador
 */
data class UpdateUserRequest(
    val nome: String,
    val email: String,
    val tipo: String
)

/**
 * Modelo de dados para a resposta da API após o registo de um novo utilizador
 */
data class RegistrationResponse(
    val user: Utilizador, // Os dados do utilizador recém-criado
    val message: String, // Mensagem de sucesso
    val verificationCode: String // O código de verificação enviado (para depuração)
)

/**
 * Modelo para o pedido de verificação de um código enviado por email
 */
data class VerificationRequest(
    val email: String,
    @SerializedName("codigoVerificacao") val codigo: String
)

/**
 * Modelo de dados para a resposta da API após uma verificação de código bem-sucedida
 */
data class VerificationResponse(
    val message: String,
    val userId: Int
)

/**
 * Modelo para o pedido de criação de um PIN de acesso
 */
data class CreatePinRequest(
    val email: String,
    val pin: String
)

/**
 * Modelo de dados para a resposta da API após a criação de um PIN
 */
data class CreatePinResponse(
    val message: String,
    val userId: Int
)

/**
 * Modelo para o pedido de login de um utilizador
 */
data class LoginRequest(
    val email: String,
    val pin: String
)

/**
 * Modelo de dados para a resposta da API após um login bem-sucedido
 */
data class LoginResponse(
    val message: String,
    val token: String, // O token de sessão para usar em pedidos futuros
    val user: Utilizador // Os dados do utilizador que fez login
)

/**
 * Modelo para o pedido de alteração do PIN atual (requer autenticação)
 */
data class AlterarPinRequest(
    val pinAtual: String,
    val novoPin: String
)

/**
 * Modelo de dados para a resposta da API após uma alteração de PIN bem-sucedida
 */
data class ChangePinResponse(
    val success: Boolean,
    val message: String
)

/**
 * Modelo de dados para a resposta da API após um pedido de logout
 */
data class LogoutResponse(
    val success: Boolean,
    val message: String
)

/**
 * Modelo de resposta genérico da API contendo apenas uma mensagem
 */
data class GenericMessageResponse(val message: String)

/**
 * Modelo de resposta genérico da API contendo um estado de sucesso e uma mensagem
 */
data class GenericSuccessResponse(val success: Boolean, val message: String)


/**
 * Modelos de Dados de ANIMAIS
 * Representa um animal de estimação
 * É usado como entidade para a tabela 'animais' da base de dados Room
 * e como modelo em respostas da API
 */
@Entity(tableName = "animais") // Anotação do Room para criar a tabela 'animais'
data class AnimalResponse(
    @PrimaryKey val id: Int, // Chave primária da tabela
    val tutorId: Int, // ID do tutor a que o animal pertence
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String?,
    val fotoUrl: String?, // URL da foto do animal
    val numeroChip: String?,
    val codigoUnico: String, // Código único do animal para partilha
    // Mapeia o campo 'dataregisto' do JSON para a propriedade 'dataRegisto' e coluna 'dataregisto'
    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto") val dataRegisto: String?,
    @SerializedName("tutornome") @ColumnInfo(name = "tutornome") val tutorNome: String?,
    @SerializedName("tutoremail") @ColumnInfo(name = "tutoremail") val tutorEmail: String?
)

/**
 * Modelo para o pedido de criação de um novo animal
 */
data class CreateAnimalRequest(
    val nome: String,
    val especie: String,
    val raca: String?,
    val dataNascimento: String?,
    val numeroChip: String?
)

/**
 * Representa uma versão resumida de um animal usada em respostas de outras operações
 */
data class AnimalResumo(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String
)

/**
 * Modelo de dados para a resposta da API após o upload bem-sucedido da foto de um animal
 */
data class UploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("fotoUrl") val fotoUrl: String, // A nova URL da foto
    @SerializedName("filename") val filename: String,
    @SerializedName("animal") val animal: AnimalResumo? = null
)

/**
 * Modelo de dados para a resposta da API após a atualização de um animal
 */
data class UpdateAnimalResponse(
    val success: Boolean,
    val message: String,
    val animal: AnimalResponse // Os dados completos do animal atualizado
)


/**
 * Modelos de Dados de HISTÓRICO (EXAMES)
 * Representa um exame médico do histórico de um animal
 * @Parcelize permite que objetos desta classe sejam passados entre Fragments
 * É também uma entidade para a tabela 'exames' da base de dados
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

/**
 * Representa um tipo de exame (ex: Raio-X Análise de Sangue)
 * É uma entidade para a tabela 'tipos_exame' da base de dados
 */
@Entity(tableName = "tipos_exame")
data class TipoExame(
    @PrimaryKey val id: Int,
    val nome: String,
    val descricao: String?
)

/**
 * Modelo de dados para a resposta da API contendo uma lista de tipos de exame disponíveis
 */
data class TiposExameResponse(
    val success: Boolean,
    val tipos: List<TipoExame>,
    val count: Int
)

/**
 * Modelo de dados para a resposta da API que agrega os exames de histórico de um animal
 */
data class ExamesResponse(
    val success: Boolean,
    val count: Int,
    val exames: List<Exame>
)

/**
 * Modelo para o pedido de criação de um novo exame
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

/**
 * Modelo para o pedido de edição de um exame existente
 */
data class UpdateExameRequest(
    @SerializedName("tipo_exame_id") val tipoExameId: Int?,
    @SerializedName("dataExame") val dataExame: String?,
    @SerializedName("clinicaId") val clinicaId: Int?,
    @SerializedName("veterinarioId") val veterinarioId: Int?,
    @SerializedName("resultado") val resultado: String?,
    @SerializedName("observacoes") val observacoes: String?
)

/**
 * Modelo de dados para a resposta da API após a criação de um novo exame
 */
data class CreateExameResponse(
    val success: Boolean,
    val message: String,
    val exame: Exame
)

/**
 * Modelo de dados para a Resposta da API após o upload da foto de um exame
 */
data class AddExameFotoResponse(
    val success: Boolean,
    val message: String,
    val fotoUrl: String
)


/**
 * Modelos de Dados de CONSULTAS
 * Representa uma clínica veterinária
 * É uma entidade para a tabela 'clinicas' da base de dados
 */
@Entity(tableName = "clinicas")
data class Clinica(
    @PrimaryKey @SerializedName("id") val id: Int,
    val nome: String
)

/**
 * Representa um médico veterinário
 * É uma entidade para a tabela 'veterinarios' da base de dados
 */
@Entity(tableName = "veterinarios")
data class Veterinario(
    @PrimaryKey @SerializedName("id") val id: Int,
    val nome: String,
    @SerializedName("clinicaid") @ColumnInfo(name = "clinicaid") val clinicaId: Int
)

/**
 * Representa uma consulta
 * É uma entidade para a tabela 'consultas' da base de dados e um modelo para respostas da API
 */
@Parcelize
@Entity(tableName = "consultas")
data class Consulta(
    @PrimaryKey val id: Int,
    @SerializedName("userid") @ColumnInfo(name = "userid") val userId: Int,
    @SerializedName("animalid") @ColumnInfo(name = "animalid") val animalId: Int,
    @SerializedName("clinicaid") @ColumnInfo(name = "clinicaid") val clinicaId: Int,
    @SerializedName("veterinarioid") @ColumnInfo(name = "veterinarioid") val veterinarioId: Int,
    val data: String,
    val motivo: String?,
    @SerializedName("observacoes") @ColumnInfo(name = "observacoes") val observacoes: String?,
    val estado: String,
    @SerializedName("datamarcacao") @ColumnInfo(name = "datamarcacao") val dataMarcacao: String,
    // Campos extra que vêm da API através de junções (joins) de tabelas
    @SerializedName("animalnome") @ColumnInfo(name = "animalnome") val animalNome: String?,
    @SerializedName("clinicanome") @ColumnInfo(name = "clinicanome") val clinicaNome: String?,
    @SerializedName("veterinarionome") @ColumnInfo(name = "veterinarionome") val veterinarioNome: String?
) : Parcelable

/**
 * Modelo para o pedido de marcação de uma nova consulta
 */
data class NovaConsulta(
    val animalId: Int,
    val clinicaId: Int,
    val veterinarioId: Int,
    val data: String,
    val motivo: String, // O motivo da consulta é obrigatório
    val observacoes: String? // As observações são opcionais
)

/**
 * Modelo para o pedido de atualização de uma consulta existente
 */
data class UpdateConsultaRequest(
    val motivo: String?,
    val data: String?,
    val clinicaId: Int?,
    val veterinarioId: Int?,
    val observacoes: String?
)

/**
 * Modelo de dados para a resposta da API após o cancelamento de uma consulta
 */
data class CancelConsultaResponse(
    val message: String,
    val consultaId: Int
)


/**
 * Modelos de Dados de VACINAS
 * Representa uma vacina (agendada ou administrada)
 * É uma entidade para a tabela 'vacinas' da base de dados e um modelo para respostas da API
 */
@Parcelize
@Entity(tableName = "vacinas")
data class Vacina(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("animalid") @ColumnInfo(name = "animalid") val animalId: Int,
    @SerializedName("tipo") val tipo: String, // Nome do tipo de vacina
    @SerializedName("tipo_vacina_id") @ColumnInfo(name = "tipo_vacina_id") val tipoVacinaId: Int?,
    @SerializedName("data_agendada") @ColumnInfo(name = "data_agendada") val dataAgendada: String?,
    @SerializedName("dataaplicacao") @ColumnInfo(name = "dataaplicacao") val dataAplicacao: String?,
    @SerializedName("observacoes") val observacoes: String?,
    @SerializedName("estado") val estado: String, // Ex: 'agendada' 'realizada' 'cancelada'
    @SerializedName("notificado") val notificado: Boolean, // Se o utilizador já foi notificado sobre esta vacina
    @SerializedName("dataregisto") @ColumnInfo(name = "dataregisto") val dataRegisto: String?,
    // Campos extra que vêm da API
    @SerializedName("animal_nome") @ColumnInfo(name = "animal_nome") val animalNome: String?,
    @SerializedName("clinicaid") @ColumnInfo(name = "clinicaid") val clinicaId: Int?,
    @SerializedName("veterinarioid") @ColumnInfo(name = "veterinarioid") val veterinarioId: Int?,
    @SerializedName("clinicanome") @ColumnInfo(name = "clinicanome") val clinicaNome: String?,
    @SerializedName("veterinarionome") @ColumnInfo(name = "veterinarionome") val veterinarioNome: String?
) : Parcelable

/**
 * Representa um tipo de vacina (ex: Raiva Leptospirose)
 * É uma entidade para a tabela 'tipos_vacina' da base de dados
 */
@Entity(tableName = "tipos_vacina")
data class TipoVacina(
    @PrimaryKey val id: Int,
    val nome: String,
    val descricao: String?
)

/**
 * Modelo de dados para a resposta da API contendo uma lista de tipos de vacina disponíveis
 */
data class TiposVacinaResponse(
    val success: Boolean,
    val tipos: List<TipoVacina>,
    val count: Int
)

/**
 * Modelo para o pedido de agendamento de uma nova vacina
 */
data class AgendarVacinaRequest(
    @SerializedName("animalId") val animalId: Int,
    @SerializedName("tipo_vacina_id") val tipoVacinaId: Int,
    @SerializedName("clinicaId") val clinicaId: Int,
    @SerializedName("veterinarioId") val veterinarioId: Int,
    @SerializedName("data_agendada") val dataAgendada: String,
    @SerializedName("observacoes") val observacoes: String?
)

/**
 * Modelo de dados para a resposta da API após o agendamento de uma vacina
 */
data class AgendarVacinaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("vacina") val vacina: Vacina,
    @SerializedName("animal") val animal: AnimalResumo,
    @SerializedName("tipo_vacina") val tipoVacina: TipoVacina
)

/**
 * Modelo de dados para a resposta da API que contém uma lista de vacinas agendadas
 */
data class VacinasAgendadasResponse(
    val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("vacinas") val vacinas: List<Vacina>
)

/**
 * Modelo de dados para a resposta da API que contém uma lista de vacinas com data próxima
 */
data class VacinasProximasResponse(
    val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("vacinas") val vacinas: List<Vacina>,
    @SerializedName("mensagem") val mensagem: String
)

/**
 * Modelo para o pedido de atualização de uma vacina existente
 */
data class UpdateVacinaRequest(
    @SerializedName("tipo_vacina_id") val tipoVacinaId: Int?,
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("clinicaId") val clinicaId: Int?,
    @SerializedName("veterinarioId") val veterinarioId: Int?,
    @SerializedName("observacoes") val observacoes: String?
)

/**
 * Modelo de dados para a resposta da API após a atualização de uma vacina
 */
data class UpdateVacinaResponse(
    val success: Boolean,
    @SerializedName("mensagem") val message: String,
    val vacina: Vacina
)

/**
 * Contém informação sobre uma vacina que foi cancelada
 */
data class CanceledVacinaInfo(
    val id: Int,
    val tipo: String,
    @SerializedName("animalid") val animalId: Int
)

/**
 * Modelo de dados para a resposta da API após o cancelamento de uma vacina
 */
data class CancelVacinaResponse(
    val success: Boolean,
    val message: String,
    val vacina: CanceledVacinaInfo
)

/**
 * Modelo para o pedido de marcação de uma vacina como 'realizada'
 */
data class MarcarVacinaRealizadaRequest(
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("lote") val lote: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("observacoes") val observacoes: String?
)

/**
 * Modelo de dados para a resposta da API após marcar uma vacina como 'realizada'
 */
data class MarkVacinaRealizadaResponse(
    val success: Boolean,
    @SerializedName("mensagem") val message: String,
    val vacina: Vacina
)
