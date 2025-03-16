package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Contest
import com.example.myapp.domain.repository.PhotoRepository

class GetContestsUseCase(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(): List<Contest> {
        return photoRepository.getContests()
    }
}