package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentEditUserBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.hide
import es.us.managemyteam.extension.popBack

class EditUserFragment : BaseFragment<FragmentEditUserBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        viewBinding.editUserContainerProfile.setOnClickListener {
            findNavController().navigate(R.id.action_edit_user_to_edit_data)
        }

        viewBinding.editUserContainerEmail.setOnClickListener {
            findNavController().navigate(R.id.action_edit_user_to_edit_email)
        }

        viewBinding.editUserContainerPassword.setOnClickListener {
            findNavController().navigate(R.id.action_edit_user_to_edit_password)
        }

        viewBinding.editUserImgBack.setOnClickListener {
            popBack()
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditUserBinding {
        return FragmentEditUserBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}