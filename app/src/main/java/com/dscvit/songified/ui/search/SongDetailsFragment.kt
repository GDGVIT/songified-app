package com.dscvit.songified.ui.search

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SimpleSongbookAdapter
import com.dscvit.songified.adapter.SongCommentsAdapter
import com.dscvit.songified.databinding.FragmentSongDetailsBinding
import com.dscvit.songified.model.AddSongInfoRequest
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SongComment
import com.dscvit.songified.model.SongDetail
import com.dscvit.songified.model.SongInfoRequest
import com.dscvit.songified.model.Songbook
import com.dscvit.songified.ui.login.LoginBottomSheetFragment
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.OnItemClickListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.get
import com.dscvit.songified.util.addOnItemClickListener
import com.dscvit.songified.util.createDialog
import com.dscvit.songified.util.createProgressDialog
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
    private lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)
        val songDetailsViewModel by viewModel<SongDetailsViewModel>()
        val songDetailsLoading = createProgressDialog(requireContext(), "Loading song details")
        binding.backSongDetails.setOnClickListener {
            it.findNavController().navigateUp()
        }
        if (sharedPref[Constants.PREF_IS_AUTH, false]!!) {

            binding.btnSaveSongDetails.visibility = View.VISIBLE
            binding.btnAddInfoSongDetails.visibility = View.VISIBLE
        } else {
            binding.btnAddInfoSongDetails.visibility = View.INVISIBLE
            binding.btnSaveSongDetails.visibility = View.INVISIBLE
        }
        val selectedSongId = arguments?.getString("selected_song_id").toString()
        songDetailsLoading.show()
        songDetailsViewModel.getSongDetails(selectedSongId).observe(
            viewLifecycleOwner,
            {
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
            }
        )
        // Song Info

        val rvSongInfo = view.findViewById(R.id.rv_song_info_song_details) as RecyclerView
        val songCommentsAdapter = SongCommentsAdapter()
        rvSongInfo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songCommentsAdapter
        }

        binding.btnSaveSongDetails.setOnClickListener {
            if (sharedPref[Constants.PREF_IS_AUTH, false]!!) {

                val saveSongDialog = createDialog(requireContext(), true, R.layout.dialog_save_song)

                val rvSimpleSongbooks =
                    saveSongDialog.findViewById(R.id.rv_dialog_songbooks_save_song) as RecyclerView
                val tvNoSongbooks =
                    saveSongDialog.findViewById(R.id.tv_dialog_save_no_songbooks) as TextView
                val simpleSongbookAdapter = SimpleSongbookAdapter()
                val pbSaveSong = saveSongDialog.findViewById(R.id.pb_save_song) as ProgressBar
                rvSimpleSongbooks.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = simpleSongbookAdapter
                }
                songDetailsViewModel.getSongbooks().observe(
                    viewLifecycleOwner,
                    {

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
                                if (simpleSongbookAdapter.itemCount == 0) {
                                    tvNoSongbooks.visibility = View.VISIBLE
                                } else {
                                    tvNoSongbooks.visibility = View.GONE
                                }
                            }
                            is Result.Error -> {
                            }
                        }
                    }
                )

                rvSimpleSongbooks.addOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        val selectedSongbookId = songbookList[position].id
                        Log.d(mTAG, selectedSongId)
                        Log.d(mTAG, "$songDetail")
                        var mTempo = " "
                        songDetail.tempo?.let {
                            mTempo = it.toFloat().roundToInt().toString()
                        }

                        val songBookRequest =
                            AddToSongbookRequest(
                                selectedSongbookId,
                                songDetail.songTitle,
                                " ",
                                songDetail.keyOf,
                                mTempo,
                                songDetail.artist.artist_title,
                                songDetail.timeSig ?: " ",
                                songDetail.artist.img ?: " "

                            )
                        Log.d(mTAG, songBookRequest.toString())
                        val saveSongLoading = createProgressDialog(
                            requireContext(),
                            "Saving to ${songbookList[position].name}"
                        )
                        saveSongLoading.show()
                        songDetailsViewModel.addToSongbook(songBookRequest)
                            .observe(
                                viewLifecycleOwner,
                                {

                                    when (it) {
                                        is Result.Loading -> {
                                            Log.d(mTAG, "Loading ...")
                                        }

                                        is Result.Success -> {
                                            Log.d(mTAG, "Saved to songbook")
                                            // shortToast("Saved to ${songbookList[position].name}")

                                            saveSongDialog.dismiss()
                                            saveSongLoading.dismiss()
                                            Snackbar.make(
                                                binding.root,
                                                "Saved to ${songbookList[position].name}",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                        is Result.Error -> {
                                        }
                                    }
                                }
                            )
                    }
                })

                saveSongDialog.show()
            } else {
                val loginBottomSheet = LoginBottomSheetFragment()

                loginBottomSheet.dismissListener(object : DialogDismissListener {
                    override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    }
                })
                loginBottomSheet.show(this.parentFragmentManager, "TAG")
            }
        }

        // Adding Song Info

        val btnAddInfo = view.findViewById<Button>(R.id.btn_add_info_song_details)
        btnAddInfo.setOnClickListener {
            if (sharedPref[Constants.PREF_IS_AUTH, false]!!) {
                val dialog = createDialog(requireContext(), false, R.layout.dialog_add_info)

                val tvSongTitle = dialog.findViewById(R.id.tv_dialog_song_info_title) as TextView
                tvSongTitle.text = binding.tvSongNameSongDetails.text.toString()
                val etSongInfo = dialog.findViewById(R.id.et_dialog_song_info) as TextInputEditText
                val btnSubmitDialog =
                    dialog.findViewById(R.id.btn_dialog_submit_song_info) as Button
                val btnCancel = dialog.findViewById(R.id.btn_dialog_add_info_cancel) as Button
                val addCommentLoading =
                    createProgressDialog(requireContext(), "Submitting comment...")
                btnSubmitDialog.setOnClickListener {
                    if (etSongInfo.text.toString().trim() != "") {

                        val addSongInfoRequest = AddSongInfoRequest(
                            songId,
                            etSongInfo.text.toString(),
                            binding.tvSongNameSongDetails.text.toString()
                        )
                        songDetailsViewModel.uploadSongInfo(addSongInfoRequest)
                            .observe(
                                viewLifecycleOwner,
                                {
                                    when (it) {
                                        is Result.Loading -> {
                                            addCommentLoading.show()
                                            Log.d(mTAG, "Submitting song info")
                                        }

                                        is Result.Success -> {
                                            addCommentLoading.dismiss()
                                            Log.d(mTAG, "Comment submitted")
                                            Snackbar.make(
                                                binding.root,
                                                "Comment submitted",
                                                Snackbar.LENGTH_SHORT
                                            ).show()

                                            dialog.dismiss()
                                        }
                                        is Result.Error -> {
                                            addCommentLoading.dismiss()
                                        }
                                    }
                                }
                            )
                    } else {
                        Snackbar.make(
                            dialog.findViewById(R.id.et_dialog_song_info),
                            "Comment cannot be empty",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                btnCancel.setOnClickListener { dialog.dismiss() }

                dialog.show()
            } else {
                val loginBottomSheet = LoginBottomSheetFragment()

                loginBottomSheet.dismissListener(object : DialogDismissListener {
                    override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    }
                })
                loginBottomSheet.show(this.parentFragmentManager, "TAG")
            }
        }

        val songInfoRequest = SongInfoRequest(selectedSongId)
        songDetailsViewModel.getSongComments(songInfoRequest).observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is Result.Loading -> {
                        Log.d(mTAG, "Loading user comments")
                        binding.pbCommentsSongDetails.visibility = View.VISIBLE
                        binding.tvNoCommentsSongDetails.visibility = View.GONE
                    }
                    is Result.Success -> {
                        Log.d(mTAG, "User comments loaded")
                        Log.d(mTAG, it.data?.songComments.toString())
                        songCommentsList = it.data?.songComments!!
                        songCommentsAdapter.updateSongCommentsList(songCommentsList)
                        binding.pbCommentsSongDetails.visibility = View.GONE
                        if (songCommentsAdapter.itemCount == 0) {
                            binding.tvNoCommentsSongDetails.visibility = View.VISIBLE
                        } else {
                            binding.tvNoCommentsSongDetails.visibility = View.GONE
                        }
                    }
                    is Result.Error -> {
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
