package com.dscvit.songified.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dscvit.handly.util.OnItemClickListener
import com.dscvit.handly.util.addOnItemClickListener
import com.dscvit.handly.util.createDialog
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SimpleSongbookAdapter
import com.dscvit.songified.adapter.SongCommentsAdapter
import com.dscvit.songified.databinding.FragmentSongDetailsBinding
import com.dscvit.songified.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class SongDetailsFragment : Fragment() {

    private val mTAG: String = "SongDetailsFragment"
    lateinit var songId: String
    lateinit var songbookList: MutableList<Songbook>
    private lateinit var songCommentsList: MutableList<SongComment>
    lateinit var songDetail: SongDetail
    private var _binding: FragmentSongDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songDetailsViewModel by viewModel<SongDetailsViewModel>()
        val songDetailsLoading = createProgressDialog(requireContext(), "Loading song details")

        val selectedSongId = arguments?.getString("selected_song_id").toString()
        songDetailsLoading.show()
        songDetailsViewModel.getSongDetails(selectedSongId).observe(viewLifecycleOwner, {
            when (it) {
                is Result.Loading -> {
                    Log.d(mTAG, "Loading...")
                }
                is Result.Success -> {
                    Log.d(mTAG, "Success")
                    songDetail = it.data?.song!!
                    binding.tvSongNameSongDetails.text = songDetail.songTitle
                    binding.tvArtistNameSongDetails.text = songDetail.artist.artist_title


                    binding.tvScaleSongDetails.text = songDetail.keyOf
                    binding.tvTempoSongDetails.text = songDetail.tempo
                    binding.tvTimsigSongDetails.text = songDetail.timeSig
                    songId = songDetail.songId
                    Glide.with(this)
                        .load(songDetail.artist.img)
                        .fallback(R.drawable.fallback_cover_art)
                        .into(binding.imgCoverSongDetails)
                    songDetailsLoading.dismiss()
                }
                is Result.Error -> {

                }

            }

        })
        //Song Info

        val rvSongInfo = view.findViewById(R.id.rv_song_info_song_details) as RecyclerView
        val songCommentsAdapter = SongCommentsAdapter()
        rvSongInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songCommentsAdapter
        }

        val btnSave = view.findViewById<Button>(R.id.btn_save_song_details)

        btnSave.setOnClickListener {


            val saveSongDialog = createDialog(requireContext(), true, R.layout.dialog_save_song)


            val rvSimpleSongbooks =
                saveSongDialog.findViewById(R.id.rv_dialog_songbooks_save_song) as RecyclerView
            val simpleSongbookAdapter = SimpleSongbookAdapter()
            val pbSaveSong = saveSongDialog.findViewById(R.id.pb_save_song) as ProgressBar
            rvSimpleSongbooks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = simpleSongbookAdapter
            }
            songDetailsViewModel.getSongbooks().observe(viewLifecycleOwner, {

                when (it) {
                    is Result.Loading -> {
                        Log.d(mTAG, "Loading songbooks")
                    }
                    is Result.Success -> {
                        Log.d(mTAG, "Songbooks loaded")

                        songbookList = it.data?.songbookList!!
                        pbSaveSong.visibility = View.GONE
                        simpleSongbookAdapter.updateSongbookList(songbookList)
                        Log.d(mTAG, it.data.toString())

                    }
                    is Result.Error -> {

                    }
                }
            })

            rvSimpleSongbooks.addOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    val selectedSongbookId = songbookList[position].id
                    Log.d(mTAG, selectedSongId)
                    val songBookRequest =
                        AddToSongbookRequest(
                            selectedSongbookId,
                            songDetail.songTitle,
                            " ",
                            songDetail.keyOf,
                            songDetail.tempo.toFloat().roundToInt(),
                            songDetail.artist.artist_title,
                            songDetail.timeSig,
                            songDetail.artist.img?:" "

                        )
                    Log.d(mTAG, songBookRequest.toString())
                    val saveSongLoading = createProgressDialog(
                        requireContext(),
                        "Saving to ${songbookList[position].name}"
                    )
                    saveSongLoading.show()
                    songDetailsViewModel.addToSongbook(songBookRequest)
                        .observe(viewLifecycleOwner, {

                            when (it) {
                                is Result.Loading -> {
                                    Log.d(mTAG, "Loading ...")
                                }

                                is Result.Success -> {
                                    Log.d(mTAG, "Saved to songbook")
                                    //shortToast("Saved to ${songbookList[position].name}")


                                    saveSongDialog.dismiss()
                                    saveSongLoading.dismiss()
                                    Snackbar.make(
                                        btnSave,
                                        "Saved to ${songbookList[position].name}",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                is Result.Error -> {

                                }
                            }
                        })
                }

            })

            saveSongDialog.show()

        }

        //Adding Song Info

        val btnAddInfo = view.findViewById<Button>(R.id.btn_add_info_song_details)
        btnAddInfo.setOnClickListener {
            val dialog = createDialog(requireContext(), false, R.layout.dialog_add_info)

            val tvSongTitle = dialog.findViewById(R.id.tv_dialog_song_info_title) as TextView
            tvSongTitle.text = binding.tvSongNameSongDetails.text.toString()
            val etSongInfo = dialog.findViewById(R.id.et_dialog_song_info) as TextInputEditText
            val btnSubmitDialog = dialog.findViewById(R.id.btn_dialog_submit_song_info) as Button
            val btnCancel = dialog.findViewById(R.id.btn_dialog_add_info_cancel) as Button
            btnSubmitDialog.setOnClickListener {
                val addSongInfoRequest = AddSongInfoRequest(songId, etSongInfo.text.toString())
                songDetailsViewModel.uploadSongInfo(addSongInfoRequest).observe(viewLifecycleOwner,
                    {
                        when (it) {
                            is Result.Loading -> {
                                Log.d(mTAG, "Submitting song info")
                            }

                            is Result.Success -> {
                                Log.d(mTAG, "Song Info submitted")
                                val snack = Snackbar.make(
                                    btnSubmitDialog,
                                    "Song Info Submitted",
                                    Snackbar.LENGTH_SHORT
                                )
                                snack.show()
                                dialog.dismiss()

                            }
                            is Result.Error -> {

                            }
                        }
                    })
            }
            btnCancel.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        val songInfoRequest = SongInfoRequest(selectedSongId)
        songDetailsViewModel.getSongComments(songInfoRequest).observe(viewLifecycleOwner, {
            when (it) {
                is Result.Loading -> {
                    Log.d(mTAG, "Loading user comments")
                }
                is Result.Success -> {
                    Log.d(mTAG, "User comments loaded")
                    Log.d(mTAG, it.data?.songComments.toString())
                    songCommentsList = it.data?.songComments!!
                    songCommentsAdapter.updateSongCommentsList(songCommentsList)
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