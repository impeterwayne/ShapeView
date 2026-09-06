package com.infinity.shapeview_ktx

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.genesys.shape.view.ShapeButton

class ShadowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shadow)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shadowRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ShapeButton>(R.id.btnOpenGeometry).setOnClickListener {
            startActivity(Intent(this, ShadowGeometryActivity::class.java))
        }
    }
}
