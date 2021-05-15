package com.dscvit.songified.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dscvit.handly.util.shortToast
import com.dscvit.songified.R
import com.dscvit.songified.databinding.BottomSheetLoginBinding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SignInRequest
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.set
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.viewmodel.ext.android.getViewModel

class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    private val mTAG = "LoginBottomSheet"
    private val rcSignIn = 101
    private lateinit var sharedPref: SharedPreferences
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var closeListener: DialogDismissListener
    private var isSignedIn = false


    private var _binding: BottomSheetLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)



        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)



        binding.googleSignInButtonBottomSheet.setOnClickListener {

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, rcSignIn)
        }
    }

    fun dismissListener(closeListener: DialogDismissListener) {
        this.closeListener = closeListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == rcSignIn && resultCode != 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d(mTAG, resultCode.toString())
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            this.isCancelable = false
            binding.googleSignInButtonBottomSheet.visibility = View.GONE
            binding.pbLogin.visibility = View.VISIBLE
            handleSignInResult(task, auth)


        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, auth: FirebaseAuth) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            var idToken: String
            val googleToken = account?.idToken
            Log.d(mTAG, googleToken ?: "")
            val credential = GoogleAuthProvider.getCredential(googleToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(mTAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        user?.getIdToken(true)?.addOnCompleteListener { task_it ->
                            if (task_it.isSuccessful) {
                                idToken = task_it.result?.token ?: ""
                                Log.d(mTAG, idToken)
                                val singInRequest = SignInRequest(idToken)
                                getViewModel<LoginViewModel>().login(singInRequest).observe(
                                    viewLifecycleOwner,
                                    {
                                        when (it) {
                                            is Result.Loading -> {

                                            }

                                            is Result.Success -> {
                                                Log.d(mTAG, "Sign In Success")
                                                Log.d(mTAG, it.data?.token ?: "")
                                                sharedPref[Constants.PREF_IS_AUTH] = true
                                                sharedPref[Constants.PREF_AUTH_TOKEN] =
                                                    it.data?.token ?: ""

                                                shortToast("Signed in as ${user.email}")
                                                isSignedIn = true
                                                binding.pbLogin.visibility = View.GONE
                                                this.dismiss()
                                            }
                                            is Result.Error -> {

                                            }
                                        }

                                    })
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                                shortToast("Some error occurred")
                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(mTAG, "signInWithCredential:failure", task.exception)

                    }
                }


            // Signed in successfully, show authenticated UI.

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SearchViewModel", "signInResult:failed code=" + e.statusCode)


        }
    }

    override fun onDismiss(dialog: DialogInterface) {

        closeListener.handleDialogClose(dialog, isSignedIn)
        super.onDismiss(dialog)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
