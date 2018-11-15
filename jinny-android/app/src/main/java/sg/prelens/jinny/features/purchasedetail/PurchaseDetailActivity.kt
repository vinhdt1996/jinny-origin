package sg.prelens.jinny.features.purchasedetail

import android.view.View
import kotlinx.android.synthetic.main.layout_toolbar.*
import sg.prelens.jinny.R
import sg.prelens.jinny.base.BaseActivity

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/22/18<br>.
 */
class PurchaseDetailActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutId(): Int {
        return R.layout.activity_purchase_detail
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
        setTitle(getString(R.string.purchase_detail), 0)
        setBackgroundToolbar(0)
        ivBack?.apply {
            setOnClickListener(this@PurchaseDetailActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}