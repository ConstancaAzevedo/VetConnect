package pt.ipt.dam2025.trabalho.repository

import pt.ipt.dam2025.trabalho.data.AnimalDao
import pt.ipt.dam2025.trabalho.model.Animal

class AnimalRepository(private val animalDao: AnimalDao) {

    suspend fun insert(animal: Animal) {
        animalDao.insert(animal)
    }
}