package com.dscvit.songified.ui.search

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionInflater
import com.dscvit.handly.util.*
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongListAdapter
import com.dscvit.songified.model.SongSearchRequest
import org.koin.android.viewmodel.ext.android.viewModel
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.Song
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.koin.android.viewmodel.ext.android.getViewModel

class SearchResultFragment : Fragment() {
    lateinit var songs: MutableList<Song>
    private val TAG: String = "SearchResultFragment"
    lateinit var searchViewModel: SearchResultViewModel
    lateinit var pbLoadingSearchResult: LinearProgressIndicator
    lateinit var songListAdapter: SongListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(this.context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition=TransitionInflater.from(this.context).inflateTransition(android.R.transition.move)
        searchViewModel = getViewModel()

        var searchResultRecyclerView = view.findViewById<RecyclerView>(R.id.rv_song_search_results);
        val back=view.findViewById(R.id.back_search_result) as ImageView

        pbLoadingSearchResult =
            view.findViewById(R.id.pb_loading_search_result) as LinearProgressIndicator
        val svSearch = view.findViewById(R.id.sv_search_result_fragment) as SearchView

        songListAdapter = SongListAdapter()
        searchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songListAdapter
        }

        back.setOnClickListener{

            svSearch.clearFocus()
            it.findNavController().navigateUp()

        }

        searchResultRecyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val selectedSongId = songs[position].song_id
                val bundle = bundleOf("selected_song_id" to selectedSongId)
                view.findNavController().navigate(R.id.action_search_result_to_song_details, bundle)
            }

        })

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {

                    searchSong(query)
                }
                return true
            }
        })


        //val searchquery = arguments?.getString("search_query").toString()
        //tvTitle.text = "\"${searchquery}\""
        //val searchRequest=SongSearchRequest(searchquery)


    }

    fun searchSong(searchquery: String) {
        searchViewModel.searchSong("song", searchquery).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Result.Loading -> {

                    //Do loading
                    pbLoadingSearchResult.visibility=View.VISIBLE
                    Log.d(TAG, "Loading...")
                }
                is Result.Success -> {
                    Log.d(TAG, "success")

                    songs = it.data?.song!!
                    if (songs.isNotEmpty()) {
                        songListAdapter.updateSongsList(songs)
                        pbLoadingSearchResult.visibility = View.GONE
                    } else {
                    }
                }
                is Result.Error -> {
                    Log.d(TAG, "Error")
                    if (!(it.message == getString(R.string.internet_error) || it.message == "404 Not Found")) {
                        requireContext().shortToast("Fatal Error")
                    }

                    Log.d("esh", it.message!!)
                }


            }
        })
    }


}