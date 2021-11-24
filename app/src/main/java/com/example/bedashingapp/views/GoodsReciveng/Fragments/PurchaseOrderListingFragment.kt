package com.example.bedashingapp.views.GoodsReciveng.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bedashingapp.BaseFragment
import com.example.bedashingapp.BuildConfig
import com.example.bedashingapp.MainActivity
import com.example.bedashingapp.R
import com.example.bedashingapp.data.model.remote.GetPoResponse
import com.example.bedashingapp.data.model.remote.PurchaseOder
import com.example.bedashingapp.utils.Status
import com.example.bedashingapp.utils.getCurrentTime
import com.example.bedashingapp.utils.gone
import com.example.bedashingapp.utils.visible
import com.example.bedashingapp.views.GoodsReciveng.Adapters.PurchaseOrderAdapter
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorker
import com.itextpdf.tool.xml.XMLWorkerFontProvider
import com.itextpdf.tool.xml.XMLWorkerHelper
import com.itextpdf.tool.xml.css.CssFile
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver
import com.itextpdf.tool.xml.html.CssAppliers
import com.itextpdf.tool.xml.html.CssAppliersImpl
import com.itextpdf.tool.xml.html.Tags
import com.itextpdf.tool.xml.parser.XMLParser
import com.itextpdf.tool.xml.pipeline.css.CSSResolver
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_purchase_order_listing.*
import java.io.*


class PurchaseOrderListingFragment : BaseFragment(), View.OnClickListener {

    lateinit var poAdapter: PurchaseOrderAdapter
    private var poList: ArrayList<PurchaseOder> = ArrayList()
    override fun getLayout(): Int {
        return R.layout.fragment_purchase_order_listing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        (context as MainActivity).hideActionIcon()
        (context as MainActivity).checkSessionConnection(
            this,
            getString(R.string.open_po)
        )
        (context as MainActivity).img_action.setOnClickListener(this)
        (context as MainActivity).showActionIcon()

    }

    override fun apiCaller(purpose: String) {
        when (purpose) {
            getString(R.string.poDetails) -> {
                getPurchaseOrdersDetails()
            }
            getString(R.string.open_po) -> {
                getPurchaseOrders()
            }
        }
    }

