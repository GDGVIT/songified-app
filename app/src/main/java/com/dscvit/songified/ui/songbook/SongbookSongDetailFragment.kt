package com.dscvit.songified.ui.songbook

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongbookSongAdapter
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SingleSongbookSong
import com.dscvit.songified.model.UpdateSongInSongbookRequest
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.viewmodel.ext.android.viewModel

class SongbookSongDetailFragment : Fragment() {
    val TAG = "SongbookSongDetail"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songbook_song_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songbookSongDetailViewModel by viewModel<SongbookSongDetailViewModel> ()
        val selectedSongbookSong: SingleSongbookSong =
            arguments?.get("selected_song") as SingleSongbookSong
        val selectedSongbookId = arguments?.getString("selected_songbook_id") as String
        val tvSongName = view.findViewById(R.id.tv_songname_songbook_song_detail) as TextView
        val tvArtistName = view.findViewById(R.id.tv_artist_songbook_song_detail) as TextView
        val tvScale = view.findViewById(R.id.tv_scale_songbook_song_detail) as TextView
        val tvTempo = view.findViewById(R.id.tv_tempo_songbook_song_detail) as TextView

        val etNotes = view.findViewById(R.id.et_notes_songbook_song_detail) as TextInputEditText
        tvSongName.text = selectedSongbookSong.songTitle
        tvArtistName.text = selectedSongbookSong.artist
        tvScale.text = selectedSongbookSong.scale
        tvTempo.text = selectedSongbookSong.tempo
        val btnSave = view.findViewById(R.id.btn_update_songbook_song) as Button
        etNotes.setText(selectedSongbookSong.songBody)
        val pbUpdateLoading=view.findViewById(R.id.pb_update_song_in_songbook) as LinearProgressIndicator
        btnSave.setOnClickListener {
            val songBody = etNotes.text.toString()
            val updateSongRequest = UpdateSongInSongbookRequest(
                selectedSongbookId,
                selectedSongbookSong.songId,
                selectedSongbookSong.songTitle,
                songBody,
                selectedSongbookSong.scale,
                selectedSongbookSong.tempo.toInt(),
                selectedSongbookSong.artist
            )
            pbUpdateLoading.visibility=View.VISIBLE
            songbookSongDetailViewModel.editSongbook(updateSongRequest).observe(viewLifecycleOwner,
                Observer {
                    when(it){
                        is Result.Loading->{
                            Log.d(TAG,"Saving songbook")
                        }
                        is Result.Success->{
                            Log.d(TAG,"Songbook updated")
                            btnSave.visibility=View.GONE
                            pbUpdateLoading.visibility=View.GONE
                            Snackbar.make(btnSave,"Song updated",Snackbar.LENGTH_SHORT).show()
                        }
                    }
                })
        }

        etNotes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnSave.visibility = View.VISIBLE
                btnSave.isEnabled = true
            }
        })
    }
}