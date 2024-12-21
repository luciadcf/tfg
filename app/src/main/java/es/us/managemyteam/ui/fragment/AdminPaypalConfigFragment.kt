package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentPaypalConfigBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.PaypalConfigBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.AdminPaypalViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminPaypalConfigFragment : BaseFragment<FragmentPaypalConfigBinding>() {

    private val adminPaypalViewModel: AdminPaypalViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        viewBinding.paypalConfigBtnAccept.setOnClickListener {
            clickOnAccept()
        }
    }

    private fun clickOnAccept() {
        val recipient = viewBinding.paypalConfigEditTextName.text
        val address = viewBinding.paypalConfigEditTextAddress.text
        val city = viewBinding.paypalConfigEditTextCity.text
        val postalcode = viewBinding.paypalConfigEditTextPostalCode.text
        val province = viewBinding.paypalConfigEditTextProvince.text

        adminPaypalViewModel.createPaypalConfig(
            PaypalConfigBo(
                recipient,
                address,
                postalcode,
                province,
                city
            )
        )
    }

    private fun setupObservers() {
        adminPaypalViewModel.getPaypalConfigData()
            .observe(viewLifecycleOwner, object : ResourceObserver<PaypalConfigBo>() {
                override fun onSuccess(response: PaypalConfigBo?) {
                    response?.let {
                        setupView(it)
                    }
                }

                override fun onLoading(loading: Boolean) {
                    super.onLoading(loading)
                    showLoader(loading)
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }
            })

        adminPaypalViewModel.getCreatePaypalConfigData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    Toast.makeText(context, "Actualización realizada con éxito", Toast.LENGTH_LONG)
                        .show()
                    popBack()
                }

            })
    }

    private fun setupView(response: PaypalConfigBo) {
        viewBinding.paypalConfigEditTextAddress.setText(response.address)
        viewBinding.paypalConfigEditTextCity.setText(response.city)
        viewBinding.paypalConfigEditTextName.setText(response.recipient)
        viewBinding.paypalConfigEditTextPostalCode.setText(response.postcode)
        viewBinding.paypalConfigEditTextProvince.setText(response.province)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaypalConfigBinding {
        return FragmentPaypalConfigBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle("Configuración de Paypal")
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}