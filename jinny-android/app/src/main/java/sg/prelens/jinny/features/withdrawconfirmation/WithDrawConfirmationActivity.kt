package sg.prelens.jinny.features.withdrawconfirmation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.actitivity_withdraw_confirmation.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.R.id.*
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.redeemcashback.RedeemCashBackActivity
import sg.prelens.jinny.features.withdrawcashback.WithDrawCashBackActivity

class WithDrawConfirmationActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        if (v == btnConfirm) {
            withDrawConfirmationViewModel.setWithDrawConfirmationRepository(id.toString(), amount)
        } else if (v == ivMid) {
            onBackPressed()
        }
    }

    private lateinit var withDrawConfirmationViewModel: WithDrawConfirmationViewModel
    private lateinit var withDrawConfirmationDialog: DialogInterface
    private var id: Int? = null
    private var amount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actitivity_withdraw_confirmation)

        btnConfirm.setOnClickListener(this)
        ivMid.setOnClickListener(this)
        tvTitle.setText(R.string.with_draw_confirmaton)
        id = intent.extras["id"] as? Int
        amount = intent.extras["amount"] as? String

        tvAmount.text = "$$amount"

        withDrawConfirmationViewModel = withDrawConfirmation()
        withDrawConfirmationViewModel.setbankInformationRepository(id ?: -1)
        withDrawConfirmationViewModel.bankInformation.observe(this, Observer {
            tvHolderName?.text = it?.result?.holder_name
            tvBankName?.text = it?.result?.bank_name
            tvAccountNumber?.text = "******${it?.result?.account_number_last_4}"
        })
        withDrawConfirmationViewModel.withDrawConfirmation.observe(this, Observer {
            withDrawConfirmationDialog = alert {
                customView {
                    val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                    addView(view, null)
                    view.tvContent.apply {
                        text = it?.message ?: ""
                    }
                    view.btnOk.setText("Back to Cashback")
                    view.btnOk.setOnClickListener({
                        hideBarCodeDialog(2)
                    })
                    view.btnCancel.apply {
                        visibility = View.GONE
                    }
                }
            }.show()
        })
        withDrawConfirmationViewModel.errorLiveData.observe(this, Observer {
            //            toast(it?.message ?: return@Observer)
            if (it != null) {
                it.parseMessage().let {
                    showDialog(it)
                }
            }
        })
    }

    private fun withDrawConfirmation(): WithDrawConfirmationViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@WithDrawConfirmationActivity).getBankInformation()
                    @Suppress("UNCHECKED_CAST")
                    return WithDrawConfirmationViewModel(repo) as T
                }
            })[WithDrawConfirmationViewModel::class.java]

    private fun hideBarCodeDialog(page: Int?) {
        withDrawConfirmationDialog.cancel()
        val intent = NavUtils.getParentActivityIntent(this)
        intent?.putExtra("page", page ?: 0)
        NavUtils.navigateUpTo(this, intent ?: return)
    }

    fun showDialog(message: String?) {
        withDrawConfirmationDialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    hideDialog()
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    fun hideDialog() {
        withDrawConfirmationDialog.cancel()
    }
}