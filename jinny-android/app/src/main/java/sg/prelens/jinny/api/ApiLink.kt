package sg.prelens.jinny.api

import android.arch.lifecycle.LiveData
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import sg.prelens.jinny.models.*
import sg.prelens.jinny.repositories.ShareDealResponse
import sg.prelens.jinny.utils.ApiResponse

interface ApiLink {
    @GET("/users")
    fun getUsers(@Query("page") page: Int, @Query("limit") limit: Int): Call<UserResponse>

    @POST("/api/v1/sessions/sign_in")
    @FormUrlEncoded
    fun signIn(@Field("email") email: String, @Field("password") password: String
               , @Field("fcm_token") token: String, @Field("device_id") deviceId: String?): Call<SignInResponse>

    @POST("/api/v1/users/sign_up")
    @FormUrlEncoded
    fun signUp(@Field("email") email: String, @Field("password") password: String
               , @Field("fcm_token") token: String): Call<SignUpResponse>

    @POST("/api/v1/users/forgot_password")
    @FormUrlEncoded
    fun forgotPassword(@Field("email") email: String): Call<ForgotPasswordResponse>

    @GET("/api/v1/memberships")
    fun getMemberships(): LiveData<ApiResponse<MembershipResponse>>

    @GET("/api/v1/memberships/{id}")
    fun getMembershipDetail(@Path("id") id: Int): Call<MembershipDetailResponse>

    @DELETE("/api/v1/memberships/{id}")
    fun removeMembership(@Path("id") id: Int): Call<RemoveMembershipResponse>

    @GET("/api/v1/merchants")
    fun getMerchants(@Query("page") page: Int, @Query("per_page") limit: Int, @Query("keyword") keyword: String): Call<MerchantResponse>

    @GET("/api/v1/merchants/{id}/branches")
    fun getMerchantsBranch(@Path("id") id: Int, @Query("page") page: Int, @Query("per_page") limit: Int): Call<MerchantBranchResponse>

    @HTTP(method = "DELETE", path = "/api/v1/sessions/sign_out", hasBody = true)
    @FormUrlEncoded
    fun logoutAccount(@Field("fcm_token") token: String): Call<LogoutResponse>

    @PUT("/api/v1/users/change_password")
    @FormUrlEncoded
    fun changePassword(@Field("current_password") currentPassword: String, @Field("new_password") newPassword: String): Call<ChangePasswordResponse>

