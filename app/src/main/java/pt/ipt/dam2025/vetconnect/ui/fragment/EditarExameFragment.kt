package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarExameBinding
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.io.File

class EditarExameFragment : Fragment() {

    // ceclara a variável de binding
    private var _binding: FragmentEditarExameBinding? = null
    // esta propriedade é válida apenas entre onCreateView e onDestroyView
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoricoViewModel
    private var exame: Exame? = null
    private var novaImagemUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(HistoricoViewModel::class.java)

        // Obtém o objeto Exame a partir dos argumentos de navegação (forma simples)
        @Suppress("DEPRECATION")
        exame = arguments?.getParcelable("exame")

        // Preenche a UI com os dados do exame recebido
        preencherDadosExame()
        // configura os listeners dos botões
        configurarListeners()
        ouvirResultadoDaCamara()
        observeViewModel()
    }

    private fun preencherDadosExame() {
        exame?.let {
            // Preenche os campos de texto com os dados do exame
            binding.editTextDataExame.setText(it.dataExame)
            binding.editTextResultado.setText(it.resultado)
            binding.editTextObservacoes.setText(it.observacoes)

            // Carrega a imagem existente, se houver
            if (!it.ficheiroUrl.isNullOrEmpty()) {
                binding.imageViewPreview.load(it.ficheiroUrl) {
                    placeholder(R.drawable.vetconnectfundo)
                    error(R.drawable.vetconnectfundo)
                }
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    private fun configurarListeners() {
        binding.buttonAlterarFoto.setOnClickListener {
            findNavController().navigate(R.id.action_editarExameFragment_to_camaraFragment)
        }

        binding.buttonGuardarAlteracoes.setOnClickListener {
            guardarAlteracoes()
        }

        binding.buttonApagarExame.setOnClickListener {
            mostrarDialogoDeConfirmacao()
        }
    }

    private fun ouvirResultadoDaCamara() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                novaImagemUri = Uri.fromFile(File(imagePath))
                binding.imageViewPreview.setImageURI(novaImagemUri)
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    private fun guardarAlteracoes() {
        exame?.let {
            val token = "seu_token_aqui" // TODO: Obter token de forma segura
            val dataExame = binding.editTextDataExame.text.toString()
            val resultado = binding.editTextResultado.text.toString()
            val observacoes = binding.editTextObservacoes.text.toString()

            // TODO: Obter os IDs dos spinners
            val tipoExameId = it.tipoExameId
            val clinicaId = it.clinicaId
            val veterinarioId = it.veterinarioId

            viewModel.atualizarExameEFoto(
                token = token,
                exameId = it.id,
                animalId = it.animalId,
                tipoExameId = tipoExameId,
                dataExame = dataExame,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes,
                novaImagemUri = novaImagemUri,
                context = requireContext()
            )
        }
    }

    private fun mostrarDialogoDeConfirmacao() {
        AlertDialog.Builder(requireContext())
            .setTitle("Apagar Exame")
            .setMessage("Tem a certeza que deseja apagar este exame? Esta ação é irreversível.")
            .setPositiveButton("Sim") { _, _ ->
                apagarExame()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun apagarExame() {
        exame?.let {
            val token = "seu_token_aqui" // TODO: Obter token de forma segura
            viewModel.deleteExame(token, it.animalId, it.id.toLong())
        }
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Operação concluída com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Erro: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
