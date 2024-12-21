package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentEditUserProfileBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.UpdateUserProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditUserProfileFragment : BaseFragment<FragmentEditUserProfileBinding>() {

    private val editUserProfileViewModel: UpdateUserProfileViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListener()
    }

    private fun setupClickListener() {
        viewBinding.editUserProfileBtnSendRequest.setOnClickListener {
            clickOnUpdate()
        }
    }

    private fun setupObservers() {
        editUserProfileViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let {
                        setupUser(it, it.isPlayer())
                    }
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    if (loading) {
                        viewBinding.editUserProfileContainerRoot.visibility = GONE
                        viewBinding.editUserProfileProgressBar.visibility = VISIBLE
                    } else {
                        viewBinding.editUserProfileContainerRoot.visibility = VISIBLE
                        viewBinding.editUserProfileProgressBar.visibility = GONE
                    }
                }
            })

        editUserProfileViewModel.getUpdateUserProfileData()
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

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    viewBinding.editUserProfileBtnSendRequest.showLoading(loading)
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }
            })
        editUserProfileViewModel.getUser()
    }

    private fun setupUser(user: UserBo, isPlayer: Boolean) {
        if (isPlayer) {
            user.dorsal?.let {
                viewBinding.editUserProfileEditTextDorsal.apply {
                    visibility = VISIBLE
                    setText(user.dorsal.toString())
                }
            }
        } else {
            viewBinding.editUserProfileEditTextDorsal.visibility = GONE
        }
        user.age?.let {
            viewBinding.editUserProfileEditTextAge.setText(user.age.toString())
        }
        viewBinding.editUserProfileEditTextName.setText(user.name)
        viewBinding.editUserProfileEditTextSurname.setText(user.surname)
        viewBinding.editUserProfileEditTextPhonenumber.setText(user.phoneNumber)
    }

    private fun clickOnUpdate() {
        val name = viewBinding.editUserProfileEditTextName.text
        val surname = viewBinding.editUserProfileEditTextSurname.text
        val dorsal = if (viewBinding.editUserProfileEditTextDorsal.text.isNotBlank()) {
            viewBinding.editUserProfileEditTextDorsal.text.toLong()
        } else {
            null
        }
        val age = if (viewBinding.editUserProfileEditTextAge.text.isNotBlank()) {
            viewBinding.editUserProfileEditTextAge.text.toInt()
        } else {
            null
        }
        val phoneNumber = viewBinding.editUserProfileEditTextPhonenumber.text

        getFocusedView().hideKeyboard()

        editUserProfileViewModel.getUpdateUserProfile(name, surname, phoneNumber, age, dorsal)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditUserProfileBinding {
        return FragmentEditUserProfileBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.edit_user_title))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }


}
