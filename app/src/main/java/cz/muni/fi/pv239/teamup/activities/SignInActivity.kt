package cz.muni.fi.pv239.teamup.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.User
import kotlinx.android.synthetic.main.activity_signin.*
import java.util.Arrays.asList


class SignInActivity : AppCompatActivity() {

    // sing-in id
    private val RC_SIGN_IN = 123

    // user
    private lateinit var user: User

    // database
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        // start sign-in activity
        signIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            // Sign in failed, check response for error code
            Log.e(this::class.java.name, "User is not signed up. Error code: " + resultCode.toString())
            // notify
            Snackbar.make(coordinatorLayout, getString(R.string.signInNotSuccssesful), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.tryAgain), { signIn() })
                    .show()
        }
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser == null) {
                    // Sign in failed, check response for error code
                    Log.e(this::class.java.name, "User is not signed up. Error code: " + resultCode.toString())
                    // notify
                    Toast.makeText(this, "You have to be signed.", Toast.LENGTH_LONG).show()
                    // exit
                    throw IllegalStateException("User singing was not successful.")
                } else {
                    // create user
                    user = User(
                            firebaseUser.uid,
                            firebaseUser.displayName ?: "",
                            firebaseUser.email ?: "",
                            firebaseUser.photoUrl.toString())
                    // store or update in database
                    database.child("users").child(user.uid).setValue(user)
                    // store user id in shared preferrences
                    val editor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit()
                    editor.putString("user.uid", user.uid)
                    editor.putString("user.name", user.displayName)
                    editor.apply()
                    // log it
                    Log.d(this::class.java.name, "Signed in as a user " + user.displayName)
                    // go to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else {
                // Sign in failed, check response for error code
                Log.e(this::class.java.name, "User is not signed up. Error code: " + resultCode.toString())
                // notify
                Snackbar.make(coordinatorLayout, getString(R.string.signInNotSuccssesful), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.tryAgain), { signIn() })
                        .show()
            }
        }
    }

    private fun signIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(asList(
                                AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                RC_SIGN_IN)
    }

}
