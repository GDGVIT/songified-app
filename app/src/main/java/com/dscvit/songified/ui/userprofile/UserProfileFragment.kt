package com.dscvit.songified.ui.userprofile

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SingleSongbookSong
import com.dscvit.songified.ui.songbook.SingleSongbookViewModel
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.PrefHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.koin.android.viewmodel.ext.android.viewModel
import com.dscvit.songified.util.PrefHelper.get
import com.dscvit.songified.util.PrefHelper.set

class UserProfileFragment : Fragment() {
    val TAG = "UserProfileFragment"
    lateinit var mGoogleSignInClient:GoogleSignInClient
    lateinit var signOutLoadingDialog:Dialog
    lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)
        val tvUserName=view.findViewById(R.id.tv_id_user_profile) as TextView
        val tvPoints=view.findViewById(R.id.tv_points_user_profile) as TextView
        val tvLevel=view.findViewById(R.id.tv_level_user_profile) as TextView
        val imgUser=view.findViewById(R.id.img_dp_user_profile) as ImageView
        val tvSongbookCounts=view.findViewById(R.id.tv_songbook_count_user_profile) as TextView

        val userProfileViewModel by viewModel<UserProfileViewModel>()
        signOutLoadingDialog= createProgressDialog(requireContext(),"Signing Out")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        userProfileViewModel.getUserInfo().observe(viewLifecycleOwner, Observer {
            when(it){
                is Result.Loading->{
                    Log.d(TAG,"Loading user profile")
                }
                is Result.Success->{
                    Log.d(TAG,"User profile loaded")
                    tvUserName.text=it.data?.userName
                    tvPoints.text=it.data?.userPoints
                    tvLevel.text=it.data?.userLevel
                    tvSongbookCounts.text=it.data?.songbooks?.size.toString()
                    Glide.with(this)
                        .load(it.data?.userImg)
                        .into(imgUser)
                }
            }
        })

        val tvSignOut=view.findViewById(R.id.tv_sign_out_user_profile) as TextView
        if (sharedPref.get(Constants.PREF_IS_AUTH,false)!!){
            tvSignOut.visibility=View.VISIBLE
        }else{
            tvSignOut.visibility=View.GONE
        }
        tvSignOut.setOnClickListener{
            signOutLoadingDialog.show()
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity()) {
                    userProfileViewModel.logout().observe(viewLifecycleOwner, Observer {
                        when(it){
                            is Result.Loading->{
                                Log.d(TAG,"Signing out")
                            }
                            is Result.Success->{
                                signOutLoadingDialog.dismiss()
                                Log.d(TAG,"Singned Out")
                                sharedPref[Constants.PREF_IS_AUTH]=false
                                sharedPref[Constants.PREF_AUTH_TOKEN]=""
                                tvSignOut.visibility=View.GONE
                                view.findNavController().navigate(R.id.navigation_search)
                            }

                        }
                    })
                }
        }
    }


}