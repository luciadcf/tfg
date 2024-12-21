package es.us.managemyteam.ui.adapter

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import es.managemyteam.app.R
import es.us.managemyteam.ui.fragment.AcceptedCallFragment
import es.us.managemyteam.ui.fragment.PendingCallFragment
import es.us.managemyteam.ui.fragment.RejectCallFragment

class CallPagerAdapter(
    fm: FragmentManager,
    private val resources: Resources
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PendingCallFragment.newInstance()
            }
            1 -> {
                AcceptedCallFragment.newInstance()
            }
            else -> {
                RejectCallFragment.newInstance()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                resources.getString(R.string.my_calls_pending)
            }
            1 -> {
                resources.getString(R.string.my_calls_accepted)
            }
            else -> {
                resources.getString(R.string.my_calls_rejected)
            }
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}