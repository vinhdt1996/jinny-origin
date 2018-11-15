package sg.prelens.jinny.exts

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.utils.PositionedCropTransformation


/**
 * Created by vinova on 1/18/18.
 */

fun ImageView.loadFromUrl(url: String?, glide: RequestManager) {
    if (url?.startsWith("http") == true) {
        this.visibility = View.VISIBLE
        glide.load(url)
                .into(this)
    } else {
        this.visibility = View.GONE
        Glide.clear(this)
    }
}

fun ImageView.loadCropTopFromUrl(url: String?, glide: RequestManager) {
    if (url?.startsWith("http") == true) {
        this.visibility = View.VISIBLE
        glide.load(url)
                .transform(PositionedCropTransformation(context, 0.5f, 0f))
                .into(this)
    } else {
        this.visibility = View.GONE
        Glide.clear(this)
    }
}