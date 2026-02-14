package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog // Importa o diálogo para selecionar uma data
import android.net.Uri // Importa a classe Uri para lidar com caminhos de ficheiros
import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.core.os.bundleOf // Importa uma função de ajuda para criar Bundles
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.fragment.app.setFragmentResultListener // Importa o listener para receber resultados de outros fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir ViewModels
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre fragments
import com.bumptech.glide.Glide // Importa a biblioteca Glide para carregar imagens de forma eficiente
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentAnimalBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModelFactory
import java.io.File // Importa a classe File para manipulação de ficheiros
import java.util.Calendar // Importa a classe Calendar para trabalhar com datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Fragment para a página do perfil do animal
 */
class AnimalFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentAnimalBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: AnimalViewModel
    // O gestor de sessão para obter o token e o ID do animal
    private lateinit var sessionManager: SessionManager
    // ID do animal a ser editado ou -1 se for para criar
    private var animalId: Int = -1

    /**
     * Chamado para inflar o layout do fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado depois da view ter sido criada
     * É aqui que a maior parte da lógica é inicializada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = AnimalViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[AnimalViewModel::class.java]

        // Obtém o ID do animal guardado na sessão
        animalId = sessionManager.getAnimalId()
        val token = sessionManager.getAuthToken()

        // Verifica se estamos em modo de edição (temos um ID) ou criação
        if (token != null && animalId != -1) {
            // Modo Edição: pede ao ViewModel para ir buscar os dados do animal
            viewModel.getAnimal(token, animalId)
        } else {
            // Modo Criação: esconde o campo do código único que ainda não existe
            binding.codigoUnico.visibility = View.GONE
        }

        // Configura todos os listeners de clique e começa a observar os LiveData
        setupListeners()
        observeViewModel()
        ouvirResultadoDaCamara()
    }

    /**
     * Regista um listener para receber o resultado do CamaraFragment
     * Esta é a forma moderna e segura de obter um resultado de outro fragment
     */
    private fun ouvirResultadoDaCamara() {
        setFragmentResultListener("requestKey") { _, bundle ->
            // Quando o resultado chega obtém o caminho da imagem
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                // Converte o caminho para um URI mostra a preview e chama a função de upload
                val fotoUri = Uri.fromFile(File(imagePath))
                binding.animalFoto.setImageURI(fotoUri)
                uploadFoto(fotoUri)
            }
        }
    }

    /**
     * Configura todos os listeners de clique para os botões e outros elementos interativos
     */
    private fun setupListeners() {
        binding.animalData.setOnClickListener { setupDatePicker() }
        binding.btnGuardar.setOnClickListener { saveOrUpdateAnimal() }
        binding.animalFoto.setOnClickListener { dispatchTakePictureIntent() }
    }

    /**
     * Configura os observadores para os LiveData do ViewModel
     * Este é o coração da UI reativa
     */
    private fun observeViewModel() {
        // Observa os dados do animal Quando o ViewModel os obtém esta função é chamada
        viewModel.animal.observe(viewLifecycleOwner) { animal ->
            // Se o animal não for nulo preenche a UI com os seus dados
            animal?.let { populateUI(it) }
        }
        // Observa o estado da operação de guardar/atualizar
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Operação bem-sucedida!", Toast.LENGTH_SHORT).show()
                // Volta para o ecrã anterior após o sucesso
                findNavController().popBackStack()
            }.onFailure { throwable ->
                // Mostra uma mensagem de erro em caso de falha
                Toast.makeText(context, "Erro: ${throwable.message}", Toast.LENGTH_SHORT).show()
            }
        }
        // Observa a URL da foto após um upload bem-sucedido
        viewModel.fotoUrl.observe(viewLifecycleOwner) { url ->
            // Se a URL não for nula usa o Glide para carregar a nova imagem
            url?.let { Glide.with(this).load(it).into(binding.animalFoto) }
        }
    }

    /**
     * Preenche os campos da UI com os dados de um animal
     */
    private fun populateUI(animal: AnimalResponse) {
        binding.codigoUnico.text = animal.codigoUnico
        binding.codigoUnico.visibility = View.VISIBLE
        binding.animalNome.setText(animal.nome)
        binding.animalEspecie.setText(animal.especie)
        binding.animalRaca.setText(animal.raca)
        binding.animalData.setText(animal.dataNascimento)
        binding.animalChip.setText(animal.numeroChip)
        // Carrega a foto do animal usando a biblioteca Glide
        if (!animal.fotoUrl.isNullOrEmpty()) {
            Glide.with(this).load(animal.fotoUrl).into(binding.animalFoto)
        }
    }

    /**
     * Recolhe os dados do formulário e decide se deve criar ou atualizar um animal
     */
    private fun saveOrUpdateAnimal() {
        val nome = binding.animalNome.text.toString()
        val especie = binding.animalEspecie.text.toString()
        // Validação simples dos campos obrigatórios
        if (nome.isEmpty() || especie.isEmpty()) {
            Toast.makeText(context, "Nome e espécie são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }
        val token = sessionManager.getAuthToken() ?: return

        // Lógica para decidir qual função do ViewModel chamar
        if (animalId == -1) {
            // Se não temos ID estamos a criar um novo animal
            viewModel.saveAnimal(token, nome, especie, binding.animalRaca.text.toString(), binding.animalData.text.toString(), binding.animalChip.text.toString())
        } else {
            // Se temos ID estamos a atualizar um animal existente
            viewModel.updateAnimal(token, animalId, nome, especie, binding.animalRaca.text.toString(), binding.animalData.text.toString(), binding.animalChip.text.toString())
        }
    }

    /**
     * Mostra um diálogo para o utilizador selecionar a data de nascimento
     */
    private fun setupDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            // Formata a data no formato que a API espera (yyyy-MM-dd)
            binding.animalData.setText(String.format(Locale.ROOT, "%d-%02d-%02d", year, month + 1, day))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    /**
     * Navega para o CamaraFragment para tirar uma nova foto
     */
    private fun dispatchTakePictureIntent() {
        // Passa o tipo de caminho como argumento para a câmara saber onde guardar a foto
        val bundle = bundleOf("pathType" to "animais")
        findNavController().navigate(R.id.action_animalFragment_to_camaraFragment, bundle)
    }

    /**
     * Chama o ViewModel para fazer o upload de uma foto recém-tirada
     */
    private fun uploadFoto(fotoUri: Uri) {
        val token = sessionManager.getAuthToken() ?: return
        // Só faz upload se estivermos a editar um animal que já existe
        if (animalId != -1) {
            viewModel.uploadPhoto(token, animalId, fotoUri)
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * Limpa a referência ao binding para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
