package es.us.managemyteam.ui.view.common_map

import es.managemyteam.app.databinding.ViewInfoWindowBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoWindowAdapter(
    context: Context,
    private val markers: Collection<MarkerItemVo>
) : GoogleMap.InfoWindowAdapter {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var viewBinding = ViewInfoWindowBinding.inflate(inflater, null, false)

    override fun getInfoContents(marker: Marker): View {
        return viewBinding.root
    }

    override fun getInfoWindow(marker: Marker): View {
        val item = markers.find {
            it.position == marker.position
        }

        viewBinding.infoWindowLabelTitle.apply {
            text = item?.title
            visibility = if (item?.title.isNullOrEmpty()) {
                GONE
            } else {
                VISIBLE
            }
        }

        return viewBinding.root
    }
}