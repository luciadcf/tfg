package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.setNavAction
import es.us.managemyteam.ui.activity.MainActivity

abstract class BaseFragment<Any : ViewBinding> : Fragment() {

    lateinit var viewBinding: Any

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): Any

    abstract fun setupToolbar(toolbar: Toolbar)

    abstract fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView)

    fun getToolbar() = (activity as MainActivity).getToolbar()

    fun showLoader(show: Boolean) {
        activity?.findViewById<View>(R.id.main__container__loader)?.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        setupMenuViews()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewBinding = inflateViewBinding(inflater, container)
        return viewBinding.root
    }


    private fun setupMenuViews() {
        if (activity is MainActivity) {
            activity?.findViewById<View>(R.id.main__view__toolbar_shadow)?.visibility =
                if (this is LoginFragment) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            setupToolbar((activity as MainActivity).getToolbar().apply { setNavAction { } })
            setupBottomBar((activity as MainActivity).getBottomBar())
        }
    }

}