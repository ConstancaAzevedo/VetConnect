package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarExameBinding
import pt.ipt.dam2025.vetconnect.model.Exame

class EditarExameFragment : Fragment() {

    // ceclara a variável de binding
    private var _binding: FragmentEditarExameBinding? = null
    // esta propriedade é válida apenas entre onCreateView e onDestroyView
    private val binding get() = _binding!!

    // TODO: Receber o objeto Exame como argumento do fragment
    private var exame: Exame? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // infla o layout usando o ViewBinding
        _binding = FragmentEditarExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Obter o objeto Exame dos argumentos
        // exame = arguments?.getSerializable("exame_key") as? Exame

        // TODO: Popular a UI com os dados do exame
        // popularDadosExame()
        
        // TODO: Carregar os dados para os spinners (Tipos de Exame, Clínicas, Veterinários)
        // carregarSpinners()

        // configura os listeners dos botões
        configurarListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // limpa a referência ao binding para evitar memory leaks
        _binding = null
    }

    private fun popularDadosExame() {
        exame?.let {
            // TODO: Definir os valores nos spinners e campos de texto usando 'binding'
        }
    }
    
    private fun carregarSpinners(){
        // TODO: Implementar a lógica para carregar os dados da API para os spinners
    }

    private fun configurarListeners() {
        binding.buttonGuardarAlteracoes.setOnClickListener {
            // TODO: Implementar a lógica para guardar as alterações
            // 1. Validar os campos
            // 2. Criar o objeto UpdateExameRequest
            // 3. Chamar o ViewModel para fazer a chamada à API
        }

        binding.buttonApagarExame.setOnClickListener {
            // TODO: Implementar a lógica para apagar o exame
            // 1. Mostrar um dialogo de confirmação
            // 2. Chamar o ViewModel para fazer a chamada à API
        }

        binding.buttonAdicionarFoto.setOnClickListener {
            // TODO: Implementar a lógica para escolher/tirar uma foto
        }
    }
}