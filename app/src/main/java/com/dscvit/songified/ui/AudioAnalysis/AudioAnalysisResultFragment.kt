package com.dscvit.songified.ui.AudioAnalysis

import android.R.color
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SimpleTagAdapter
import com.dscvit.songified.model.AnalysedDataRequest
import com.dscvit.songified.model.Result
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt


class AudioAnalysisResultFragment : Fragment() {
    val TAG = "UserProfileFragment"
    lateinit var processingLayout: LinearLayout
    lateinit var contentLayout: LinearLayout
    lateinit var tvSongName: TextView
    lateinit var tvScale: TextView
    lateinit var tvTempo: TextView
    lateinit var rvGenres: RecyclerView
    lateinit var rvMoods: RecyclerView
    lateinit var tvEnergy: TextView
    lateinit var tvMusicalEra: TextView
    lateinit var tvEmotionalProfile: TextView
    lateinit var pbEnergy: LinearProgressIndicator
    lateinit var pbEmotionalProfile: LinearProgressIndicator
    lateinit var simpleGenreAdatper: SimpleTagAdapter
    lateinit var simpleMoodAdapter: SimpleTagAdapter
    lateinit var audioAnalysisViewModel: AudioAnalysisViewModel
    lateinit var audioAnalysisLoading: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioAnalysisViewModel = getViewModel()
        audioAnalysisLoading = createProgressDialog(
            requireContext(),
            "Getting analysed song data"
        )

        processingLayout = view.findViewById(R.id.layout_processing_audio_analysis) as LinearLayout
        contentLayout = view.findViewById(R.id.layout_content_audio_analyis) as LinearLayout
        val tvRefresh = view.findViewById(R.id.tv_refresh_audio_analysis) as TextView
        tvSongName = view.findViewById(R.id.tv_songname_audio_analysis) as TextView
        tvScale = view.findViewById(R.id.tv_scale_audio_analysis) as TextView
        tvTempo = view.findViewById(R.id.tv_tempo_audio_analysis) as TextView
        rvGenres = view.findViewById(R.id.rv_genres_audio_analysis) as RecyclerView
        rvMoods = view.findViewById(R.id.rv_moods_audio_analyis) as RecyclerView

        tvEnergy = view.findViewById(R.id.tv_energy_audio_analysis) as TextView
        pbEnergy = view.findViewById(R.id.pb_energy_audio_analysis) as LinearProgressIndicator

        tvMusicalEra = view.findViewById(R.id.tv_musical_era_audio_analysis) as TextView
        tvEmotionalProfile = view.findViewById(R.id.tv_emotional_audio_analysis) as TextView
        pbEmotionalProfile =
            view.findViewById(R.id.pb_emotional_audio_analysis) as LinearProgressIndicator

        simpleGenreAdatper = SimpleTagAdapter()
        simpleMoodAdapter = SimpleTagAdapter()

        rvGenres.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = simpleGenreAdatper
        }

        rvMoods.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = simpleMoodAdapter
        }
        val analyseSongId = arguments?.getString("analyse_song_id").toString()
        val analyseSongName = arguments?.getString("analyse_song_name").toString()
        tvSongName.text = analyseSongName
        loadAnalysedData(analyseSongId)
        tvRefresh.setOnClickListener{
            loadAnalysedData(analyseSongId)
        }

    }

    private fun loadAnalysedData(analyseSongId: String) {
        val analysedDataRequest = AnalysedDataRequest(analyseSongId)
        audioAnalysisViewModel.getAnalysedData(analysedDataRequest).observe(viewLifecycleOwner,
            Observer {
                when (it) {
                    is Result.Loading -> {
                        audioAnalysisLoading.show()
                        Log.d(TAG, "Loading Analysed Data...")
                    }

                    is Result.Success -> {
                        Log.d(TAG, it.data.toString())
                        if (it.data?.status == "Finished") {
                            Log.d(TAG, "Analysis Staus : Finished")
                            Log.d(TAG, "Analysed Data loaded")
                            val keysArray =
                                it.data?.analysisData?.fullScaleAnalysis?.scaleAnalysis?.key?.keys
                            for (keyIndex in keysArray.indices) {
                                tvScale.text = tvScale.text.toString() + keysArray[keyIndex]
                                if (keyIndex != keysArray.size - 1) {
                                    tvScale.text = tvScale.text.toString() + " | "
                                }
                            }
                            /* tvScale.text =
                                 it.data?.analysisData?.fullScaleAnalysis?.scaleAnalysis?.key?.keys?.get(
                                     0
                                 )*/
                            tvTempo.text =
                                it.data?.analysisData?.fullScaleAnalysis?.scaleAnalysis?.bpm.toFloat()
                                    .roundToInt().toString()
                            simpleGenreAdatper.updateTagsList(it.data?.analysisData?.audioAnalysisResponseData?.audioAnalysis?.genreTags)
                            simpleMoodAdapter.updateTagsList(it.data?.analysisData?.audioAnalysisResponseData?.audioAnalysis?.moodTags)
                            tvMusicalEra.text =
                                it.data?.analysisData?.audioAnalysisResponseData?.audioAnalysis?.musicalEraTag
                            val energy =
                                it.data?.analysisData?.audioAnalysisResponseData?.audioAnalysis?.energy

                            tvEnergy.text = " Energy Level : ${energy}"
                            when (energy) {
                                "low" -> {
                                    pbEnergy.progress = 15
                                }
                                "medium" -> {
                                    pbEnergy.progress = 30
                                }
                                "high" -> {
                                    pbEnergy.progress = 55
                                }
                                else -> {
                                    pbEnergy.visibility = View.GONE
                                }
                            }

                            val emotionalProfile =
                                it.data?.analysisData?.audioAnalysisResponseData?.audioAnalysis?.emotionalProfile
                            tvEmotionalProfile.text = "Emotional Profile : ${emotionalProfile}"

                            when (emotionalProfile) {
                                "positive" -> {
                                    pbEmotionalProfile.progress = 55
                                }
                                "negative" -> {
                                    pbEmotionalProfile.progress = 5
                                }
                                "balanced" -> {
                                    pbEmotionalProfile.progress = 30
                                }
                                "variable" -> {
                                    pbEmotionalProfile.setIndicatorColor(resources.getColor(R.color.songified_accent))
                                    pbEmotionalProfile.trackColor =
                                        resources.getColor(R.color.songified_accent)



                                    pbEmotionalProfile.progress = 55
                                }
                            }
                            processingLayout.visibility = View.GONE
                            contentLayout.visibility = View.VISIBLE

                            audioAnalysisLoading.dismiss()
                        } else {
                            processingLayout.visibility = View.VISIBLE
                            contentLayout.visibility = View.GONE
                            audioAnalysisLoading.dismiss()


                            Log.d(TAG, "AnalysisStatus: ${it.data?.status}")
                        }

                    }
                }
            })
    }
}