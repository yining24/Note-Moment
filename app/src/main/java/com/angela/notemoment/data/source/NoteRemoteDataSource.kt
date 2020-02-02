package com.angela.notemoment.data.source

object NoteRemoteDataSource : NoteDataSource {

    override suspend fun getMarketingHots() {

//        if (!isInternetConnected()) {
//            return Result.Fail(getString(R.string.internet_not_connected))
//        }
//        // Get the Deferred object for our Retrofit request
//        val getResultDeferred = StylishApi.retrofitService.getMarketingHots()
//        return try {
//            // this will run on a thread managed by Retrofit
//            val listResult = getResultDeferred.await()
//
//            listResult.error?.let {
//                return Result.Fail(it)
//            }
//            Result.Success(listResult.toHomeItems())
//
//        } catch (e: Exception) {
//            Logger.w("[${this::class.simpleName}] exception=${e.message}")
//            Result.Error(e)
//        }
    }
}