package com.example.socialadda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.socialadda.daos.UserDao
import com.example.socialadda.databinding.ActivitySignInBinding
import com.example.socialadda.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {

    //this is just a constant
    private val RC_SIGN_IN: Int = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignInBinding
    private val TAG = "MainActivity"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_in)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        //initializing the googleSignInClient with the help of context and gso
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        //initializing the auth
        auth = Firebase.auth

        //add the click listener to the signIn button and call signIn method
        binding.signInButton.setOnClickListener{
            signIn()
        }
    }

    //in the onStart we are fetching the user from the auth and passing it to updateUI so that user don't need to sign in if he already signed in into our app
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        //this intent gives user a choice to choose their google account
        val signInIntent = googleSignInClient.signInIntent
        //this will call that method by passing signIn intent and a constant
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //get the task from googleSignIn and pass it to another method below
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {
        try {
            // Google Sign In was successful, authenticate with Firebase and get the account
            val account = completedTask?.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            //take the token id from the account and pass it to the below method
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            // ...
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        //get the credentials from the token id received
        val credential = GoogleAuthProvider.getCredential(idToken,null)

        //the upcoming process will take some time so show the progress bar and hide the signIn button till then
        binding.signInButton.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        //do the signIn part in the background thread using the coroutines
        GlobalScope.launch(Dispatchers.IO) {
            //get the user here from the credentials
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            //comeback to the main thread and call the updateUI by passing the user into it
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        //if user is not null, that means signIn is successful, then start the main activity
        if (firebaseUser!=null){

            //create the instance of user from the information received from the firebase user
            val user = User(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            //create instance of the dao so that we can use addUser method od that
            val usersDao = UserDao()
            //add the user with the help of dao
            usersDao.addUser(user)

            //start the main activity
            val mainActivityIntent = Intent(this,MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else{
            //otherwise show the signIn button again and hide the progress bar so that user can try again
            binding.signInButton.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}