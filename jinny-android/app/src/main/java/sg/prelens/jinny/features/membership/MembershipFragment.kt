package sg.prelens.jinny.features.membership

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Trace
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.work.State
import com.bumptech.glide.Glide
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.fragment_membership.*
import kotlinx.android.synthetic.main.item_retry.view.*
import kotlinx.android.synthetic.main.layout_search_view.*
import kotlinx.android.synthetic.main.layout_toolbar_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseFragment
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.*
import sg.prelens.jinny.features.addmembership.AddMembershipActivity
import sg.prelens.jinny.features.cashbackinfo.CashBackInfoActivity
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.membershipdetail.MembershipDetailActivity
import sg.prelens.jinny.models.HowToInfo
import sg.prelens.jinny.models.Membership
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.*
import sg.vinova.trackingtool.model.EventType

class MembershipFragment : BaseFragment(), MembershipListener, TextWatcher, ViewPagerListener {
    override fun onChangeCompleted(page: Int) {
        if (page == 0) {
            svMembership?.clearFocus()
            nsvMembership?.requestFocus()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val key = "search_keyword: " + s?.toString()
        if (!TrackingHelper.hasConnection(this@MembershipFragment.requireContext())) {
            val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.memberships_search, description = key)
            TrackingHelper.setDB(default, roomDB
                    ?: return, this@MembershipFragment.requireContext())
        }
        TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.memberships_search, key, requireContext())?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.memberships_search, description = key)
                TrackingHelper.setDB(default, roomDB
                        ?: return@Observer, this@MembershipFragment.requireContext())
            }
        })
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        (activity as MainActivity).membershipSearchView = edtSearch
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        log("text input: $s")
        model.filterStarredList(s.toString()).observe(requireActivity(), Observer<PagedList<Membership>> {
            starredAdapter.setList(ArrayList(it))
        })
        model.filterOtherList(s.toString()).observe(requireActivity(), Observer<PagedList<Membership>> {
            otherAdapter.setList(ArrayList(it))
        })
    }

    companion object {
        fun newInstance(): MembershipFragment {
            val fragment = MembershipFragment()
            return fragment
        }
    }

    private lateinit var model: MembershipViewModel
    private var mOldScrollY: Int = 0
    private var retryLayout: View? = null
    private lateinit var otherAdapter: MembershipAdapter
    private lateinit var starredAdapter: MembershipAdapter
    private lateinit var fadeIn: ScaleAnimation
    private lateinit var fadeOut: ScaleAnimation
    private var roomDB: RoomDB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_membership, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Trace.beginSection("MembershipFragment onActivityCreated")
        val spinnerArrayAdapter = ArrayAdapter<String>(
                requireContext(), R.layout.layout_sort_by_item, resources.getStringArray(R.array.sorting_item_array_membership)
        )

        roomDB = RoomDB.newInstance(this@MembershipFragment.requireContext())

        spinnerArrayAdapter.setDropDownViewResource(R.layout.layout_sort_by_item)
        spSortBy.adapter = spinnerArrayAdapter
        model = getViewModel()
        svMembership?.clearFocus()
        nsvMembership?.requestFocus()
        initAdapter()
        initViewModel()
        initSwipeToRefresh()
        setUpFabAnimation()
        bindEvent()
        AppEvent.addMembershipListener(this)
        AppEvent.addViewPagerChangeListener(this)
        Trace.endSection()

        ivInfo?.visibility = View.VISIBLE
        ivInfo?.text = getString(R.string.how_to_add_membership)
        ivInfo?.setOnClickListener {
            startActivity(Intent(activity, CashBackInfoActivity::class.java)
                    .putExtra("STATUS", HowToInfo.Membership)
                    .putExtra("isFIRSTTIME", false))
        }
