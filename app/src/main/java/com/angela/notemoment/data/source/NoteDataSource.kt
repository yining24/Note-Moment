package com.angela.notemoment.data.source

interface NoteDataSource {
    suspend fun getMarketingHots()
   // suspend fun getMarketingHots(): Result<List<HomeItem>>
}