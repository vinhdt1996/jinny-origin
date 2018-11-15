package sg.prelens.jinny.features.merchantbranchdetail

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_merchant_branch_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.features.merchantbranch.MerchantBranchDetailAdapter
import sg.prelens.jinny.models.MerchantBranch

class MerchantBranchDetailActivity : AppCompatActivity() {
    private lateinit var merchantBranch: MerchantBranch
    private lateinit var urlLogo: String
    private lateinit var merchantName: String
    private lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_branch_detail)
        urlLogo = intent.extras["logo_url"] as String
        merchantName = intent.extras["merchant_name"] as String
        merchantBranch = intent.extras["merchant_branch"] as MerchantBranch
        glide = Glide.with(this)
        prepareTopBar()
        prepareGui()
        if (merchantBranch.opening_hours.isEmpty()){
            tvWorkingTimeTitle?.visibility = View.GONE
        }else {
            tvWorkingTimeTitle?.visibility = View.VISIBLE
            val merchantBranchDetailAdapter = MerchantBranchDetailAdapter(glide, merchantBranch.opening_hours)
            rvBranchDetail.apply {
                layoutManager = LinearLayoutManager(this@MerchantBranchDetailActivity)
                adapter = merchantBranchDetailAdapter
            }
        }

    }

    private fun prepareTopBar(){
        tvTitle.text = merchantName
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvTitle.visibility = View.VISIBLE
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { onBackPressed() }
        ivLogo.loadFromUrl(urlLogo, glide)
    }

    private fun prepareGui(){
        tvBranchName.text = merchantBranch.name
        tvAddress.text = merchantBranch.address
    }
}