package com.dscvit.songified.ui.search

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.dscvit.handly.util.getFileName
import com.dscvit.handly.util.shortToast
import com.dscvit.songified.R
import com.dscvit.songified.model.Result
import com.dscvit.songified.ui.login.LoginBottomSheetFragment
import com.dscvit.songified.util.Constants

import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import com.dscvit.songified.util.PrefHelper.get
import com.dscvit.songified.util.PrefHelper.set
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton

class SearchFragment : Fragment() {
    val RC_SIGN_IN = 101

    val TAG: String = "SearchFragment"

    lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)
        Log.d(TAG,sharedPref.get(Constants.PREF_IS_AUTH,false).toString())

        val btnGoogleSignIn=view.findViewById(R.id.google_sign_in_button) as SignInButton
        val loginBottomSheet = LoginBottomSheetFragment()



        if(!sharedPref.get(Constants.PREF_IS_AUTH,false)!!){

            btnGoogleSignIn.visibility=View.VISIBLE

        }else{
            btnGoogleSignIn.visibility=View.GONE

        }
        btnGoogleSignIn.setOnClickListener{
            loginBottomSheet.DismissListener(object : DialogDismissListener {
                override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    if (isSignedIn) {
                        //findNavController().navigate(R.id.action_search_to_upload_song)
                        btnGoogleSignIn.visibility=View.GONE

                    } else {
                        btnGoogleSignIn.visibility=View.VISIBLE

                    }

                }

            })
            loginBottomSheet.show(requireActivity().supportFragmentManager,"TAG")
        }





        val btnSearch: ImageView = view.findViewById(R.id.btn_search_fragment)
        val svSearch = view.findViewById(R.id.sv_search_fragment) as EditText

        svSearch.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    val bundle = bundleOf("search_query" to "searchString")
                    val extras = FragmentNavigatorExtras(svSearch to "search_bar_transition")

                    view.findNavController()
                        .navigate(R.id.action_search_to_search_result, bundle, null, extras)
                }


        }



        btnSearch.setOnClickListener {
            val searchString: String = svSearch.text.toString()
            if (searchString != "") {
                val bundle = bundleOf("search_query" to searchString)
                val extras = FragmentNavigatorExtras(svSearch to "search_bar_transition")

                it.findNavController()
                    .navigate(R.id.action_search_to_search_result, bundle, null, extras)
            }
        }

        val btnUpload=view.findViewById(R.id.btn_upload_search_fragment) as Button
        btnUpload.setOnClickListener{
            if(!sharedPref.get(Constants.PREF_IS_AUTH,false)!!){

                loginBottomSheet.DismissListener(object : DialogDismissListener {
                    override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                        if (isSignedIn) {
                            //findNavController().navigate(R.id.action_search_to_upload_song)
                            btnGoogleSignIn.visibility=View.GONE
                        } else {
                            btnGoogleSignIn.visibility=View.VISIBLE

                        }

                    }

                })
                loginBottomSheet.show(this.parentFragmentManager, "TAG")
            }else{
                btnGoogleSignIn.visibility=View.GONE
                findNavController().navigate(R.id.action_search_to_upload_song)
            }

        }







    }



}