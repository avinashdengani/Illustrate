package com.example.illustrate

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.azeesoft.lib.colorpicker.ColorPickerDialog


//import top.defaults.colorpicker.ColorPickerPopup

class MainActivity : AppCompatActivity() {
    private  lateinit var drawingView: DrawingBoardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById<DrawingBoardView>(R.id.drawingView)
        drawingView.setBrushSize(6.0F)
    }

    public fun onShowBrushDialog(view: View){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush)
        brushDialog.setTitle("Select brush Size")

        brushDialog.show()

        val smallBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnSmallBrush)
        smallBrushBtn.setOnClickListener{
            drawingView.setBrushSize(5.0F)
            brushDialog.dismiss()
        }

        val mediumBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnMediumBrush)
        mediumBrushBtn.setOnClickListener{
            drawingView.setBrushSize(10.0F)
            brushDialog.dismiss()
        }

        val largeBrushBtn = brushDialog.findViewById<ImageButton>(R.id.btnLargeBrush)
        largeBrushBtn.setOnClickListener{
            drawingView.setBrushSize(15.0F)
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
        colorPickerDialog.setOnColorPickedListener { color, hexVal ->

            drawingView.setColor(color)
        }
        colorPickerDialog.setHexaDecimalTextColor(Color.parseColor("#ffffff"));
        colorPickerDialog.show();
    }
}