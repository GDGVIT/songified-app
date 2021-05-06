package com.dscvit.songified.ui.songbook

import android.app.Dialog
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dscvit.handly.util.OnItemClickListener
import com.dscvit.handly.util.addOnItemClickListener
import com.dscvit.handly.util.addOnItemLongClickListener
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongbookAdapter
import com.dscvit.songified.adapter.SongbookSongAdapter
import com.dscvit.songified.model.*
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.w3c.dom.Text

class SingleSongbookFragment : Fragment() {
    lateinit var songs: MutableList<SingleSongbookSong>
    val TAG = "SingleSongBookFragment"
    lateinit var songbookSongAdapter:SongbookSongAdapter
    lateinit var singleSongbookViewModel:SingleSongbookViewModel
    lateinit var singleSongbookLoadingDialog: Dialog
    lateinit var imgNoSong:ImageView
    lateinit var tvNoSong:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_songbook, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvSongs = view.findViewById(R.id.rv_songs_songbook) as RecyclerView
        val back=view.findViewById(R.id.back_single_songbook) as ImageView

        back.setOnClickListener{
            it.findNavController().navigateUp()
        }
        imgNoSong=view.findViewById(R.id.img_no_song_single_songbook) as ImageView
        tvNoSong=view.findViewById(R.id.tv_no_songs_single_songbook) as TextView

        songbookSongAdapter = SongbookSongAdapter()
        rvSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songbookSongAdapter
        }
        singleSongbookViewModel = getViewModel()
        val selectedSongBookId = arguments?.getString("selected_songbook_id").toString()

        val tvTitle=view.findViewById(R.id.tv_songbook_name_single_songbook) as TextView
        tvTitle.text=arguments?.getString("selected_songbook_name").toString()
        val singleSongbookRequest = SingleSongbookRequest(selectedSongBookId)

        getSongsInSongbook(singleSongbookRequest)


        rvSongs.addOnItemLongClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val deleteSongRequest=SongbookSongDeleteRequest(selectedSongBookId,songs[position].songId)
                val alertDialogBuilder = AlertDialog.Builder(requireContext(),R.style.MyAlertDialog)
                alertDialogBuilder.setTitle("Delete Song")
                alertDialogBuilder.setMessage("Are you sure you want to delete ${songs[position].songTitle} ?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
                    val deleteSongLoading= createProgressDialog(requireContext(),"Deleting ${songs[position].songTitle}")
                    deleteSongLoading.show()
                    singleSongbookViewModel.deleteSong(deleteSongRequest).observe(viewLifecycleOwner,
                        Observer {
                            when(it){
                                is Result.Loading->{
                                    Log.d(TAG,"Deleting song")
                                }

                                is Result.Success->{

                                    Log.d(TAG,"Response : ${it.data?.message}")
                                    deleteSongLoading.dismiss()
                                    getSongsInSongbook(singleSongbookRequest)
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


        rvSongs.addOnItemClickListener(object :OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) {
                val selectedSong=songs[position]
                val bundle = bundleOf("selected_song" to selectedSong,"selected_songbook_id" to selectedSongBookId)
                view.findNavController().navigate(R.id.action_single_songbook_to_song_detail, bundle)
            }
        })
    }

    private fun getSongsInSongbook(singleSongbookRequest:SingleSongbookRequest){
        singleSongbookLoadingDialog= createProgressDialog(requireContext(),"Loading songs ...")
        singleSongbookLoadingDialog.show()
        singleSongbookViewModel.getSingleSongbook(singleSongbookRequest).observe(viewLifecycleOwner,
            Observer {
                when (it) {
                    is Result.Loading -> {
                        Log.d(TAG, "Loading songs in songbook")
                    }
                    is Result.Success -> {
                        Log.d(TAG, "Songs in songbook loaded")
                        songs = it.data?.data?.songbookSongs!!
                        songbookSongAdapter.updateSongsList(songs)
                        singleSongbookLoadingDialog.dismiss()
                        if (songbookSongAdapter.itemCount==0){
                            imgNoSong.visibility=View.VISIBLE
                            tvNoSong.visibility=View.VISIBLE
                        }else{
                            imgNoSong.visibility=View.GONE
                            tvNoSong.visibility=View.GONE
                        }
                    }
                }
            })
    }


}