package pt.ipt.dam2025.vetconnect.ui.fragment

import android.Manifest // Importa a lista de permissões do Android
import android.content.pm.PackageManager // Importa para verificar o estado das permissões
import android.os.Build // Importa para verificar a versão do Android
import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.util.Log // Importa a classe Log para registar mensagens de erro
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.activity.result.contract.ActivityResultContracts // Importa o contrato para pedir permissões
import androidx.camera.core.CameraSelector // Importa para selecionar a câmara (frontal/traseira)
import androidx.camera.core.ImageCapture // Importa o caso de uso para capturar imagens
import androidx.camera.core.ImageCaptureException // Importa a exceção para erros na captura de imagem
import androidx.camera.core.Preview // Importa o caso de uso para mostrar a preview da câmara
import androidx.camera.lifecycle.ProcessCameraProvider // Importa para ligar o ciclo de vida da câmara ao do Fragment
import androidx.core.content.ContextCompat // Importa para obter permissões e executores
import androidx.core.os.bundleOf // Importa uma função de ajuda para criar Bundles
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.fragment.app.setFragmentResult // Importa para enviar um resultado de volta ao fragment anterior
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre fragments
import pt.ipt.dam2025.vetconnect.databinding.FragmentCamaraBinding // Importa a classe de ViewBinding gerada
import java.io.File // Importa a classe File para manipulação de ficheiros
import java.text.SimpleDateFormat // Importa para formatar datas
import java.util.Locale // Importa para definir a localização para formatação
import java.util.concurrent.ExecutorService // Importa para gerir threads em background
import java.util.concurrent.Executors // Importa para criar executores de threads

/**
 * O código foi fornecido pelo professor com apenas diferença de que foi alterado para ser um fragment
 * Fragment genérico para a câmara capaz de guardar fotos em diferentes diretórios
 * Este fragment é reutilizável para tirar fotos de animais, exames, etc.
 */
class CamaraFragment : Fragment() {

    // Variável para o ViewBinding
    private var _binding: FragmentCamaraBinding? = null
    private val binding get() = _binding!!

    // Variável para o caso de uso de captura de imagem do CameraX
    private var imageCapture: ImageCapture? = null
    // Executor para correr as operações da câmara numa thread em background
    private lateinit var cameraExecutor: ExecutorService
    // O tipo de caminho (ex: "animais", "exames") para determinar onde guardar a foto
    private var pathType: String? = null

    // Launcher para o pedido de permissões Esta é a forma moderna de pedir permissões
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        // Itera sobre as permissões pedidas para ver se alguma foi negada
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) {
                permissionGranted = false
            }
        }

        if (!permissionGranted) {
            // Se alguma permissão foi negada, mostra uma mensagem e volta para trás
            Toast.makeText(requireContext(), "Permissão negada", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
            // Se todas as permissões foram concedidas, inicia a câmara
            startCamera()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamaraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtém o tipo de caminho (ex: "animais") passado como argumento pelo fragment anterior
        pathType = arguments?.getString("pathType")
        if (pathType == null) {
            // Se nenhum caminho for especificado, é um erro. Mostra uma mensagem e volta para trás.
            Toast.makeText(requireContext(), "Erro: Tipo de caminho não especificado.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        // Verifica se todas as permissões necessárias já foram concedidas
        if (allPermissionsGranted()) {
            // Se sim, inicia a câmara
            startCamera()
        } else {
            // Se não, pede as permissões
            requestPermissions()
        }

        // Configura o listener para o botão de tirar foto
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        // Inicializa o executor de threads
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Tira uma foto e guarda-a no diretório apropriado
     */
    private fun takePhoto() {
        // Garante que o caso de uso de captura de imagem foi inicializado
        val imageCapture = imageCapture ?: return

        // Determina o diretório de saída com base no pathType recebido como argumento
        val outputDirectory = when (pathType) {
            "animais" -> File(requireContext().getExternalFilesDir(null), "Animals")
            "exames" -> File(requireContext().getExternalFilesDir(null), "Exames")
            else -> {
                Toast.makeText(requireContext(), "Erro: Caminho de destino inválido.", Toast.LENGTH_LONG).show()
                return
            }
        }
        
        // Garante que o diretório pai existe, se não, cria-o
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        // Cria um ficheiro para a foto com um nome único baseado na data/hora atual
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Cria as opções de saída que especificam onde e como guardar o ficheiro
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Tira a foto
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()), // O executor onde os callbacks serão chamados
            object : ImageCapture.OnImageSavedCallback { // O callback para o resultado
                // Chamado se ocorrer um erro ao guardar a foto
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Falha na captura da foto: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Falha ao guardar a foto.", Toast.LENGTH_SHORT).show()
                }

                // Chamado se a foto for guardada com sucesso
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Usa setFragmentResult para enviar o caminho absoluto da foto de volta ao fragment anterior
                    setFragmentResult("requestKey", bundleOf("imagePath" to photoFile.absolutePath))
                    // Fecha o fragment da câmara
                    findNavController().popBackStack()
                }
            }
        )
    }

    /**
     * Inicializa e liga a câmara ao ciclo de vida do fragment
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Configura o caso de uso de Preview para mostrar a imagem da câmara no ecrã
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.viewFinder.surfaceProvider
            }

            // Inicializa o caso de uso de Captura de Imagem
            imageCapture = ImageCapture.Builder().build()
            // Seleciona a câmara traseira por defeito
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Desliga quaisquer casos de uso anteriores antes de ligar os novos
                cameraProvider.unbindAll()
                // Liga os casos de uso (preview e captura) à câmara e ao ciclo de vida do fragment
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Falha ao ligar os casos de uso", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * Inicia o pedido de permissões usando o activityResultLauncher
     */
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    /**
     * Verifica se todas as permissões na lista REQUIRED_PERMISSIONS foram concedidas
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Desliga o executor de threads para libertar recursos
        cameraExecutor.shutdown()
        // Limpa a referência ao binding para evitar memory leaks
        _binding = null
    }

    /**
     * Companion object para guardar constantes relacionadas com a câmara
     */
    companion object {
        private const val TAG = "VetConnectCamara" // Tag para os logs
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS" // Formato para o nome do ficheiro da foto
        // Lista de permissões necessárias
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            // A permissão de escrita só é necessária para versões do Android mais antigas (API <= 28)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}