//        val user = activity?.getCurrentUser()
//        val guest = SharePreference.getInstance().getGuestAccount()
//        if (user?.isFirstTime_Membership == true || guest?.isFirstTime_Membership == true) {
////            user?.isFirstTime_Membership = false
////            guest?.isFirstTime_Membership = false
////            activity?.saveUserPref(user ?: return)
//            SharePreference.getInstance().saveGuestAccount(guest ?: return)
//            startActivity(Intent(activity, CashBackInfoActivity::class.java)
//                    .putExtra("STATUS", HowToInfo.Membership)
//                    .putExtra("isFIRSTTIME", true))
//        }
    }

    fun setUpFabAnimation() {
        fadeIn = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        fadeOut = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

        fadeIn.duration = 300
        fadeIn.fillAfter = true
        fadeOut.duration = 300
        fadeOut.fillAfter = true
    }

    private fun initAdapter() {
        val glide = Glide.with(this)
        otherAdapter = MembershipAdapter(glide)
        starredAdapter = MembershipAdapter(glide)
        rvStarred?.layoutManager = GridLayoutManager(this.requireContext(), 2)
        rvStarred?.adapter = starredAdapter
        rvStarred?.itemAnimator = null
        rvOthers?.layoutManager = GridLayoutManager(this.requireContext(), 2)
        rvOthers?.adapter = otherAdapter
        rvOthers?.itemAnimator = null
//        val searchIcon = svMembership?.findViewById(android.support.v7.appcompat.R.id.search_mag_icon) as ImageView
//        searchIcon.setImageResource(R.drawable.search)
        ViewCompat.setNestedScrollingEnabled(rvOthers, false)
    }

    private fun initViewModel() {
        model.filterOtherList().observe(requireActivity(), Observer<PagedList<Membership>> {
            if (it?.size!! == 0 && starredAdapter.getList().size == 0) {
                tvStarred?.visibility = View.GONE
                tvStarredDescription?.visibility = View.GONE
                tvOther?.visibility = View.GONE
                tvOtherDescription?.visibility = View.VISIBLE
                ivMemberships?.visibility = View.VISIBLE
                tvSortBy?.visibility = View.GONE
                spSortBy?.visibility = View.GONE
                ivDropdown?.visibility = View.GONE
                vLineLine?.visibility = View.GONE
//                goneLine()
//                vLine.layoutParams = RelativeLayout.LayoutParams(0,0)
            } else if (it.size > 0 && starredAdapter.getList().size == 0) {
                tvStarred?.visibility = View.VISIBLE
                tvStarredDescription?.visibility = View.VISIBLE
                tvStarredDescription?.setText(R.string.empty_starred_msg)
                val span = SpannableString("  " + tvStarredDescription?.text)
                span.setSpan(ImageSpan(this@MembershipFragment.context, R.drawable.star_action_on, DynamicDrawableSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                tvStarredDescription?.text = span
                tvOther?.visibility = View.VISIBLE
                tvOtherDescription?.visibility = View.GONE
                ivMemberships?.visibility = View.GONE
                tvSortBy?.visibility = View.VISIBLE
                spSortBy?.visibility = View.VISIBLE
                ivDropdown?.visibility = View.VISIBLE
                vLineLine?.visibility = View.VISIBLE
            } else if (it.size == 0 && starredAdapter.getList().size > 0) {
                tvStarred?.visibility = View.VISIBLE
                tvStarredDescription?.visibility = View.GONE
                tvOther?.visibility = View.GONE
                tvOtherDescription?.visibility = View.GONE
                ivMemberships?.visibility = View.GONE
                tvSortBy?.visibility = View.GONE
                spSortBy?.visibility = View.GONE
                ivDropdown?.visibility = View.GONE
                vLineLine?.visibility = View.GONE
                // goneLine()
            } else if (it.size > 0 && starredAdapter.getList().size > 0) {
                tvStarred?.visibility = View.VISIBLE
                tvStarredDescription?.visibility = View.GONE
                tvOther?.visibility = View.VISIBLE
                tvOtherDescription?.visibility = View.GONE
                ivMemberships?.visibility = View.GONE
                tvSortBy?.visibility = View.VISIBLE
                spSortBy?.visibility = View.VISIBLE
                ivDropdown?.visibility = View.VISIBLE
                vLineLine?.visibility = View.VISIBLE
            }
            srlMembership?.isRefreshing = false
            otherAdapter.setList(ArrayList(it))
            Handler(Looper.getMainLooper()).postDelayed({
                if (rvStarred?.itemAnimator == null)
                    rvStarred?.itemAnimator = DefaultItemAnimator()
            }, 1000)
        })
        model.filterStarredList().observe(requireActivity(), Observer<PagedList<Membership>> {
            if (it?.size!! == 0 && otherAdapter.getList().size == 0) {
                tvStarred?.visibility = View.GONE
                tvStarredDescription?.visibility = View.GONE
                tvOther?.visibility = View.GONE
                tvOtherDescription?.visibility = View.VISIBLE
                ivMemberships?.visibility = View.VISIBLE
                tvSortBy?.visibility = View.GONE
                spSortBy?.visibility = View.GONE
                ivDropdown?.visibility = View.GONE
                vLineLine?.visibility = View.GONE
//                goneLine()
//                vLine.layoutParams = RelativeLayout.LayoutParams(0,0)
            } else if (it.size > 0 && otherAdapter.getList().size == 0) {
                tvStarred?.visibility = View.VISIBLE
                tvStarredDescription?.visibility = View.GONE
                tvOther?.visibility = View.GONE
                tvOtherDescription?.visibility = View.GONE
                ivMemberships?.visibility = View.GONE
                tvSortBy?.visibility = View.GONE
                spSortBy?.visibility = View.GONE
                ivDropdown?.visibility = View.GONE
                vLineLine?.visibility = View.GONE
                // goneLine()
            } else if (it.size == 0 && otherAdapter.getList().size > 0) {
                tvStarred?.visibility = View.VISIBLE
                tvStarredDescription?.visibility = View.VISIBLE
                tvStarredDescription?.setText(R.string.empty_starred_msg)
                val span = SpannableString("  " + tvStarredDescription?.text)
                span.setSpan(ImageSpan(this@MembershipFragment.context, R.drawable.star_action_on, DynamicDrawableSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                tvStarredDescription?.setText(span)
                tvOther?.visibility = View.VISIBLE
                tvOtherDescription?.visibility = View.GONE
                ivMemberships?.visibility = View.GONE
                tvSortBy?.visibility = View.VISIBLE
                spSortBy?.visibility = View.VISIBLE
                ivDropdown?.visibility = View.VISIBLE
                vLineLine.visibility = View.VISIBLE
            } else if (it.size > 0 && otherAdapter.getList().size > 0) {
                tvStarred?.visibility = View.VISIBLE
                tvStarredDescription?.visibility = View.GONE
                tvOther?.visibility = View.VISIBLE
                tvOtherDescription?.visibility = View.GONE
                ivMemberships?.visibility = View.GONE
                tvSortBy?.visibility = View.VISIBLE
                spSortBy?.visibility = View.VISIBLE
                ivDropdown?.visibility = View.VISIBLE
                vLineLine?.visibility = View.VISIBLE
            }
            srlMembership?.isRefreshing = false
            starredAdapter.setList(ArrayList(it))
            Handler(Looper.getMainLooper()).postDelayed({
                if (rvStarred?.itemAnimator == null)
                    rvStarred?.itemAnimator = DefaultItemAnimator()
            }, 1000)
        })

        model.error.observe(this, Observer {
            srlMembership?.isRefreshing = false
            if (it != null) {
                if (retryLayout == null) retryLayout = retryView.inflate()
                retryLayout?.visibility = View.VISIBLE
                retryLayout?.error_msg?.text = it
                retryLayout?.retry_button?.setOnClickListener {
                    model.retry()
                }
            } else {
                retryLayout?.visibility = View.GONE
            }
        })
    }

    private fun initSwipeToRefresh() {
        srlMembership?.setOnRefreshListener {
            Trace.beginSection("Membership Fragment swipeToRefresh")
            model.refresh()
            svMembership?.clearFocus()
            nsvMembership?.requestFocus()
            Trace.endSection()
        }
    }

    override fun onRefreshMembership() {
        model.refresh()
    }

    override fun onResume() {
        super.onResume()
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unregisterMembershipListener(this)
        AppEvent.unregisterViewPagerChangeListener(this)
    }

    private fun bindEvent() {
        Trace.beginSection("MembershipFragment bindEvent")
        spSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                model.filterOtherList(position).observe(requireActivity(), Observer<PagedList<Membership>> {
                    otherAdapter.setList(ArrayList(it))
                })
            }
        }
        activity?.setUpHideSoftKeyboard(coordinator)
        edtSearch?.addTextChangedListener(this)
        edtSearch?.setOnClickListener {
            svMembership?.clearFocus()
            nsvMembership?.requestFocus()
            (activity as MainActivity).membershipSearchView = edtSearch
        }
        svMembership.setBackgroundColor(Color.parseColor("#f2f2f4"))
        edtSearch.hint = getString(R.string.search_membership_hint)
        nsvMembership?.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            val viewChild = v?.getChildAt(0)
            //Scrolling down
            if (scrollY - mOldScrollY >= (v?.height?.div(5) ?: 0)) {
                onScroll(false)
                mOldScrollY = scrollY
            }
            //On Top
            if (scrollY <= (viewChild?.top ?: 0)) {
                onScroll(true)
                mOldScrollY = scrollY
            }

        }
        rvStarred?.setOnItemClickListener { _, _, position, _ ->
            if (position < 0 && position > starredAdapter.list.size - 1) {
                return@setOnItemClickListener
            } else {
                activity?.startActivity<MembershipDetailActivity>("id" to starredAdapter.list?.get(position)?.id,
                        "name" to starredAdapter.list?.get(position)?.merchant?.name)
            }
        }
        rvOthers?.setOnItemClickListener { _, _, position, _ ->
            if (position < 0 && position > otherAdapter.list.size - 1) {
                return@setOnItemClickListener
            } else {
                activity?.startActivity<MembershipDetailActivity>("id" to otherAdapter.list?.get(position)?.id,
                        "name" to otherAdapter.list?.get(position)?.merchant?.name)
            }
        }
        fab.setOnClickListener {
            if (!TrackingHelper.hasConnection(this@MembershipFragment.requireContext())) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_merchant_list, description = null)
                TrackingHelper.setDB(default, roomDB
                        ?: return@setOnClickListener, this@MembershipFragment.requireContext())
            }
            TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.memberships_merchant_list, "", requireContext())?.observe(this, Observer {
                if (it?.state == State.FAILED){
                    val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_merchant_list, description = null)
                    TrackingHelper.setDB(default, roomDB
                            ?: return@Observer, this@MembershipFragment.requireContext())
                }
            })
            startActivity(activity?.intentFor<AddMembershipActivity>()?.newTask())
        }

        Trace.endSection()
    }

    private fun onScroll(show: Boolean) {
        Trace.beginSection("MembershipFragment onScroll")
        if (!show && fab?.scaleX == 1f && fab?.visibility == View.VISIBLE) {
            fadeOut.hasEnded().run {
                fab?.startAnimation(fadeOut)
            }
        } else if (show && fab?.visibility == View.GONE) {
            fadeIn.hasEnded().run {
                fab?.startAnimation(fadeIn)
            }
        }

        if (!show) {
            Handler().postDelayed({
                fab?.visibility = View.GONE
            }, 300)
        } else fab?.visibility = View.VISIBLE
        Trace.endSection()
    }

    private fun getViewModel(): MembershipViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@MembershipFragment.requireContext())
                            .getMembershipRepository()
                    @Suppress("UNCHECKED_CAST")
                    return MembershipViewModel(repo, ServiceLocator.instance(this@MembershipFragment.requireContext()).getDiskIOExecutor()) as T
                }
            })[MembershipViewModel::class.java]

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            if (activity?.getCurrentUser()?.isFirstTime_Membership == true || SharePreference.getInstance().getGuestAccount()?.isFirstTime_Membership == true) {
                startActivity(Intent(activity, CashBackInfoActivity::class.java)
                        .putExtra("STATUS", HowToInfo.Membership)
                        .putExtra("isFIRSTTIME", true))
            }
        }
    }
}