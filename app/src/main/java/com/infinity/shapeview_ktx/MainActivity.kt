package com.infinity.shapeview_ktx

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.genesys.shape.layout.ShapeLinearLayout

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

        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<ShapeLinearLayout>(R.id.cardButtons).setOnClickListener {
            startActivity(Intent(this, ButtonActivity::class.java))
        }

        findViewById<ShapeLinearLayout>(R.id.cardShadows).setOnClickListener {
            startActivity(Intent(this, ShadowActivity::class.java))
        }

        findViewById<ShapeLinearLayout>(R.id.cardShadowGeometry).setOnClickListener {
            startActivity(Intent(this, ShadowGeometryActivity::class.java))
        }

        findViewById<ShapeLinearLayout>(R.id.cardGradients).setOnClickListener {
            startActivity(Intent(this, GradientActivity::class.java))
        }

        findViewById<ShapeLinearLayout>(R.id.cardMedia).setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        findViewById<ShapeLinearLayout>(R.id.cardContour).setOnClickListener {
            startActivity(Intent(this, ContourActivity::class.java))
        }
    }
}