package com.dscvit.songified.ui.songbook

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongbookSongAdapter
import com.dscvit.songified.databinding.FragmentSingleSongbookBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SingleSongbookRequest
import com.dscvit.songified.model.SingleSongbookSong
import com.dscvit.songified.model.SongbookSongDeleteRequest
import com.dscvit.songified.util.OnItemClickListener
import com.dscvit.songified.util.addOnItemClickListener
import com.dscvit.songified.util.addOnItemLongClickListener
import com.dscvit.songified.util.createProgressDialog
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.Locale

class SingleSongbookFragment : Fragment() {
    lateinit var songs: MutableList<SingleSongbookSong>
    private val mTAG = "SingleSongBookFragment"
    private lateinit var songbookSongAdapter: SongbookSongAdapter
    lateinit var singleSongbookViewModel: SingleSongbookViewModel
    private lateinit var singleSongbookLoadingDialog: Dialog

    private var _binding: FragmentSingleSongbookBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedSongBookId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (savedInstanceState == null) {
            _binding = FragmentSingleSongbookBinding.inflate(inflater, container, false)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarSingleSongbookFragment.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        songbookSongAdapter = SongbookSongAdapter()
        binding.rvSongsSongbook.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songbookSongAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        singleSongbookViewModel = getViewModel()
        selectedSongBookId = arguments?.getString("selected_songbook_id").toString()

        binding.toolbarSingleSongbookFragment.title =
            arguments?.getString("selected_songbook_name").toString()
                .capitalize(Locale.getDefault())
        val singleSongbookRequest = SingleSongbookRequest(selectedSongBookId)

        getSongsInSongbook(singleSongbookRequest)

        binding.rvSongsSongbook.addOnItemLongClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val deleteSongRequest =
                    SongbookSongDeleteRequest(selectedSongBookId, songs[position].songId)
                val alertDialogBuilder =
                    AlertDialog.Builder(requireContext(), R.style.MyAlertDialog)
                alertDialogBuilder.setTitle("Delete Song")
                alertDialogBuilder.setMessage("Are you sure you want to delete ${songs[position].songTitle} ?")
// builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                    val deleteSongLoading = createProgressDialog(
                        requireContext(),
                        "Deleting ${songs[position].songTitle}"
                    )
                    deleteSongLoading.show()
                    singleSongbookViewModel.deleteSong(deleteSongRequest)
                        .observe(
                            viewLifecycleOwner,
                            {
                                when (it) {
                                    is Result.Loading -> {
                                        Log.d(mTAG, "Deleting song")
                                    }

                                    is Result.Success -> {

                                        Log.d(mTAG, "Response : ${it.data?.message}")
                                        deleteSongLoading.dismiss()
                                        Snackbar.make(
                                            binding.root,
                                            "Deleted",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        getSongsInSongbook(singleSongbookRequest)
                                    }
                                    is Result.Error -> {
                                    }
                                }
                            }
                        )
                }

                alertDialogBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }

                alertDialogBuilder.show()
            }
        })

        binding.rvSongsSongbook.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val selectedSong = songs[position]
//                val bundle = bundleOf(
//                    "selected_song" to selectedSong,
//                    "selected_songbook_id" to selectedSongBookId
//
//                )
                ViewCompat.setTransitionName(view, "song_name_transition_${selectedSong.songId}")
                val extras =
                    FragmentNavigatorExtras(view to "song_name_transition_${selectedSong.songId}")
                view.findNavController().navigate(
                    SingleSongbookFragmentDirections.actionSingleSongbookToSongDetail(
                        selectedSong,
                        selectedSongBookId
                    ),
                    extras
                )
//                view.findNavController()
//                    .navigate(R.id.action_single_songbook_to_song_detail, bundle, null, extras)
            }
        })
    }

    private fun getSongsInSongbook(singleSongbookRequest: SingleSongbookRequest) {
        singleSongbookLoadingDialog = createProgressDialog(requireContext(), "Loading songs ...")
        singleSongbookLoadingDialog.show()
        singleSongbookViewModel.getSingleSongbook(singleSongbookRequest).observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is Result.Loading -> {
                        Log.d(mTAG, "Loading songs in songbook")
                    }
                    is Result.Success -> {
                        Log.d(mTAG, "Songs in songbook loaded")
                        songs = it.data?.data?.songbookSongs!!
                        songbookSongAdapter.updateSongsList(songs, selectedSongBookId)
                        singleSongbookLoadingDialog.dismiss()
                        if (songbookSongAdapter.itemCount == 0) {

                            binding.layoutNoSongs.visibility = View.VISIBLE
                            binding.toolbarSingleSongbookFragment.subtitle = ""
                        } else {

                            binding.layoutNoSongs.visibility = View.GONE
                            binding.toolbarSingleSongbookFragment.subtitle =
                                getString(R.string.songs_count_data, songbookSongAdapter.itemCount)
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
