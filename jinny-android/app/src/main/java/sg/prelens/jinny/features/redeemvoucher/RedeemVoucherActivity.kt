package sg.prelens.jinny.features.redeemvoucher

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import androidx.work.State
import com.bumptech.glide.Glide
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_redeem_voucher.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.exts.log
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import sg.prelens.jinny.models.Voucher
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class RedeemVoucherActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var dialog: DialogInterface
    private lateinit var voucher: Voucher

    private val redeemVoucherViewModel: RedeemVoucherViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(
                        this@RedeemVoucherActivity)
                        .redeemVoucherRepository()
                @Suppress("UNCHECKED_CAST")
                return RedeemVoucherViewModel(repo,
                        ServiceLocator.instance(this@RedeemVoucherActivity)
                                .getNetworkExecutor()) as T
            }
        })[RedeemVoucherViewModel::class.java]
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_voucher)
        voucher = intent.extras["voucher"] as Voucher?
                ?: throw IllegalStateException("Voucher should not be null")

        roomDB = RoomDB.newInstance(this)

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_redeem, description = getString(R.string.deal_info, voucher.merchant_name, voucher.id))
            TrackingHelper.setDB(default, roomDB
                    ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_redeem, getString(R.string.deal_info, voucher.merchant_name, voucher.id), this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_redeem, description = getString(R.string.deal_info, voucher.merchant_name, voucher.id))
                TrackingHelper.setDB(default, roomDB
                        ?: return@Observer, this)
            }
        })

        init()
        redeemVoucherViewModel.redeemLiveData.observe(this, Observer {
            log("it" + it?.result)
//            toast("Redeem deal successfully")
            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deals_detail_redeem, getString(R.string.deal_info, voucher.merchant_name, voucher.id), this)?.observe(this, Observer {
                if (it?.state == State.FAILED){
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deals_detail_redeem, description = getString(R.string.deal_info, voucher.merchant_name, voucher.id))
                    TrackingHelper.setDB(default, roomDB
                            ?: return@Observer, this)
                }
            })

            showDialog("Redeem deal successfully")
        })
        redeemVoucherViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it.parseMessage().let {
                    showDialog(it)
                }
            }
        })
    }

    fun init() {
        tvTitle.apply {
            text = voucher.merchant_name
            visibility = View.VISIBLE

        }
        tvTitle.setTextColor(ContextCompat.getColor(this@RedeemVoucherActivity
                , R.color.black))
        ivBack?.apply {
            setOnClickListener(this@RedeemVoucherActivity)
            visibility = View.VISIBLE
        }
        tvExpire.text = String.format(this.getString(R.string.expiry_date), voucher.expires_at_in_words)
        tvPromotionTitle.text = voucher.description
        if (voucher.can_cashback == false) {
            tvCashBackPercent?.visibility = View.GONE
        } else {
            if (voucher.cashback_type?.equals("cashback_percent") == true && voucher.cashback_percent?.equals("0.00") == false) {
                tvCashBackPercent?.visibility = View.VISIBLE
                tvCashBackPercent?.text = "Submit Receipt for " + voucher.cashback_percent.toString() + "% " + "Cashback"
            } else if (voucher.cashback_type?.equals("cashback_amount") == true && voucher.cashback_amount?.equals("0.00") == false) {
                tvCashBackPercent?.visibility = View.VISIBLE
                tvCashBackPercent?.text = "Submit Receipt for $" + voucher.cashback_amount.toString() + " Cashback"
            } else {
                tvCashBackPercent?.visibility = View.GONE
            }
        }
        if (voucher.qrcode?.url?.original.equals("http://app.myjinny.com:/images/original/missing.jpg") || voucher.qrcode?.url?.original.equals("http://jinny.vinova.sg:/images/original/missing.jpg")) {
            ivQrCode?.visibility = View.GONE
        } else {
            ivQrCode.loadFromUrl(voucher.qrcode?.url?.original, Glide.with(this))
            ivQrCode?.visibility = View.VISIBLE
        }
        btnRedeem.setOnClickListener(this)
    }

    private fun promtUserToRedeem() {
        dialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.tvContent.text = getString(R.string.do_you_want_to_redeem)
                view.btnOk.apply {
                    setOnClickListener(this@RedeemVoucherActivity)
                    text = getString(R.string.yes)
                }
                view.btnCancel.apply {
                    setOnClickListener(this@RedeemVoucherActivity)
                    text = getString(R.string.no)
                }
            }
        }.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnRedeem -> {
                promtUserToRedeem()
            }
            R.id.btnOk -> {
                dialog.cancel()
                redeemVoucherViewModel.redeemVoucher(voucher.id, voucher.users_voucher_id)
                val intent = Intent(this, PromotionDetailActivity::class.java)
                intent.putExtra("voucherId", voucher.id)
                intent.putExtra("usersVoucherId", voucher.users_voucher_id)
                intent.putExtra("voucherName", voucher.merchant_name)
                intent.putExtra("isArchived", voucher.archived)
                intent.putExtra("isRedeemed", voucher.is_redeemed)
                NavUtils.navigateUpTo(this, intent)
            }
            R.id.btnCancel -> {
                dialog.cancel()
            }
        }
    }

    fun showDialog(message: String?) {
        if (hasWindowFocus())
            dialog = alert {
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
        dialog.cancel()
    }

}


