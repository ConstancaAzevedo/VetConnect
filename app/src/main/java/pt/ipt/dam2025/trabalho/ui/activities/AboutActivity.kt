package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R

// Activity para mostrar a lista de bibliotecas utilizadas na aplicação
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        val librariesTextView = findViewById<TextView>(R.id.libraries_list)
        librariesTextView.movementMethod = LinkMovementMethod.getInstance()

        val librariesText = """
            <b>Android Jetpack (Google)</b><br>
            <i>Origem:</i> <a href="https://developer.android.com/jetpack">https://developer.android.com/jetpack</a><br>
            Conjunto de bibliotecas para facilitar o desenvolvimento de apps Android de alta qualidade.
            <ul>
                <li><b>Core KTX:</b> Extensões Kotlin para APIs do Android.</li>
                <li><b>AppCompat:</b> Permite o uso de funcionalidades modernas em versões antigas do Android.</li>
                <li><b>Activity & Fragment KTX:</b> Simplifica o uso de Activities e Fragments.</li>
                <li><b>Lifecycle (ViewModel, LiveData, Runtime):</b> Gerencia o ciclo de vida de componentes de UI.</li>
                <li><b>Room:</b> Camada de abstração sobre o SQLite para persistência de dados.</li>
                <li><b>CameraX:</b> Simplifica a adição de funcionalidades de câmera.</li>
                <li><b>ConstraintLayout:</b> Para criar layouts grandes e complexos com uma hierarquia de visualização plana.</li>
                <li><b>WorkManager:</b> Para tarefas em segundo plano que precisam ser executadas de forma garantida.</li>
                <li><b>ViewBinding:</b> Gera uma classe de vinculação para cada arquivo de layout XML.</li>
            </ul><br>

            <b>Networking (Square, Inc. & Google)</b><br>
            <ul>
                <li><b>Retrofit:</b> Cliente HTTP para Android e Java. Usado para comunicação com a nossa API REST. <i>Origem:</i> <a href="https://square.github.io/retrofit/">https://square.github.io/retrofit/</a></li>
                <li><b>OkHttp Logging Interceptor:</b> Interceptor para o OkHttp que registra requisições e respostas HTTP. <i>Origem:</i> <a href="https://square.github.io/okhttp/">https://square.github.io/okhttp/</a></li>
                <li><b>Gson:</b> Biblioteca para converter objetos Java/Kotlin para a sua representação JSON e vice-versa. <i>Origem:</i> <a href="https://github.com/google/gson">https://github.com/google/gson</a></li>
                <li><b>Retrofit Gson Converter:</b> Um conversor que usa Gson para a serialização e desserialização de corpos de requisição e resposta.</li>
            </ul><br>

            <b>Asynchronous Programming (JetBrains)</b><br>
            <ul>
                <li><b>Kotlin Coroutines:</b> Para gerenciar operações assíncronas de forma concisa e eficiente. <i>Origem:</i> <a href="https://kotlinlang.org/docs/coroutines-overview.html">https://kotlinlang.org/docs/coroutines-overview.html</a></li>
            </ul><br>

            <b>User Interface</b><br>
            <ul>
                <li><b>Material Components for Android (Google):</b> Componentes e temas para construir interfaces com o Material Design. <i>Origem:</i> <a href="https://material.io/develop/android">https://material.io/develop/android</a></li>
                <li><b>Glide (Sam Judd / Google):</b> Biblioteca para carregamento de imagens e GIFs. <i>Origem:</i> <a href="https://github.com/bumptech/glide">https://github.com/bumptech/glide</a></li>
            </ul><br>

            <b>Machine Learning (Google)</b><br>
            <ul>
                <li><b>Google ML Kit - Barcode Scanning:</b> API para escanear e processar códigos de barras e QR codes. <i>Origem:</i> <a href="https://developers.google.com/ml-kit/vision/barcode-scanning">https://developers.google.com/ml-kit/vision/barcode-scanning</a></li>
            </ul>
        """

        librariesTextView.text = Html.fromHtml(librariesText, Html.FROM_HTML_MODE_COMPACT)
    }
}
