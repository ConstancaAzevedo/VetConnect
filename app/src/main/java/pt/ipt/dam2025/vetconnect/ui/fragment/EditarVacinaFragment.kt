package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog // Importa o diálogo para selecionar uma data
import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.ArrayAdapter // Importa o ArrayAdapter para popular spinners e AutoCompleteTextViews
import android.widget.EditText // Importa a classe EditText
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarVacinaBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory
import java.text.SimpleDateFormat // Importa para formatar e fazer parse de datas
import java.util.Calendar // Importa a classe Calendar para trabalhar com datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Fragment da página para editar uma vacina
 */
class EditarVacinaFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentEditarVacinaBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que vai orquestrar a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: VacinaViewModel
    // O gestor de sessão para obter o token de autenticação
    private lateinit var sessionManager: SessionManager
    // O objeto Vacina que contém os dados a serem editados (pode ser nulo inicialmente)
    private var vacina: Vacina? = null

    // Listas locais para guardar os dados dos spinners e permitir encontrar os IDs mais tarde
    private val tiposVacinaList = mutableListOf<TipoVacina>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     * É aqui que o nosso layout XML é inflado e se torna um objeto View
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentEditarVacinaBinding.inflate(inflater, container, false)
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
        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[VacinaViewModel::class.java]

        // Obtém o objeto Vacina que foi passado como argumento pelo fragment anterior
        @Suppress("DEPRECATION")
        vacina = arguments?.getParcelable("vacina")

        // Validação crucial: Se não recebemos os dados da vacina, não podemos continuar
        if (vacina == null) {
            Toast.makeText(context, "Erro ao carregar dados da vacina.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // Volta para o ecrã anterior
            return
        }

        // Se recebemos os dados, o fluxo de inicialização continua
        setupListeners() // Configura os listeners dos botões e spinners
        observeLists() // Começa a observar as listas de dados do ViewModel
        observeViewModel() // Começa a observar o resultado da operação de guardar
        populateUi(vacina!!) // Preenche o formulário com os dados da vacina que recebemos
    }

    /**
     * Preenche os campos do formulário com os dados existentes da vacina
     * Esta função é chamada uma vez no início para popular o ecrã
     */
    private fun populateUi(vacina: Vacina) {
        // Formata a data de aplicação (se existir) para um formato legível
        binding.dataAplicacao.setText(formatDate(vacina.dataAplicacao))
        binding.editObservacoes.setText(vacina.observacoes)
    }

    /**
     * Configura os listeners de clique para os botões e spinners do ecrã
     */
    private fun setupListeners() {
        binding.dataAplicacao.setOnClickListener { showDatePickerDialog(binding.dataAplicacao) }
        binding.btnGuardar.setOnClickListener { guardarAlteracoes() }

        // Quando uma nova clínica é selecionada no spinner
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            // obtém a clínica selecionada
            clinicasList.getOrNull(position)?.let {
                // e pede ao ViewModel para carregar a lista de veterinários dessa clínica.
                viewModel.carregaVeterinarios(it.id)
            }
        }
    }

    /**
     * Configura os observadores para as listas de dados do ViewModel
     * Esta função é o coração da UI reativa deste ecrã, garantindo que os spinners são preenchidos e pré-selecionados corretamente
     */
    private fun observeLists() {
        // Observa a lista de todos os tipos de vacina disponíveis
        viewModel.tiposVacina.observe(viewLifecycleOwner) { tipos ->
            tiposVacinaList.clear(); tiposVacinaList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerNomeVacina.setAdapter(adapter)
            // Pré-seleciona o tipo de vacina que a vacina atual tem
            val tipoVacinaAtual = tipos.find { it.id == vacina?.tipoVacinaId }
            binding.spinnerNomeVacina.setText(tipoVacinaAtual?.nome, false)
        }

        // Observa a lista de todas as clínicas
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear(); clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)
            // Pré-seleciona a clínica atual e despoleta o carregamento dos veterinários
            val clinicaAtual = clinicas.find { it.id == vacina?.clinicaId }
            binding.spinnerClinica.setText(clinicaAtual?.nome, false)
            clinicaAtual?.id?.let { viewModel.carregaVeterinarios(it) }
        }

        // Observa a lista de veterinários (que é carregada dinamicamente)
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear(); veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)
            // Pré-seleciona o veterinário atual DEPOIS da lista correta ter sido carregada
            val veterinarioAtual = veterinarios.find { it.id == vacina?.veterinarioId }
            binding.spinnerVeterinario.setText(veterinarioAtual?.nome, false)
        }
    }

    /**
     * Recolhe os dados do formulário, valida-os e chama o ViewModel para guardar as alterações
     */
    private fun guardarAlteracoes() {
        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações.", Toast.LENGTH_LONG).show()
            return
        }

        // Obtém os valores dos campos de texto
        val dataAplicacao = binding.dataAplicacao.text.toString()
        val observacoes = binding.editObservacoes.text.toString()

        // Obtém os nomes selecionados nos spinners
        val tipoVacinaNome = binding.spinnerNomeVacina.text.toString()
        val clinicaNome = binding.spinnerClinica.text.toString()
        val veterinarioNome = binding.spinnerVeterinario.text.toString()

        // Encontra os IDs correspondentes aos nomes selecionados usando as listas locais
        val tipoVacinaId = tiposVacinaList.find { it.nome == tipoVacinaNome }?.id
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        // Validação dos campos obrigatórios
        if (dataAplicacao.isBlank() || tipoVacinaId == null || clinicaId == null || veterinarioId == null) {
            Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        // Cria o objeto de pedido (Request) para enviar para a API
        val request = UpdateVacinaRequest(
            tipoVacinaId = tipoVacinaId,
            dataAplicacao = dataAplicacao,
            clinicaId = clinicaId,
            veterinarioId = veterinarioId,
            observacoes = observacoes
        )

        // Obtém o ID da vacina a ser editada e chama o ViewModel para a atualizar
        vacina?.let {
            viewModel.updateVacina(token, it.id, request)
        }
    }

    /**
     * Configura o observador para o resultado da operação de atualizar a vacina
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Vacina atualizada com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Volta para o ecrã anterior
            }.onFailure { throwable ->
                Toast.makeText(context, "Falha na operação: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Mostra um diálogo para o utilizador escolher uma data
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // formata a data para dd-MM-yyyy para mostrar no ecrã
            val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
            editText.setText(displayDate)
        }
        DatePickerDialog(
            requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Formata a data recebida da API (yyyy-MM-dd) para um formato mais legível (dd-MM-yyyy)
     */
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            // tenta interpretar a data como yyyy-MM-dd (ignorando a parte da hora, se existir)
            val datePart = dateString.substringBefore("T")
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(datePart)
            // formata para dd-MM-yyyy
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            // se a data já estiver no formato correto ou for inválida, devolve a string original
            dateString
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * É crucial limpar a referência ao binding aqui para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência ao binding
    }
}
