package com.infinity.shapeview_ktx

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.genesys.shape.layout.ShapeConstraintLayout
import com.genesys.shape.layout.ShapeFrameLayout
import com.genesys.shape.view.ShapeImageView

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mediaRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
    }

    private fun setupListeners() {
        findViewById<ShapeImageView>(R.id.ivRounded).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_rounded_clicked), Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeImageView>(R.id.ivCircle).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_circle_clicked), Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeImageView>(R.id.ivCustom).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_custom_clicked), Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeFrameLayout>(R.id.rippleFrame).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_frame_clicked), Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeConstraintLayout>(R.id.rippleConstraint).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_constraint_clicked), Toast.LENGTH_SHORT).show()
        }
    }
}
