package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentEditPasswordBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.UpdatePasswordViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPasswordFragment : BaseFragment<FragmentEditPasswordBinding>() {

    private val editPasswordViewModel: UpdatePasswordViewModel by viewModel()
    private var isStaffFirstTime = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isStaffFirstTime = arguments?.getBoolean(
            getString(R.string.argument__edit_password__is_staff_first_time),
            false
        ) ?: false
        setupObservers()
        setupClickListener()
    }

    private fun setupClickListener() {
        viewBinding.editPasswordBtnSendRequest.setOnClickListener {
            clickOnUpdate()
        }
    }

    private fun clickOnUpdate() {
        val currentPassword = viewBinding.editPasswordEditTextConfirmOldPassword.text
        val newPassword = viewBinding.editPasswordEditTextNewPassword.text
        val confirmPassword = viewBinding.editPasswordEditTextConfirmNewPassword.text

        editPasswordViewModel.getUpdatePassword(currentPassword, newPassword, confirmPassword)
        getFocusedView().hideKeyboard()
    }

    private fun setupObservers() {
        editPasswordViewModel.getUpdatePasswordData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    response?.let {
                        Toast.makeText(
                            context,
                            getString(R.string.update_user_success),
                            Toast.LENGTH_LONG
                        ).show()
                        if (!isStaffFirstTime) {
                            popBack()
                        } else {
                            editPasswordViewModel.enableUser(
                                FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            )
                        }
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    viewBinding.editPasswordBtnSendRequest.apply {
                        if (!isStaffFirstTime) {
                            showLoading(loading)
                        } else {
                            showLoading(true)
                        }
                    }
                }
            })

        editPasswordViewModel.getEnableUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    response?.let {
                        if (it) {
                            findNavController().navigate(R.id.action_edit_password_to_events)
                        }
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    if (!loading) {
                        viewBinding.editPasswordBtnSendRequest.showLoading(false)
                    }
                }
            })
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditPasswordBinding {
        return FragmentEditPasswordBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.edit_user_password))
            if (!isStaffFirstTime) {
                setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
                setNavAction { popBack() }
            } else {
                setNavIcon(null)
            }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}