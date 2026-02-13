package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarVacinaBinding
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment da página para editar uma vacina
 */

class EditarVacinaFragment : Fragment() {

    // variável para o View Binding para aceder aos componentes do layout de forma segura
    private var _binding: FragmentEditarVacinaBinding? = null
    private val binding get() = _binding!!

    // ViewModel para interagir com a lógica da API e base de dados
    private lateinit var viewModel: VacinaViewModel
    private lateinit var sessionManager: SessionManager

    // objeto da vacina a ser editada recebido como argumento do ecrã anterior
    private var vacina: Vacina? = null

    // listas locais para guardar os dados dos spinners e permitir encontrar os IDs
    private val tiposVacinaList = mutableListOf<TipoVacina>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    /*
     * infla o layout do fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*
     * chamado após a criação da View
     * aqui é que a lógica principal é configurada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // inicializa o ViewModel
        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[VacinaViewModel::class.java]

        // obtém o objeto Vacina passado como argumento a partir do ecrã anterior
        @Suppress("DEPRECATION")
        vacina = arguments?.getParcelable("vacina")

        // se a vacina não for encontrada mostra um erro e volta para o ecrã anterior
        if (vacina == null) {
            Toast.makeText(context, "Erro ao carregar dados da vacina.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // chama as funções para configurar o ecrã
        setupListeners()
        observeLists()
        observeViewModel()
        populateUi(vacina!!)
    }

    /*
     * preenche os campos de texto com os dados iniciais da vacina a ser editada
     */
    private fun populateUi(vacina: Vacina) {
        binding.dataAplicacao.setText(formatDate(vacina.dataAplicacao))
        binding.editObservacoes.setText(vacina.observacoes)
    }

    /*
     * configura os listeners de clique para os botões e spinners
     */
    private fun setupListeners() {
        // mostra o DatePicker quando se clica no campo da data
        binding.dataAplicacao.setOnClickListener { showDatePickerDialog(binding.dataAplicacao) }
        // guarda as alterações quando se clica no botão "Guardar"
        binding.buttonGuardarAlteracoes.setOnClickListener { guardarAlteracoes() }

        // quando uma clínica é selecionada pede ao ViewModel para carregar a lista de veterinários correspondente
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            val selectedClinica = clinicasList.getOrNull(position)
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    /*
     * observa as LiveData do ViewModel que contêm as listas para os spinners
     * quando as listas chegam popula os spinners e pré-seleciona os valores
     */
    private fun observeLists() {
        // observa a lista de tipos de vacina
        viewModel.tiposVacina.observe(viewLifecycleOwner) { tipos ->
            tiposVacinaList.clear()
            tiposVacinaList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerNomeVacina.setAdapter(adapter)

            // pré seleciona o tipo de vacina que a vacina atual tem
            val tipoVacinaAtual = tipos.find { it.id == vacina?.tipoVacinaId }
            binding.spinnerNomeVacina.setText(tipoVacinaAtual?.nome, false)
        }

        // observa a lista de clínicas
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)

            // pré seleciona a clínica atual e pede para carregar os veterinários dessa clínica
            val clinicaAtual = clinicas.find { it.id == vacina?.clinicaId }
            binding.spinnerClinica.setText(clinicaAtual?.nome, false)
            clinicaAtual?.id?.let { viewModel.carregaVeterinarios(it) }
        }

        // observa a lista de veterinários (que muda dinamicamente com a clínica)
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)

            // pré seleciona o veterinário atual
            val veterinarioAtual = veterinarios.find { it.id == vacina?.veterinarioId }
            binding.spinnerVeterinario.setText(veterinarioAtual?.nome, false)
        }
    }

    /*
     * recolhe os dados do formulário valida-os e envia o pedido de atualização para a API
     */
    private fun guardarAlteracoes() {
        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações.", Toast.LENGTH_LONG).show()
            return
        }

        // obtém os valores dos campos de texto
        val dataAplicacao = binding.dataAplicacao.text.toString()
        val observacoes = binding.editObservacoes.text.toString()

        // obtém os nomes selecionados nos spinners
        val tipoVacinaNome = binding.spinnerNomeVacina.text.toString()
        val clinicaNome = binding.spinnerClinica.text.toString()
        val veterinarioNome = binding.spinnerVeterinario.text.toString()

        // encontra os IDs correspondentes aos nomes selecionados usando as listas locais
        val tipoVacinaId = tiposVacinaList.find { it.nome == tipoVacinaNome }?.id
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        // verifica se todos os campos obrigatórios estão preenchidos
        if (dataAplicacao.isBlank() || tipoVacinaId == null || clinicaId == null || veterinarioId == null) {
            Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        // cria o objeto de pedido para enviar para a API
        val request = UpdateVacinaRequest(
            tipoVacinaId = tipoVacinaId,
            dataAplicacao = dataAplicacao,
            clinicaId = clinicaId,
            veterinarioId = veterinarioId,
            observacoes = observacoes
        )

        // obtém o token e chama o ViewModel para atualizar a vacina
        vacina?.let {
            viewModel.updateVacina(token, it.id, request)
        }
    }

    /*
     * observa o resultado da operação e mostra uma mensagem
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Vacina atualizada com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // volta para o ecrã anterior
            }.onFailure { throwable ->
                Toast.makeText(context, "Falha na operação: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /*
     * mostra um diálogo para o utilizador escolher uma data
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

    /*
     * formata a data recebida da API (yyyy-MM-dd) para um formato mais legível (dd-MM-yyyy)
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

    /*
     * limpa o View Binding quando a View é destruída para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
