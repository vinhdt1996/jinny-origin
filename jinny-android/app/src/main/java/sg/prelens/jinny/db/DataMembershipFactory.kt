package sg.prelens.jinny.db

import sg.prelens.jinny.models.CashBackInfoModel

class DataMembershipFactory {
    companion object {
        fun getCashBackInfos(): List<CashBackInfoModel> =
                listOf(
                        CashBackInfoModel(
                                0,
                                "Store memberships",
                                "Store your membership cards digitally on Jinny \n" +
                                        "by simply adding with the + button.",
                                "howToAddMemberShip/Screen_1.webp"
                        ),
                        CashBackInfoModel(
                                1,
                                "Add membership",
                                "Choose from our list of memberships to add.",
                                "howToAddMemberShip/Screen_2.webp"
                        ),
                        CashBackInfoModel(
                                2,
                                "Add by scanning barcode",
                                "Add by scanning the membership barcode on your card.",
                                "howToAddMemberShip/Screen_3.webp"
                        ),
                        CashBackInfoModel(
                                3,
                                "Or add by manual entry",
                                "If there is no barcode, you can add by entering your membership number too.",
                                "howToAddMemberShip/Screen_4.webp"
                        ),
                        CashBackInfoModel(
                                4,
                                "Successfully added",
                                "That's it! You can now show this during purchase to enjoy your membership benefits.",
                                "howToAddMemberShip/Screen_5.webp"
                        )

                )
    }
}