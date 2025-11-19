package pt.ipt.dam2025.trabalho

import android.app.Application

/**
 * Classe de Aplicação personalizada para o VetConnect.
 * É usada para inicializar e manter uma instância única da base de dados.
 */
class VetConnectApplication : Application() {
    // A base de dados será criada de forma "lazy" (preguiçosa), ou seja,
    // só na primeira vez que for acedida.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
