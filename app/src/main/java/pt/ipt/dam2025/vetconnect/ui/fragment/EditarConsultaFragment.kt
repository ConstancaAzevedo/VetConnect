package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog // Importa o diálogo para selecionar uma data
import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.ArrayAdapter // Importa o ArrayAdapter para popular spinners e AutoCompleteTextViews
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarConsultaBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory
import java.text.SimpleDateFormat // Importa para formatar e fazer parse de datas
import java.util.Calendar // Importa a classe Calendar para trabalhar com datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Fragment para a página de editar uma consulta
 */
class EditarConsultaFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentEditarConsultaBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que vai orquestrar a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: ConsultaViewModel
    // O gestor de sessão para obter o token de autenticação
    private lateinit var sessionManager: SessionManager
    // O objeto Consulta que contém os dados a serem editados
    private var consulta: Consulta? = null

    // Listas locais para guardar os dados dos spinners para depois encontrar os IDs
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentEditarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     * É o local ideal para inicializar componentes, obter dados e configurar listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        // Obtém o objeto Consulta que foi passado como argumento pelo fragment anterior
        // A anotação Suppress é usada porque getParcelable está "deprecated" mas é a forma correta de usar para retrocompatibilidade
        @Suppress("DEPRECATION")
        consulta = arguments?.getParcelable("consulta")

        // Validação crucial: Se não recebemos os dados da consulta, não podemos continuar
        if (consulta == null) {
            Toast.makeText(context, "Erro ao carregar dados da consulta.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // Volta para o ecrã anterior
            return
        }

        // Configura os listeners dos botões e spinners
        setupListeners()
        // Começa a observar as listas de dados (clínicas e veterinários) do ViewModel
        observeLists()
        // Começa a observar o resultado da operação de guardar as alterações
        observeViewModel()
        // Preenche o formulário com os dados da consulta que recebemos
        populateUi(consulta!!)
    }

    /**
     * Preenche os campos do formulário com os dados existentes da consulta
     */
    private fun populateUi(consulta: Consulta) {
        binding.motivo.setText(consulta.motivo)
        // Formata a data da API (yyyy-MM-dd) para um formato mais legível (dd-MM-yyyy)
        binding.dataConsulta.setText(formatDateForDisplay(consulta.data))
        binding.observacoes.setText(consulta.observacoes)
    }

    /**
     * Configura os listeners de clique para os botões e spinners
     */
    private fun setupListeners() {
        binding.dataConsulta.setOnClickListener { showDatePicker() }
        binding.btnGuardar.setOnClickListener { guardarAlteracoes() }

        // Quando uma nova clínica é selecionada no spinner
        binding.clinicaSpinner.setOnItemClickListener { _, _, position, _ ->
            // obtém a clínica selecionada
            clinicasList.getOrNull(position)?.let {
                // e pede ao ViewModel para carregar a lista de veterinários dessa clínica.
                viewModel.carregaVeterinarios(it.id)
            }
        }
    }

    /**
     * Configura os observadores para as listas de clínicas e veterinários
     * Esta função é o coração da UI reativa deste ecrã
     */
    private fun observeLists() {
        // Observa a lista de todas as clínicas disponíveis
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            // Cria um adapter para o spinner de clínicas
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.clinicaSpinner.setAdapter(adapter)

            // Lógica para pré-selecionar a clínica atual da consulta
            val clinicaAtual = clinicas.find { it.id == consulta?.clinicaId }
            if (clinicaAtual != null) {
                // Define o texto do spinner para o nome da clínica atual sem despoletar o listener de clique
                binding.clinicaSpinner.setText(clinicaAtual.nome, false)
                // Carrega manualmente os veterinários para a clínica que foi pré-selecionada
                viewModel.carregaVeterinarios(clinicaAtual.id)
            }
        }

        // Observa a lista de veterinários (que é carregada dinamicamente)
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            // Cria um adapter para o spinner de veterinários
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.veterinarioSpinner.setAdapter(adapter)

            // Lógica para pré-selecionar o veterinário atual da consulta
            // Isto é chamado depois dos veterinários da clínica correta terem sido carregados
            val vetAtual = veterinarios.find { it.id == consulta?.veterinarioId }
            vetAtual?.let { binding.veterinarioSpinner.setText(it.nome, false) }
        }
    }

    /**
     * Recolhe todos os dados do formulário, valida-os e chama o ViewModel para guardar as alterações
     */
    private fun guardarAlteracoes() {
        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações.", Toast.LENGTH_LONG).show()
            return
        }

        // Obtém os valores dos campos do formulário
        val clinicaNome = binding.clinicaSpinner.text.toString()
        val veterinarioNome = binding.veterinarioSpinner.text.toString()
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id
        val data = binding.dataConsulta.text.toString()
        val motivo = binding.motivo.text.toString()
        val observacoes = binding.observacoes.text.toString()

        // Validação dos campos obrigatórios
        if (motivo.isBlank() || clinicaId == null || veterinarioId == null || data.isBlank()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // Converte a data para o formato que a API espera (yyyy-MM-dd)
        val apiDate = reformatDateForApi(data)
        if (apiDate == null) {
            Toast.makeText(context, "Formato de data inválido. Use dd-MM-yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        // Cria o objeto de pedido (Request) para enviar à API
        val request = UpdateConsultaRequest(motivo, apiDate, clinicaId, veterinarioId, observacoes)
        // Chama a função do ViewModel para atualizar a consulta
        consulta?.id?.let {
            viewModel.updateConsulta(token, it, request)
        }
    }

    /**
     * Configura o observador para o resultado da operação de atualizar a consulta
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Consulta atualizada com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Volta para o ecrã anterior
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao atualizar consulta: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Mostra um diálogo para o utilizador selecionar uma data
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                binding.dataConsulta.setText(displayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Converte a data da API (yyyy-MM-dd) para o formato do ecrã (dd-MM-yyyy)
     */
    private fun formatDateForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString.substring(0, 10))
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            dateString
        }
    }

    /**
     * Converte a data do formato do ecrã (dd-MM-yyyy) para o formato da API (yyyy-MM-dd)
     */
    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(displayDate)?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * É crucial limpar a referência ao binding aqui para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
