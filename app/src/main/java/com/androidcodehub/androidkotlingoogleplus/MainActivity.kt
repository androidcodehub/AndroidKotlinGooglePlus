package com.androidcodehub.androidkotlingoogleplus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity


import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.plus.People
import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.model.people.Person
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var signInButton: SignInButton? = null
    private var gso: GoogleSignInOptions? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val SIGN_IN = 35
    private var tv: TextView? = null
    private var iv: ImageView? = null

    private var btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        signInButton = findViewById<View>(R.id.sign_in_button) as SignInButton
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
                .addApi(Plus.API)
                .build()

        signInButton!!.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, SIGN_IN)
        }

        tv = findViewById<View>(R.id.text) as TextView
        iv = findViewById<View>(R.id.iv) as ImageView
        btn = findViewById<View>(R.id.btn) as Button
        //   aQuery = new AQuery(this);

        btn!!.setOnClickListener { Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { Toast.makeText(this@MainActivity, "Logout Successfully!", Toast.LENGTH_SHORT).show() } }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //If signin
        if (requestCode == SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            //Calling a new function to handle signin
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        //If the login succeed
        if (result.isSuccess) {
            //Getting google account
            val acct = result.signInAccount

            //Displaying name and email
            val name = acct!!.displayName
            val mail = acct.email
            // String photourl = acct.getPhotoUrl().toString();

            val givenname = ""
            val familyname = ""
            val displayname = ""
            val birthday = ""

            Plus.PeopleApi.load(mGoogleApiClient, acct.id).setResultCallback { loadPeopleResult ->
                val person = loadPeopleResult.personBuffer.get(0)

                Log.d("GivenName ", person.name.givenName)
                Log.d("FamilyName ", person.name.familyName)
                Log.d("DisplayName ", person.displayName)
                Log.d("gender ", person.gender.toString()) //0 = male 1 = female
                var gender = ""
                if (person.gender == 0) {
                    gender = "Male"
                } else {
                    gender = "Female"
                }

                if (person.hasBirthday()) {
                    tv!!.text = person.name.givenName + " \n" + person.name.familyName + " \n" + gender + "\n" + person.birthday
                } else {
                    tv!!.text = person.name.givenName + " \n" + person.name.familyName + " \n" + gender

                }

                Picasso.with(this@MainActivity).load(person.image.url).into(iv)
            }
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }
}

