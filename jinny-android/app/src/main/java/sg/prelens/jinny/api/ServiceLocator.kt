package sg.prelens.jinny.api

import android.app.Application
import android.content.Context
import android.support.annotation.VisibleForTesting
import sg.prelens.jinny.db.VoucherDB
import sg.prelens.jinny.repositories.*
import sg.prelens.jinny.repositories.inmemory.bypage.InMemoryByPageKeyedRepository
import sg.prelens.jinny.repositories.inmemory.bypage.MerchantBranchInMemoryByPageKeyedRepository
import sg.prelens.jinny.repositories.inmemory.bypage.MerchantInMemoryByPageKeyedRepository
import sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory.CashBackActivityHistoryRepository
import sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory.CashBackHistoryRepository
import sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory.VoucherPurchasedHistoryRepository
import sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory.WithdrawalHistoryRepository
import sg.prelens.jinny.repositories.inmemory.promotion.PromotionInMemoryByPageKeyedRepository
import sg.prelens.jinny.repositories.inmemory.promotion.promotionredeemed.PromotionRedeemedInMemoryByPageKeyRepository
import sg.prelens.jinny.repositories.inmemory.promotionarchived.PromotionArchivedInMemoryByPageKeyedRepository
import sg.prelens.jinny.repositories.inmemory.promotionstarred.PromotionStarredInMemoryByPageKeyedRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Super simplified service locator implementation to allow us to replace default implementations
 * for testing.
 */
interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        private var token: String? = null

        fun reInit(context: Context) {
            synchronized(LOCK) {
                instance = null
                instance = instance(context)
            }
        }

        fun instance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(
                            app = context.applicationContext as Application,
                            useInMemoryDb = false)
                }
                return instance!!
            }
        }

        /**
         * Allows tests to replace the default implementations.
         */
        @VisibleForTesting
        fun swap(locator: ServiceLocator) {
            instance = locator
        }
    }

    //fun getRepository():
    fun getNetworkExecutor(): Executor

    fun getDiskIOExecutor(): Executor
    fun getJApi(): ApiLink
    fun getUploadAPI(): ApiLink
    fun getUserRepository(): UserRepository
    fun getAuthRepository(): AuthRepository
    fun getForgotPasswordRepository(): ForgotPasswordRespository
    fun getMembershipRepository(): MembershipRepository
    fun getMerchantsRepository(): MerchantRepository
    fun deleteAccount(): LogoutRespository
    fun getMembershipDetailRepository(): MembershipDetailRepository
    fun changePasswordRepository(): ChangePasswordRespository
    fun getPromotionRepository(): PromotionRepository
    fun getPromotionStarredRepository(): PromotionRepository
    fun getPromotionArchivedRepository(): PromotionRepository
    fun getPromotionDetailRepository(): PromotionDetailRepository
    fun redeemVoucherRepository(): RedeemVoucherRepository
    fun addBarcodeRepository(): AddBarcodeRespository
    fun addQrCodeRepository(): AddQrCodeRepository
    fun addBookmarkRepository(): AddBookmarkRespository
    fun getMerchantBranchRepository(): MerchantBranchRepository
    fun getRegionRepository(): RegionRespository
    fun updateProfileRepository(): UpdateProfileUserRespository
    fun getMyProfile(): MyProfileRespository
    fun getCashBackResult(): CashBackResultRepository
    fun addBankAccountRepRepository(): AddBankAccountRespository
    fun getBankAccountResult(): BankAccountRepository
    fun getBankInformation(): WithDrawConfirmationRepository
    fun postWithDrawConfirmation(): WithDrawConfirmationRepository
    fun editBankAccount(): AddBankAccountRespository
    fun getCashBackOverView(): CashBackOverViewRepository
    fun getCashBackActivityHistory(): CashBackHistoryRepository
    fun getWithdrawalHistory(): CashBackHistoryRepository
    fun getVouchersHistory(): CashBackHistoryRepository
    fun postMaillingAddress(): MaillingAddressRespository
    fun fetchPurchasedVoucherDetail(): CashBackVoucherDetailRespository
    fun getRedeemVoucherRepository(): PromotionRedeemedInMemoryByPageKeyRepository
    fun getOperationRepository(): PromotionOperationRepository
    fun getSettingRepository(): SettingRepository
    fun filterPageListPromotion(): PromotionRepository.PromotionFilterRepository

    fun getCashBackHistoryDetail(): CashBackHistoryDetailRepository

    fun addShareDeal(): ShareDealRepository
}

/**
 * default implementation of ServiceLocator that uses production endpoints.
 */
open class DefaultServiceLocator(val app: Application, val useInMemoryDb: Boolean) : ServiceLocator {

