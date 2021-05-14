package com.dscvit.songified.ui.songbook

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscvit.handly.util.OnItemClickListener
import com.dscvit.handly.util.addOnItemClickListener
import com.dscvit.handly.util.addOnItemLongClickListener
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongbookSongAdapter
import com.dscvit.songified.databinding.FragmentSingleSongbookBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SingleSongbookRequest
import com.dscvit.songified.model.SingleSongbookSong
import com.dscvit.songified.model.SongbookSongDeleteRequest
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.*

class SingleSongbookFragment : Fragment() {
    lateinit var songs: MutableList<SingleSongbookSong>
    private val mTAG = "SingleSongBookFragment"
    private lateinit var songbookSongAdapter: SongbookSongAdapter
    lateinit var singleSongbookViewModel: SingleSongbookViewModel
    private lateinit var singleSongbookLoadingDialog: Dialog

    private var _binding: FragmentSingleSongbookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleSongbookBinding.inflate(inflater, container, false)
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
        }
        singleSongbookViewModel = getViewModel()
        val selectedSongBookId = arguments?.getString("selected_songbook_id").toString()


        binding.toolbarSingleSongbookFragment.title =
            arguments?.getString("selected_songbook_name").toString().capitalize(Locale.getDefault())
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
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                    val deleteSongLoading = createProgressDialog(
                        requireContext(),
                        "Deleting ${songs[position].songTitle}"
                    )
                    deleteSongLoading.show()
                    singleSongbookViewModel.deleteSong(deleteSongRequest)
                        .observe(viewLifecycleOwner,
                            {
                                when (it) {
                                    is Result.Loading -> {
                                        Log.d(mTAG, "Deleting song")
                                    }

                                    is Result.Success -> {

                                        Log.d(mTAG, "Response : ${it.data?.message}")
                                        deleteSongLoading.dismiss()
                                        getSongsInSongbook(singleSongbookRequest)
                                    }
                                    is Result.Error -> {

                                    }
                                }
                            })
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
                val bundle = bundleOf(
                    "selected_song" to selectedSong,
                    "selected_songbook_id" to selectedSongBookId
                )
                view.findNavController()
                    .navigate(R.id.action_single_songbook_to_song_detail, bundle)
            }
        })
    }

    private fun getSongsInSongbook(singleSongbookRequest: SingleSongbookRequest) {
        singleSongbookLoadingDialog = createProgressDialog(requireContext(), "Loading songs ...")
        singleSongbookLoadingDialog.show()
        singleSongbookViewModel.getSingleSongbook(singleSongbookRequest).observe(viewLifecycleOwner,
            {
                when (it) {
                    is Result.Loading -> {
                        Log.d(mTAG, "Loading songs in songbook")
                    }
                    is Result.Success -> {
                        Log.d(mTAG, "Songs in songbook loaded")
                        songs = it.data?.data?.songbookSongs!!
                        songbookSongAdapter.updateSongsList(songs)
                        singleSongbookLoadingDialog.dismiss()
                        if (songbookSongAdapter.itemCount == 0) {
                            binding.imgNoSongSingleSongbook.visibility = View.VISIBLE
                            binding.tvNoSongsSingleSongbook.visibility = View.VISIBLE
                        } else {
                            binding.imgNoSongSingleSongbook.visibility = View.GONE
                            binding.tvNoSongsSingleSongbook.visibility = View.GONE
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