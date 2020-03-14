package io.keepcoding.eh_ho.feature.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.ResetPasswordModel
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

    override fun onResetPassword(model: ResetPasswordModel) {

        enableLoading(enable = true)
        UserRepo.resetPassword(
            this,
            model,
            {
                enableLoading(enable = false)
                if (it != null) {
                    showAlertEmailSent(it.login)
                }

            },
            {
                enableLoading(enable = false)
                handleRequestError(it)
            }
        )
    }

    private fun showAlertEmailSent(login: String) {
        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setMessage("We found an account that matches the username ${login}," +
                    " you should receive an email with instructions on how to reset your password shortly.")
            setPositiveButton("OK") { dialog, i ->
                launchTopicsActivity()
                dialog.dismiss()}
            show()
        }    }

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
