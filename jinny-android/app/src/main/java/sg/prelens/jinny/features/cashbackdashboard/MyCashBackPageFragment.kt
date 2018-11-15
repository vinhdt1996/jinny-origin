package sg.prelens.jinny.features.cashbackdashboard

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_cash_back_page.*
import kotlinx.android.synthetic.main.fragment_my_cash_back.*
import sg.prelens.jinny.PagingRequestHelper
import sg.prelens.jinny.R
import sg.prelens.jinny.features.cashback.RequestCashBackActivity
import sg.prelens.jinny.features.cashbackhistorydetail.CashbackHistoryDetailActivity
import sg.prelens.jinny.repositories.Status
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.CashBackDashBoardListener
import java.util.*

abstract class MyCashBackPageFragment : Fragment(), CashBackDashBoardListener {
    abstract fun getViewModel(): CashBackDashboardViewModel
    private var currentPage: Int = -1
    private lateinit var model: CashBackDashboardViewModel
    private lateinit var glide: RequestManager

    companion object {
        const val TYPE_CASH_BACK_ACTIVITY = 0
        const val TYPE_WITHDRAWAL = 1
        const val TYPE_VOUCHERS = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_cash_back_page, container, false)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model = getViewModel()
        glide = Glide.with(this)
        AppEvent.addCashBackDashBoardListener(this)
        init()
        initPullToRefresh()
    }

    open fun init() {
        val cashBackHistoryAdapter = CashBackDashboardAdapter(glide){
        }
        currentPage = 0
        setOnClickListener(cashBackHistoryAdapter)

        rvCashBack?.apply {
            layoutManager = LinearLayoutManager(this@MyCashBackPageFragment.context)
            adapter = cashBackHistoryAdapter
            setHasFixedSize(false)
        }
        model.data.observe(this, Observer {
            if (it?.size!! > 0) {
                rvCashBack?.visibility = View.VISIBLE
                tvNoItem?.visibility = View.GONE
                cashBackHistoryAdapter.setList(ArrayList(it.toList()))
                srlCashBackDashBoard?.isRefreshing = false
                if (currentPage != -1) {
                    viewPager?.currentItem = currentPage
                }
            } else {
                rvCashBack?.visibility = View.GONE
                tvNoItem?.visibility = View.VISIBLE
            }
        })

        model.refreshState.observe(this, Observer {
            if (it?.status == Status.SUCCESS){
                srlCashBackDashBoard?.isRefreshing = false
            }
        })

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                currentPage = position
            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }
        })
    }

    fun setOnClickListener(cashBackDashboardAdapter: CashBackDashboardAdapter) {
        cashBackDashboardAdapter.setOnItemClickListener(object : OnItemClickCashBackListenerImpl {
            override fun onItemClickCashBackResubmitListener(position: Int) {
                startActivity(Intent(this@MyCashBackPageFragment.requireActivity(), RequestCashBackActivity::class.java)
                        .putExtra("id", cashBackDashboardAdapter.list[position].id)
                        .putExtra("usersVoucherId", cashBackDashboardAdapter.list[position].cashback_id?.toInt()))
            }

            override fun onItemClickCashBackListener(position: Int) {
                // Cr6 stagging release
                startActivity(Intent(activity, CashbackHistoryDetailActivity::class.java)
                        .putExtra("id", cashBackDashboardAdapter.list[position].cashback_id))
            }

            override fun onItemClickListener(position: Int) {
                cashBackDashboardAdapter.list[position].isClicked = !cashBackDashboardAdapter.list.get(position).isClicked
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unregisterCashBackDashBoardListener(this)
    }

    override fun onStart() {
        super.onStart()
        model.refresh()
        AppEvent.notifyRefreshCashBackOverView()
    }

    override fun onRefreshCashBackDashBoard(page: Int) {
        model.refresh()
        currentPage = page
    }

    private fun initPullToRefresh() {
        srlCashBackDashBoard?.setOnRefreshListener {
            model.refresh()
            AppEvent.notifyRefreshCashBackOverView()
        }
    }
}