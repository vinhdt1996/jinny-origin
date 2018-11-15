package sg.prelens.jinny.models

import java.io.Serializable

class CashBackHistory(val updated_at: String?,
                      val id: String?,
                      val voucher_description: String?,
                      val cashback_amount: String?,
                      val cashback_status: String?,
                      val bank_info: String?,
                      val amount: String?,
                      val status: String?,
                      val title: String?,
                      val description: String?,
                      var isClicked: Boolean = false,
                      var voucher_cashback_type: String?,
                      var submitted: String?,
                      var image: Logo?,
                      var allow_resubmit : Boolean = false,
                      var cashback_id : String?) : Serializable