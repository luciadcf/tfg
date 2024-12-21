package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentClubBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.ClubBo
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.ClubViewModel
import es.us.managemyteam.util.DateUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClubFragment : BaseFragment<FragmentClubBinding>() {

    private val clubViewModel: ClubViewModel by viewModel()
    private var userIsAdmin = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserIsAdminObserver()
        setupClubObserver()
        setupClickListeners()

    }

    private fun setupClubObserver() {
        clubViewModel.getClubData()
            .observe(viewLifecycleOwner, object : ResourceObserver<ClubBo>() {
                override fun onSuccess(response: ClubBo?) {
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
                    viewBinding.clubProgressBar.visibility = if (loading) VISIBLE else GONE
                }

            })
    }

    private fun setupUserIsAdminObserver() {
        clubViewModel.getUserData()
            .observe(viewLifecycleOwner, object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let {
                        userIsAdmin = it.isAdmin()
                        clubViewModel.getClub()
                    }
                }
            })
        clubViewModel.getUser()
    }

    private fun setupClickListeners() {
        viewBinding.clubFabEdit.setOnClickListener {
            findNavController().navigate(R.id.action_club_to_edit_club)
        }
    }

    private fun setupView(club: ClubBo) {
        viewBinding.clubLabelNameValue.text = club.name
        viewBinding.clubLabelDateFoundationValue.apply {
            if (club.dateFoundation != null) {
                text = DateUtil.format(club.dateFoundation!!, CLUB_DATE_FORMAT)
            } else {
                visibility = GONE
            }
        }
        viewBinding.clubLabelPresidentValue.text = club.president
        viewBinding.clubLabelCoachValue.text = club.coach
        viewBinding.clubLabelLocationValue.text = club.location
        viewBinding.clubLabelMailValue.text = club.mail
        viewBinding.clubLabelPhoneNumberValue.text = club.phoneNumber.toString()
        viewBinding.clubLabelWebValue.text = club.web

        viewBinding.clubFabEdit.visibility = if (userIsAdmin) {
            VISIBLE
        } else {
            GONE
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentClubBinding {
        return FragmentClubBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.club))
            setNavIcon(null)
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.show()
    }

}
