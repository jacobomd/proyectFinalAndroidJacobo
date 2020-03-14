package io.keepcoding.eh_ho.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.domain.SignUpModel
import kotlinx.android.synthetic.main.fragment_sign_up.*

const val MIN_PASS_LENGHT = 10

class SignUpFragment: Fragment() {

    var listener: SignUpInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
            if (context is SignUpInteractionListener) {
                listener = context
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSignUp.setOnClickListener {signUp()}
        labelSignIn.setOnClickListener {goToSignIn()}
    }

    private fun goToSignIn() {
        listener?.onGoToSignIn()
    }

    private fun signUp() {
        if (isFormValid()) {
            val model = SignUpModel(
                inputUsername.text.toString(),
                inputEmail.text.toString(),
                inputPassword.text.toString()
            )

            listener?.onSignUp(model)
        }

        else
            showFormErrors()
    }



    private fun isFormValid() =
        inputUsername.text?.isNotEmpty() ?: false &&
        inputPassword.text?.isNotEmpty() ?: false &&
        inputPassword.text?.length ?: 0 >= MIN_PASS_LENGHT &&
        inputPassword.text?.toString() == inputConfirmPassword.text?.toString()

    private fun showFormErrors() {
        if (inputEmail.text?.isEmpty() == true)
            inputEmail.error = getString(R.string.error_empty)
        if (inputUsername.text?.isEmpty() == true)
            inputUsername.error = getString(R.string.error_empty)
        if (inputPassword.text?.isEmpty() == true)
            inputPassword.error = getString(R.string.error_empty)
        if (inputPassword.text?.length ?: 0 < MIN_PASS_LENGHT)
            inputPassword.error = getString(R.string.error_password_length)
        if (inputPassword.text != inputConfirmPassword.text)
            inputConfirmPassword.error = getString(R.string.error_password_match)
    }

    interface  SignUpInteractionListener {
        fun onGoToSignIn()
        fun onSignUp(signUpModel: SignUpModel)
    }
}