    @GET("/api/v1/vouchers")
    fun getPromotions(@Query("keyword") keyword: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order") order: String): Call<PromotionResponse>

    @GET("/api/v1/vouchers/starred")
    fun getStarredPromotions(@Query("keyword") keyword: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order") order: String): Call<PromotionResponse>

    @GET("/api/v1/vouchers/archived")
    fun getArchivedPromotions(@Query("keyword") keyword: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order") order: String): Call<PromotionResponse>

    @GET("/api/v1/vouchers/{id}")
    fun fetchPromotionDetail(@Path("id") id: String, @Query("users_voucher_id") users_voucher_id: Int): Call<PromotionDetailResponse>

    @PUT("/api/v1/vouchers/{id}/bookmark") //asdasdasdasdsad
    fun bookmarkVoucher(@Path("id") id: String, @Query("users_voucher_id") users_voucher_id: Int): Call<AddBookmarkResonse>

    @PUT("/api/v1/vouchers/{id}/archive")
    fun archiveVoucher(@Path("id") id: String, @Query("users_voucher_id") users_voucher_id: Int): Call<DefaultResonse>

    @GET("/api/v1/vouchers/redeemed")
    fun getRedeemedVouchers(@Query("keyword") keyword: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order") order: String): Call<PromotionResponse>

    @PUT("/api/v1/vouchers/{id}/cashback")
    @Multipart
    fun requestCashBack(@Path("id") id: String, @Part("users_voucher_id") users_voucher_id: Int, @Part cashback_image: MultipartBody.Part): Call<DefaultResonse>

    @DELETE("/api/v1/vouchers/{id}")
    fun removeVoucher(@Path("id") id: String, @Query("users_voucher_id") users_voucher_id: Int): Call<RemoveVoucherResponse>

    @POST("/api/v1/vouchers/{id}/redeem")
    fun redeemVoucher(@Path("id") id: String, @Query("users_voucher_id") users_voucher_id: Int): Call<PromotionDetailResponse>

    @POST("/api/v1/memberships")
    @FormUrlEncoded
    fun addBarcode(@Field("code") code: String, @Field("merchant_id") merchantID: Int): Call<MembershipDetailResponse>

    @POST("/api/v1/vouchers")
    @FormUrlEncoded
    fun addQrCode(@Field("key") code: String): Call<VoucherResponse>

    @POST("/api/v1/memberships/{id}/toggle_bookmark")
    fun addBookmark(@Path("id") id: Int): Call<AddBookmarkResonse>

    @GET("/api/v1/residentail_regions")
    fun getRegions(): Call<RegionList>

    @PUT("/api/v1/users/update_profile")
    @FormUrlEncoded
    fun updateProfile(@Field("email") email: String?,
                      @Field("dob") dob: String?, @Field("gender") gender: String?,
                      @Field("residential_region_id") residentialRegionId: Int?): Call<UpdateProfileUser>

    @GET("/api/v1/users/my_profile")
    fun getMyProfile(): Call<MyProfile>

    @PUT("/api/v1/users/change_token")
    @FormUrlEncoded
    fun changeToken(@Field("old_fcm_token") oldToken: String,
                    @Field("new_fcm_token") newToken: String): Call<ChangeFirebaseResponse>

    @POST("/api/v1/users/sign_up_guest")
    @FormUrlEncoded
    fun signUpGuest(@Field("device_id") deviceID: String, @Field("fcm_token") token: String): Call<SkipSignUpResponse>

    @PUT("/api/v1/users/setup_profile")
    @FormUrlEncoded
    fun setUpProfileGuest(@Field("email") email: String?,
                          @Field("password") dob: String?): Call<SetUpProfileGuest>

    @GET("/api/v1/cashbacks/cashbacks_information")
    fun fetchCashBackInformation(): LiveData<ApiResponse<CashBackOverViewResponse>>

    @GET("/api/v1/cashbacks/cashbacks_activity")
    fun fetchCashBackHistory(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<CashBackHistoryResponse>

    @GET("/api/v1/cashbacks/withdrawals_history")
    fun fetchWithdrawalHistory(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<CashBackHistoryResponse>

    @GET("/api/v1/cashbacks/vouchers_purchased")
    fun fetchVouchersHistory(@Query("page") page: Int, @Query("per_page") perPage: Int): Call<CashBackHistoryResponse>

    @POST("/api/v1/bank_informations")
    @FormUrlEncoded
    fun addBankAccount(@Field("holder_name") holder_name: String?, @Field("bank_name") bank_name: String?,
                       @Field("account_number") account_name: String?): Call<BankInformationResponse>

    @GET("/api/v1/cashbacks/purchase_voucher")
    fun fetchPurchasedVoucherDetail(@Query("voucher_id") voucher_id: String): Call<VoucherPurchaseDetailResponse>

    @GET("/api/v1/cashbacks/redeem_cashback")
    fun getBankAccountResult(): Call<RedeemCashBackResponse>

    @GET("/api/v1/bank_informations/{id}")
    fun getBankInformation(@Path("id") id: Int?): Call<BankInformationResponse>

    @POST("/api/v1/cashbacks/withdraw")
    @FormUrlEncoded
    fun postWithDrawConformation(@Field("bank_information_id") bank_information_id: String?, @Field("amount") amount: String?): Call<DefaultResonse>

    @PUT("/api/v1/bank_informations/{id}")
    @FormUrlEncoded
    fun editBankAccount(@Path("id") id: Int?, @Field("holder_name") holder_name: String?, @Field("bank_name") bank_name: String?,
                        @Field("account_number") account_name: String?): Call<DefaultResonse>

    @DELETE("/api/v1/bank_informations/{id}")
    fun removeBankAccount(@Path("id") id: String?): Call<DefaultResonse>

    @POST("/api/v1/cashbacks/purchase_physical")
    @FormUrlEncoded
    fun postMaillingAddress(@Field("voucher_id") voucher_id: String?, @Field("name") name: String?,
                            @Field("street") street: String?, @Field("floor") floor: String?,
                            @Field("postal_code") postal_code: String?, @Field("remembered") remembered: Boolean?): Call<DefaultResonse>

    @PUT("/api/v1/cashbacks/mailing_address")
    @FormUrlEncoded
    fun putMaillingAddress(@Field("voucher_id") voucher_id: String?, @Field("name") name: String?,
                           @Field("street") street: String?, @Field("floor") floor: String?,
                           @Field("postal_code") postal_code: String?, @Field("remembered") remembered: Boolean?): Call<DefaultResonse>

    @GET("/api/v1/cashbacks/mailing_address")
    fun getMaillingAddressInfo(): Call<MaillingAddressResponse>

    @POST("/api/v1/cashbacks/purchase")
    @FormUrlEncoded
    fun postMaillingPurchase(@Field("voucher_id") voucher_id: String?): Call<DefaultResonse>

    @GET("/api/v1/notifications_setting")
    fun getNotificationSetting(): Call<SettingNotificationResponse>

    @PUT("/api/v1/notifications_setting")
    @FormUrlEncoded
    fun putNotificationSetting(@Field("push_notification") push_notification: Boolean,
                               @Field("store_discount_alert") store_discount_alert: Boolean,
                               @Field("voucher_expiry") voucher_expiry: Boolean,
                               @Field("days_to_remind") days_to_remind: Int?): Call<SettingNotificationResponse>

    @GET("/api/v1/cashbacks/cashback_detail/{id}")
    fun getCashBackHistoryDetail(@Path("id") id: String?): Call<CashBackHistoryDetailReponse>

    @PUT("/api/v1/vouchers/{id}/add_deal")
    fun putShareDeal(@Path("id") id: String?): Call<ShareDealResponse>

    @GET("/api/v1/bank_informations/bank_name_list")
    fun getBankName(): Call<BankNameRespone>
}