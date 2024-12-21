package es.us.managemyteam.ui.view.verticalnavigation

import es.managemyteam.app.databinding.ViewVerticalNavigationBinding
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.navigation.NavigationView

class VerticalNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr) {

    private val viewBinding =
        ViewVerticalNavigationBinding.inflate(LayoutInflater.from(context), this)

    fun setNeedCloseDrawerListener(needListener: NeedCloseDrawerListener) {
        viewBinding.verticalNavigationContainerMenu.setNeedCloseDrawerListener(needListener)
    }

    fun getVerticalMenuView() = viewBinding.verticalNavigationContainerMenu

}