    // thread pool used for disk access
    @Suppress("PrivatePropertyName")
    private val DISK_IO = Executors.newSingleThreadExecutor()
    @Suppress("PrivatePropertyName")
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    @Suppress("PrivatePropertyName")
    private val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4))
    // thread pool used for network requests
    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(CORE_POOL_SIZE)
    private val uApi by lazy {
        ApiGenerator.create()
    }
    private val uploadApi by lazy {
        ApiGenerator.createUpload()
    }
    private val db by lazy {
        VoucherDB.create(app, useInMemoryDb)
    }

    override fun getNetworkExecutor(): Executor = NETWORK_IO
    override fun getDiskIOExecutor(): Executor = DISK_IO
    override fun getJApi(): ApiLink = uApi
    override fun getUploadAPI(): ApiLink = uploadApi
    override fun getUserRepository(): UserRepository = InMemoryByPageKeyedRepository(getJApi(), getNetworkExecutor())
    override fun getAuthRepository(): AuthRepository = AuthRepository(getJApi())
    override fun getForgotPasswordRepository(): ForgotPasswordRespository = ForgotPasswordRespository(getJApi())
    override fun getMembershipRepository(): MembershipRepository = MembershipRepositoryImpl(getJApi(), getDiskIOExecutor())
    override fun getMerchantsRepository(): MerchantRepository = MerchantInMemoryByPageKeyedRepository(getJApi(), getNetworkExecutor())
    override fun getMembershipDetailRepository(): MembershipDetailRepository = MembershipDetailRepository(getJApi())
    override fun deleteAccount(): LogoutRespository = LogoutRespository(getJApi())
    override fun changePasswordRepository(): ChangePasswordRespository = ChangePasswordRespository(getJApi())
    override fun getPromotionRepository(): PromotionRepository = PromotionInMemoryByPageKeyedRepository(getJApi(), getNetworkExecutor())
    override fun addBarcodeRepository(): AddBarcodeRespository = AddBarcodeRespository(getJApi())
    override fun addQrCodeRepository(): AddQrCodeRepository = AddQrCodeRepository(getJApi())
    override fun addBookmarkRepository(): AddBookmarkRespository = AddBookmarkRespository(getJApi())
    override fun getMerchantBranchRepository(): MerchantBranchRepository = MerchantBranchInMemoryByPageKeyedRepository(getJApi(), getNetworkExecutor())
    override fun getRegionRepository(): RegionRespository = RegionRespository(getJApi())
    override fun updateProfileRepository(): UpdateProfileUserRespository = UpdateProfileUserRespository(getJApi())
    override fun redeemVoucherRepository(): RedeemVoucherRepository = RedeemVoucherRepository(getJApi())
    override fun getMyProfile(): MyProfileRespository = MyProfileRespository(getJApi())
    override fun getPromotionDetailRepository(): PromotionDetailRepository =
            PromotionDetailRepository(getJApi())

    override fun getPromotionStarredRepository(): PromotionRepository = PromotionStarredInMemoryByPageKeyedRepository(getJApi(), getNetworkExecutor())
    override fun getPromotionArchivedRepository(): PromotionRepository = PromotionArchivedInMemoryByPageKeyedRepository(getJApi(), getNetworkExecutor())
    override fun getCashBackResult(): CashBackResultRepository = CashBackResultRepository(getUploadAPI())
    override fun addBankAccountRepRepository(): AddBankAccountRespository = AddBankAccountRespository(getJApi())
    override fun getBankAccountResult(): BankAccountRepository = BankAccountRepository(getJApi())
    override fun getBankInformation(): WithDrawConfirmationRepository = WithDrawConfirmationRepository(getJApi())
    override fun postWithDrawConfirmation(): WithDrawConfirmationRepository = WithDrawConfirmationRepository(getJApi())
    override fun editBankAccount(): AddBankAccountRespository = AddBankAccountRespository(getJApi())
    override fun getCashBackOverView(): CashBackOverViewRepository = CashBackOverViewRepository(getJApi())
    override fun getCashBackActivityHistory(): CashBackHistoryRepository = CashBackActivityHistoryRepository(getJApi(), getNetworkExecutor())
    override fun getWithdrawalHistory(): CashBackHistoryRepository = WithdrawalHistoryRepository(getJApi(), getNetworkExecutor())
    override fun getVouchersHistory(): CashBackHistoryRepository = VoucherPurchasedHistoryRepository(getJApi(), getNetworkExecutor())
    override fun postMaillingAddress(): MaillingAddressRespository = MaillingAddressRespository(getJApi())
    override fun fetchPurchasedVoucherDetail(): CashBackVoucherDetailRespository = CashBackVoucherDetailRespository(getJApi())
    override fun getRedeemVoucherRepository(): PromotionRedeemedInMemoryByPageKeyRepository = PromotionRedeemedInMemoryByPageKeyRepository(getJApi(), getNetworkExecutor())

    override fun getOperationRepository(): PromotionOperationRepository = PromotionOperationRepository(
            getJApi(), getNetworkExecutor()
    )

    override fun getSettingRepository(): SettingRepository = SettingRepository(getJApi())

    override fun filterPageListPromotion(): PromotionRepository.PromotionFilterRepository = PromotionFilter(getNetworkExecutor())

    override fun getCashBackHistoryDetail(): CashBackHistoryDetailRepository = CashBackHistoryDetailRepository(getJApi())

    override fun addShareDeal(): ShareDealRepository = ShareDealRepository(getJApi())
}