package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentAdministrationBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.hide
import es.us.managemyteam.extension.popBack

class AdministrationFragment : BaseFragment<FragmentAdministrationBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        viewBinding.administratorContainerCreateCoach.setOnClickListener {
            findNavController().navigate(R.id.action_administration_to_create_coach)
        }

        viewBinding.administratorContainerAcceptsPlayer.setOnClickListener {
            findNavController().navigate(R.id.action_administration_to_accept_player)
        }

        viewBinding.administratorContainerEditClub.setOnClickListener {
            findNavController().navigate(R.id.action_administration_to_edit_club)
        }

        viewBinding.administratorContainerPaypalConfig.setOnClickListener {
            findNavController().navigate(R.id.action_administration_to_paypal_config)
        }

        viewBinding.administratorContainerUnableUser.setOnClickListener {
            findNavController().navigate(R.id.action_administration_to_unable_users)
        }

        viewBinding.administrationMenuImgBack.setOnClickListener {
            popBack()
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdministrationBinding {
        return FragmentAdministrationBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}