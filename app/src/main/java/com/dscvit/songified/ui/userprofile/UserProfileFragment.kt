package com.dscvit.songified.ui.userprofile

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dscvit.handly.util.createProgressDialog
import com.dscvit.songified.R
import com.dscvit.songified.databinding.FragmentUserProfileBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SongbookDeleteRequest
import com.dscvit.songified.ui.login.LoginBottomSheetFragment
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.get
import com.dscvit.songified.util.PrefHelper.set
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class UserProfileFragment : Fragment() {
    val mTAG = "UserProfileFragment"
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signOutLoadingDialog: Dialog
    private lateinit var sharedPref: SharedPreferences

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)

        if (sharedPref[Constants.PREF_IS_AUTH, false]!!) {


            val userProfileViewModel by viewModel<UserProfileViewModel>()
            signOutLoadingDialog = createProgressDialog(requireContext(), "Signing Out")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

            userProfileViewModel.getUserInfo().observe(viewLifecycleOwner, {
                when (it) {
                    is Result.Loading -> {
                        Log.d(mTAG, "Loading user profile")
                    }
                    is Result.Success -> {
                        Log.d(mTAG, "User profile loaded")
                        binding.tvIdUserProfile.text = it.data?.userName
                        binding.tvPointsUserProfile.text = it.data?.userPoints
                        binding.tvLevelUserProfile.text = it.data?.userLevel
                        binding.tvEmailUserProfile.text = it.data?.userEmail
                        binding.tvSongbookCountUserProfile.text =
                            it.data?.songbooks?.size.toString()
                        Glide.with(this)
                            .load(it.data?.userImg)
                            .into(binding.imgDpUserProfile)
                        binding.shimmerDpUserProfile.hideShimmer()
                        binding.shimmerInfoUserProfile.hideShimmer()
                    }
                    is Result.Error -> {

                    }
                }
            })

            if (sharedPref[Constants.PREF_IS_AUTH, false]!!) {
                binding.btnSignOutUserProfile.visibility = View.VISIBLE
            } else {
                binding.btnSignOutUserProfile.visibility = View.GONE
            }
            binding.btnSignOutUserProfile.setOnClickListener {

                val alertDialogBuilder =
                    AlertDialog.Builder(requireContext(), R.style.MyAlertDialog)
                alertDialogBuilder.setTitle("Sign Out")
                alertDialogBuilder.setMessage("Are you sure you want to sign out ?")

                alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->


                    signOutLoadingDialog.show()
                    mGoogleSignInClient.signOut()
                        .addOnCompleteListener(requireActivity()) {
                            userProfileViewModel.logout().observe(viewLifecycleOwner, {
                                when (it) {
                                    is Result.Loading -> {
                                        Log.d(mTAG, "Signing out")
                                    }
                                    is Result.Success -> {
                                        signOutLoadingDialog.dismiss()
                                        Log.d(mTAG, "Signed Out")
                                        sharedPref[Constants.PREF_IS_AUTH] = false
                                        sharedPref[Constants.PREF_AUTH_TOKEN] = ""
                                        binding.btnSignOutUserProfile.visibility = View.GONE

                                        view.findNavController().navigate(R.id.navigation_search)
                                        findNavController().popBackStack(R.id.navigation_userprofile,true)
                                    }
                                    is Result.Error -> {
                                        signOutLoadingDialog.dismiss()
                                    }

                                }
                            })
                        }
                }

                alertDialogBuilder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }

                alertDialogBuilder.show()
            }
        } else {
            val loginBottomSheet = LoginBottomSheetFragment()


            loginBottomSheet.dismissListener(object : DialogDismissListener {
                override fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean) {
                    if (isSignedIn) {

                        findNavController().navigate(R.id.navigation_userprofile)
                    } else {

                        findNavController().navigate(R.id.navigation_search)
                        findNavController().popBackStack(R.id.navigation_userprofile,true)

                    }

                }

            })
            loginBottomSheet.show(this.parentFragmentManager, "TAG")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}