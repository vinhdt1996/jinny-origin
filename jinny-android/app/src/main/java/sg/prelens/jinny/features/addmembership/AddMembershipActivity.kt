package sg.prelens.jinny.features.addmembership

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_add_membership.*
import kotlinx.android.synthetic.main.layout_search_view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideKeyboard
import sg.prelens.jinny.exts.log
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.setOnItemClickListener
import sg.prelens.jinny.features.barcode.BarcodeActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.vinova.trackingtool.model.EventType

class AddMembershipActivity : BaseActivity(), View.OnClickListener, View.OnTouchListener, TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.search_keyword,
                getString(R.string.search_keyword, s.toString()), this@AddMembershipActivity.baseContext)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        log("text input: $s")
//        FirebaseAnalyticsUtil.putActionParameterAnalytics(this, AnalyticConst.search_keyword, AnalyticConst.search_keyword, s?.toString())
        model.filter(s.toString())
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val imm: InputMethodManager = this@AddMembershipActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)
        return false
    }

    private val model: MerchantsViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@AddMembershipActivity)
                        .getMerchantsRepository()
                @Suppress("UNCHECKED_CAST")
                return MerchantsViewModel(repo) as T
            }
        })[MerchantsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGui()
        model.filter()
    }

    private fun initGui() {
        val glide = Glide.with(this)
        val merchantAdapter: MerchantAdapter? = MerchantAdapter(glide) {
            model.retry()
        }
        rvMerchant.apply {
            layoutManager = LinearLayoutManager(this@AddMembershipActivity)
            adapter = merchantAdapter
        }

        model.merchants.observe(this, Observer {
        })

        model.networkState.observe(this, Observer {
            merchantAdapter?.setNetworkState(it)
        })
        model.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage().let {
                    showDialog(it)
                }
            }
        })
        rvMerchant.setOnItemClickListener { _, _, position, _ ->
            this.startActivity<BarcodeActivity>()
        }

//        val searchIcon = svMerchant?.findViewById(android.support.v7.appcompat.R.id.search_mag_icon) as ImageView
//        searchIcon.setImageResource(R.drawable.search)

        edtSearch?.hint = getString(R.string.search_merchant_hint)
        edtSearch.addTextChangedListener(this)

//        edtSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(q: String): Boolean {
//                log("text input: $q")
//                model.filter(q)
//                return false
//            }
//
//            override fun onQueryTextSubmit(q: String): Boolean {
//                return false
//            }
//        })
    }

    override fun init() {
        super.init()
        setTitle(getString(R.string.add_membership_upper), 0)
        setBackgroundToolbar(0)
        ivBack?.apply {
            setOnClickListener(this@AddMembershipActivity)
            visibility = View.VISIBLE
        }

        main_constraint_add_membership.setOnTouchListener(this@AddMembershipActivity)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                hideKeyboard()
                onBackPressed()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_membership
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
}