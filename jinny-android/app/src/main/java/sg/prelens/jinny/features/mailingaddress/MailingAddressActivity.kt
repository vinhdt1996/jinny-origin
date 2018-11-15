package sg.prelens.jinny.features.mailingaddress

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_mailing_address.*
import kotlinx.android.synthetic.main.layout_dialog_mailling.view.*
import kotlinx.android.synthetic.main.layout_dialog_vertical_mailling.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail_normal.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideLoading
import sg.prelens.jinny.exts.hideSoftKeyboard
import sg.prelens.jinny.exts.showLoading
import sg.prelens.jinny.models.CashbackVoucher
import sg.prelens.jinny.models.MaillingAddress
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class MailingAddressActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        val name = edtName?.text?.toString()?.trim() ?: ""
        val streetName = edtStreetName?.text?.toString()?.trim() ?: ""
        val floor = edtFloorAndUnit?.text?.toString()?.trim() ?: ""
        val postalCode = edtPostalCode?.text?.toString()?.trim() ?: ""
        when (v) {
            ivSaveAddress -> {
                loading.showLoading()
                if (!ivSaveAddress.isSelected) {
                    if (name.isEmpty()) {
//                    toast("Please input name")
                        showDialog("Please input name")
                    } else if (streetName.isEmpty()) {
//                    toast("Please enter a valid street name")
                        showDialog("Please enter a valid street name")
                    } else if (floor.isEmpty()) {
//                    toast("Please enter a valid floor and unit")
                        showDialog("Please enter a valid floor and unit")
                    } else if (postalCode.isEmpty()) {
//                    toast("Please enter a valid postal code")
                        showDialog("Please enter a valid postal code")
                    } else {
                        ivSaveAddress.isSelected = true
                        ivSaveAddress.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_box_on))
                        isRemembered = true
                        val maillingAddress = MaillingAddress(cashbackVoucher?.id
                                ?: "", name, "", streetName, floor, postalCode, isRemembered)
                        maillingAddressViewModel.putMaillingAddress(maillingAddress)
                    }
                } else {
                    loading.showLoading()
                    if (name.isEmpty()) {
//                    toast("Please input name")
                        showDialog("Please input name")
                    } else if (streetName.isEmpty()) {
//                    toast("Please enter a valid street name")
                        showDialog("Please enter a valid street name")
                    } else if (floor.isEmpty()) {
//                    toast("Please enter a valid floor and unit")
                        showDialog("Please enter a valid floor and unit")
                    } else if (postalCode.isEmpty()) {
//                    toast("Please enter a valid postal code")
                        showDialog("Please enter a valid postal code")
                    } else {
                        ivSaveAddress.isSelected = false
                        ivSaveAddress.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_box))
                        isRemembered = false
                        val maillingAddress = MaillingAddress(cashbackVoucher?.id
                                ?: "", name, "", streetName, floor, postalCode, isRemembered)
                        maillingAddressViewModel.putMaillingAddress(maillingAddress)
                    }
                }
            }
            tvSaveAddress -> {
                if (!ivSaveAddress.isSelected) {
                    loading.showLoading()
                    if (name.isEmpty()) {
//                    toast("Please input name")
                        showDialog("Please input name")
                    } else if (streetName.isEmpty()) {
//                    toast("Please enter a valid street name")
                        showDialog("Please enter a valid street name")
                    } else if (floor.isEmpty()) {
//                    toast("Please enter a valid floor and unit")
                        showDialog("Please enter a valid floor and unit")
                    } else if (postalCode.isEmpty()) {
//                    toast("Please enter a valid postal code")
                        showDialog("Please enter a valid postal code")
                    } else {
                        ivSaveAddress.isSelected = true
                        ivSaveAddress.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_box_on))
                        isRemembered = true
                        val maillingAddress = MaillingAddress(cashbackVoucher?.id
                                ?: "", name, "", streetName, floor, postalCode, isRemembered)
                        maillingAddressViewModel.putMaillingAddress(maillingAddress)
                    }
                } else {
                    loading.showLoading()
                    if (name.isEmpty()) {
//                    toast("Please input name")
                        showDialog("Please input name")
                    } else if (streetName.isEmpty()) {
//                    toast("Please enter a valid street name")
                        showDialog("Please enter a valid street name")
                    } else if (floor.isEmpty()) {
//                    toast("Please enter a valid floor and unit")
                        showDialog("Please enter a valid floor and unit")
                    } else if (postalCode.isEmpty()) {
//                    toast("Please enter a valid postal code")
                        showDialog("Please enter a valid postal code")
                    } else {
                        ivSaveAddress.isSelected = false
                        ivSaveAddress.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_box))
                        isRemembered = false
                        val maillingAddress = MaillingAddress(cashbackVoucher?.id
                                ?: "", name, "", streetName, floor, postalCode, isRemembered)
                        maillingAddressViewModel.putMaillingAddress(maillingAddress)
                    }
                }
            }
            btnDone -> {
                if (name.isEmpty()) {
//                    toast("Please input name")
                    showDialog("Please input name")
                } else if (streetName.isEmpty()) {
//                    toast("Please enter a valid street name")
                    showDialog("Please enter a valid street name")
                } else if (floor.isEmpty()) {
//                    toast("Please enter a valid floor and unit")
                    showDialog("Please enter a valid floor and unit")
                } else if (postalCode.isEmpty()) {
//                    toast("Please enter a valid postal code")
                    showDialog("Please enter a valid postal code")
                } else {
                    maillingAddressDiaLog = alert {
                        customView {
                            val view = layoutInflater.inflate(R.layout.layout_dialog_mailling, null)
                            addView(view, null)
                            view.tvContent.apply {
                                text = getString(R.string.confirm_mailling_address, cashbackVoucher?.description, cashbackVoucher?.price)
                                if (cashbackVoucher?.description?.trim()?.isNotEmpty() == true) {
                                    val textTemp = SpannableString(text)
                                    textTemp.setSpan(ForegroundColorSpan(Color.RED), text.indexOf(cashbackVoucher?.description
                                            ?: ""), text.indexOf(cashbackVoucher?.description
                                            ?: "") + cashbackVoucher?.description?.length!!, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    text = textTemp
                                }
                            }
                            view.btnConfirm.apply {
                                setOnClickListener({
                                    hideMaillingAddressDiaLog()
                                    val maillingAddress = MaillingAddress(cashbackVoucher?.id
                                            ?: "", name, "", streetName, floor, postalCode, isRemembered)
                                    maillingAddressViewModel.postMaillingAddress(maillingAddress)
                                })
                                view.btnNo.apply {
                                    setOnClickListener {
                                        hideMaillingAddressDiaLog()
                                    }
                                }
                            }
                        }
                    }.show()
                }
            }
        }
    }

    private lateinit var maillingAddressViewModel: MaillingAddressViewModel
    private lateinit var maillingAddressDiaLog: DialogInterface
    private lateinit var maillingAddressDiaLogSuccess: DialogInterface
    private var roomDB: RoomDB? = null
    private val loading: Dialog by lazy {
        Dialog(this@MailingAddressActivity)
    }
    private var isRemembered = false
    private var cashbackVoucher: CashbackVoucher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mailing_address)

        roomDB = RoomDB.newInstance(this)

        tvTitle?.text = getString(R.string.mailling_address)
        ivBack?.setOnClickListener {
            onBackPressed()
        }

        cashbackVoucher = intent?.extras?.get("Voucher") as? CashbackVoucher?

        ivSaveAddress?.setOnClickListener(this)
        tvSaveAddress?.setOnClickListener(this)
        btnDone.setOnClickListener(this)

        clMaillingAdress?.setOnClickListener {
            hideSoftKeyboard()
        }

        maillingAddressViewModel = maillingAddress()
        maillingAddressViewModel.postMaillingAddressResult.observe(this, Observer {
            loading.hideLoading()
            if (it != null) {
                it?.let {
                    showDiaLogConfirmSuccess(it.message)
                }
            }
        })

        maillingAddressViewModel.putMaillingAddressResult.observe(this, Observer {
            loading.hideLoading()
            if (it != null) {
                it?.let {
                    maillingAddressDiaLogSuccess = alert {
                        customView {
                            val view = layoutInflater.inflate(R.layout.layout_dialog_vertical_mailling, null)
                            addView(view, null)
                            view.tvContent2.apply {
                                text = it.message
                            }
                            view.btnViewMyVoucher.apply {
                                text = "OK"
                                setOnClickListener({
                                    maillingAddressDiaLogSuccess.cancel()
                                })
                                view.btnReturn.apply {
                                    visibility = View.GONE
                                }
                            }
                        }
                    }.show()
                }
            }
        })


        maillingAddressViewModel.getMaillingAddressResult.observe(this, Observer {
            loading.hideLoading()
            TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.mailing_address, "", this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.mailing_address, description = null)
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })
            it?.let {
                if (it.result.mailing_name.isNotEmpty() && it.result.street.isNotEmpty() && it.result.floor.isNotEmpty() && it.result.postal_code.trim().isNotEmpty()) {
                    edtName?.setText(it.result.mailing_name)
                    edtStreetName?.setText(it.result.street)
                    edtFloorAndUnit?.setText(it.result.floor)
                    edtPostalCode?.setText(it.result.postal_code)
                    ivSaveAddress?.isSelected = true
                    isRemembered = true
                    ivSaveAddress?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_box_on))
                } else {
                    ivSaveAddress?.isSelected = false
                    isRemembered = false
                    ivSaveAddress?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_box))
                }
            }
        })

        maillingAddressViewModel.errorLiveData.observe(this, Observer {
            loading.hideLoading()
            if (it != null) {
                it?.let {
                    showDialog(it.message)
                }
            }
        })
    }

    private fun maillingAddress(): MaillingAddressViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@MailingAddressActivity).postMaillingAddress()
                    @Suppress("UNCHECKED_CAST")
                    return MaillingAddressViewModel(repo) as T
                }
            })[MaillingAddressViewModel::class.java]

    private fun hideMaillingAddressDiaLog() {
        maillingAddressDiaLog.cancel()
    }

    private fun showDiaLogConfirmSuccess(message: String?) {
        maillingAddressDiaLogSuccess = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical_mailling, null)
                addView(view, null)
                view.tvContent2.apply {
                    text = message ?: ""
                }
                view.btnViewMyVoucher.apply {
                    setOnClickListener({
                        hideMaillingAddressDiaLogSuccess(1)
                    })
                    view.btnReturn.apply {
                        setOnClickListener {
                            hideMaillingAddressDiaLogSuccess(2)
                        }
                    }
                }
            }
        }.show()
    }

    private fun hideMaillingAddressDiaLogSuccess(page: Int?) {
        loading.hideLoading()
        maillingAddressDiaLogSuccess.cancel()
        val intent = NavUtils.getParentActivityIntent(this)
        intent?.putExtra("page", page ?: 0)
        NavUtils.navigateUpTo(this, intent ?: return)
        AppEvent.notifyRefreshCashBackOverView()
        AppEvent.notifyRefreshCashBackDashBoard(2)
    }

    fun showDialog(message: String?) {
        maillingAddressDiaLogSuccess = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_mailling, null)
                addView(view, null)
                view.btnNo?.visibility = View.GONE
                view.btnConfirm?.text = "Ok"
                view.btnConfirm?.setOnClickListener {
                    hideDialog()
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    fun hideDialog() {
        loading.hideLoading()
        maillingAddressDiaLogSuccess.cancel()
    }

}
