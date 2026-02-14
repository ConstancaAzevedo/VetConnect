plugins {
    // Plugin principal para construir uma aplicação Android
    id("com.android.application")

    // KSP -> ferramenta para processamento de anotações em Kotlin
    // Usado por bibliotecas para gerar código automaticamente durante a compilação
    alias(libs.plugins.ksp)

    // Plugin para permitir que data classes sejam parceláveis
    // o que permite passá-las entre ecrãs
    id("kotlin-parcelize")
}

android {
    // Namespace único da aplicação para identificar o código
    namespace = "pt.ipt.dam2025.vetconnect"
    // A versão do Android SDK com a qual a aplicação é compilada
    compileSdk = 36

    defaultConfig {
        // Identificador único da aplicação na Play Store
        applicationId = "pt.ipt.dam2025.vetconnect"
        // A versão mínima do Android em que a aplicação pode ser instalada
        minSdk = 28
        // A versão do Android para a qual a aplicação foi testada
        targetSdk = 36
        // Código de versão interna -> aumentar a cada nova release na PlayStore
        versionCode = 1
        // Nome da versão visível para o utilizador
        versionName = "1.0"

        // O "runner" que executa os testes de instrumentação (testes de UI)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Configurações para a versão de "release" (para a Play Store)
        release {
            // Ativar a "minificação" para ofuscar o código e reduzir o tamanho do APK
            isMinifyEnabled = false // TODO: Ativar para produção
            // Ficheiros de regras para o ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Funcionalidades do processo de build a serem ativadas
    buildFeatures {
        // ViewBinding: Cria classes de "binding" para aceder às views do XML de forma segura
        viewBinding = true
        // DataBinding: Permite ligar dados diretamente a layouts XML (não estamos a usar ativamente)
        dataBinding = true
        // BuildConfig: Gera uma classe `BuildConfig` com constantes do processo de build
        buildConfig = true
    }
}

kotlin {
    // Define a versão da JVM que o compilador Kotlin deve usar
    jvmToolchain(17)
}

// Bloco onde todas as dependências (bibliotecas externas) do projeto são declaradas
dependencies {

    implementation(libs.androidx.core.ktx) // Extensões Kotlin para o Android Core
    implementation(libs.androidx.appcompat) // Suporte para componentes de UI em versões antigas do Android
    implementation(libs.material) // Componentes de UI do Material Design (Buttons, Cards, ...)
    implementation(libs.androidx.activity) // Gestão das Activities
    implementation(libs.androidx.constraintlayout) // Layout para criar interfaces

    // Componente para gerir a navegação entre ecrãs
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit) // Framework para testes unitários
    androidTestImplementation(libs.androidx.junit) // Framework para testes de instrumentação
    androidTestImplementation(libs.androidx.espresso.core) // Framework para escrever testes de UI

    // Coil: Biblioteca para carregar imagens
    implementation(libs.coil)
    // Glide: Outra biblioteca para carregamento de imagens
    implementation(libs.glide)
    ksp(libs.ksp)

    // Retrofit: Cliente HTTP para fazer pedidos à nossa API REST
    implementation(libs.retrofit)
    // Gson Converter: Converte o JSON da API para objetos Kotlin e vice-versa
    implementation(libs.converter.gson)
    implementation(libs.gson)
    // Logging Interceptor: Permite ver os detalhes dos pedidos e respostas da API no Logcat
    implementation(libs.logging.interceptor)

    // WorkManager: Para agendar tarefas em segundo plano (ex: notificações)
    implementation(libs.androidx.work.runtime.ktx)

    // Room: Biblioteca para criar e gerir a base de dados local
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // O processador de anotações que gera o código do Room

    // código do professor para Camera
    //copiado do outro ficheiro "code-Camera X app.vf"
    // CameraX core library using the camera2 implementation
    val cameraxVersion = "1.5.1"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    // implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation(libs.androidx.camera.core)
    // implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    // implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    // implementation "androidx.camera:camera-video:${camerax_version}"
    // If you want to additionally use the CameraX View class
    // implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    // implementation "androidx.camera:camera-mlkit-vision:${camerax_version}"
    // If you want to additionally use the CameraX Extensions library
    // implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    implementation(libs.androidx.camera.extensions)

}
