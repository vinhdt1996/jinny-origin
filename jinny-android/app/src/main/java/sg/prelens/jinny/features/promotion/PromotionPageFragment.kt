package sg.prelens.jinny.features.promotion

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.fragment_promotion_page.*
import kotlinx.android.synthetic.main.layout_search_view.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideLoading
import sg.prelens.jinny.exts.setOnItemClickListener
import sg.prelens.jinny.exts.setUpHideSoftKeyboard
import sg.prelens.jinny.exts.showLoading
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.membership.NoPaddingArrayAdater
import sg.prelens.jinny.features.promotion.adapter.FeatureDealAdapter
import sg.prelens.jinny.features.promotion.adapter.OtherDealAdapter
import sg.prelens.jinny.features.promotion.adapter.PromotionAdapter
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.ViewPagerListener
import sg.prelens.jinny.utils.VoucherListener
import sg.vinova.trackingtool.model.EventType

abstract class PromotionPageFragment : Fragment(), VoucherListener, TextWatcher, ViewPagerListener {
    override fun afterTextChanged(s: Editable?) {
        if (!TrackingHelper.hasConnection(this@PromotionPageFragment.requireContext())) {
            val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deals_search, description = getString(R.string.search_keyword, s?.toString()))
            TrackingHelper.setDB(default, roomDB
                    ?: return, this@PromotionPageFragment.requireContext())
        }
        TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deals_search, getString(R.string.search_keyword, s?.toString()), requireContext())?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deals_search, description = getString(R.string.search_keyword, s?.toString()))
                TrackingHelper.setDB(default, roomDB
                        ?: return@Observer, this@PromotionPageFragment.requireContext())
            }
        })
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        query = s.toString()
        model.filter(query, order)
        (activity as MainActivity).promotionSearchView = edtSearch
    }

    override fun onChangeCompleted(page: Int) {
        if (page == 1) {
            svVoucher?.clearFocus()
            nsvPromotion?.requestFocus()
        }
    }

    protected lateinit var model: PromotionViewModel
    private var mOldScrollY: Int = 0
    open lateinit var voucherAdapter: PromotionAdapter
    private lateinit var voucherFeatureAdapter: FeatureDealAdapter
    private lateinit var otherDealAdapter: OtherDealAdapter
    private var query: String = ""
    private var order: String = ""
    private val loadingProgress: Dialog by lazy {
        Dialog(context)
    }
    private var id: String? = null

    companion object {
        const val TYPE_ALL = 0
        const val TYPE_STARRED = 1
        const val TYPE_ARCHIVED = 2
        const val TYPE_REDEEMED = 3
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("rvState", rvVouchers?.layoutManager?.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvVouchers?.layoutManager?.onRestoreInstanceState(savedInstanceState?.getParcelable("rvState"))
    }

    private var roomDB: RoomDB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_promotion_page, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model = getViewModel()
        roomDB = RoomDB.newInstance(this@PromotionPageFragment.requireContext())
        initList()
        initSwipeToRefresh()
        AppEvent.addVoucherListener(this)
        setUpFilter()
        bindEvent()
        loadingProgress.showLoading()
//        edtSearch?.setOnClickListener{
//            svVoucher?.clearFocus()
//            nsvPromotion?.requestFocus()
//        }
        AppEvent.addViewPagerChangeListener(this)
    }

    private fun setUpFilter() {
        val spinnerArrayAdapter = NoPaddingArrayAdater<String>(
                context, R.layout.layout_sort_by_item_promotion, resources.getStringArray(R.array.sorting_item_array_deal)
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.layout_sort_by_item_promotion)
        spSortByPromotion?.adapter = spinnerArrayAdapter
        spSortByPromotion?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                order = when (position) {
                    0 -> "recent"
                    else -> "expiry"
                }
                model.filter(query, order)
            }
        }
    }

    open fun initList() {
        rvFeatureVouchers?.layoutManager = LinearLayoutManager(this@PromotionPageFragment.requireContext(), LinearLayoutManager.VERTICAL, false)
        rvOtherVoucher?.layoutManager = LinearLayoutManager(this@PromotionPageFragment.requireContext(), LinearLayoutManager.VERTICAL, false)
        tvVoucher?.text = String.format(getString(R.string.title_voucher), "Other")
        rvVouchers?.setHasFixedSize(false)
//        val searchIcon = svVoucher?.findViewById(android.support.v7.appcompat.R.id.search_mag_icon) as ImageView
//        searchIcon.setImageResource(R.drawable.search)

        val glide = Glide.with(this)
        initAdapter(glide)

        val linearLayoutManager = PromotionLinearLayoutManager(context)
        rvVouchers?.apply {
            layoutManager = linearLayoutManager
            adapter = voucherAdapter
        }


        model.promotions.observe(this, Observer<PagedList<PromotionList>> { pageList ->
            val hideMode = if (pageList != null && pageList.size != 0) View.GONE else View.VISIBLE
            tvVoucherDescription?.visibility = hideMode
            tvSortBy?.visibility = if (hideMode == View.GONE) View.VISIBLE else View.GONE
            ivDropdown?.visibility = if (hideMode == View.GONE) View.VISIBLE else View.GONE
            spSortByPromotion?.visibility = if (hideMode == View.GONE) View.VISIBLE else View.GONE
            tvVoucher?.visibility = if (hideMode == View.GONE) View.VISIBLE else View.GONE
            loadingProgress.hideLoading()
            voucherAdapter.submitList(pageList)
            if (srlVoucher?.isRefreshing == true) {
                srlVoucher?.isRefreshing = false
            }
            // todo Cr6 Feature Deal
            model.filterPageList(pageList ?: return@Observer)
        })

        model.featureResult.observe(this, Observer {
            loadingProgress.hideLoading()
            if (it?.size != 0) {
                tvFeature?.visibility = View.VISIBLE
                rvFeatureVouchers?.visibility = View.VISIBLE
                voucherFeatureAdapter = FeatureDealAdapter(this@PromotionPageFragment.requireContext(), it, glide)
                rvFeatureVouchers?.apply {
                    adapter = voucherFeatureAdapter
                }
                voucherFeatureAdapter.setList(it)
                voucherFeatureAdapter.notifyDataSetChanged()
                setItemClickFeatureDeal()
            } else {
                tvFeature?.visibility = View.GONE
                rvFeatureVouchers?.visibility = View.GONE
            }

        })

        model.otherResult.observe(this, Observer {
            loadingProgress.hideLoading()
            if (it?.size != 0) {
                otherDealAdapter = OtherDealAdapter(this@PromotionPageFragment.requireContext(), it, glide)
                rvOtherVoucher?.apply {
                    adapter = otherDealAdapter
                }
                otherDealAdapter.setList(it)
                otherDealAdapter.notifyDataSetChanged()
                setItemClickOtherDeal()
            } else {
                rvOtherVoucher?.visibility = View.GONE
            }

        })

        model.filterPageListResult.observe(this, Observer {
            if (it?.size != null) {
                updateBadge(it.size)
            }
        })

        model.networkState.observe(this, Observer {
            loadingProgress.hideLoading()
            voucherAdapter.setNetworkState(it)
        })

    }

    protected open fun initAdapter(glide: RequestManager) {
        voucherAdapter = PromotionAdapter(glide) {
            model.filter()
        }
    }

    protected open fun updateBadge(size: Int?) {

    }

    override fun onRefreshVoucher() {
        loadingProgress.showLoading()
        model.refresh()
    }

    override fun onRedeemVoucher() {
    }

    private fun initSwipeToRefresh() {
        srlVoucher?.setOnRefreshListener {
            model.refresh()
            spSortByPromotion?.setSelection(0)
            edtSearch?.setText("")
            svVoucher?.clearFocus()
            nsvPromotion?.requestFocus()
        }
    }

    private fun bindEvent() {
        activity?.setUpHideSoftKeyboard(srlVoucher)

//        edtSearch?.setOnEditorActionListener { _, _, _ ->
//            (activity as MainActivity).membershipSearchView = edtSearch
//            false
//        }
//        edtSearch?.setOnClickListener {
//            (activity as MainActivity).promotionSearchView = edtSearch
//        }
        edtSearch?.addTextChangedListener(this)
        svVoucher.setBackgroundColor(Color.parseColor("#f2f2f4"))
        edtSearch.hint = getString(R.string.search_voucher_hint)
//        svVoucher?.setOnQueryTextListener(object : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(q: String): Boolean {
//                query = q
//                model.filter(query, order)
//                return false
//            }
//
//            override fun onQueryTextSubmit(q: String): Boolean {
//                return false
//            }
//        })

        nsvPromotion?.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            val viewChild = v?.getChildAt(0)
            if (v?.height == 0) return@setOnScrollChangeListener
            //On Top
            if (scrollY <= (viewChild?.top ?: 0)) {
                AppEvent.notifyNestedScrollChanged(true)
                mOldScrollY = scrollY
            }
            //Scrolling down
            else if (scrollY - mOldScrollY >= (v?.height?.div(5) ?: 0)) {
                AppEvent.notifyNestedScrollChanged(false)
                mOldScrollY = scrollY
            }

        }

        rvVouchers?.setOnItemClickListener { _, _, position, _ ->
            openPromotionDetail(position)
        }

        rvFeatureVouchers?.setOnItemClickListener { _, _, position, _ ->
            activity?.startActivity<PromotionDetailActivity>(
                    "voucherId" to voucherFeatureAdapter.getList()?.get(position)?.id,
                    "usersVoucherId" to voucherFeatureAdapter.getList()?.get(position)?.users_voucher_id,
                    "voucherName" to voucherFeatureAdapter.getList()?.get(position)?.merchant?.name)
        }

        rvOtherVoucher?.setOnItemClickListener { _, _, position, _ ->
            activity?.startActivity<PromotionDetailActivity>(
                    "voucherId" to otherDealAdapter.getList()?.get(position)?.id,
                    "usersVoucherId" to otherDealAdapter.getList()?.get(position)?.users_voucher_id,
                    "voucherName" to otherDealAdapter.getList()?.get(position)?.merchant?.name)
        }

        setOnClickListener()

        model.bookmarkLiveData.observe(requireActivity(), Observer {
            it?.let {
                AppEvent.notifyRefreshVoucher()
            }
        })
        model.archiveLiveData.observe(requireActivity(), Observer {
            it?.let {
                loadingProgress.hideLoading()
                AppEvent.notifyRefreshVoucher()
            }
        })
    }

    open fun setOnClickListener() {
        voucherAdapter.setOnClickItemListener(object : IOnItemPromotionClickListener {
            override fun onItemStarClickListener(position: Int, isBookmarked: Boolean, id_voucher: String, id_userVoucher: Int) {
                id = id_voucher
                model.bookmarkVoucher(id_voucher, id_userVoucher)
            }

            override fun onItemAtchivedClickListener(position: Int, isArchived: Boolean, id_voucher: String, id_userVoucher: Int) {
                loadingProgress.showLoading()
                id = id_voucher
                model.archiveVoucher(id_voucher, id_userVoucher)
            }
        })

    }

    fun setItemClickFeatureDeal() {
        voucherFeatureAdapter.setOnClickItemListener(object : IOnItemPromotionClickListener {
            override fun onItemStarClickListener(position: Int, isBookmarked: Boolean, id_voucher: String, id_userVoucher: Int) {
                id = id_voucher
                model.bookmarkVoucher(id_voucher, id_userVoucher)
            }

            override fun onItemAtchivedClickListener(position: Int, isArchived: Boolean, id_voucher: String, id_userVoucher: Int) {
                loadingProgress.showLoading()
                id = id_voucher
                model.archiveVoucher(id_voucher, id_userVoucher)
            }

        })
    }

    fun setItemClickOtherDeal() {
        otherDealAdapter.setOnClickItemListener(object : IOnItemPromotionClickListener {
            override fun onItemStarClickListener(position: Int, isBookmarked: Boolean, id_voucher: String, id_userVoucher: Int) {
                id = id_voucher
                model.bookmarkVoucher(id_voucher, id_userVoucher)
            }

            override fun onItemAtchivedClickListener(position: Int, isArchived: Boolean, id_voucher: String, id_userVoucher: Int) {
                loadingProgress.showLoading()
                id = id_voucher
                model.archiveVoucher(id_voucher, id_userVoucher)
            }

        })
    }

    override fun onStart() {
        super.onStart()
        loadingProgress.showLoading()
        model.refresh()
    }

    open fun openPromotionDetail(position: Int) {
        activity?.startActivity<PromotionDetailActivity>(
                "voucherId" to voucherAdapter.currentList?.get(position)?.id,
                "usersVoucherId" to voucherAdapter.currentList?.get(position)?.users_voucher_id,
                "voucherName" to voucherAdapter.currentList?.get(position)?.merchant?.name)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unregisterVoucherListener(this)
        AppEvent.unregisterViewPagerChangeListener(this)
    }

    abstract fun getViewModel(): PromotionViewModel

}