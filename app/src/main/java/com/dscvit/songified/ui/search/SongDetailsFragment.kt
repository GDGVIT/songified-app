package com.dscvit.songified.ui.search

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dscvit.handly.util.OnItemClickListener
import com.dscvit.handly.util.addOnItemClickListener
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.handly.util.shortToast
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SimpleSongbookAdapter
import com.dscvit.songified.adapter.SongCommentsAdapter
import com.dscvit.songified.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class SongDetailsFragment : Fragment() {

    private val TAG: String = "SongDetailsFragment"
    lateinit var songId: String
    lateinit var songbookList: MutableList<Songbook>
    lateinit var songCommentsList: MutableList<SongComment>
    lateinit var songDetail: SongDetail
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songDetailsViewModel by viewModel<SongDetailsViewModel>()
        val songDetailsLoading= createProgressDialog(requireContext(),"Loading song details")
        val tvSongName = view.findViewById<TextView>(R.id.tv_song_name_song_details)
        val tvArtist = view.findViewById<TextView>(R.id.tv_artist_name_song_details)
        val tvTempScale = view.findViewById<TextView>(R.id.tv_tempo_scale_song_details)
        val tvScale=view.findViewById(R.id.tv_scale_song_details) as TextView
        val tvTempo=view.findViewById(R.id.tv_tempo_song_details) as TextView
        val tvTimSig=view.findViewById(R.id.tv_timsig_song_details) as TextView
        val imgSong = view.findViewById<ImageView>(R.id.img_cover_song_details)
        val selectedSongId = arguments?.getString("selected_song_id").toString()
        songDetailsLoading.show()
        songDetailsViewModel.getSongDetails(selectedSongId).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Result.Loading -> {
                    Log.d(TAG, "Loading...")
                }
                is Result.Success -> {
                    Log.d(TAG, "Success")
                    songDetail = it.data?.song!!
                    tvSongName.text = songDetail?.songTitle
                    tvArtist.text = songDetail?.artist?.artist_title
                    tvTempScale.text = "Tempo : ${songDetail?.tempo} | Scale : ${songDetail?.keyOf}"

                    tvScale.text=songDetail?.keyOf
                    tvTempo.text=songDetail?.tempo
                    tvTimSig.text=songDetail?.timeSig
                    songId = songDetail?.songId ?: ""
                    Glide.with(this)
                        .load(songDetail?.artist?.img)
                        .fallback(R.drawable.fallback_cover_art)
                        .into(imgSong)
                    songDetailsLoading.dismiss()
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


            val saveSongDialog = Dialog(requireContext())
            saveSongDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            saveSongDialog.setCancelable(true)
            saveSongDialog.setContentView(R.layout.dialog_save_song)
            saveSongDialog.getWindow()?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            saveSongDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);

            val rvSimpleSongbooks =
                saveSongDialog.findViewById(R.id.rv_dialog_songbooks_save_song) as RecyclerView
            val simpleSongbookAdapter = SimpleSongbookAdapter()
            val pbSaveSong = saveSongDialog.findViewById(R.id.pb_save_song) as ProgressBar
            rvSimpleSongbooks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = simpleSongbookAdapter
            }
            songDetailsViewModel.getSongbooks().observe(viewLifecycleOwner, Observer {

                when (it) {
                    is Result.Loading -> {
                        Log.d(TAG, "Loading songbooks")
                    }
                    is Result.Success -> {
                        Log.d(TAG, "Songbooks loaded")

                        songbookList = it.data?.songbookList!!
                        pbSaveSong.visibility = View.GONE
                        simpleSongbookAdapter.updateSongbookList(songbookList)
                        Log.d(TAG, it.data.toString())

                    }
                }
            })

            rvSimpleSongbooks.addOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int, view: View) {
                    val selectedSongbookId = songbookList[position].id
                    Log.d(TAG, selectedSongId)
                    val songBookRequest =
                        AddToSongbookRequest(
                            selectedSongbookId,
                            songDetail.songTitle,
                            " ",
                            songDetail.keyOf,
                            songDetail.tempo.toFloat().roundToInt(),
                            songDetail.artist.artist_title

                        )
                    Log.d(TAG, songBookRequest.toString())
                    val saveSongLoading = createProgressDialog(requireContext(), "Saving to ${songbookList[position].name}")
                    saveSongLoading.show()
                    songDetailsViewModel.addToSongbook(songBookRequest)
                        .observe(viewLifecycleOwner, Observer {

                            when (it) {
                                is Result.Loading -> {
                                    Log.d(TAG, "Loading ...")
                                }

                                is Result.Success -> {
                                    Log.d(TAG, "Saved to songbook")
                                    //shortToast("Saved to ${songbookList[position].name}")


                                    saveSongDialog.dismiss()
                                    saveSongLoading.dismiss()
                                    Snackbar.make(
                                        btnSave,
                                        "Saved to ${songbookList[position].name}",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
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
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_add_info)
            dialog.getWindow()?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);

            val tvSongTitle = dialog.findViewById(R.id.tv_dialog_song_info_title) as TextView
            tvSongTitle.text = tvSongName.text.toString()
            val etSongInfo = dialog.findViewById(R.id.et_dialog_song_info) as TextInputEditText
            val btnSubmitDialog = dialog.findViewById(R.id.btn_dialog_submit_song_info) as Button
            val btnCancel = dialog.findViewById(R.id.btn_dialog_add_info_cancel) as Button
            btnSubmitDialog.setOnClickListener {
                val addSongInfoRequest = AddSongInfoRequest(songId, etSongInfo.text.toString())
                songDetailsViewModel.uploadSongInfo(addSongInfoRequest).observe(viewLifecycleOwner,
                    Observer {
                        when (it) {
                            is Result.Loading -> {
                                Log.d(TAG, "Submitting song info")
                            }

                            is Result.Success -> {
                                Log.d(TAG, "Song Info submitted")
                                val snack = Snackbar.make(
                                    btnSubmitDialog,
                                    "Song Info Submitted",
                                    Snackbar.LENGTH_SHORT
                                )
                                snack.show()
                                dialog.dismiss()

                            }
                        }
                    })
            }
            btnCancel.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        val songInfoRequest = SongInfoRequest(selectedSongId)
        songDetailsViewModel.getSongComments(songInfoRequest).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Result.Loading -> {
                    Log.d(TAG, "Loading user comments")
                }
                is Result.Success -> {
                    Log.d(TAG, "User comments loaded")
                    Log.d(TAG, it.data?.songComments.toString())
                    songCommentsList = it.data?.songComments!!
                    songCommentsAdapter.updateSongCommentsList(songCommentsList)
                }
            }
        })
    }
}