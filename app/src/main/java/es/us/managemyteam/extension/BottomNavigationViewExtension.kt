package es.us.managemyteam.extension

import es.managemyteam.app.R
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.ui.activity.MainActivity

fun BottomNavigationView.hide() {
    visibility = GONE
}

fun BottomNavigationView.show() {
    visibility = VISIBLE
}

fun BottomNavigationView.onNavigationItemSelected(itemId: Int) {
    val mainActivity = getBaseActivity() as MainActivity
    if (selectedItemId == itemId) return
    when (itemId) {
        R.id.action_events -> mainActivity.getNavGraph().navigate(R.id.action_menu_to_events)
        R.id.action_chat -> mainActivity.getNavGraph().navigate(R.id.action_menu_to_chat)
        R.id.action_profile -> mainActivity.getNavGraph().navigate(R.id.action_menu_to_profile)
        else -> Log.d("", "No action found for this id")
    }
}