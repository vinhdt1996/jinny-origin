package sg.prelens.jinny.features.cashbackinfo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_cash_back_info_page.*
import sg.prelens.jinny.R
import sg.prelens.jinny.models.CashBackInfoModel
import java.io.IOException
import java.io.InputStream


class CashBackInfoPageFragment : Fragment() {
    lateinit var data: CashBackInfoModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cash_back_info_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvTitle.text = data.title
        tvDes.text = data.description
        var assetInStream: InputStream? = null
        try {
            assetInStream = activity?.assets?.open(data.imagePath)
            val bit = BitmapFactory.decodeStream(assetInStream)
            ivPage.setImageBitmap(bit)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            assetInStream?.close()
        }

    }

    companion object {
        fun newInstance(data: CashBackInfoModel): CashBackInfoPageFragment {
            val fragment = CashBackInfoPageFragment()
            fragment.data = data
            return fragment
        }
    }

}