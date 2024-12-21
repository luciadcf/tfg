package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentLoginBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.getFocusedView
import es.us.managemyteam.extension.hide
import es.us.managemyteam.extension.hideKeyboard
import es.us.managemyteam.extension.showErrorDialog
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.activity.MainActivity
import es.us.managemyteam.ui.viewmodel.LoginViewModel
import es.us.managemyteam.util.FirebaseAuthUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModel()
    private val auth = FirebaseAuthUtil.getFirebaseAuthInstance()
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressed()
        setupViews()
        setupObservers()
        setupEnterClickListener()
    }

    private fun setupViews() {
        viewBinding.loginLabelGoToRegistrationHere.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registration)
        }

        viewBinding.loginLabelGoToRecoverPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_recover_password)
        }
    }

    private fun setupBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun setupObservers() {
        loginViewModel.getLoginData()
            .observe(viewLifecycleOwner, object : ResourceObserver<String>() {
                override fun onSuccess(response: String?) {
                    response?.let {
                        (activity as MainActivity).refresh()
                        loginViewModel.getUser(it)
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                    auth.signOut()
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    viewBinding.loginBtnEnter.showLoading(loading)
                }
            })

        loginViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let {
                        if (it.isStaff() && it.enable == false) {
                            findNavController().navigate(
                                R.id.action_login_to_update_password, bundleOf(
                                    Pair(
                                        getString(R.string.argument__edit_password__is_staff_first_time),
                                        true
                                    )
                                )
                            )
                        } else {
                            findNavController().navigate(R.id.action_login_to_events)
                        }
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                    auth.signOut()
                }
            })
    }

    private fun setupEnterClickListener() {
        viewBinding.loginBtnEnter.setOnClickListener {
            val email = viewBinding.loginEditTextEmail.text.trim()
            val password = viewBinding.loginEditTextPassword.text.trim()

            getFocusedView().hideKeyboard()

            loginViewModel.login(email, password)
        }
    }


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}