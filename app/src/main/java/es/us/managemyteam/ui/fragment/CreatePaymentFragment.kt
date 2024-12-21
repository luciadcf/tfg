package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentCreatePaymentBinding
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.viewmodel.CreatePaymentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePaymentFragment : BaseFragment<FragmentCreatePaymentBinding>() {

    private val createPaymentViewModel: CreatePaymentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupClickListeners()
    }

    private fun setupObserver() {
        createPaymentViewModel.getPaypalData()
            .observe(viewLifecycleOwner, object : ResourceObserver<Boolean>() {
                override fun onSuccess(response: Boolean?) {
                    response?.let {
                        if (it) {
                            Toast.makeText(context, "Pago realizado con éxito", Toast.LENGTH_LONG)
                                .show()
                            popBack()
                        }
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(error.serverErrorMessage ?: getString(error.errorMessageId))
                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        createPaymentViewModel.catchResult(requestCode, resultCode, data)
    }

    private fun setupClickListeners() {
        viewBinding.createPaymentBtnSend.setOnClickListener {
            createPaymentViewModel.goToPaypal(
                this@CreatePaymentFragment,
                viewBinding.createPaymentEditQuantity.text,
                viewBinding.createPaymentEditConcept.text
            )
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePaymentBinding {
        return FragmentCreatePaymentBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle("Pagar")
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction {
                popBack()
            }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }
}