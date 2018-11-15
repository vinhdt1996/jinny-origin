package sg.prelens.jinny.features.cashbackhistorydetail

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_cashback_history_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.exts.getCurrentUser
import sg.prelens.jinny.exts.hideLoading
import sg.prelens.jinny.exts.showLoading
import sg.prelens.jinny.features.cashback.RequestCashBackActivity
import sg.prelens.jinny.models.CashBackHistory
import sg.prelens.jinny.utils.SharePreference
import tech.linjiang.pandora.preference.SharedPref

class CashbackHistoryDetailActivity : AppCompatActivity() {

    private val loadingProgress: Dialog by lazy {
        Dialog(this)
    }

    private lateinit var model: CashBackHistoryDetailViewModel
    private var cashBackId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashback_history_detail)

        model = getModel

        cashBackId = intent?.extras?.getString("id", null)

        loadingProgress.showLoading()
        if (cashBackId != null) {
            model.requestData(cashBackId ?: "")
        }

        tvTitle?.text = getString(R.string.cash_back_detail_new)
        ivBack?.setOnClickListener {
            onBackPressed()
        }

        model.result.observe(this, Observer {
            if (it != null) {
                loadingProgress.hideLoading()
                initView(it)
            }
        })

    }

    fun initView(cashBackHistory: CashBackHistory?) {
        tvCashbackTitle?.text = cashBackHistory?.voucher_description
        tvExpire?.text = cashBackHistory?.submitted
        tvTXN?.text = cashBackHistory?.id
        tvStatus?.text = cashBackHistory?.cashback_status?.capitalize()
        Glide.with(this).load(cashBackHistory?.image?.url?.original).into(ivImageCashBack)
        when (cashBackHistory?.cashback_status) {
            "pending" -> {
                btnResubmit?.visibility = View.GONE
                tvRejectDescription?.visibility = View.GONE
                tvStatus?.setTextColor(ContextCompat.getColor(this, R.color.pending))
            }
            "rejected" -> {
                tvStatus?.setTextColor(ContextCompat.getColor(this, R.color.crimson))
                tvRejectDescription?.visibility = View.VISIBLE
                tvRejectDescription?.text = cashBackHistory.description
                if (cashBackHistory.allow_resubmit) {
                    btnResubmit?.visibility = View.VISIBLE
                } else {
                    btnResubmit?.visibility = View.GONE
                }
            }
            else -> {
                tvRejectDescription?.visibility = View.GONE
                btnResubmit?.visibility = View.GONE
                tvStatus?.setTextColor(ContextCompat.getColor(this, R.color.neon_green))
            }
        }

        btnResubmit?.setOnClickListener {
            startActivity(Intent(this, RequestCashBackActivity::class.java)
                    .putExtra("id", cashBackHistory?.id)
                    .putExtra("usersVoucherId", cashBackHistory?.cashback_id?.toInt()))
        }
    }

    private val getModel: CashBackHistoryDetailViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@CashbackHistoryDetailActivity).getCashBackHistoryDetail()
                return CashBackHistoryDetailViewModel(repo) as T
            }
        })[CashBackHistoryDetailViewModel::class.java]
    }
}
