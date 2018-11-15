package sg.prelens.jinny.features.merchantbranch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_merchant_branches.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.BuildConfig
import sg.prelens.jinny.R
import sg.prelens.jinny.R.id.ivBack
import sg.prelens.jinny.R.id.tvTitle
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.exts.setOnItemClickListener
import sg.prelens.jinny.features.merchantbranchdetail.MerchantBranchDetailActivity

class MerchantBranchActivity : AppCompatActivity() {
    private val id: Int by lazy {
        try {
            intent.extras["merchant_id"] as Int
        } catch (ex: ClassCastException) {
            throw IllegalStateException("Membership id could not be null")
        }
    }
    private  var urlLogo: String? = null
    private  var merchantName: String? = null
    private lateinit var glide: RequestManager
    private val merchantBranchViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@MerchantBranchActivity)
                        .getMerchantBranchRepository()
                @Suppress("UNCHECKED_CAST")
                return MerchantBranchViewModel(id, repo, ServiceLocator.instance(this@MerchantBranchActivity).getNetworkExecutor()) as T
            }
        })[MerchantBranchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_branches)
        urlLogo = intent.extras["logo_url"] as String?
        merchantName = intent.extras["merchant_name"] as String?
        if (urlLogo == null) {
           if(BuildConfig.DEBUG) throw IllegalStateException("urlLogo could not be null")
            else onBackPressed()
        }
        if (merchantName == null) {
            if(BuildConfig.DEBUG)
                throw IllegalStateException("merchantName could not be null")
            else onBackPressed()
        }
        glide = Glide.with(this)
        prepareTopBar()
        initAdapter()
    }

    private fun prepareTopBar(){
        tvTitle.text = merchantName
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvTitle.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { onBackPressed() }
        ivLogo.loadFromUrl(urlLogo, glide)
    }

    private fun initAdapter() {
        val merchantBranchAdapter = MerchantBranchAdapter(glide) {
            //            membershipDetailViewModel.retry()
        }
        rvMerchantBranches?.apply {
            layoutManager = LinearLayoutManager(this@MerchantBranchActivity)
            adapter = merchantBranchAdapter
        }
        rvMerchantBranches?.setOnItemClickListener { _, _, position, _ ->
            val merchant = merchantBranchAdapter.currentList?.get(position)
            startActivity<MerchantBranchDetailActivity>("merchant_name" to merchantName, "logo_url" to urlLogo, "merchant_branch" to merchant)
        }
        merchantBranchViewModel.merchantBranch.observe(this, Observer {
            merchantBranchAdapter.submitList(it)
        })
    }
}