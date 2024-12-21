package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentTermsAndConditionsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.hide
import es.us.managemyteam.extension.popBack
import es.us.managemyteam.util.HtmlUtil

class TermsAndConditionsFragment : BaseFragment<FragmentTermsAndConditionsBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val htmlAsString = getString(R.string.terms_and_conditions_description)

        viewBinding.termsAndConditionsLabelDescription.text = HtmlUtil.fromHtml(htmlAsString)

        setupClickListeners()
    }

    private fun setupClickListeners() {

        viewBinding.termsAndConditionsImgBack.setOnClickListener {
            popBack()
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTermsAndConditionsBinding {
        return FragmentTermsAndConditionsBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.hide()
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }

}