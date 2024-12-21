package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentMyTeamBinding
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.contract.BaseAdapterClickListener
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.MyTeamAdapter
import es.us.managemyteam.ui.viewmodel.MyTeamViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyTeamFragment : BaseFragment<FragmentMyTeamBinding>(), BaseAdapterClickListener<UserBo> {

    private val myTeamAdapter = MyTeamAdapter().apply {
        setItemClickListener(this@MyTeamFragment)
    }
    private val myTeamViewModel: MyTeamViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupPlayersObserver()
    }

    private fun setupList() {
        viewBinding.myTeamListPlayers.apply {
            addItemDecoration(DividerItemDecoration(context, VERTICAL))
            adapter = myTeamAdapter
        }
    }

    private fun setupPlayersObserver() {
        myTeamViewModel.getPlayersData().observe(viewLifecycleOwner, object :
            ResourceObserver<List<UserBo>>() {
            override fun onSuccess(response: List<UserBo>?) {
                response?.let {
                    myTeamAdapter.setData(it)
                    myTeamAdapter.notifyDataSetChanged()
                }
            }

            override fun onLoading(loading: Boolean) {
                showLoader(loading)

            }

            override fun onError(error: Error) {
                super.onError(error)
                showErrorDialog(error.serverErrorMessage ?: "",
                    DialogInterface.OnClickListener { _, _ ->
                        popBack()
                    })
            }
        })
    }

    //region BaseFragment

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyTeamBinding {
        return FragmentMyTeamBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.my_team_title))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction {
                popBack()
            }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.show()
    }

    override fun onAdapterItemClicked(item: UserBo, position: Int) {
        findNavController().navigate(
            R.id.action_my_team_to_profile,
            bundleOf(
                Pair(getString(R.string.argument__user_uuid), item.uuid)
            )
        )
    }

    //endregion

}