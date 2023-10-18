package org.merakilearn.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.merakilearn.R
import org.merakilearn.datasource.adapter.CategoryAdapter

class ModuleActtivity : AppCompatActivity() {

    private val adapter = CategoryAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module_acttivity)
    }
}