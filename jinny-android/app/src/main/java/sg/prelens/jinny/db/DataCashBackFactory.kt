package sg.prelens.jinny.db

import sg.prelens.jinny.models.CashBackInfoModel

class DataCashBackFactory{
    companion object {
        fun getCashBackInfos() : List<CashBackInfoModel> =
                listOf(
                        CashBackInfoModel(
                                0,
                                "Receive awesome deals ",
                                "Discover great deals every week with Jinny.",
                                "cashbackinfopages/How_to_earn_cashback_-_Screen_1_v2.webp"
                        ),
                        CashBackInfoModel(
                                1,
                                "Shop with our deals",
                                "Simply show the promo code during payment to redeem.",
                                "cashbackinfopages/How_to_earn_cashback_-_Screen_2.webp"
                        ),
                        CashBackInfoModel(
                                2,
                                "Submit receipt",
                                "Submit the full receipt of your redeemed deals\nto get cashback.",
                                "cashbackinfopages/How_to_earn_cashback_-_Screen_3.webp"
                        ),
                        CashBackInfoModel(
                                3,
                                "Get real cashback",
                                "Once the purchase is verified, we will credit the cashback\nto your Jinny account in 3 working days.",
                                "cashbackinfopages/How_to_earn_cashback_-_Screen_4.webp"
                        ),
                        CashBackInfoModel(
                                4,
                                "Withdraw cashback",
                                "Add a bank account to withdraw your available cashback.\nThe minimum withdrawal amount is SGD 10.",
                                "cashbackinfopages/How_to_earn_cashback_-_Screen_5.webp"
                        ),
                        CashBackInfoModel(
                                5,
                                "Buy vouchers with cashback",
                                "Or, you can purchase cash vouchers in Jinny\nwith your cashback.",
                                "cashbackinfopages/How_to_earn_cashback_-_Screen_6.webp"
                        )

                )
    }

}