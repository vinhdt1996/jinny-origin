package sg.prelens.jinny.widget

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.graphics.Color
import android.support.v7.app.AlertDialog
import sg.prelens.jinny.R
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.FragmentManager

class DialogLoadingFragment : DialogFragment() {

    companion object {

        private var loading: DialogLoadingFragment? = null

        private fun newInstance(): DialogLoadingFragment {
            return DialogLoadingFragment()
        }

        fun showLoadingDialog(fm: FragmentManager?) {
            if (loading == null) {
                loading = DialogLoadingFragment.newInstance()
            }
            loading?.show(fm, "fragment")
        }

        fun hideLoadingDialog() {
            loading?.dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(this@DialogLoadingFragment.requireActivity())
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.layout_loading, null)
        alertDialogBuilder.setView(view)
        val dialog = alertDialogBuilder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}