package sg.prelens.jinny.utils

import android.support.v4.view.ViewPager
import java.util.concurrent.CopyOnWriteArraySet


object AppEvent {
    private var nestedScrollListeners: Set<NestedScrollChangeListener> = CopyOnWriteArraySet<NestedScrollChangeListener>()
    private var membershipListeners: Set<MembershipListener> = CopyOnWriteArraySet<MembershipListener>()
    private var voucherListeners: Set<VoucherListener> = CopyOnWriteArraySet<VoucherListener>()
    private var cashBackOverViewListeners: Set<CashBackOverViewListener> = CopyOnWriteArraySet<CashBackOverViewListener>()
    private var cashBackDashBoardListeners: Set<CashBackDashBoardListener> = CopyOnWriteArraySet<CashBackDashBoardListener>()
    private var updateBadgeListeners: Set<BadgeListener> = CopyOnWriteArraySet<BadgeListener>()
    private var pageChangeListener: Set<ViewPagerListener> = CopyOnWriteArraySet<ViewPagerListener>()

    fun addViewPagerChangeListener(listener: ViewPagerListener?) {
        if (listener != null)
            pageChangeListener = pageChangeListener.plus(listener)
    }

    fun addNestedScrollListener(listener: NestedScrollChangeListener?) {
        if (listener != null)
            nestedScrollListeners = nestedScrollListeners.plus(listener)
    }

    fun addMembershipListener(listener: MembershipListener?) {
        if (listener != null)
            membershipListeners = membershipListeners.plus(listener)
    }

    fun addVoucherListener(listener: VoucherListener?) {
        if (listener != null)
            voucherListeners = voucherListeners.plus(listener)
    }

    fun addCashBackListener(listener: CashBackOverViewListener?) {
        if (listener != null)
            cashBackOverViewListeners = cashBackOverViewListeners.plus(listener)
    }

    fun addCashBackDashBoardListener(listener: CashBackDashBoardListener?) {
        if (listener != null)
            cashBackDashBoardListeners = cashBackDashBoardListeners.plus(listener)
    }

    fun addUpdateBadgeListener(listener: BadgeListener?) {
        if (listener != null)
            updateBadgeListeners = updateBadgeListeners.plus(listener)
    }

    fun unregisterViewPagerChangeListener(listener: ViewPagerListener?) {
        pageChangeListener = pageChangeListener.minus(listener ?: return)
    }

    fun unregisterNestedScrollListener(listener: NestedScrollChangeListener?) {
        nestedScrollListeners = nestedScrollListeners.minus(listener ?: return)
    }

    fun unregisterMembershipListener(listener: MembershipListener?) {
        membershipListeners = membershipListeners.minus(listener ?: return)
    }

    fun unregisterVoucherListener(listener: VoucherListener?) {
        voucherListeners = voucherListeners.minus(listener ?: return)
    }

    fun unregisterCashBackListener(listener: CashBackOverViewListener?) {
        cashBackOverViewListeners = cashBackOverViewListeners.minus(listener ?: return)
    }

    fun unregisterCashBackDashBoardListener(listener: CashBackDashBoardListener?) {
        cashBackDashBoardListeners = cashBackDashBoardListeners.minus(listener ?: return)
    }

    fun unregisterUpdateBadgeListener(listener: BadgeListener?) {
        updateBadgeListeners = updateBadgeListeners.minus(listener ?: return)
    }

    fun notifyViewPagerChanged(page: Int) {
        for (listener in pageChangeListener)
            listener.onChangeCompleted(page)
    }

    fun notifyNestedScrollChanged(isShow: Boolean) {
        for (listener in nestedScrollListeners)
            listener.onScroll(isShow)
    }

    fun notifyRefreshMembership() {
        for (listener in membershipListeners)
            listener.onRefreshMembership()
    }

    fun notifyRefreshVoucher() {
        for (listener in voucherListeners)
            listener.onRefreshVoucher()
    }

    fun notifyRefreshCashBackOverView() {
        for (listener in cashBackOverViewListeners)
            listener.onRefreshCashBackOverView()
    }

    fun notifyRefreshCashBackDashBoard(page: Int) {
        for (listener in cashBackDashBoardListeners)
            listener.onRefreshCashBackDashBoard(page)
    }

    fun notifyUpdateBadge(size: Int) {
        for (listener in updateBadgeListeners)
            listener.onUpdateBadge(size)
    }
}

interface ViewPagerListener{
    fun onChangeCompleted(page: Int)
}

interface NestedScrollChangeListener{
    fun onScroll(isShow: Boolean)
}
interface MembershipListener {
    fun onRefreshMembership()
}

interface VoucherListener {
    fun onRefreshVoucher()
    fun onRedeemVoucher()
}

interface CashBackOverViewListener {
    fun onRefreshCashBackOverView()
}

interface CashBackDashBoardListener {
    fun onRefreshCashBackDashBoard(page:Int)
}

interface BadgeListener {
    fun onUpdateBadge(size: Int)
}