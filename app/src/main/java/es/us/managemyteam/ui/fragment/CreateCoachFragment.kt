package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentCreateCoachBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.Role
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.CreateCoachViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateCoachFragment : BaseFragment<FragmentCreateCoachBinding>() {

    private val createCoachViewModel: CreateCoachViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupCreateCoachObserver()
    }

    private fun setupCreateCoachObserver() {
        createCoachViewModel.getCreateCoachData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    showInformationDialog(getString(R.string.create_coach_success))
                    popBack()
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(
                        getString(error.errorMessageId)
                    ) { dialog, _ -> dialog.dismiss() }
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    viewBinding.createCoachBtnSendRequest.showLoading(loading)
                }
            })

    }

    private fun setupClickListeners() {
        viewBinding.createCoachImgBack.setOnClickListener {
            popBack()
        }
        viewBinding.createCoachBtnSendRequest.setOnClickListener {
            clickOnAcceptRegister()
        }
    }

    private fun clickOnAcceptRegister() {
        val email = viewBinding.createCoachEditTextEmail.text.trim()
        val password = viewBinding.createCoachEditTextPassword.text.trim()
        val confirmPassword = viewBinding.createCoachEditTextConfirmPassword.text.trim()
        val name = viewBinding.createCoachEditTextName.text.trim()
        val surname = viewBinding.createCoachEditTextSurname.text.trim()
        val phoneNumber = viewBinding.createCoachEditTextPhonenumber.text.trim()

        getFocusedView().hideKeyboard()

        createCoachViewModel.createCoach(
            email,
            password,
            confirmPassword,
            name,
            surname,
            phoneNumber,
            Role.STAFF
        )
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateCoachBinding {
        return FragmentCreateCoachBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}