package com.dscvit.songified.ui.intro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscvit.songified.util.shortToast
import com.dscvit.songified.HomeActivity
import com.dscvit.songified.R
import com.dscvit.songified.databinding.FragmentIntro3Binding
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SignInRequest
import com.dscvit.songified.ui.login.LoginViewModel
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.set
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.viewmodel.ext.android.getViewModel

class IntroFragment3 : Fragment() {
    private var _binding: FragmentIntro3Binding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val rcSignIn = 101
    private val mTAG = "IntroFragment3"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIntro3Binding.inflate(inflater, container, false)
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

        binding.tvNotNowIntro.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            sharedPref[Constants.PREF_IS_FIRST_TIME] = false
            activity?.finish()
        }
        binding.googleSignInIntro.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, rcSignIn)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == rcSignIn && resultCode != 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d(mTAG, resultCode.toString())
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

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
                                                binding.googleSignInIntro.visibility = View.GONE
                                                binding.pbLoginIntro.visibility = View.VISIBLE
                                                binding.tvNotNowIntro.visibility = View.GONE
                                            }

                                            is Result.Success -> {

                                                binding.pbLoginIntro.visibility = View.GONE
                                                Log.d(mTAG, "Sign In Success")
                                                Log.d(mTAG, it.data?.token ?: "")
                                                sharedPref[Constants.PREF_IS_AUTH] = true
                                                sharedPref[Constants.PREF_AUTH_TOKEN] =
                                                    it.data?.token ?: ""

                                                shortToast("Signed in as ${user.email}")
                                                sharedPref[Constants.PREF_IS_FIRST_TIME] = false
                                                val intent =
                                                    Intent(activity, HomeActivity::class.java)
                                                startActivity(intent)

                                                activity?.finish()
                                            }
                                            is Result.Error -> {
                                                binding.googleSignInIntro.visibility = View.VISIBLE
                                                binding.pbLoginIntro.visibility = View.GONE
                                                binding.tvNotNowIntro.visibility = View.VISIBLE
                                                Snackbar.make(
                                                    binding.root,
                                                    "Some error occurred",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
