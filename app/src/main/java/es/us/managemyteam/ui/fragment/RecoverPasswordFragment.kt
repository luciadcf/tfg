package es.us.managemyteam.ui.fragment

import es.managemyteam.app.databinding.FragmentRecoverPasswordBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.hide
import es.us.managemyteam.extension.popBack
import es.us.managemyteam.extension.showErrorDialog
import es.us.managemyteam.extension.showInformationDialog
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.RecoverPasswordViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecoverPasswordFragment : BaseFragment<FragmentRecoverPasswordBinding>() {

    private val recoverPasswordViewModel: RecoverPasswordViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObserver()
    }

    private fun setupObserver() {
        recoverPasswordViewModel.getRecoverPasswordData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    showInformationDialog("Le hemos enviado un correo electrónico para restablecer su contraseña")
                    popBack()
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(error.serverErrorMessage ?: getString(error.errorMessageId))
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    viewBinding.recoverPasswordBtnSendRequest.showLoading(loading)
                }
            })
    }

    private fun setupClickListeners() {
        viewBinding.recoverPasswordImgBack.setOnClickListener {
            popBack()
        }

        viewBinding.recoverPasswordBtnSendRequest.setOnClickListener {
            recoverPasswordViewModel.getRecoverPassword(viewBinding.recoverPasswordEditTextEmail.text.trim())
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecoverPasswordBinding {
        return FragmentRecoverPasswordBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}