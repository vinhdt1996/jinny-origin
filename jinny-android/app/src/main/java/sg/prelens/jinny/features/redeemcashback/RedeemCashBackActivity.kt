package sg.prelens.jinny.features.redeemcashback

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NavUtils
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_mailing_address.*
import kotlinx.android.synthetic.main.activity_redeem_cash_back.*
import kotlinx.android.synthetic.main.layout_dialog_mailling.view.*
import kotlinx.android.synthetic.main.layout_dialog_vertical_mailling.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.*
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.setOnItemClickListener
import sg.prelens.jinny.features.addbankaccount.AddBankAccountActivity
import sg.prelens.jinny.features.cashbackvoucherdetail.CashBackVoucherDetailActivity
import sg.prelens.jinny.features.mailingaddress.MailingAddressActivity
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.withdrawcashback.WithDrawCashBackActivity
import sg.prelens.jinny.models.MaillingAddress
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class RedeemCashBackActivity : BaseActivity(), View.OnClickListener {

    private lateinit var maillingAddressDiaLog: DialogInterface
    private lateinit var maillingAddressDiaLogSuccess: DialogInterface
    private lateinit var redeemCashBackViewModel: RedeemCashBackViewModel
    private lateinit var bankAdapter: BankAdapter
    private lateinit var voucherAdapter: CashBackVoucherAdapter
    private var voucher_id: String? = null

    private lateinit var glide: RequestManager

    override fun getLayoutId(): Int {
        return R.layout.activity_redeem_cash_back
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

    private var roomDB: RoomDB? = null

    override fun init() {
        super.init()
        roomDB = RoomDB.newInstance(this)
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_reward_list, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_reward_list, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_reward_list, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })
        setTitle(getString(R.string.redeem_cashback), 0)
        setBackgroundToolbar(0)

        ivBack?.apply {
            setOnClickListener(this@RedeemCashBackActivity)
        }
        tvAddAccount.setOnClickListener {
            startActivity(Intent(this, AddBankAccountActivity::class.java))
        }
        initAdapter()
        redeemCashBackViewModel = getRedeemCashBackViewModel()
        redeemCashBackViewModel.redeemCashBackResult.observe(this, Observer {
            it?.let {
                bankAdapter.setList(it.result?.bank_informations)
                voucherAdapter.setList(it.result?.cashback_vouchers)
            }
        })
        redeemCashBackViewModel.postMaillingPurchaseResult.observe(this, Observer {
            it?.let {
                showDiaLogConfirmSuccess(it.message)
            }
        })
        redeemCashBackViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it.parseMessage().let {
                    //                toast(it.parseMessage() ?: "")
                    showDialog(it)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private fun getRedeemCashBackViewModel(): RedeemCashBackViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@RedeemCashBackActivity).getBankAccountResult()
                    @Suppress("UNCHECKED_CAST")
                    return RedeemCashBackViewModel(repo) as T
                }
            })[RedeemCashBackViewModel::class.java]

    private fun initAdapter() {
        glide = Glide.with(this)
        bankAdapter = BankAdapter(this, arrayListOf())
        rvNameRedeem?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvNameRedeem?.adapter = bankAdapter
        rvNameRedeem.setOnItemClickListener { _, _, position, _ ->
            startActivity(intentFor<WithDrawCashBackActivity>()
                    .putExtra("id", bankAdapter.getList()?.get(position)?.id)
                    .putExtra("holder_name", bankAdapter.getList()?.get(position)?.holder_name))
        }

        voucherAdapter = CashBackVoucherAdapter(this, arrayListOf(), glide)
        rvCashBackVoucher?.layoutManager = GridLayoutManager(this, 2)
        rvCashBackVoucher?.adapter = voucherAdapter
        rvCashBackVoucher.setOnItemClickListener { _, _, position, _ ->
            startActivity(intentFor<CashBackVoucherDetailActivity>()
                    .putExtra("id_voucher", voucherAdapter.getList()?.get(position)?.id))
        }
        voucherAdapter.setOnItemClickListener(object : IOnItemClickListener {
            override fun onItemClickListener(position: Int) {
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.cashbacks_reward_detail_purchase, getString(R.string.reward_voucher_info,
                        voucherAdapter.getList()?.get(position)?.description, voucherAdapter.getList()?.get(position)?.id), this@RedeemCashBackActivity)?.observe(this@RedeemCashBackActivity, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.cashbacks_reward_detail_purchase, description = getString(R.string.reward_voucher_info,
                                voucherAdapter.getList()?.get(position)?.description, voucherAdapter.getList()?.get(position)?.id))
                        TrackingHelper.setDB(default, roomDB
                                ?: return@Observer, this@RedeemCashBackActivity)
                    }
                })
                if (voucherAdapter.getList()?.get(position)?.cashback_type?.equals("Physical voucher") == true) {
                    startActivity(intentFor<MailingAddressActivity>()
                            .putExtra("Voucher", voucherAdapter.getList()?.get(position)))
                } else if (voucherAdapter.getList()?.get(position)?.cashback_type?.equals("E-voucher") == true) {
                    val description = voucherAdapter.getList()?.get(position)?.description ?: ""
                    val price = voucherAdapter.getList()?.get(position)?.price ?: ""
                    maillingAddressDiaLog = alert {
                        customView {
                            val view = layoutInflater.inflate(R.layout.layout_dialog_mailling, null)
                            addView(view, null)
                            view.tvContent.apply {
                                text = getString(R.string.confirm_mailling_address, description, price)
                                if (description.trim().isNotEmpty() == true) {
                                    val textTemp = SpannableString(text)
                                    textTemp.setSpan(ForegroundColorSpan(Color.RED), text.indexOf(description), text.indexOf(description) + description.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    text = textTemp
                                }
                            }
                            view.btnConfirm.apply {
                                setOnClickListener({
                                    hideMaillingAddressDiaLog()
                                    voucher_id = voucherAdapter.getList()?.get(position)?.id
                                    redeemCashBackViewModel.postIdPurchase(voucherAdapter.getList()?.get(position)?.id)
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
        })
    }

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
        maillingAddressDiaLogSuccess.cancel()
        val intent = NavUtils.getParentActivityIntent(this)
        intent?.putExtra("page", page ?: 0)
        NavUtils.navigateUpTo(this, intent ?: return)
    }
}