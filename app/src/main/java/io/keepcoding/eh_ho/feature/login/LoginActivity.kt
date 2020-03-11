package io.keepcoding.eh_ho.feature.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.SignModel
import io.keepcoding.eh_ho.domain.SignUpModel
import io.keepcoding.eh_ho.feature.topics.view.ui.TopicsActivity
import io.keepcoding.eh_ho.login.SignInFragment
import io.keepcoding.eh_ho.login.SignUpFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(),
    SignInFragment.SignInInteractionListener,
    SignUpFragment.SignUpInteractionListener {

    val signInFragment: SignInFragment =
        SignInFragment()
    val signUpFragment: SignUpFragment =
        SignUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        if (savedInstanceState == null) {
            checkSession()
        }
    }

    private fun checkSession() {
        if (UserRepo.isLogged(this))
            //launchTopicsActivity()
            onGoToSignIn()
        else
            onGoToSignIn()
    }


    // Interface del fragment SignInInteractionListener
    override fun onGoToSignUp() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signUpFragment)
            .commit()
    }

    override fun onSignIn(signModel: SignModel) {
        enableLoading(true)
        UserRepo.signIn(this, signModel,
            {
                enableLoading(false)
                launchTopicsActivity()
            },
            {
                enableLoading(false)
                handleRequestError(it)
            })

    }

    // Interface del fragment SignUpInteractionListener
    override fun onGoToSignIn() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signInFragment)
            .commit()
    }

    override fun onSignUp(signUpModel: SignUpModel) {
        enableLoading(true)
        UserRepo.signUp(
            this,
            signUpModel,
            {
                enableLoading(false)
                launchTopicsActivity()
            },
            {
                enableLoading(false)
                handleRequestError(it)
            }
        )
    }

    private fun handleRequestError(requestError: RequestError) {
        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()

    }


    private fun enableLoading(enable: Boolean) {
        if (enable) {
            fragmentContainer.visibility = View.INVISIBLE
            viewLoading.visibility = View.VISIBLE
        } else {
            fragmentContainer.visibility = View.VISIBLE
            viewLoading.visibility = View.INVISIBLE
        }
    }


    private fun launchTopicsActivity() {
        val intent = Intent(this, TopicsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
