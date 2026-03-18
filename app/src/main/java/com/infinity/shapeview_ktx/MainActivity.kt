package com.infinity.shapeview_ktx

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.genesys.shape.layout.ShapeConstraintLayout
import com.genesys.shape.layout.ShapeFrameLayout
import com.genesys.shape.view.ShapeButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<ShapeButton>(R.id.btnPrimary).setOnClickListener {
            Toast.makeText(this, "Primary Button clicked!", Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeButton>(R.id.btnRipple).setOnClickListener {
            Toast.makeText(this, "Ripple Button clicked!", Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeButton>(R.id.btnOutline).setOnClickListener {
            Toast.makeText(this, "Outline Button clicked!", Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeFrameLayout>(R.id.rippleFrame).setOnClickListener {
            Toast.makeText(this, "ShapeFrameLayout tapped!", Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeConstraintLayout>(R.id.rippleConstraint).setOnClickListener {
            Toast.makeText(this, "ShapeConstraintLayout tapped!", Toast.LENGTH_SHORT).show()
        }
    }
}