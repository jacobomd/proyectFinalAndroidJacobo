package io.keepcoding.eh_ho.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.domain.SignModel
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class SignInFragment: Fragment() {

    var listener: SignInInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SignInInteractionListener){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       with(view) {
            labelCreateAccount.setOnClickListener {
                goToSignUp()
            }
            buttonLogin.setOnClickListener {
                signIn()
                println("boton de signIn pulsadooo")
            }
           labelResetPassword.setOnClickListener {
               println ("label de reset password pulsadoooo")
           }
        }
    }

    private fun signIn () {
        if (isFormValid()) {
            val model = SignModel(
                inputUsername.text.toString(),
                inputPassword.text.toString()
            )
            listener?.onSignIn(model)
        }
        else
            showFormErrors()
    }

    private fun showFormErrors() {
        if (inputUsername.text?.isEmpty() == true)
            inputUsername.error = getString(R.string.error_empty)
        if (inputPassword.text?.isEmpty() == true)
            inputPassword.error = getString(R.string.error_empty)
    }

    private fun isFormValid() =
        inputUsername.text?.isNotEmpty() ?: false &&
                inputPassword.text?.isNotEmpty() ?: false


    private fun goToSignUp () {
        listener?.onGoToSignUp()
    }

    interface  SignInInteractionListener {
        fun onGoToSignUp()
        fun onSignIn(userName: SignModel)
    }

}