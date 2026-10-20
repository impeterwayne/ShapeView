package com.infinity.shapeview_ktx

import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.infinity.shapeview_ktx.contour.ContourShowcaseView

class ContourActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(0xFFF8FAFC.toInt())
            isFillViewport = true
            addView(ContourShowcaseView(context))
        }

        setContentView(scrollView)

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
