package sg.prelens.jinny.features.cashbackinfo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_first_time_info.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.saveFirstTimeCashBack
import sg.prelens.jinny.exts.saveFirstTimeMembership
import sg.prelens.jinny.models.HowToInfo
import sg.prelens.jinny.utils.SharePreference

class HowToFirstTimeInfo : Fragment() {

    private var status: HowToInfo? = null

    companion object {
        fun newInstance(status: HowToInfo): HowToFirstTimeInfo {
            val fragment = HowToFirstTimeInfo()
            fragment.status = status
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first_time_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLetGo?.setOnClickListener {
            val intent = NavUtils.getParentActivityIntent(this@HowToFirstTimeInfo.requireActivity())
            when (status) {
                HowToInfo.Membership -> {
                    activity?.saveFirstTimeMembership(false)
                    SharePreference.getInstance().saveFirstTimeMembership(false)
                    intent?.putExtra("page", 0)
                    NavUtils.navigateUpTo(this@HowToFirstTimeInfo.requireActivity(), intent
                            ?: return@setOnClickListener)
                }
                else -> {
                    activity?.saveFirstTimeCashBack(false)
                    SharePreference.getInstance().saveFirstTimeCashBack(false)
                    intent?.putExtra("page", 1)
                    NavUtils.navigateUpTo(this@HowToFirstTimeInfo.requireActivity(), intent
                            ?: return@setOnClickListener)
                }
            }
        }
    }

}