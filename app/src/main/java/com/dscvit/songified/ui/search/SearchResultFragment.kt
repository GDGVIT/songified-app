package com.dscvit.songified.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.dscvit.handly.util.OnItemClickListener
import com.dscvit.handly.util.addOnItemClickListener
import com.dscvit.handly.util.hideKeyboard
import com.dscvit.handly.util.shortToast
import com.dscvit.songified.R
import com.dscvit.songified.adapter.SongListAdapter
import com.dscvit.songified.databinding.FragmentSearchResultBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.Song
import org.koin.android.viewmodel.ext.android.getViewModel

class SearchResultFragment : Fragment() {
    private lateinit var songs: MutableList<Song>
    private val mTAG: String = "SearchResultFragment"
    private lateinit var searchViewModel: SearchResultViewModel

    private lateinit var songListAdapter: SongListAdapter
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (_binding == null) {
            _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(this.context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(this.context).inflateTransition(android.R.transition.move)
        searchViewModel = getViewModel()


        val back = view.findViewById(R.id.back_search_result) as ImageView



        songListAdapter = SongListAdapter()
        binding.rvSongSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songListAdapter
        }

        back.setOnClickListener {

            binding.svSearchResultFragment.clearFocus()
            it.findNavController().navigateUp()

        }

        binding.rvSongSearchResults.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val selectedSongId = songs[position].song_id
                val bundle = bundleOf("selected_song_id" to selectedSongId)
                view.findNavController().navigate(R.id.action_search_result_to_song_details, bundle)
            }

        })

        binding.svSearchResultFragment.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                binding.imgNoSearchResult.visibility = View.GONE
                binding.tvNoSearchResult.visibility = View.GONE
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    hideKeyboard()
                    searchSong(query)

                }
                return true
            }
        })


    }

    fun searchSong(searchQuery: String) {
        searchViewModel.searchSong("song", searchQuery).observe(viewLifecycleOwner, {
            when (it) {
                is Result.Loading -> {

                    //Do loading
                    binding.pbLoadingSearchResult.visibility = View.VISIBLE
                    Log.d(mTAG, "Loading...")
                }
                is Result.Success -> {
                    Log.d(mTAG, "success")

                    songs = it.data?.song!!
                    if (songs.isNotEmpty()) {
                        binding.rvSongSearchResults.visibility = View.VISIBLE
                        binding.imgNoSearchResult.visibility = View.GONE
                        binding.tvNoSearchResult.visibility = View.GONE
                        songListAdapter.updateSongsList(songs)
                        binding.pbLoadingSearchResult.visibility = View.GONE
                    } else {
                        shortToast("No results found")
                    }
                }
                is Result.Error -> {
                    Log.d(mTAG, "Error")
                    if (!(it.message == getString(R.string.internet_error) || it.message == "404 Not Found")) {
                        binding.rvSongSearchResults.visibility = View.GONE
                        binding.imgNoSearchResult.visibility = View.VISIBLE
                        binding.tvNoSearchResult.visibility = View.VISIBLE
                        binding.tvNoSearchResult.text =
                            getString(R.string.no_search_result, searchQuery)
                        binding.pbLoadingSearchResult.visibility = View.GONE
                    }

                    Log.d("esh", it.message!!)
                }


            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}