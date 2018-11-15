package sg.prelens.jinny.features.voucherbranch

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_screen_slide_voucher_item_page.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.withArguments


class VoucherBranchPageFragment : Fragment() {
    private lateinit var glide: RequestManager

    companion object {
        fun newInstance(imageUrl: String, position: Int) = VoucherBranchPageFragment().withArguments("image_url" to imageUrl, "position" to position)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_screen_slide_voucher_item_page, container, false)
        val url: String = arguments?.getString("image_url") ?: ""
        glide = Glide.with(this)
        if (url.startsWith("http")) {
            glide.load(url).fitCenter().into(view.ivVoucher)
        }
        return view
    }
}
