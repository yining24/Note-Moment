package com.angela.notemoment.data.source

interface NoteRepository {
    suspend fun getMarketingHots()
    //suspend fun getMarketingHots(): Result<List<HomeItem>>

}