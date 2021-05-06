package com.dscvit.songified.repository

import androidx.lifecycle.liveData
import com.dscvit.songified.model.Result

open class BaseRepo {
    protected fun <T> makeRequest(request:suspend()->Result<T>)= liveData {
        emit(Result.Loading<T>())

        when(val response=request()){
            is Result.Success->{
                emit(Result.Success(response.data))
            }
            is Result.Error->{
                emit(Result.Error<T>(response.message))
            }

            else->{

            }
        }

    }
}