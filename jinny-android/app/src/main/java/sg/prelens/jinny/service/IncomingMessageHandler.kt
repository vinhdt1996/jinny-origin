package sg.prelens.jinny.service

import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.MSG_START
import sg.prelens.jinny.constant.MSG_STOP
import sg.prelens.jinny.constant.MSG_UPDATE_MEMBERSHIP
import sg.prelens.jinny.constant.MSG_UPDATE_VOUCHER
import sg.prelens.jinny.features.main.MainActivity
import java.lang.ref.WeakReference

internal class IncomingMessageHandler(activity: MainActivity) : Handler() {
    // Prevent possible leaks with a weak reference.
    private val mainActivity: WeakReference<MainActivity> = WeakReference(activity)

    override fun handleMessage(msg: Message) {
        val mainActivity = mainActivity.get() ?: return
        when (msg.what){
            MSG_UPDATE_VOUCHER -> {
                mainActivity.findViewById<TextView>(R.id.tvNotificationVoucher)
                        .apply {
                            visibility = if (msg.obj.toString().toLong() >0)
                                View.VISIBLE else View.INVISIBLE
                            text = msg.obj.toString()
                        }
            }
            MSG_UPDATE_MEMBERSHIP -> {

            }
            MSG_START ->{

            }
            MSG_STOP ->{

            }
        }
    }


}