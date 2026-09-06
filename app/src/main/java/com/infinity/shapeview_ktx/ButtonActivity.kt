package com.infinity.shapeview_ktx

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.genesys.shape.view.ShapeButton
import com.genesys.shape.view.ShapeCheckBox

class ButtonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_button)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.buttonRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
    }

    private fun setupListeners() {
        findViewById<ShapeButton>(R.id.btnPrimary).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_primary_clicked), Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeButton>(R.id.btnRipple).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_ripple_clicked), Toast.LENGTH_SHORT).show()
        }

        findViewById<ShapeButton>(R.id.btnGhost).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_ghost_clicked), Toast.LENGTH_SHORT).show()
        }

        val onChipToggle = {
            Toast.makeText(this, getString(R.string.toast_chip_toggled), Toast.LENGTH_SHORT).show()
        }
        findViewById<ShapeCheckBox>(R.id.chipDesign).setOnCheckedChangeListener { _, _ -> onChipToggle() }
        findViewById<ShapeCheckBox>(R.id.chipDevelop).setOnCheckedChangeListener { _, _ -> onChipToggle() }
        findViewById<ShapeCheckBox>(R.id.chipDeploy).setOnCheckedChangeListener { _, _ -> onChipToggle() }

        findViewById<ShapeCheckBox>(R.id.cbStandard).setOnCheckedChangeListener { _, _ ->
            Toast.makeText(this, getString(R.string.toast_checkbox_toggled), Toast.LENGTH_SHORT).show()
        }
    }
}
