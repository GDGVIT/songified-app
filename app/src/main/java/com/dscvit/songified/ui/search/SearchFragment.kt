package com.dscvit.songified.ui.search

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.dscvit.songified.R
import com.dscvit.songified.databinding.FragmentSearchBinding
import com.dscvit.songified.ui.login.LoginBottomSheetFragment
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.get

class SearchFragment : Fragment() {

    private val mTAG: String = "SearchFragment"

    private lateinit var sharedPref: SharedPreferences
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)
        Log.d(mTAG, sharedPref[Constants.PREF_IS_AUTH, false].toString())


        val loginBottomSheet = LoginBottomSheetFragment()


/*
        if (!sharedPref[Constants.PREF_IS_AUTH, false]!!) {

            binding.googleSignInButton.visibility = View.VISIBLE

        } else {
            binding.googleSignInButton.visibility = View.GONE

        }


        binding.googleSignInButton.setOnClickListener {
            loginBottomSheet.dismissListener(object : DialogDismissListener {
                override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    if (isSignedIn) {

                        binding.googleSignInButton.visibility = View.GONE

                    } else {
                        binding.googleSignInButton.visibility = View.VISIBLE

                    }

                }

            })
            loginBottomSheet.show(requireActivity().supportFragmentManager, "TAG")
        }
*/






        binding.svSearchFragment.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val bundle = bundleOf("search_query" to "searchString")
                val extras =
                    FragmentNavigatorExtras(binding.svSearchFragment to "search_bar_transition")

                v.findNavController()
                    .navigate(R.id.action_search_to_search_result, bundle, null, extras)
            }


        }



        binding.btnSearchFragment.setOnClickListener {
            val bundle = bundleOf("search_query" to "searchString")
            val extras =
                FragmentNavigatorExtras(binding.svSearchFragment to "search_bar_transition")

            view.findNavController()
                .navigate(R.id.action_search_to_search_result, bundle, null, extras)
        }

        val btnUpload = view.findViewById(R.id.btn_upload_search_fragment) as Button
        btnUpload.setOnClickListener {
            if (!sharedPref[Constants.PREF_IS_AUTH, false]!!) {

                loginBottomSheet.dismissListener(object : DialogDismissListener {
                    override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {


                    }

                })
                loginBottomSheet.show(this.parentFragmentManager, "TAG")
            } else {

                findNavController().navigate(R.id.action_search_to_upload_song)
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}