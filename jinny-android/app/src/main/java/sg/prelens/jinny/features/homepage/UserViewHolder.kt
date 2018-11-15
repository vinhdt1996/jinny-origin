package sg.prelens.jinny.features.homepage

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_user.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.models.User

/**
 * Created by vinova on 1/18/18.
 */
class UserViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {

    fun bind(user: User?) {
        view.apply {
            ivAvatar.loadFromUrl(user?.avatar, glide)
            tvUsername.text = user?.username
        }
    }

    fun update(item: Any?) {
        val update = item as? Bundle
        val avatar = update?.get("avatar") as? String
        if (!avatar.isNullOrEmpty()) {
            view.ivAvatar.loadFromUrl(avatar, glide)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user, parent, false)
            return UserViewHolder(view, glide)
        }
    }

}