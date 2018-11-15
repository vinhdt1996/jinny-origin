package sg.prelens.jinny.features.voucherbranch

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_screen_slide_voucher_pager.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.hideKeyboard


class VoucherBranchPagerActivity : FragmentActivity() {
    private var mPagerAdapter: PagerAdapter? = null
    private lateinit var mImageUrls: ArrayList<String>
    private var mPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_slide_voucher_pager)
        mImageUrls = intent.getStringArrayListExtra("listImage") ?: throw IllegalStateException("Images URL should not be null")
        mPosition = intent.extras["position"] as Int? ?: throw IllegalStateException("position should not be null")
        setupViewPager()
        prepareTopBar()
        vpVoucher.currentItem = mPosition
        setUpGui(mPosition)
    }

    private fun setupViewPager() {
        mPagerAdapter = VoucherBranchPagerAdapter(supportFragmentManager, mImageUrls)
        vpVoucher.adapter = mPagerAdapter
        vpVoucher.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                hideKeyboard()
                setUpGui(position)
            }
        })
    }

    private fun prepareTopBar() {
        ivBack.visibility = View.VISIBLE
        //ivBack.setImageResource(R.drawable.back_white)
        ivBack.setOnClickListener { onBackPressed() }
        ivNext.setOnClickListener({ nextPage() })
        ivPrev.setOnClickListener({ prevPage() })
        //rlToolbar?.background = ContextCompat.getDrawable(this, R.color.black)
        //tvTitle?.gone()
    }

    private fun nextPage() {
        vpVoucher.currentItem += 1
    }

    private fun prevPage() {
        vpVoucher.currentItem -= 1
    }

    private fun setUpGui(position: Int) {
        if (position <= 0) ivPrev.visibility = View.GONE
        else ivPrev.visibility = View.VISIBLE
        if (position >= mImageUrls.size - 1) ivNext.visibility = View.GONE
        else ivNext.visibility = View.VISIBLE
    }
}