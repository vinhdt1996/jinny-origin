package sg.prelens.jinny.features.cashback

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_cashback_kit.*
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.PIC_FILE_NAME
import sg.prelens.jinny.constant.REQUEST_CAMERA_PERMISSION
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.widget.Toast
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.exts.hideLoading
import sg.prelens.jinny.exts.showLoading
import sg.prelens.jinny.features.cashbackresult.CashBackResultActivity
import java.io.FileOutputStream
import java.io.IOException


class CameraKitFragment : Fragment(), View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private val loadingProgress: Dialog by lazy {
        Dialog(this@CameraKitFragment.requireContext())
    }

    private var id: String? = null
    private var usersVoucherId: Int? = null

    /**
     * This is the output file for our picture.
     */
    private lateinit var file: File

    override fun onClick(v: View?) {

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_cashback_kit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission()
//            return
//        }
        setUpView()
        camera.requestPermissions(activity)
    }

    private fun setUpView() {
        btnTakePhoto.setOnClickListener {
            loadingProgress.showLoading()
            camera.captureImage { _, byteArray ->
                var output: FileOutputStream? = null
                try {
                    output = FileOutputStream(file).apply {
                        write(byteArray)
                    }
                } catch (e: IOException) {
                    //Log.e(ImageSaver.TAG, e.toString())
                } finally {
                    output?.let {
                        try {
                            it.close()
                        } catch (e: IOException) {
                        }
                    }
                }
                loadingProgress.hideLoading()
                activity?.startActivity<CashBackResultActivity>(
                        "file" to file.absolutePath,
                        "id" to id,
                        "usersVoucherId" to usersVoucherId)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        file = File(activity?.getExternalFilesDir(null), PIC_FILE_NAME)
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
    }

    override fun onPause() {
        camera.onPause()
        super.onPause()
    }

//    private fun requestCameraPermission() {
////        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
////            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
////        } else {
////            requestPermissions(arrayOf(Manifest.permission.CAMERA,
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
////                    Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
////        }
//        if (checkSelfPermission(this@CameraKitFragment.requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
//        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
////                if (requestCode == REQUEST_CAMERA_PERMISSION) {
////                    if (grantResults.size != 3 ||
////                            grantResults[0] != PackageManager.PERMISSION_GRANTED ||
////                            grantResults[1] != PackageManager.PERMISSION_GRANTED) {
////                        ErrorDialog.newInstance(getString(R.string.request_permission))
////                                .show(childFragmentManager, FRAGMENT_DIALOG)
////                    } else {
////                        setUpView()
////                    }
////                } else {
////                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////                }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                setUpView()
//            } else {
//                onStop()
//            }
//        }
//    }

    companion object {
        private const val FRAGMENT_DIALOG = "dialog"

        @JvmStatic
        fun newInstance(id: String, usersVoucherId: Int): CameraKitFragment {
            val fragment = CameraKitFragment()
            fragment.id = id
            fragment.usersVoucherId = usersVoucherId
            return fragment
        }

    }


}