package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentPaymentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.PaymentBo
import es.us.managemyteam.extension.setNavIcon
import es.us.managemyteam.extension.setToolbarTitle
import es.us.managemyteam.extension.show
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.adapter.MyPaymentsAdapter
import es.us.managemyteam.ui.viewmodel.MyPaymentsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPaymentsFragment : BaseFragment<FragmentPaymentBinding>() {

    private val paymentsViewModel: MyPaymentsViewModel by viewModel()
    private val myPaymentsAdapter = MyPaymentsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.myPaymentsListContent.adapter = myPaymentsAdapter
        setupObserver()
        setupClickListeners()
    }

    private fun setupObserver() {
        paymentsViewModel.getMyPaymentsData()
            .observe(viewLifecycleOwner, object : ResourceObserver<List<PaymentBo>>() {
                override fun onSuccess(response: List<PaymentBo>?) {
                    response?.let {
                        if (it.isNotEmpty()) {
                            myPaymentsAdapter.apply {
                                setData(response)
                                notifyDataSetChanged()
                            }
                            viewBinding.myPaymentsViewEmptyPayments.root.visibility = GONE
                        } else {
                            viewBinding.myPaymentsViewEmptyPayments.root.visibility = VISIBLE
                        }
                    }
                }

            })
        paymentsViewModel.getMyPayments()
    }

    private fun setupClickListeners() {
        viewBinding.myPaymentsBtnPay.setOnClickListener {
            findNavController().navigate(R.id.action_my_payments_to_create_payment)
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentBinding {
        return FragmentPaymentBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle("Mis pagos")
            setNavIcon(null)
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.show()
    }

}