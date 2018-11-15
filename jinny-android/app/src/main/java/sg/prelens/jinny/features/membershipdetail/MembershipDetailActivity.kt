package sg.prelens.jinny.features.membershipdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_membership_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.setOnItemClickListener
import sg.prelens.jinny.features.membershipdetail.bookmark.AddBookmarkViewModel
import sg.prelens.jinny.features.merchantbranch.MerchantBranchActivity
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import sg.prelens.jinny.models.Membership
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.prelens.jinny.utils.VoucherListener
import sg.vinova.trackingtool.model.EventType

class MembershipDetailActivity : BaseActivity(), View.OnClickListener {

    private var id: Int? = null
    private var merchantId: Int? = null
    private var name: String? = null
    private var logoUrl: String? = null
    private lateinit var glide: RequestManager
    private var isBookmark: Boolean = false
    private var membership: Membership? = null
    private lateinit var addBookmarkViewModel: AddBookmarkViewModel
    private val membershipDetailViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@MembershipDetailActivity)
                        .getMembershipDetailRepository()
                @Suppress("UNCHECKED_CAST")
                return MembershipDetailViewModel(repo, ServiceLocator.instance(this@MembershipDetailActivity).getNetworkExecutor()) as T
            }
        })[MembershipDetailViewModel::class.java]
    }

    private fun addBookmarkViewModel(): AddBookmarkViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@MembershipDetailActivity)
                            .addBookmarkRepository()
                    @Suppress("UNCHECKED_CAST")
                    return AddBookmarkViewModel(repo) as T
                }
            })[AddBookmarkViewModel::class.java]

    override fun getLayoutId(): Int {
        return R.layout.activity_membership_detail
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glide = Glide.with(this)
        id = intent.extras["id"] as? Int
        name = intent.extras["name"] as? String

        roomDB = RoomDB.newInstance(this)

        if (id == null) {
            throw IllegalStateException("Membership id could not be null")
        }

        if (name == null) {
            throw IllegalStateException("Name membership id could not be null")
        }

        membershipDetailViewModel.membershipLiveData.observe(this, Observer {
            membership = it
            TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.memberships_detail, getString(R.string.membership_info,
                    it?.merchant?.name, it?.code, it?.id?.toString()), this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_detail, description = getString(R.string.membership_info,
                            membership?.merchant?.name, membership?.code, membership?.id?.toString()))
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }

            })
            ivLogo.loadFromUrl(it?.merchant?.logo?.url?.original,
                    glide)
            tvBarcode.text = it?.code
            if (it?.merchant?.description?.trim()?.equals("") == true) {
                tvDescription?.visibility = View.GONE
            } else {
                tvDescription?.text = it?.merchant?.description
            }
            if (it?.has_bookmark == true) {
                setBookMark()
            } else {
                setUnBookmark()
            }
            ivBarcode.setImageBitmap(convertToBarCode(it?.code ?: return@Observer))
            isBookmark = it?.has_bookmark ?: false
            logoUrl = it?.merchant?.logo?.url?.original
            merchantId = it?.merchant?.id
        })

        membershipDetailViewModel.removeMembershipLiveData.observe(this, Observer {

            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.membership_remove, getString(R.string.membership_info, membership?.merchant?.name,
                    membership?.code, membership?.id?.toString()), this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.membership_remove, description = getString(R.string.membership_info, membership?.merchant?.name,
                            membership?.code, membership?.id?.toString()))
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })

            AppEvent.notifyRefreshMembership()
            NavUtils.navigateUpFromSameTask(this)
        })

        membershipDetailViewModel.setId(id ?: return)
        initAdapter()
        setTitle(name!!, 0)
        initViewModelBookmark()
        ibBookmark.setOnClickListener(this)
        tvRemoveMembership.setOnClickListener(this)
        tvRemoveMembership.setOnFocusChangeListener({ v, b -> if (b) onClick(v) })

        val onLogoClick = View.OnClickListener {
            startActivity<MerchantBranchActivity>("merchant_id" to merchantId, "logo_url" to logoUrl, "merchant_name" to name)
        }
        ivInfo.setOnClickListener(onLogoClick)
        ivLogo.setOnClickListener(onLogoClick)
        vLine.visibility = View.GONE
    }

    private fun initAdapter() {
        val glide = Glide.with(this)

        val relatedPromotionAdapter = RelatedPromotionAdapter(glide) {
            //            membershipDetailViewModel.retry()
        }

        rvRelatedPromotion?.apply {
            layoutManager = LinearLayoutManager(this@MembershipDetailActivity)
            adapter = relatedPromotionAdapter
        }

        rvRelatedPromotion?.setOnItemClickListener { _, _, position, _ ->
            startActivity<PromotionDetailActivity>(
                    "voucherId" to relatedPromotionAdapter.currentList?.get(position)?.id,
                    "usersVoucherId" to relatedPromotionAdapter.currentList?.get(position)?.users_voucher_id,
                    "voucherName" to relatedPromotionAdapter.currentList?.get(position)?.merchant_name)
        }
        membershipDetailViewModel.relatedPromotions.observe(this, Observer {
            relatedPromotionAdapter.submitList(it)
        })
    }

    private fun initViewModelBookmark() {
        addBookmarkViewModel = addBookmarkViewModel()
        addBookmarkViewModel.addBookmarkLiveData.observe(this, Observer {
            it?.let {
                //                toast(it.message ?: "Error!!!")
                showDialog(it.message ?: "Error!!!")
            }
            AppEvent.notifyRefreshMembership()
        })
        addBookmarkViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage()?.let {
                    //                toast(it)
                    showDialog(it)
                }
            }
        })
    }

    private fun convertToBarCode(code: String): Bitmap? {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(code, BarcodeFormat.CODE_128, 2560, 1024)
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
        ivBack?.setOnClickListener(this)
    }

    override fun onRestart() {
        super.onRestart()
        membershipDetailViewModel.setId(id ?: return)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvRemoveMembership -> {
                membershipDetailViewModel.removeMembership(id ?: return)
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.ibBookmark -> {
                isBookmark = if (!isBookmark) {
                    setBookMark()
                    TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.membership_bookmark, getString(R.string.membership_info, membership?.merchant?.name,
                            membership?.code, membership?.id?.toString()), this)?.observe(this, Observer {
                        if (it?.state == State.FAILED) {
                            val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.membership_bookmark, description = getString(R.string.membership_info, membership?.merchant?.name,
                                    membership?.code, membership?.id?.toString()))
                            TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                        }
                    })
                    true
                } else {
                    setUnBookmark()
                    TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.membership_unbookmark, getString(R.string.membership_info, membership?.merchant?.name,
                            membership?.code, membership?.id?.toString()), this)?.observe(this, Observer {
                        if (it?.state == State.FAILED) {
                            val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.membership_unbookmark, description = getString(R.string.membership_info, membership?.merchant?.name,
                                    membership?.code, membership?.id?.toString()))
                            TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                        }

                    })
                    false
                }
                addBookmarkViewModel.setBookMarkID(id!!)
            }
        }
    }
}
