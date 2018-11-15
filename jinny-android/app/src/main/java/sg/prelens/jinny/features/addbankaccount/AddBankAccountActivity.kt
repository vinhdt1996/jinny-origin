package sg.prelens.jinny.features.addbankaccount

import android.app.TaskStackBuilder
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_add_bank_account.*
import kotlinx.android.synthetic.main.activity_add_bank_account.view.*
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import kotlinx.android.synthetic.main.layout_dialog_vertical_mailling.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideKeyboard
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.parseResString
import sg.prelens.jinny.features.redeemcashback.RedeemCashBackActivity
import sg.prelens.jinny.features.withdrawconfirmation.WithDrawConfirmationViewModel
import sg.prelens.jinny.models.BankInformation
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.vinova.trackingtool.model.EventType

class AddBankAccountActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.edtHolderName?.isFocusable = true
        v?.edtHolderName?.isFocusableInTouchMode = true
        v?.edtAccountNumber?.isFocusable = true
        v?.edtAccountNumber?.isFocusableInTouchMode = true
        return false
    }

    private lateinit var addBankAccountViewModel: AddBankAccountViewModel
    private lateinit var getBankViewModel: WithDrawConfirmationViewModel
    private var id: Int? = null
    private var holderName: String = ""
    private var bankName: String = ""
    private var accountNumber: String = ""
    private lateinit var dialogInterface: DialogInterface
    /**
     * if have userId, this will use edit bank api, this make code clearer
     */
    private var isAddBank: Boolean = false

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                holderName = edtHolderName?.text.toString().trim()
//                bankName = tvBankNameSpinner?.text.toString().trim()
                accountNumber = edtAccountNumber?.text.toString().trim()
                if (holderName.isEmpty()) {
                    showDialog("Please input bank account holder name", null)
                } else if (bankName.isEmpty()) {
                    showDialog("Please input bank name", null)
                } else if (accountNumber.isEmpty()) {
                    showDialog("Please input account number", null)
                } else {
                    val bankAccount = BankInformation(id, holderName, bankName, accountNumber, "")
                    if (isAddBank) {
                        addBankAccountViewModel.addBankAccount(bankAccount)
                    } else {
                        addBankAccountViewModel.editBankAccount(id, bankAccount)
                    }
                }
            }
            ivMid -> {
                onBackPressed()
            }
        }
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_account)

        id = intent?.extras?.get("id") as? Int
        if (id == null) isAddBank = true
        val holderName = intent?.extras?.get("holder_name") as? String?

        roomDB = RoomDB.newInstance(this)
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.bank_account_add, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.bank_account_add, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.bank_account_add, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        clContentAccount?.setOnClickListener {
            hideKeyboard()
        }

        edtHolderName?.setOnTouchListener(this)
        edtAccountNumber?.setOnTouchListener(this)
        spnBankName?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                spnBankName?.setSelection(0)
            }

            override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                bankName = adapter?.getItemAtPosition(position)?.toString() ?: return
            }

        }

        btnSave?.setOnClickListener(this)
        ivMid?.setOnClickListener(this)
        tvTitle.text = if (isAddBank)
            R.string.add_bank_account.parseResString()
        else
            R.string.edit_bank_account.parseResString()

        if (holderName != null) {
            edtHolderName?.setText(holderName)
        }
        addBankAccountViewModel = addBankAcount()
        getBankViewModel = getBankAcountViewModel()
        addBankAccountViewModel.addBankAccountLiveData.observe(this, Observer {
            //            toast("Bank account added!")
            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.bank_account_add, "bank_account_id: " + it?.result?.id, this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.bank_account_add, description = "bank_account_id: $id")
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })
            val intent = Intent(this, RedeemCashBackActivity::class.java)
            showDialog("Bank account added!", intent)
        })

        addBankAccountViewModel.editBankAccountLiveData.observe(this, Observer {
            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.bank_account_edit, "bank_account_id: $id", this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.bank_account_edit, description = "bank_account_id: $id")
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })

            val intent = Intent(this, RedeemCashBackActivity::class.java)
            showDialog("Bank account updated!", intent)
        })

        addBankAccountViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it.parseMessage().let {
                    showDialog(it, null)
                }
            }
        })

        if (isAddBank) {
            addBankAccountViewModel.listBankAccountLiveData.observe(this, Observer {
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, it?.result)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnBankName?.adapter = adapter
                spnBankName?.setSelection(it?.result?.indexOf(bankName) ?: 0)
            })
        } else {
            getBankViewModel.bankInformation.observe(this, Observer {
                it?.let {
                    bankName = it.result.bank_name ?: return@let
                    edtAccountNumber.setText(it.result.account_number)
                    addBankAccountViewModel.listBankAccountLiveData.observe(this, Observer {
                        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, it?.result)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spnBankName?.adapter = adapter
                        spnBankName?.setSelection(it?.result?.indexOf(bankName) ?: 0)
                    })
                }
            })
        }



        getBankViewModel.setbankInformationRepository(id ?: 0)
    }

    private fun showDialog(message: String?, intent: Intent?) {
        dialogInterface = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (intent != null) {
                        hideDialog()
                        NavUtils.navigateUpTo(this@AddBankAccountActivity, intent)
                    } else {
                        hideDialog()
                    }
                    isCancelable = false
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    private fun hideDialog() {
        dialogInterface.cancel()
    }

    private fun addBankAcount(): AddBankAccountViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@AddBankAccountActivity).addBankAccountRepRepository()
                    @Suppress("UNCHECKED_CAST")
                    return AddBankAccountViewModel(repo) as T
                }
            })[AddBankAccountViewModel::class.java]

    private fun getBankAcountViewModel(): WithDrawConfirmationViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@AddBankAccountActivity).postWithDrawConfirmation()
                    @Suppress("UNCHECKED_CAST")
                    return WithDrawConfirmationViewModel(repo) as T
                }
            })[WithDrawConfirmationViewModel::class.java]
}

