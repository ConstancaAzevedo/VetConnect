package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentAnimalBinding

/**
 * Activity para a página do perfil do animal
 */

class AnimalFragment : Fragment() {

    // variável de suporte que pode ser nula para evitar memory leaks quando a view do fragmento é destruída
    private var _binding: FragmentAnimalBinding? = null

    // propriedade não-nula que dá acesso seguro ao binding e só é válida entre onCreateView e onDestroyView
    private val binding get() = _binding!!


    // view do fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // define um listener para o resultado do CamaraFragment
        setFragmentResultListener("requestKey") { key, bundle ->
            val result = bundle.getString("bundleKey")
            if (result != null) {
                binding.animalFoto.setImageURI(result.toUri())
                // TODO: guardar o URI na base de dados
            }
        }

        // listener na foto do animal para iniciar a câmara
        binding.animalFoto.setOnClickListener {
            findNavController().navigate(R.id.action_animalFragment_to_camaraFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
