package es.us.managemyteam.ui.view.common_map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import es.managemyteam.app.R
import es.us.managemyteam.extension.tint

class MapStyleUtil {

    companion object {

        private const val DEFAULT_ALPHA = 80
        private const val STROKE_WIDTH = 36

        fun getCurrentPositionDrawable(context: Context, color: Int): Drawable? {
            val drawable =
                ContextCompat.getDrawable(context, R.drawable.current_position) as LayerDrawable?
            if (drawable != null) {
                val alphaColor = Color.argb(
                    DEFAULT_ALPHA,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
                )
                val backDrawable = drawable.getDrawable(0) as GradientDrawable
                val frontDrawable = drawable.getDrawable(1) as GradientDrawable

                backDrawable.tint(alphaColor)
                frontDrawable.tint(color)
                frontDrawable.setStroke(STROKE_WIDTH, alphaColor)
            }

            return drawable
        }

    }

}