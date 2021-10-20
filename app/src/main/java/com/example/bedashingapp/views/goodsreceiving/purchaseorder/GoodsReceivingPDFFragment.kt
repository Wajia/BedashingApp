package com.example.bedashingapp.views.goodsreceiving.purchaseorder

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.R
import com.example.bedashingapp.data.api.ApiHelper
import com.example.bedashingapp.data.api.RetrofitBuilder
import com.example.bedashingapp.data.model.db.ItemEntity
import com.example.bedashingapp.data.model.remote.ItemPO
import com.example.bedashingapp.helper.DateUtilsApp
import com.example.bedashingapp.helper.ViewModelFactory
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.viewmodel.MainActivityViewModel
import com.example.bedashingapp.views.goodsreceiving.purchaseorder.adapters.POPDFAdapter
import kotlinx.android.synthetic.main.fragment_goods_receiving_pdf.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GoodsReceivingPDFFragment : BaseFragment() {
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var poItemsList: ArrayList<ItemPO> = ArrayList()
    private var poItemDetailsList: ArrayList<ItemEntity> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_goods_receiving_pdf
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        mainActivityViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(
                ApiHelper(RetrofitBuilder.getApiService("dynamic ip here")),
                requireActivity().application
            )
        ).get(MainActivityViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getting data
        poItemsList.addAll(mainActivityViewModel.getPOItems().filter { it.QuantityReceived > 0.0 })
        getItemDetailsMasterData()

        tv_vendor_name.text =
            mainActivityViewModel.getSelectedPO()?.Supplier?.SupplierName!![0].FormattedName
        tv_vendor_id.text = mainActivityViewModel.getSelectedPO()?.Supplier?.PartyID
        tv_date.text = DateUtilsApp.getUTCFormattedDateTimeString(
            SimpleDateFormat("dd/MM/yyyy"), Calendar.getInstance().time
        )

        btn_save_pdf.setOnClickListener{
            if(Build.VERSION.SDK_INT >= 29) {
                downloadPreviewAsPDF()
            }else{
                showToastLong("Cannot generate PDF on current android version. (Requires Android 10 or latest)")
            }
        }
    }


    private var adapter: POPDFAdapter? = null
    private fun setRecyclerView() {
        adapter = POPDFAdapter(poItemsList, poItemDetailsList, requireContext())
        rv_po_pdf.adapter = adapter
        rv_po_pdf.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_po_pdf.layoutManager = layoutManager
    }

    private fun getItemDetailsMasterData(){
        var ids: ArrayList<String> = ArrayList()
        for (line in poItemsList) {
            ids.add(line.ProductID)
        }

        mainActivityViewModel.getItemsBarcode(ids).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        rlProgress.visibility = View.GONE
                        poItemDetailsList.clear()
                        poItemDetailsList.addAll(resource.data as ArrayList)
                        setRecyclerView()
                    }
                    Status.LOADING -> {
                        rlProgress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        rlProgress.visibility = View.GONE
                        showToastLong(resource.message!!)
                    }
                }
            }
        })
    }


    //for generating pdf
    private val PERMISSION_EXTERNAL_STORAGE = 101
    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadPreviewAsPDF() {
        //First checking permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_EXTERNAL_STORAGE)
        } else {
            takeScreenshot(scroll)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_EXTERNAL_STORAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeScreenshot(scroll)
                } else {
                    Toast.makeText(requireContext(), "Please grant storage permission in order to proceed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun takeScreenshot(view: View) {
        val now = Date()
        DateFormat.format("dd/MM/yyyy_hh:mm:ss", now)
        try {
            btn_save_pdf.visibility = View.GONE
            // image naming and path  to include sd card  appending name you choose for file
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/TVGApp"
            )
            if (!file.exists())
                file.mkdirs()
            val mPath: String =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/TVGApp/" + now + ".pdf"
            val outputPath = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/TVGApp/" + now + ".pdf"
            ) //change with your path

            val bitmap: Bitmap = getScreenShot(view)
            // Create a PdfDocument with a page of the same size as the image
            val document = PdfDocument()
            val pageInfo: PdfDocument.PageInfo =
                PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page: PdfDocument.Page = document.startPage(pageInfo)

            // Draw the bitmap onto the page
            val canvas: Canvas = page.canvas
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)
            document.writeTo(FileOutputStream(mPath))
            document.close()
            val finalUri: Uri? = copyFileToDownloads(requireContext(), outputPath, now)
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_STREAM, finalUri)
            intent.setDataAndType(finalUri, getMimeTypeForUri(requireContext(), finalUri!!))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Share file"))

            try {
                requireContext().startActivity(Intent.createChooser(intent, "Share"))
            } catch (e: Exception) {
                Toast.makeText(context, "Error opening file", Toast.LENGTH_LONG).show()
            }


        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }

    }


    //Kitkat or above
    fun getMimeTypeForUri(context: Context, finalUri: Uri): String =
        DocumentFile.fromSingleUri(context, finalUri)?.type ?: "application/octet-stream"


    private fun copyFileToDownloads(context: Context, downloadedFile: File, date: Date): Uri? {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$date.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val authority = "${context.packageName}.provider"
            val destinyFile = File(Environment.DIRECTORY_DOWNLOADS, "$date.pdf")
            FileProvider.getUriForFile(context, authority, destinyFile)
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream =
                    BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }
    }

    private fun getScreenShot(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(
            scroll.getChildAt(0).width,
            scroll.getChildAt(0).height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

}