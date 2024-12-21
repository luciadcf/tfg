package es.us.managemyteam.ui.view.verticalnavigation

import es.managemyteam.app.databinding.RowVerticalMenuBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import es.us.managemyteam.ui.adapter.BaseAdapter

class VerticalMenuAdapter(
    private val verticalMenuList: List<VerticalMenuVO>
) : BaseAdapter<VerticalMenuVO, RowVerticalMenuBinding, VerticalMenuAdapter.VerticalMenuViewHolder>(
    verticalMenuList.toMutableList()
) {

    override fun onCreate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VerticalMenuViewHolder {
        return VerticalMenuViewHolder(
            RowVerticalMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBind(item: VerticalMenuVO, position: Int, holder: VerticalMenuViewHolder) {
        holder.setup(item = verticalMenuList[position])
    }

    //region ViewHolder

    class VerticalMenuViewHolder(viewBinding: RowVerticalMenuBinding) :
        BaseViewHolder<VerticalMenuVO, RowVerticalMenuBinding>(viewBinding) {

        override fun setup(viewBinding: RowVerticalMenuBinding, item: VerticalMenuVO) {
            viewBinding.verticalMenuImgRowIcon.setImageResource(item.icon)
            viewBinding.verticalMenuLabelRowTitle.text = item.title
        }
    }
    //endregion

}
