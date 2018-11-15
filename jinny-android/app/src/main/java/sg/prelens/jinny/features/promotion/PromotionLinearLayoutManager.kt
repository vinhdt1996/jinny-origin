package sg.prelens.jinny.features.promotion

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class PromotionLinearLayoutManager(context: Context?) : LinearLayoutManager(context){
    override fun isAutoMeasureEnabled(): Boolean  = true
}