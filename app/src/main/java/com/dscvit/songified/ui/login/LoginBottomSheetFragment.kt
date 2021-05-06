package com.dscvit.songified.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.dscvit.handly.util.shortToast
import com.dscvit.songified.R
import com.dscvit.songified.model.Result
import com.dscvit.songified.model.SignInRequest
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.DialogDismissListener
import com.dscvit.songified.util.PrefHelper
import com.dscvit.songified.util.PrefHelper.set
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.viewmodel.ext.android.getViewModel

class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    val TAG = "LoginBottomSheet"
    val RC_SIGN_IN = 101
    lateinit var sharedPref: SharedPreferences
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    lateinit var closeListener: DialogDismissListener
    var isSignedIn = false
    lateinit var pbLogin:CircularProgressIndicator
    lateinit var btnGoogle:SignInButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.bottom_sheet_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        sharedPref = PrefHelper.customPrefs(requireContext(), Constants.PREF_NAME)


        btnGoogle = view.findViewById(R.id.google_sign_in_button_bottom_sheet) as SignInButton
        pbLogin=view.findViewById(R.id.pb_login) as CircularProgressIndicator
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);


        val account = GoogleSignIn.getLastSignedInAccount(requireContext())

        btnGoogle.setOnClickListener() {

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    fun DismissListener(closeListener: DialogDismissListener) {
        this.closeListener = closeListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode === RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            this.isCancelable=false
            btnGoogle.visibility=View.GONE
            pbLogin.visibility=View.VISIBLE
            handleSignInResult(task, auth)


        }
    }


    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, auth: FirebaseAuth) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            var idToken = ""
            val googletoken = account?.idToken

            val credential = GoogleAuthProvider.getCredential(googletoken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        user?.getIdToken(true)?.addOnCompleteListener() {
                            if (it.isSuccessful) {
                                idToken = it.result?.token ?: ""
                                Log.d(TAG, idToken)
                                val singInRequest = SignInRequest(idToken)
                                getViewModel<LoginViewModel>().login(singInRequest).observe(
                                    viewLifecycleOwner,
                                    Observer {
                                        when (it) {
                                            is Result.Loading -> {

                                            }

                                            is Result.Success -> {
                                                Log.d(TAG, "Sign In Success")
                                                Log.d(TAG, it.data?.token ?: "")
                                                sharedPref[Constants.PREF_IS_AUTH] = true
                                                sharedPref[Constants.PREF_AUTH_TOKEN] =
                                                    it.data?.token ?: ""

                                                shortToast("Signed in as ${user.email}")
                                                isSignedIn = true
                                                pbLogin.visibility=View.GONE
                                                this.dismiss()
                                            }
                                        }

                                    })
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)

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

        if (closeListener!=null){
            closeListener.handleDialogClose(dialog, isSignedIn);
        }
        super.onDismiss(dialog)


    }
}
