package com.vivant.annecharlotte.happydreche.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnSuccessListener
import com.vivant.annecharlotte.happydreche.R

import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.vivant.annecharlotte.happydreche.MainActivity


class WelcomeActivity : AppCompatActivity() {

    //FOR DATA
    private val RC_SIGN_IN = 123
    private val TAG = "WelcomeActivity"
    val USER_ID = "userId"

    lateinit var providers: List<AuthUI.IdpConfig>

    private var userId: String? = null
    private var createOK: Boolean = false
    private var context: Context? = null

    lateinit var loginBtn: Button
    lateinit var newAccountBtn: Button

    //FOR DESIGN


    private var mainActivityLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mainActivityLayout = findViewById(R.id.welcome_activity_layout)

        createOK = false
        context = applicationContext

        loginBtn = findViewById(R.id.welcome_button_connect)
        loginBtn.setOnClickListener(View.OnClickListener { startSignInActivity() })

        /*newAccountBtn = findViewById(R.id.welcome_button_new_user)
        newAccountBtn.setOnClickListener(View.OnClickListener {
            startSignInActivity()
        })*/
    }

    // --------------------
    // NAVIGATION
    // --------------------

    //Launch Sign-In Activity with email
    private fun startSignInActivity() {

        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(), // email authentication
            AuthUI.IdpConfig.FacebookBuilder().build(), //Facebook authentication
            AuthUI.IdpConfig.GoogleBuilder().build() //Google authentication
        )

        showSingInOptions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser // get current user
                Toast.makeText(this, ""+user!!.email, Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
// To pass any data to next activity
// intent.putExtra("keyIdentifier", value)
// start your next activity
                startActivity(intent)
            }
            else {
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showSingInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.LoginTheme)
            .build(), RC_SIGN_IN )
    }
}