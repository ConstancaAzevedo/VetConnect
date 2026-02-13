package pt.ipt.dam2025.vetconnect.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentCamaraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * O código foi fornecido pelo professor com apenas diferença de que foi alterado para ser um fragment
 * Fragment genérico para a câmara capaz de guardar fotos em diferentes diretórios
 */
class CamaraFragment : Fragment() {

    private var _binding: FragmentCamaraBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var pathType: String? = null

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) {
                permissionGranted = false
            }
        }

        if (!permissionGranted) {
            Toast.makeText(requireContext(), "Permissãonegada", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
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

        // Obtém o tipo de caminho (ex: "animais" ou "exames") dos argumentos
        pathType = arguments?.getString("pathType")
        if (pathType == null) {
            Toast.makeText(requireContext(), "Erro: Tipo de caminho não especificado.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Determina o diretório de saída com base no pathType
        val outputDirectory = when (pathType) {
            "animais" -> File(requireContext().getExternalFilesDir(null), "Animals")
            "exames" -> File(requireContext().getExternalFilesDir(null), "Exames")
            else -> {
                Toast.makeText(requireContext(), "Erro: Caminho de destino inválido.", Toast.LENGTH_LONG).show()
                return
            }
        }
        
        // Garante que o diretório pai existe
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Cria as opções de saída especificando o ficheiro
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Configura o listener para ser notificado após a foto ter sido tirada
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Falha na captura da foto: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Falha ao guardar a foto.", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // A foto foi guardada com sucesso devolve o caminho absoluto do ficheiro
                    setFragmentResult("requestKey", bundleOf("imagePath" to photoFile.absolutePath))
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.viewFinder.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Falha ao ligar os casos de uso", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    companion object {
        private const val TAG = "VetConnectCamara"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}