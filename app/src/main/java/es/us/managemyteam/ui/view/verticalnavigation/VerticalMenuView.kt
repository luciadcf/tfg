package es.us.managemyteam.ui.view.verticalnavigation

import es.managemyteam.app.R
import es.managemyteam.app.databinding.ViewVerticalMenuBinding
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import es.us.managemyteam.contract.BaseAdapterClickListener
import es.us.managemyteam.data.model.UserBo
import es.us.managemyteam.extension.getBaseActivity
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.activity.MainActivity
import es.us.managemyteam.ui.viewmodel.MenuViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerticalMenuView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), VerticalMenuClickListener,
    BaseAdapterClickListener<VerticalMenuVO> {

    private var needClosingDrawerListener: NeedCloseDrawerListener? = null
    private var viewBinding =
        ViewVerticalMenuBinding.inflate(LayoutInflater.from(context), this, true)

    private val menuViewModel: MenuViewModel by (context as AppCompatActivity).viewModel()
    private var userIsAdmin = false

    fun initialize(activity: MainActivity) {
        setupUserIsAdminObserver(activity)
    }

    fun refresh() {
        menuViewModel.getUser()
    }

    private fun setupUserIsAdminObserver(activity: MainActivity) {
        menuViewModel.getUserData().observe(activity,
            object : ResourceObserver<UserBo>() {
                override fun onSuccess(response: UserBo?) {
                    response?.let { user ->
                        userIsAdmin = user.isAdmin()
                        setupMenuList(userIsAdmin, user.isStaff())
                    }
                }

                override fun onError(error: es.us.managemyteam.repository.util.Error) {
                    super.onError(error)
                    userIsAdmin = false
                    setupMenuList(userIsAdmin, false)
                }
            })
        menuViewModel.getUser()

    }

    private fun setupMenuList(userIsAdmin: Boolean, userIsStaff: Boolean) {
        viewBinding.verticalMenuListOption.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewBinding.verticalMenuListOption.adapter =
            VerticalMenuAdapter(
                VerticalMenuVO.getDefaultMenu(
                    context,
                    userIsAdmin,
                    userIsStaff
                )
            ).apply {
                setItemClickListener(this@VerticalMenuView)
            }
    }

    //region VerticalMenuClickListener

    override fun onAdapterItemClicked(item: VerticalMenuVO, position: Int) {
        onVerticalMenuClicked(item.id)
    }

    override fun onVerticalMenuClicked(menuId: VerticalMenuId) {
        when (menuId) {
            VerticalMenuId.LOGOUT_ID -> setupLogoutClick()
            VerticalMenuId.MY_CLUB_ID -> setupClubClick()
            VerticalMenuId.ADMINISTRATION_ID -> setupAdministrationClick()
            VerticalMenuId.MY_TEAM_ID -> setupMyTeamClick()
            VerticalMenuId.MY_CALLS_ID -> setupMyCallsClick()
            VerticalMenuId.PAYMENTS_ID -> setupPaymentsClick()
            VerticalMenuId.TERMS_CONDITIONS_ID -> setupTermsClick()
        }
        needClosingDrawerListener?.onNeedClosingDrawer()
    }

    fun setNeedCloseDrawerListener(needListener: NeedCloseDrawerListener) {
        needClosingDrawerListener = needListener
    }
    //endregion

    private fun setupLogoutClick() {
        menuViewModel.logout()
        (getBaseActivity() as MainActivity).getNavGraph().navigate(R.id.action_menu_to_login)
    }

    private fun setupTermsClick() {
        (getBaseActivity() as MainActivity).getNavGraph().navigate(R.id.action_menu_to_terms)
    }

    private fun setupClubClick() {
        (getBaseActivity() as MainActivity).getNavGraph().navigate(R.id.action_menu_to_club)
    }

    private fun setupAdministrationClick() {
        (getBaseActivity() as MainActivity).getNavGraph()
            .navigate(R.id.action_menu_to_admin_menu)
    }

    private fun setupMyTeamClick() {
        (getBaseActivity() as MainActivity).getNavGraph()
            .navigate(R.id.action_menu_to_my_team)
    }

    private fun setupPaymentsClick() {
        (getBaseActivity() as MainActivity).getNavGraph()
            .navigate(R.id.action_menu_to_payment)
    }

    private fun setupMyCallsClick() {
        (getBaseActivity() as MainActivity).getNavGraph()
            .navigate(R.id.action_menu_to_call)
    }

}