    private fun checkPermissions(response: GetPoResponse?) {
        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(
                object : MultiplePermissionsListener {
                    @SuppressLint("MissingPermission")
                    override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport?) {
                        when {
                            multiplePermissionsReport!!.areAllPermissionsGranted() -> {
                                createPdf(response!!.value)
                            }
                            multiplePermissionsReport.isAnyPermissionPermanentlyDenied -> {
                                Toast.makeText(context, "Permanent Denied", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else -> {
                                Toast.makeText(context, "Denied", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        permissionToken: PermissionToken?
                    ) {
                        Toast.makeText(context, "Rational", Toast.LENGTH_SHORT).show()
                        permissionToken!!.continuePermissionRequest()
                    }

                }
            )
            .check()
    }

    private fun getPurchaseOrders() {
        (context as MainActivity).mainActivityViewModel.getPO(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getUserBranch(),
            (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar()

                        if (it.data!!.value.isEmpty()) {
                            tv_no_po.visible()
                        } else {
                            poList.addAll(it.data.value)
                            tv_no_po.gone()
                            setRecyclerView()
                        }
                    }
                    Status.LOADING -> {
                        showProgressBar("", "")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        Log.d("error_retrofit", resource.message!!)
                        showToastLong(resource.message)
                    }
                }
            }
        })
    }

    private fun getPurchaseOrdersDetails() {
        (context as MainActivity).mainActivityViewModel.getPODetails(
            (context as MainActivity).sessionManager!!.getBaseURL(),
            (context as MainActivity).sessionManager!!.getSessionId(),
            (context as MainActivity).sessionManager!!.getCompany(),
            (context as MainActivity).sessionManager!!.getUserBranch(),
            (context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()
        ).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        var response = it.data
                        hideProgressBar()
                        checkPermissions(response)
                    }
                    Status.LOADING -> {
                        showProgressBar("", "")
                    }
                    Status.ERROR -> {
                        hideProgressBar()
                        Log.d("error_retrofit", resource.message!!)
                        showToastLong(resource.message)
                    }
                }
            }
        })
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    fun createPdf(model: List<PurchaseOder>) {
        var purchaseOrder: PurchaseOder
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        if (!docsFolder.exists()) {
            docsFolder.mkdir()
            Log.i("Error_createPdf", "Created a new directory for PDF")
        } else {
            if (docsFolder.delete()) {
                docsFolder.mkdir()
            }
        }
        var rowCode = ""
        var count = 1
        for (poIndex in model.indices) {
            purchaseOrder = model[poIndex]

            for (documentIndex in 0 until purchaseOrder.DocumentLines.size) {
                if (purchaseOrder.DocumentLines.get(documentIndex).LineStatus.equals(
                        "bost_Open",
                        true
                    )
                ) {
                    rowCode = """$rowCode<tr>
                <td class="so-grid-td1">$count</td>
                <td class="so-grid-td2">${purchaseOrder.DocNum}</td>
                <td class="so-grid-td2">${purchaseOrder.DocumentLines[documentIndex].ItemCode} <br/></td>
                <td class="so-grid-td3">${purchaseOrder.DocumentLines[documentIndex].Quantity}</td>
                <td class="so-grid-td4">${purchaseOrder.DocumentLines[documentIndex].RemainingOpenQuantity}</td>
                <td class="so-grid-td5">${
                        purchaseOrder.DocumentLines.get(documentIndex).Quantity.toDouble() - purchaseOrder.DocumentLines[documentIndex].RemainingOpenQuantity
                    }</td>
                <td class="so-grid-td5">${purchaseOrder.RequriedDate}</td>
              </tr>
"""
                    count++
                }
            }
        }
        val codes = """<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-18"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>PDF Template</title>
    <link rel="stylesheet" href="dist/css/style.css"/>
  </head>
  <body>
    <div class="wrapper">
      <div class="min-wrapper">
        <table class="header">
          <tbody>
            <tr>
              <td class="header-title">
                <b><h1>Open PO Report</h1></b>
              </td>
              <td class="header-Logo">
                <img src="file:///android_asset/logo.png"></img>
              </td>
              <td class="header-info">
                <table class="header-table">
                  <tbody>
                    <tr>
                      <td class="td-heading"><u>Document Date</u></td>
                      <td class="td-devider">:</td>
                      <td class="td-value">${getCurrentTime("EEE, MMM d, yyyy")}</td>
                    </tr>
                    <tr>
                      <td class="td-heading"><u>Head Office Card Code</u></td>
                      <td class="td-devider">:</td>
                      <td class="td-value">${(context as MainActivity).sessionManager!!.getUserHeadOfficeCardCode()}</td>
                    </tr>
                    <tr>
                      <td class="td-heading"><u>Branch Name</u></td>
                      <td class="td-devider">:</td>
                     
                      <td class="td-value">${(context as MainActivity).sessionManager!!.getUserBranchName()}</td>
                    </tr>
                  </tbody>
                </table>
              </td>
            </tr>
          </tbody>  
        </table>


        <div class="grid">
          <table class="grid-table">
            <thead>
              <tr>
                <th class="so-grid-th1">#</th>
                <th class="so-grid-th1">PO Doc#</th>
                <th class="so-grid-th2">Item Code</th>
                <th class="so-grid-th3">Qty</th>
                <th class="so-grid-th4">Open Qty</th>
                <th class="so-grid-th5">Received Qty</th>
                <th class="so-grid-th5">Required Date</th>
              </tr>
            </thead>
            <tbody>
             $rowCode     </tbody>
          </table>
        </div>

      </div>
    </div>
  </body>
</html> """
        var pdfFile = File(docsFolder.absolutePath, "Open-PO-Report.pdf")
        val output: OutputStream = FileOutputStream(pdfFile)
        val document = Document()
        document.pageSize = PageSize.A4
        val writer = PdfWriter.getInstance(document, output)
        document.open()
        try {
            // get input stream
            val ims: InputStream = requireActivity().resources.assets.open("images/logo.png")


            val bmp = BitmapFactory.decodeStream(ims)
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = com.itextpdf.text.Image.getInstance(stream.toByteArray())
            document.add(image)
        } catch (ex: IOException) {
            Log.e("Error", ex.toString())
        }
        try {
            val `is`: InputStream = ByteArrayInputStream(codes.toByteArray())
            val assetManager: AssetManager = requireActivity().resources.assets
            val inputStream = assetManager.open("style.css")
            val cssResolver: CSSResolver = StyleAttrCSSResolver()
            val cssFile: CssFile = XMLWorkerHelper.getCSS(inputStream)
            cssResolver.addCss(cssFile)
            val fontProvider = XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS)
            fontProvider.register("assets/NotoNaskhArabic-Regular.ttf", BaseFont.IDENTITY_H)
            val cssAppliers: CssAppliers = CssAppliersImpl(fontProvider)

            // HTML
            val htmlContext = HtmlPipelineContext(cssAppliers)
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory())

            // Pipelines
            val pdf = PdfWriterPipeline(document, writer)
            val html = HtmlPipeline(htmlContext, pdf)
            val css = CssResolverPipeline(cssResolver, html)

            // XML Worker
            val worker = XMLWorker(css, true)
            val p = XMLParser(worker)
            p.parse(`is`)
        } catch (e: Exception) {
            Log.d("Error_Parse", "E: $e")
        }
        document.close()
        previewPdf(pdfFile)

    }

    private fun previewPdf(pdfFile: File?) {
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list: List<*> =
            requireContext().packageManager.queryIntentActivities(
                testIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        if (list.isNotEmpty()) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val photoURI = FileProvider.getUriForFile(
                requireActivity(), BuildConfig.APPLICATION_ID + ".provider",
                pdfFile!!
            )
            intent.setDataAndType(photoURI, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } else {
            showToastLong("Pdg Generated")

        }
    }

    private fun setRecyclerView() {
        poAdapter = PurchaseOrderAdapter(
            requireContext(), poList
        )
        rv_purchase_oder.visible()
        rv_purchase_oder.adapter = poAdapter
        rv_purchase_oder.setHasFixedSize(true)
        rv_purchase_oder.layoutManager = LinearLayoutManager(context)
    }

    override fun onClick(view: View?) {
        when (view) {
            (context as MainActivity).img_action -> {
                if (poList.isNotEmpty()) {
                    (context as MainActivity).checkSessionConnection(
                        this,
                        getString(R.string.poDetails)
                    )

                }
            }
        }
    }

}