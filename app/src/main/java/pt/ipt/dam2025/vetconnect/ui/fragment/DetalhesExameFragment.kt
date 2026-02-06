package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesExameBinding
import pt.ipt.dam2025.vetconnect.model.Exame

/**
 * Fragment para a página de detalhes de um exame
 */

class DetalhesExameFragment : Fragment() {

    private var _binding: FragmentDetalhesExameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtém o objeto Exame dos argumentos da forma moderna
        val exame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("exame", Exame::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("exame")
        }

        // Se o exame for nulo, não podemos fazer nada para evitar um crash
        if (exame == null) {
            // TODO: Adicionar lógica para lidar com o erro, como fechar o ecrã.
            return
        }

        populateUi(exame)
    }

    private fun populateUi(exame: Exame) {
        // Preenche os campos de texto com os dados do exame
        binding.textViewTipoExameDetalhe.text = exame.tipo ?: "Exame não especificado"
        binding.textViewDataExameDetalhe.text = exame.dataExame ?: "Sem data"
        binding.textViewClinicaDetalhe.text = exame.clinicaNome ?: "Clínica não especificada"
        binding.textViewVeterinarioDetalhe.text = exame.veterinarioNome ?: "Veterinário não especificado"

        // Verifica e mostra o resultado se existir
        binding.layoutResultadoDetalhe.isVisible = !exame.resultado.isNullOrBlank()
        binding.textViewResultadoDetalhe.text = exame.resultado

        // Verifica e mostra as observações se existirem
        binding.layoutObservacoesDetalhe.isVisible = !exame.observacoes.isNullOrBlank()
        binding.textViewObservacoesDetalhe.text = exame.observacoes

        // Verifica e mostra a foto do relatório se existir
        binding.layoutFotoDetalhe.isVisible = !exame.ficheiroUrl.isNullOrBlank()
        if (binding.layoutFotoDetalhe.isVisible) {
            // TODO: Carregar a imagem a partir do URL com uma biblioteca como Glide ou Coil
            // Ex: Glide.with(this).load(exame.ficheiroUrl).into(binding.imageViewRelatorioDetalhe)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
