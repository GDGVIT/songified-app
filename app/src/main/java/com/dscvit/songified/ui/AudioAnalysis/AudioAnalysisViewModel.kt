package com.dscvit.songified.ui.AudioAnalysis

import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.AnalysedDataRequest
import com.dscvit.songified.repository.AppRepo

class AudioAnalysisViewModel (private val repo: AppRepo) : ViewModel() {
    fun getAnalysedData(analysedDataRequest: AnalysedDataRequest)=repo.getAnalysedData(analysedDataRequest)


}