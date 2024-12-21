package es.us.managemyteam.ui.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import es.managemyteam.app.R
import es.managemyteam.app.databinding.ActivityMainBinding
import es.us.managemyteam.extension.onNavigationItemSelected
import es.us.managemyteam.ui.view.verticalnavigation.NeedCloseDrawerListener


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        view.setOnApplyWindowInsetsListener { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }
            WindowInsets.CONSUMED
        }
        setContentView(view)

        navController = findNavController(R.id.main__container__nav_host)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupToolbar()
        setupBottomBar()
        setupSliderNavigationMenu()
    }

    fun refresh() {
        viewBinding.mainNavigationMenu.getVerticalMenuView().refresh()
    }

    private fun setupToolbar() {
        setSupportActionBar(getToolbar())
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupBottomBar() {
        getBottomBar().apply {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.action_menu -> {
                        viewBinding.dashboardDrawerMain.openDrawer(
                            GravityCompat.END
                        )
                    }

                    else -> this.onNavigationItemSelected(it.itemId)
                }
                true
            }
        }
    }

    private fun setupSliderNavigationMenu() {
        viewBinding.mainNavigationMenu.getVerticalMenuView().initialize(this)
        viewBinding.dashboardDrawerMain.setScrimColor(Color.TRANSPARENT)
        viewBinding.dashboardDrawerMain.drawerElevation = 0f
        viewBinding.dashboardDrawerMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        viewBinding.mainNavigationMenu.setNeedCloseDrawerListener(object : NeedCloseDrawerListener {
            override fun onNeedClosingDrawer() {
                closeDrawer()
            }
        })
    }

    private fun closeDrawer() {
        viewBinding.dashboardDrawerMain.closeDrawers()
    }

    fun getToolbar() = viewBinding.toolbar

    fun getBottomBar() = viewBinding.mainBottombarMenu

    fun getNavGraph() = navController

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}
