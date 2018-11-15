package sg.prelens.jinny.features.promotiondetail

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
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_promotion_detail.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import sg.prelens.jinny.BuildConfig
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.gone
import sg.prelens.jinny.exts.setOnItemClickListener
import sg.prelens.jinny.features.cashback.RequestCashBackActivity
import sg.prelens.jinny.features.redeemvoucher.RedeemVoucherActivity
import sg.prelens.jinny.features.voucherbranch.VoucherBranchPagerActivity
import sg.prelens.jinny.models.Logo
import sg.prelens.jinny.models.Voucher
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class PromotionDetailActivity : AppCompatActivity(), View.OnClickListener {

    private var voucherId: String? = null
    private var usersVoucherId: Int? = null
    private lateinit var glide: RequestManager
    private var voucherName: String? = null
    private var promotionAdapter: PromotionDetailAdapter? = null
    private var isArchived: Boolean? = null
    private var isRedeemed: Boolean = false
    private lateinit var listLogo: List<Logo>
    private var voucher: Voucher? = null
    private var isBookmarked: Boolean = false
    private lateinit var dialog: DialogInterface

    private val promotionDetailViewModel: PromotionDetailViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(
                        this@PromotionDetailActivity)
                        .getPromotionDetailRepository()
                @Suppress("UNCHECKED_CAST")
                return PromotionDetailViewModel(repo,
                        ServiceLocator.instance(this@PromotionDetailActivity)
                                .getNetworkExecutor()) as T
            }
        })[PromotionDetailViewModel::class.java]
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_detail)

        roomDB = RoomDB.newInstance(this)

        glide = Glide.with(this)
        voucherId = intent.extras["voucherId"] as? String ?: throw IllegalStateException("Voucher id could not be null")
        usersVoucherId = intent.extras["usersVoucherId"] as? Int ?: throw IllegalStateException("Users Voucher id could not be null")
        voucherName = intent.extras["voucherName"] as? String ?: throw IllegalStateException("Voucher name could not be null")
        isArchived = intent.extras["isArchived"] as? Boolean
        isRedeemed = intent.extras["isRedeemed"] as? Boolean ?: false

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_detail, description = getString(R.string.deal_info, voucherName, voucherId))
            TrackingHelper.setDB(default, roomDB
                    ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_detail, getString(R.string.deal_info, voucherName, voucherId), this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_detail, description = getString(R.string.deal_info, voucherName, voucherId))
                TrackingHelper.setDB(default, roomDB
                        ?: return@Observer, this)
            }
        })

        init()
        setUpList()
        promotionDetailViewModel.promotionDetailLiveData.observe(this, Observer {
            voucher = it ?: return@Observer
            listLogo = it.images ?: listOf()
            promotionAdapter?.setList(listLogo)
            tvPromotionTitle?.text = it.description
            tvExpire?.text = String.format(this.getString(R.string.expiry_date), it.expires_at_in_words)
            tvTerms?.text = it.terms
//            if (it.cashback_percent?.toString()?.trim()?.equals("") == true){
//                tvCashBackPercent?.visibility = View.GONE
//            }
//            else {
//                tvCashBackPercent?.text = it.cashback_percent.toString() + getString(R.string.cashback_available_s)
//            }
            if (it.can_cashback == false) {
                tvCashBackPercent?.visibility = View.GONE
            } else {
                if (it.cashback_type?.equals("cashback_percent") == true && it.cashback_percent?.equals("0.00") == false) {
                    tvCashBackPercent?.visibility = View.VISIBLE
                    tvCashBackPercent?.text = "Submit Receipt for " + it.cashback_percent.toString() + "% " + "Cashback"
                } else if (it.cashback_type?.equals("cashback_amount") == true && it.cashback_amount?.equals("0.00") == false) {
                    tvCashBackPercent?.visibility = View.VISIBLE
                    tvCashBackPercent?.text = "Submit Receipt for $" + it.cashback_amount.toString() + " Cashback"
                } else {
                    tvCashBackPercent?.visibility = View.GONE
                }
            }

            if (isArchived == null) {
                isArchived = it.archived ?: false
            }
            isBookmarked = it.is_bookmarked ?: false
            ibBookmark.setImageDrawable(ContextCompat.getDrawable(this@PromotionDetailActivity,
                    if (it.is_bookmarked == true && isArchived == false && !isRedeemed)
                        R.drawable.star_action_on
                    else R.drawable.star_action_off))
//            btnRedeem.isEnabled = !isArchived && !isRedeemed

            isRedeemed = it.is_redeemed ?: false
            if (isArchived == false && isRedeemed == false) {
                btnRedeem.visibility = View.VISIBLE
            }
            when (isArchived) {
                true -> {
                    btnRedeem.visibility = View.GONE
                    btnCashBack.visibility = View.GONE
                    ibBookmark.visibility = View.GONE
                    when (it.is_expired) {
                        true -> {
                            ibArchived.visibility = View.GONE
                        }
                        false -> {
                            ibArchived.visibility = View.VISIBLE
                            ibArchived.setImageDrawable(ContextCompat.getDrawable(this@PromotionDetailActivity, R.drawable.unarchive))
                        }
                    }
                }
                false -> {
                    when (isRedeemed) {
                        true -> {
                            ibBookmark.visibility = View.GONE
                            btnRedeem.visibility = View.GONE
                            if (it.can_cashback == true) {
                                when (it.is_cashbacked) {
                                    true -> {
                                        btnCashBack.visibility = View.GONE
                                    }
                                    false -> {
                                        btnCashBack.visibility = View.VISIBLE
                                    }
                                }
                            } else {
                                btnCashBack.visibility = View.GONE
                            }
                        }
                        false -> {
                            btnRedeem.visibility = View.VISIBLE
                        }
                    }
                    ibArchived.visibility = View.VISIBLE
                    ibArchived.setImageDrawable(ContextCompat.getDrawable(this@PromotionDetailActivity, R.drawable.archive))
                }
            }
            when (it.need_promocode) {
                true -> btnRedeem?.text = resources.getString(R.string.submit_receipt_button)
                false -> btnRedeem?.text = resources.getString(R.string.redeem_promo)
            }
        })

        promotionDetailViewModel.errorLiveData.observe(this, Observer {
            it?.printStackTrace()
        })

        promotionDetailViewModel.bookmarkLiveData.observe(this, Observer {
            if (isBookmarked) {
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deal_unbookmark, getString(R.string.deal_info, voucherName, voucherId), this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deal_unbookmark, description = getString(R.string.deal_info, voucherName, voucherId))
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
            } else {
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deal_bookmark, getString(R.string.deal_info, voucherName, voucherId), this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deal_bookmark, description = getString(R.string.deal_info, voucherName, voucherId))
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
            }
            toggleBookmark()
        })

        promotionDetailViewModel.archiveLiveData.observe(this, Observer {

            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deal_archive, getString(R.string.deal_info, voucherName, voucherId), this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deal_archive, description = getString(R.string.deal_info, voucherName, voucherId))
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })

            AppEvent.notifyRefreshVoucher()
            onBackPressed()
        })

        promotionDetailViewModel.removeLiveData.observe(this, Observer {
            //            toast(R.string.remove_voucher_success)
            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deal_sharing, getString(R.string.deal_info, voucherName, voucherId), this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deal_sharing, description = getString(R.string.deal_info, voucherName, voucherId))
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })
            showDialog(getString(R.string.remove_voucher_success), 0)
            AppEvent.notifyRefreshVoucher()
