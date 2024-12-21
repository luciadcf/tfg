package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentEditEmailBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.UpdateEmailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditEmailFragment : BaseFragment<FragmentEditEmailBinding>() {

    private val editEmailViewModel: UpdateEmailViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
        setupClickListener()
    }

    private fun setupView() {
        viewBinding.editEmailEditTextNewEmail.setText(editEmailViewModel.getUserLoggedEmail())
    }

    private fun setupClickListener() {
        viewBinding.editEmailBtnSendRequest.setOnClickListener {
            clickOnUpdate()
        }
    }

    private fun clickOnUpdate() {
        val currentPassword = viewBinding.editEmailEditTextPassword.text
        val newEmail = viewBinding.editEmailEditTextNewEmail.text

        editEmailViewModel.getUpdateEmail(currentPassword, newEmail)
    }

    private fun setupObserver() {
        editEmailViewModel.getUpdateEmailData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    response?.let {
                        Toast.makeText(
                            context,
                            getString(R.string.update_user_success),
                            Toast.LENGTH_LONG
                        ).show()
                        popBack()
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    viewBinding.editEmailBtnSendRequest.showLoading(loading)
                }
            })
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditEmailBinding {
        return FragmentEditEmailBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.edit_user_email))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}