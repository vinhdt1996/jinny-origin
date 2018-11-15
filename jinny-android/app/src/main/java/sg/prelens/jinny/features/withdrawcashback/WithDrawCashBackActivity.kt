package sg.prelens.jinny.features.withdrawcashback

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.constraint.Group
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_with_draw_cash_back.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideSoftKeyboard
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.parseResString
import sg.prelens.jinny.exts.setUpHideSoftKeyboard
import sg.prelens.jinny.features.addbankaccount.AddBankAccountActivity
import sg.prelens.jinny.features.withdrawconfirmation.WithDrawConfirmationActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class WithDrawCashBackActivity : AppCompatActivity(), View.OnClickListener {

    private var removeBankViewModel: WithDrawCashBackViewModel? = null
    private var dialog: DialogInterface? = null
    private var id: Int? = null
    private lateinit var withDrawCashBackDialog: DialogInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_draw_cash_back)
        tvTitle?.text = R.string.with_draw_cash_back.parseResString()
        id = intent.extras["id"] as? Int
        val holderName = intent.extras["holder_name"] as? String
        removeBankViewModel = getWithDrawCashBackViewModel()

        setUpHideSoftKeyboard(main_constraint)
        edtAmountWithDrawCashBack.setOnTouchListener({ v, event ->
            edtAmountWithDrawCashBack?.setText("")
            edtAmountWithDrawCashBack.setOnTouchListener(null)
            false
        })

        btnContinueWithDrawCrashBack.setOnClickListener {
            val amount = edtAmountWithDrawCashBack?.text.toString()
            val notifi: String?
            val regex = "^(?=.)([+-]?([0-9]*)(\\.([0-9]+))?)\$"
            hideSoftKeyboard()

//            if (amount.isEmpty()) {
//                notifi = R.string.notify_withdraw_cashback_missing.parseResString()
//                showBarCodeDialog(notifi)
//            } else if (!amount.matches(Regex(regex))) {
//                notifi = R.string.notify_withdraw_cashback_invalid.parseResString()
//                showBarCodeDialog(notifi)
//            } else {
//                if (amount.toFloat() >= 10) {
//                    startActivity(intentFor<WithDrawConfirmationActivity>()
//                            .putExtra("id", id)
//                            .putExtra("amount", amount))
//                } else if (amount.toFloat() < 10) {
//                    notifi = R.string.notify_withdraw_cashback.parseResString()
//                    showBarCodeDialog(notifi)
//                }
//            }

            notifi = when (true) {
                amount.isEmpty() -> R.string.notify_withdraw_cashback_missing.parseResString()
                !amount.matches(Regex(regex)) -> R.string.notify_withdraw_cashback_missing.parseResString()
                amount.toFloat() < 10 -> R.string.notify_withdraw_cashback.parseResString()
                else -> {
                    startActivity(intentFor<WithDrawConfirmationActivity>()
                            .putExtra("id", id)
                            .putExtra("amount", amount))
                    return@setOnClickListener
                }
            }
            showBarCodeDialog(notifi)
        }

        gEditBankAccountWDCashBack.addOnClickListener(View.OnClickListener
        {
            startActivity(intentFor<AddBankAccountActivity>()
                    .putExtra("id", id)
                    .putExtra("holder_name", holderName)
            )
        })

        tvRemoveBankAccountWDCashBack.setOnClickListener(
                {
                    promtUserToRemove()
                })

        vRemoveBankAccountWDCashBack.setOnClickListener(
                {
                    promtUserToRemove()
                })

        ivMid.setOnClickListener {
            onBackPressed()
        }

        removeBankViewModel?.defaultResonse?.observe(this, Observer {
            //            toast(it?.message ?: "Success")

            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.bank_account_add, "bank_account_id: $id", this)?.observe(this, Observer {

            })

            showDialog(it?.message ?: "Success", 0)
//            NavUtils.navigateUpFromSameTask(this)
        })

        removeBankViewModel?.errorLiveData?.observe(this, Observer
        {
            if (it != null) {
                it?.parseMessage().let {
                    //                toast(it.message ?: "")
                    showDialog(it, null)
                }
            }
        })
    }

    private fun promtUserToRemove() {
        dialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.tvContent.text = getString(R.string.do_you_want_to_remove_bank_account)
                view.btnOk.apply {
                    setOnClickListener(this@WithDrawCashBackActivity)
                    text = getString(R.string.yes)
                }
                view.btnCancel.apply {
                    setOnClickListener(this@WithDrawCashBackActivity)
                    text = getString(R.string.no)
                }
            }
        }.show()
    }

    private fun Group.addOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnOk -> {
                removeBankViewModel?.removeBankAccount(id.toString())
                dialog?.cancel()
            }
            R.id.btnCancel -> {
                dialog?.cancel()
            }
        }
    }

    private fun getWithDrawCashBackViewModel(): WithDrawCashBackViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@WithDrawCashBackActivity).getBankAccountResult()
                    @Suppress("UNCHECKED_CAST")
                    return WithDrawCashBackViewModel(repo) as T
                }
            })[WithDrawCashBackViewModel::class.java]

    private fun hideBarCodeDialog() {
        withDrawCashBackDialog.cancel()
//        val intent = Intent(this, MainActivity::class.java)
//        NavUtils.navigateUpTo(this, intent)
    }

    private fun showBarCodeDialog(message: String?) {
        withDrawCashBackDialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.tvContent.apply {
                    text = message ?: ""
                }
                view.btnOk.setText(R.string.cancel)
                view.btnOk.setOnClickListener({
                    hideBarCodeDialog()
                })
                view.btnCancel.apply {
                    visibility = View.GONE
                }
            }
        }.show()
    }

    fun showDialog(message: String?, flag: Int?) {
        dialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (flag == null) {
                        hideDialog()
                    } else {
                        hideDialog()
                        NavUtils.navigateUpFromSameTask(this@WithDrawCashBackActivity)
                    }
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    fun hideDialog() {
        dialog?.cancel()
    }
}
