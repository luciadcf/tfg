package es.us.managemyteam.extension

import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import es.managemyteam.app.R

fun Toolbar.setNavAction(listener: (View) -> Unit) {
    setNavigationOnClickListener(listener)
}

fun Toolbar.setNavIcon(iconDrawable: Drawable?) {
    navigationIcon = iconDrawable
}

fun Toolbar.show() {
    visibility = VISIBLE
}

fun Toolbar.hide() {
    visibility = GONE
}

fun Toolbar.setToolbarTitle(title: String?) {
    findViewById<TextView>(R.id.main__label__toolbar_title).text = title ?: ""
}