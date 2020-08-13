package es.us.managemyteam.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.R
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.databinding.FragmentUserBinding
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.UserViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class UserFragment : BaseFragment<FragmentUserBinding>() {

    private val userViewModel: UserViewModel by viewModel()
    private var userIsAdmin = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserIsAdminObserver()
        setupUserObserver()
        setupClickListeners()

    }

    private fun setupUserObserver() {
        userViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let { setupView(it) }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(
                        getString(error.errorMessageId),
                        getDefaultDialogErrorListener()
                    )
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    if (loading) {
                        viewBinding.userProgressBar.startAnimation()
                    } else {
                        viewBinding.userProgressBar.stopAnimationAndHide()
                    }
                }

            })
    }

    private fun setupUserIsAdminObserver() {
        userViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let {
                        userIsAdmin = it.isAdmin()
                        userViewModel.getUser()
                    }
                }
            })
        userViewModel.getUser()
    }

    private fun setupClickListeners() {
        viewBinding.userFabEdit.setOnClickListener {
            findNavController().navigate(R.id.action_user_to_edit_user)
        }
    }

    private fun setupView(user: UserBo) {
        viewBinding.userLabelNameValue.text = user.name
        viewBinding.userLabelSurnameValue.text = user.surname
        viewBinding.userLabelMailValue.text = user.email
        viewBinding.userLabelPhoneNumberValue.text = user.phoneNumber
        viewBinding.userLabelAgeValue.text = user.age.toString()

    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserBinding {
        return FragmentUserBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.user))
            setNavIcon(null)
            show()
        }
    }

    override fun setupBottomBar(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.show()
    }

}