//            NavUtils.navigateUpFromSameTask(this)
        })

        promotionDetailViewModel.setVoucherId(id = voucherId
                ?: return, users_voucher_id = usersVoucherId ?: return)
        rvImagePromotion.setOnItemClickListener { _, _, position, _ ->
            val listImages: ArrayList<String> = arrayListOf()
            for (logo in listLogo) listImages.add(logo.url?.original ?: "")
            startActivity(intentFor<VoucherBranchPagerActivity>()
                    .putExtra("position", position)
                    .putStringArrayListExtra("listImage", listImages))
        }

        btnCashBack.setOnClickListener {
            startActivity(intentFor<RequestCashBackActivity>()
                    .putExtra("id", voucherId)
                    .putExtra("usersVoucherId", usersVoucherId ?: 0)
                    .putExtra("name", voucherName))
        }
    }

    private fun setUpList() {
        val linearLayoutManager = LinearLayoutManager(this@PromotionDetailActivity,
                LinearLayoutManager.VERTICAL, false)
        rvImagePromotion.isNestedScrollingEnabled = false
        rvImagePromotion.setHasFixedSize(false)
        promotionAdapter = PromotionDetailAdapter(glide, arrayListOf())
        rvImagePromotion.apply {
            layoutManager = linearLayoutManager
            adapter = promotionAdapter
        }
    }

    private fun toggleBookmark() {
        ibBookmark.setImageDrawable(ContextCompat.getDrawable(this@PromotionDetailActivity,
                if (!isBookmarked) R.drawable.star_action_on else R.drawable.star_action_off))
        isBookmarked = !isBookmarked
        AppEvent.notifyRefreshVoucher()
    }

    private fun init() {
        vLine?.visibility = View.GONE
        tvTitle.text = voucherName
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvTitle.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener(this)
        ibBookmark.setOnClickListener(this)
        ibArchived.setOnClickListener(this)
        btnRedeem.setOnClickListener(this)
        tvRemove.setOnClickListener(this)
        btnShare.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.notifyRefreshVoucher()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
//                NavUtils.navigateUpFromSameTask(this)
                onBackPressed()
            }
            R.id.tvRemove -> {
                promtUserToRemove()
            }
            R.id.ibBookmark -> {
                if (isRedeemed.not())
                    promotionDetailViewModel.setBookmarked(voucherId, usersVoucherId)
            }
            R.id.ibArchived -> {
                promotionDetailViewModel.archiveVoucher(voucherId, usersVoucherId)
            }
            R.id.btnRedeem -> {
                when (voucher?.need_promocode) {
                    true -> {
                        startActivity(intentFor<RequestCashBackActivity>()
                                .putExtra("id", voucherId)
                                .putExtra("usersVoucherId", usersVoucherId ?: 0)
                                .putExtra("name", voucherName))
                    }
                    false -> {
                        startActivity(intentFor<RedeemVoucherActivity>()
                                .putExtra("voucher", voucher))
                    }
                }

            }
            R.id.btnOk -> {
                promotionDetailViewModel.removeVoucher(voucherId, usersVoucherId)
                dialog.cancel()
                NavUtils.navigateUpFromSameTask(this)
            }
            R.id.btnCancel -> {
                dialog.cancel()
            }
            R.id.btnShare -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "Check out this deal from Jinny: " +
                                BuildConfig.BASE_URL + "share_deal/?screen=voucher&id=${voucherId}")
                startActivity(Intent.createChooser(sharingIntent, "Share using"))
            }
        }
    }

//    override fun onBackPressed() {
//        NavUtils.navigateUpFromSameTask(this)
//    }

    private fun promtUserToRemove() {
        dialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.tvContent.text = getString(R.string.do_you_want_to_remove)
                view.btnOk.apply {
                    setOnClickListener(this@PromotionDetailActivity)
                    text = getString(R.string.yes)
                }
                view.btnCancel.apply {
                    setOnClickListener(this@PromotionDetailActivity)
                    text = getString(R.string.no)
                }
            }
        }.show()
    }

    fun showDialog(message: String?, flag: Int?) {
        dialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (flag == null) {
                        hideDialog()
                    } else {
                        hideDialog()
                        NavUtils.navigateUpFromSameTask(this@PromotionDetailActivity)
                    }
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