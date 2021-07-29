package com.dscvit.songified.ui.songbook

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.dscvit.songified.R
import com.dscvit.songified.databinding.FragmentSongbookSongDetailsBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SingleSongbookSong
import com.dscvit.songified.model.UpdateSongInSongbookRequest
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class SongbookSongDetailFragment : Fragment() {
    private val mTAG = "SongbookSongDetail"
    private var _binding: FragmentSongbookSongDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongbookSongDetailsBinding.inflate(inflater, container, false)
        val selectedSongbookSong: SingleSongbookSong =
            arguments?.get("selected_song") as SingleSongbookSong
        ViewCompat.setTransitionName(
            binding.root,
            "song_name_transition_${selectedSongbookSong.songId}"
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(this.context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(this.context).inflateTransition(android.R.transition.move)

        val songbookSongDetailViewModel by viewModel<SongbookSongDetailViewModel>()
        val selectedSongbookSong: SingleSongbookSong =
            arguments?.get("selected_song") as SingleSongbookSong
        val selectedSongbookId = arguments?.getString("selected_songbook_id") as String

        binding.backSongbookSongDetails.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.tvSongnameSongbookSongDetail.text = selectedSongbookSong.songTitle
        binding.tvArtistSongbookSongDetail.text = selectedSongbookSong.artist
        binding.tvScaleSongbookSongDetail.text = selectedSongbookSong.scale
        binding.tvTempoSongbookSongDetail.text = selectedSongbookSong.tempo
        binding.tvTimeSigSongbookSongDetail.text = selectedSongbookSong.timSig
        binding.etNotesSongbookSongDetail.setText(selectedSongbookSong.songBody)
        Glide.with(this)
            .load(selectedSongbookSong.coverArt)
            .fallback(R.drawable.fallback_cover_art)
            .placeholder(R.drawable.fallback_cover_art)
            .into(binding.imgCoverArtSongbookSongDetails)

        binding.btnUpdateSongbookSong.setOnClickListener {

            if (binding.etNotesSongbookSongDetail.text.toString() != "") {
                val songBody = binding.etNotesSongbookSongDetail.text.toString()
                val updateSongRequest = UpdateSongInSongbookRequest(
                    selectedSongbookId,
                    selectedSongbookSong.songId,
                    selectedSongbookSong.songTitle,
                    songBody,
                    selectedSongbookSong.scale,
                    selectedSongbookSong.tempo.toInt(),
                    selectedSongbookSong.artist,
                    selectedSongbookSong.timSig,
                    selectedSongbookSong.coverArt
                )
                binding.pbUpdateSongInSongbook.visibility = View.VISIBLE
                songbookSongDetailViewModel.editSongbook(updateSongRequest)
                    .observe(
                        viewLifecycleOwner,
                        {
                            when (it) {
                                is Result.Loading -> {
                                    binding.btnUpdateSongbookSong.isEnabled = false
                                    Log.d(mTAG, "Saving songbook")
                                }
                                is Result.Success -> {
                                    Log.d(mTAG, "Songbook updated")
                                    binding.btnUpdateSongbookSong.visibility = View.GONE
                                    binding.pbUpdateSongInSongbook.visibility = View.GONE
                                    Snackbar.make(
                                        binding.root,
                                        "Song updated",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                is Result.Error -> {

                                    binding.pbUpdateSongInSongbook.visibility = View.GONE
                                    binding.btnUpdateSongbookSong.isEnabled = true
                                    Snackbar.make(
                                        binding.root,
                                        "Some error occured",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                    Log.d(mTAG, it.message ?: "some error")
                                }
                            }
                        }
                    )
            }
        }

        binding.etNotesSongbookSongDetail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnUpdateSongbookSong.visibility = View.VISIBLE
                binding.btnUpdateSongbookSong.isEnabled = true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
