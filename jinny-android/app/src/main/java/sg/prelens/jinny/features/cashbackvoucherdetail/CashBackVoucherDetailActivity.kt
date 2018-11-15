package sg.prelens.jinny.features.cashbackvoucherdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_cash_back_voucher_detail.*
import kotlinx.android.synthetic.main.layout_dialog_mailling.view.*
import kotlinx.android.synthetic.main.layout_dialog_vertical_mailling.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.features.mailingaddress.MailingAddressActivity
import sg.prelens.jinny.features.redeemcashback.RedeemCashBackViewModel
import sg.prelens.jinny.models.CashbackVoucher
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class CashBackVoucherDetailActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutId(): Int {
        return R.layout.activity_cash_back_voucher_detail
    }

    private var id_voucher: String? = null
    private var cashbackVoucher: CashbackVoucher? = null
    private lateinit var glide: RequestManager
    private lateinit var cashBackVoucherDetailViewModel: CashBackVoucherDetailViewModel
    private lateinit var redeemCashBackViewModel: RedeemCashBackViewModel
    private lateinit var maillingAddressDiaLog: DialogInterface
    private lateinit var maillingAddressDiaLogSuccess: DialogInterface
    private var voucherPurchaseDetail: CashbackVoucher? = null

    private fun cashBackVoucherDetailViewModel(): CashBackVoucherDetailViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@CashBackVoucherDetailActivity).fetchPurchasedVoucherDetail()
                    @Suppress("UNCHECKED_CAST")
                    return CashBackVoucherDetailViewModel(repo) as T
                }
            })[CashBackVoucherDetailViewModel::class.java]

    private fun redeemCashBackViewModel(): RedeemCashBackViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@CashBackVoucherDetailActivity).getBankAccountResult()
                    @Suppress("UNCHECKED_CAST")
                    return RedeemCashBackViewModel(repo) as T
                }
            })[RedeemCashBackViewModel::class.java]

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        roomDB = RoomDB.newInstance(this)
        glide = Glide.with(this)
        id_voucher = intent?.extras?.get("id_voucher") as? String
        cashBackVoucherDetailViewModel = cashBackVoucherDetailViewModel()
        redeemCashBackViewModel = redeemCashBackViewModel()
        cashBackVoucherDetailViewModel.setCashBackVoucherDetailID(id_voucher ?: "")
        cashBackVoucherDetailViewModel.cashBackLiveData.observe(this, Observer {
            it?.let {
                cashbackVoucher = it.result
                TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_reward_detail, getString(R.string.reward_voucher_info, it.result.description, it.result.id), this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_reward_detail, description = null)
                        TrackingHelper.setDB(default, roomDB
                                ?: return@Observer, this)
                    }
                })
                tvVoucherName?.text = it.result.description
                tvVoucherType?.text = it.result.cashback_type
                tvDescription?.text = it.result.terms
                tvVoucherExpiredDate?.text = "Valid Till: ${it.result.expires_at_in_words}"
                ivVoucher?.loadFromUrl(it.result.image?.url?.original, glide)
                voucherPurchaseDetail = it.result
            }
        })
        redeemCashBackViewModel.postMaillingPurchaseResult.observe(this, Observer {
            it?.let {
                showDiaLogConfirmSuccess(it.message)
            }
        })

        redeemCashBackViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage().let {
                    //                toast(it.message ?: "")
                    showDialog(it)
                }
            }
        })

        cashBackVoucherDetailViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage().let {
                    //                toast(it.message ?: "")
                    showDialog(it)
                }
            }
        })

    }

    override fun replaceFragmentId(): Int {
        return 0
    }

    override fun isFullScreen(): Boolean {
        return false
    }

    override fun isBackPressed(): Boolean {
        return true
    }

    override fun init() {
        super.init()
        setBackgroundToolbar(0)
        tvTitle?.text = getString(R.string.cash_back_detail)
        ivMid?.setOnClickListener(this)
        btnRedeem?.setOnClickListener(this)
        ivBookmark?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivMid -> {
                onBackPressed()
            }
            R.id.btnRedeem -> {
                if (voucherPurchaseDetail?.cashback_type.equals("E-voucher")) {
                    maillingAddressDiaLog = alert {
                        customView {
                            val view = layoutInflater.inflate(R.layout.layout_dialog_mailling, null)
                            addView(view, null)
                            view.tvContent.apply {
                                val description = voucherPurchaseDetail?.description
                                val price = voucherPurchaseDetail?.price
                                text = getString(R.string.confirm_mailling_address, description, price)
                                if (description?.trim()?.isNotEmpty() == true) {
                                    val textTemp = SpannableString(text)
                                    textTemp.setSpan(ForegroundColorSpan(Color.RED), text.indexOf(description), text.indexOf(description) + description.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    text = textTemp
                                }
                            }
                            view.btnConfirm.apply {
                                setOnClickListener({
                                    hideMaillingAddressDiaLog()
                                    redeemCashBackViewModel.postIdPurchase(id_voucher)
                                })
                                view.btnNo.apply {
                                    setOnClickListener {
                                        hideMaillingAddressDiaLog()
                                    }
                                }
                            }
                        }
                    }.show()
                } else if (voucherPurchaseDetail?.cashback_type.equals("Physical voucher")) {
                    startActivity(intentFor<MailingAddressActivity>().putExtra("Voucher", voucherPurchaseDetail))
                }
            }
            R.id.ivBookmark -> {
                if (!ivBookmark.isSelected) {
                    ivBookmark.isSelected = true
                    ivBookmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_action_on))
                } else {
                    ivBookmark.isSelected = false
                    ivBookmark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_action_off))
                }
            }
        }
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
        maillingAddressDiaLogSuccess.cancel()
        val intent = NavUtils.getParentActivityIntent(this)
        intent?.putExtra("page", page ?: 0)
        NavUtils.navigateUpTo(this, intent ?: return)
    }

    private fun hideMaillingAddressDiaLog() {
        maillingAddressDiaLog.cancel()
    }

}