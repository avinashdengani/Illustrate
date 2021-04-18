package com.example.illustrate

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.azeesoft.lib.colorpicker.ColorPickerDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


//import top.defaults.colorpicker.ColorPickerPopup

class MainActivity : AppCompatActivity() {

    private var brushSizeButtons:Int = 0
    var currentColor = Color.BLACK

    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 100
    }
    private  lateinit var drawingView: DrawingBoardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById<DrawingBoardView>(R.id.drawingView)
        drawingView.setBrushSize(6.0F)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY) {
                try{
                    if(data!!.data != null){
                        val imageBackground = findViewById<ImageView>(R.id.imgBackground)
                        imageBackground.setImageURI(data!!.data)
                    }else{
                        Toast.makeText(
                                this,
                                "Error parsing the image",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    public fun onShowBrushDialog(view: View){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush)
        brushDialog.setTitle("Select brush Size")


        val smallBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnSmallBrush)
        val mediumBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnMediumBrush)
        val largeBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnLargeBrush)
        val extraLargeBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnExtraLargeBrush)


        smallBrushBtn.setBackgroundResource(R.drawable.default_brushsize_border)
        mediumBrushBtn.setBackgroundResource(R.drawable.default_brushsize_border)
        largeBrushBtn.setBackgroundResource(R.drawable.default_brushsize_border)
        extraLargeBrushBtn.setBackgroundResource(R.drawable.default_brushsize_border)

        if(brushSizeButtons == 1){
            smallBrushBtn.setBackgroundResource(R.drawable.selected_brushsize_border)
        }else if(brushSizeButtons == 2){
            mediumBrushBtn.setBackgroundResource(R.drawable.selected_brushsize_border)
        }
        else if(brushSizeButtons == 3){
            largeBrushBtn.setBackgroundResource(R.drawable.selected_brushsize_border)
        }else if(brushSizeButtons == 4){
            extraLargeBrushBtn.setBackgroundResource(R.drawable.selected_brushsize_border)
        }

        brushDialog.show()

        smallBrushBtn.setOnClickListener{
            drawingView.setBrushSize(4.0F)
            brushSizeButtons = 1
            brushDialog.dismiss()
        }

        mediumBrushBtn.setOnClickListener{
            drawingView.setBrushSize(8.0F)
            brushSizeButtons = 2
            brushDialog.dismiss()
        }


        largeBrushBtn.setOnClickListener{
            drawingView.setBrushSize(12.0F)
            brushSizeButtons = 3
            brushDialog.dismiss()
        }


        extraLargeBrushBtn.setOnClickListener{
            drawingView.setBrushSize(16.0F)
            brushSizeButtons = 4
            brushDialog.dismiss()
        }

    }
    public  fun onShowColorDialog(view: View){
//        ColorPickerPopup
//            .Builder(this)
//            .initialColor(Color.BLACK) // Set initial color
//            .enableBrightness(true) // Enable brightness slider or not
//            .enableAlpha(true) // Enable alpha slider or not
//            .okTitle("Choose")
//            .cancelTitle("Cancel")
//            .showIndicator(true)
//            .showValue(true)
//            .build()
//            .show(view, object : ColorPickerPopup.ColorPickerObserver() {
//                override fun onColorPicked(color: Int) {
//                    drawingView.setColor(color)
//                }
//                fun onColor(color: Int, fromUser: Boolean) {}
//            })

        val colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this)
        colorPickerDialog.setBackgroundColor(Color.WHITE)
        colorPickerDialog.setHexaDecimalTextColor(currentColor);
        colorPickerDialog.setOnColorPickedListener { color, hexVal ->
            currentColor = color
            drawingView.setColor(color)
        }
        colorPickerDialog.show();
    }

    public fun openGallery(view: View){
        if(isReadStorageAllowed()){
            //open the gallery
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, GALLERY)
            //read the image
            //set it as background of the lowest layer
        }else{
            requestStoragePermission()
        }
    }
    public fun onRedo(view: View){
        drawingView.redo()
    }

    public fun onUndo(view:View){
        drawingView.undo()
    }
    public  fun onSave(view: View){
        if(isWriteStorageAllowed()) {
            val bitmap = getBitmapFromView(findViewById(R.id.drawingViewContainer))
            SaveBitmapAsyncTask(bitmap).execute()
        }else{
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE),
        STORAGE_PERMISSION_CODE
        )
    }

    private fun isReadStorageAllowed():Boolean {
        return  ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
    private fun isWriteStorageAllowed():Boolean {
        return  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun getBitmapFromView(view: View) :Bitmap{
        val bitmap = Bitmap.createBitmap(view.width,view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgImage = view.background

        if(bgImage != null){
            bgImage.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)
        return bitmap
    }

    private inner class  SaveBitmapAsyncTask(val bitmap:Bitmap) :AsyncTask< Any, Void, String>(){
        private lateinit var  progressDialog:Dialog
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result = ""
            if(bitmap!=null){
                try{
                    val filename = "illustrate-" + (System.currentTimeMillis()/100) + ".png"
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    val basePath = externalCacheDir!!.absoluteFile.toString()
                    val filePath = basePath + File.separator + filename
                    val file = File(filePath)

                    val fos = FileOutputStream(file)
                    fos.write(bytes.toByteArray())
                    fos.close()

                    result = file.absolutePath
                }catch(e: IOException){
                    e.printStackTrace()
                    Toast.makeText(
                            applicationContext,
                            "Failed to save image :( \nTry Again!",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return  result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
            if(!result!!.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "Image have been saved successfully!",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
        private fun showProgressDialog() {
            progressDialog = Dialog(this@MainActivity)
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.show()
        }
        private fun hideProgressDialog() {
            if(progressDialog != null){
                progressDialog.dismiss()
            }
        }
    }
}