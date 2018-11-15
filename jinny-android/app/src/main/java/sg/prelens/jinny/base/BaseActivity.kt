package sg.prelens.jinny.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import sg.prelens.jinny.R

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/12/18<br>.
 */
abstract class BaseActivity : AppCompatActivity() {
    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun replaceFragmentId(): Int
    abstract fun isFullScreen(): Boolean
    abstract fun isBackPressed(): Boolean

    private lateinit var dialogInterface: DialogInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(getLayoutId())
        init()
        initViewModel()
    }

    open fun initToolbar() {
        if (isBackPressed()) {
            ivBack?.visibility = View.VISIBLE
        }
    }

    open fun initViewModel() {
    }

    open fun init() {
        initToolbar()
    }

    open fun setTitle(title: String, @ColorRes colorId: Int) {
//        tvTitleWithIcon?.visibility = View.GONE
        tvTitle?.visibility = View.VISIBLE
        tvTitle?.text = title
        tvTitle?.setTextColor(
                ContextCompat.getColor(
                        this,
                        if (colorId == 0) R.color.black else colorId
                )
        )
    }

    open fun setImageTitle() {
//        tvTitleWithIcon?.visibility = View.VISIBLE
        tvTitle?.visibility = View.GONE
    }

    open fun setBackgroundToolbar(@ColorRes colorId: Int) {
        rlToolbar?.setBackgroundColor(ContextCompat.getColor(this, if (colorId == 0) R.color.white else colorId))
    }

    fun getIconDrawable(id: Int): Drawable? {
        return ContextCompat.getDrawable(this, id)
    }

    fun setBookMark() {
        ibBookmark.setImageDrawable(getIconDrawable(R.drawable.star_action_on))
    }

    fun setUnBookmark() {
        ibBookmark.setImageDrawable(getIconDrawable(R.drawable.star_action_off))
    }

     fun showDialog(message: String?) {
        dialogInterface = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    hideDialog()
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

     fun hideDialog() {
        dialogInterface.cancel()
    }
}