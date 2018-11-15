package sg.prelens.jinny.features.membership

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

open class NoPaddingArrayAdater<T>(context: Context?, layoutId:Int, items: Array<T>?): ArrayAdapter<T>(context, layoutId, items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view  = super.getView(position, convertView, parent)
        view.setPadding(0,view.paddingTop,0,view.paddingBottom)
        return super.getView(position, convertView, parent)
    }
}