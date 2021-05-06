package com.dscvit.songified.ui.audioanalysis

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SimpleTagAdapter
import com.dscvit.songified.databinding.FragmentAudioAnalysisBinding
import com.dscvit.songified.model.AnalysedDataRequest
import com.dscvit.songified.model.Result
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.math.roundToInt


class AudioAnalysisResultFragment : Fragment() {
    private val mTAG = "UserProfileFragment"

    private lateinit var audioAnalysisViewModel: AudioAnalysisViewModel
    private lateinit var audioAnalysisLoading: Dialog
    private lateinit var simpleGenreAdapter: SimpleTagAdapter
    private lateinit var simpleMoodAdapter: SimpleTagAdapter
    private var _binding: FragmentAudioAnalysisBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAudioAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioAnalysisViewModel = getViewModel()
        audioAnalysisLoading = createProgressDialog(
            requireContext(),
            "Getting analysed song data"
        )



        simpleGenreAdapter = SimpleTagAdapter()
        simpleMoodAdapter = SimpleTagAdapter()

        binding.rvGenresAudioAnalysis.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = simpleGenreAdapter
        }

        binding.rvMoodsAudioAnalyis.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = simpleMoodAdapter
        }
        val analyseSongId = arguments?.getString("analyse_song_id").toString()
        val analyseSongName = arguments?.getString("analyse_song_name").toString()
        binding.tvSongnameAudioAnalysis.text = analyseSongName

        loadAnalysedData(analyseSongId)
        binding.tvRefreshAudioAnalysis.setOnClickListener {
            loadAnalysedData(analyseSongId)
        }

    }

    private fun loadAnalysedData(analyseSongId: String) {
        val analysedDataRequest = AnalysedDataRequest(analyseSongId)
        audioAnalysisViewModel.getAnalysedData(analysedDataRequest).observe(viewLifecycleOwner,
            {
                when (it) {
                    is Result.Loading -> {
                        audioAnalysisLoading.show()
                        Log.d(mTAG, "Loading Analysed Data...")
                    }

                    is Result.Success -> {
                        Log.d(mTAG, it.data.toString())
                        if (it.data?.status == "Finished") {
                            Log.d(mTAG, "Analysis Status : Finished")
                            Log.d(mTAG, "Analysed Data loaded")
                            val keysArray =
                                it.data.analysisData.fullScaleAnalysis.scaleAnalysis.key.keys
                            var keysString = ""
                            for (keyIndex in keysArray.indices) {
                                keysString += keysArray[keyIndex]
                                if (keyIndex != keysArray.size - 1) {
                                    keysString += " | "
                                }
                                binding.tvScaleAudioAnalysis.text = keysString
                            }
                            /* tvScale.text =
                                 it.data?.analysisData?.fullScaleAnalysis?.scaleAnalysis?.key?.keys?.get(
                                     0
                                 )*/
                            binding.tvTempoAudioAnalysis.text =
                                it.data.analysisData.fullScaleAnalysis.scaleAnalysis.bpm.toFloat()
                                    .roundToInt().toString()
                            simpleGenreAdapter.updateTagsList(it.data.analysisData.audioAnalysisResponseData.audioAnalysis.genreTags)
                            simpleMoodAdapter.updateTagsList(it.data.analysisData.audioAnalysisResponseData.audioAnalysis.moodTags)
                            binding.tvMusicalEraAudioAnalysis.text =
                                it.data.analysisData.audioAnalysisResponseData.audioAnalysis.musicalEraTag
                            val energy =
                                it.data.analysisData.audioAnalysisResponseData.audioAnalysis.energy

                            binding.tvEnergyAudioAnalysis.text =
                                getString(R.string.energy_level_data, energy)
                            when (energy) {
                                "low" -> {
                                    binding.pbEnergyAudioAnalysis.progress = 15
                                }
                                "medium" -> {
                                    binding.pbEnergyAudioAnalysis.progress = 30
                                }
                                "high" -> {
                                    binding.pbEnergyAudioAnalysis.progress = 55
                                }
                                else -> {
                                    binding.pbEnergyAudioAnalysis.visibility = View.GONE
                                }
                            }

                            val emotionalProfile =
                                it.data.analysisData.audioAnalysisResponseData.audioAnalysis.emotionalProfile
                            binding.tvEmotionalAudioAnalysis.text =
                                getString(R.string.emotional_profile_data, emotionalProfile)

                            when (emotionalProfile) {
                                "positive" -> {
                                    binding.pbEmotionalAudioAnalysis.progress = 55
                                }
                                "negative" -> {
                                    binding.pbEmotionalAudioAnalysis.progress = 5
                                }
                                "balanced" -> {
                                    binding.pbEmotionalAudioAnalysis.progress = 30
                                }
                                "variable" -> {

                                    binding.pbEmotionalAudioAnalysis.progress = 55
                                }
                            }
                            binding.layoutProcessingAudioAnalysis.visibility = View.GONE
                            binding.layoutContentAudioAnalyis.visibility = View.VISIBLE

                            audioAnalysisLoading.dismiss()
                        } else {
                            binding.layoutProcessingAudioAnalysis.visibility = View.VISIBLE
                            binding.layoutContentAudioAnalyis.visibility = View.GONE
                            audioAnalysisLoading.dismiss()


                            Log.d(mTAG, "AnalysisStatus: ${it.data?.status}")
                        }

                    }
                    is Result.Error -> {

                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}