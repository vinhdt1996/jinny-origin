package sg.prelens.jinny.features.promotion

interface IOnItemPromotionClickListener {
    fun onItemStarClickListener(position: Int, isBookmarked: Boolean, id_voucher: String, id_userVoucher: Int)
    fun onItemAtchivedClickListener(position: Int, isArchived: Boolean, id_voucher: String, id_userVoucher: Int)
}