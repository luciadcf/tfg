package es.us.managemyteam.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.R
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.databinding.FragmentUserProfileBinding
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.UserProfileViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>() {

    private val userProfileViewModel: UserProfileViewModel by viewModel()
    private var userIsLogged = false
    private var userIsPlayer = false
    private var userId: String? = null
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = arguments?.getString(getString(R.string.argument__user_uuid))

        if (userId == null) {
            viewBinding.userContainerEdit.visibility = VISIBLE
        }

        setupBackPressed()
        setupUserObserver(userId)
        setupClickListeners()

    }

    private fun setupBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun setupUserObserver(userId: String?) {
        userProfileViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    userIsLogged = response != null
                    response?.let {
                        userIsPlayer = it.isPlayer()
                        setupView(it)
                    }
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
        userProfileViewModel.getUser(userId)
    }

    private fun setupClickListeners() {
        viewBinding.userFabEdit.setOnClickListener {
            findNavController().navigate(R.id.action_user_to_edit_user)
        }

        viewBinding.userFabDelete.setOnClickListener {

            showDialog()

        }
    }

    private fun setupView(user: UserBo) {
        viewBinding.userLabelNameValue.text = user.name
        viewBinding.userLabelSurnameValue.text = user.surname
        viewBinding.userLabelMailValue.text = user.email
        viewBinding.userLabelPhoneNumberValue.text = user.phoneNumber
        viewBinding.userLabelAgeValue.text = user.age?.toString() ?: "--"

        viewBinding.userFabEdit.visibility = if (userIsLogged) {
            VISIBLE
        } else {
            GONE
        }

        if (userIsPlayer) {
            viewBinding.userLabelDorsalValue.text = user.dorsal?.toString() ?: ""
        } else {
            viewBinding.userLabelDorsalValue.text = ""
        }
    }

    private fun showDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog:AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this.context)

        // Set a title for alert dialog
        builder.setTitle("Está a punto de eliminar su cuenta de usuario.")

        // Set a message for alert dialog
        builder.setMessage("¿Seguro que desea eliminarla?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> findNavController().navigate(R.id.action_menu_to_login)
                DialogInterface.BUTTON_NEGATIVE -> dialog.cancel()
            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Sí",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("No",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserProfileBinding {
        return FragmentUserProfileBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.user_profile))
            if (userId == null) {
                setNavIcon(null)
            } else {
                setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
                setNavAction {
                    popBack()
                }
            }
            show()
        }
    }

    override fun setupBottomBar(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.show()
    }

}