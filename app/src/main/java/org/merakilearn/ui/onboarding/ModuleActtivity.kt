package org.merakilearn.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_module_acttivity.categoryListRv
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.datasource.adapter.CategoryAdapter
import org.merakilearn.datasource.network.model.LoginResponseC4CA
import org.merakilearn.expandablerecyclerviewlist.listener.ExpandCollapseListener
import org.merakilearn.ui.model.Category
import org.merakilearn.ui.model.CategoryList
import org.navgurukul.learn.ui.learn.LearnFragmentViewModel

class ModuleActtivity : AppCompatActivity() {

    private val adapter = CategoryAdapter()
    private val viewModel: LearnFragmentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module_acttivity)

       // viewModel.getC4CAPathways()

        val data = listOf(
            Category("Action", listOf(CategoryList("My Spy"),CategoryList("BloodShot"),CategoryList("Midway"))),
            Category("Drama", listOf(CategoryList("The Godfather"),CategoryList("The Dark Knight"))),
            Category("War", listOf(CategoryList("Apocalypse Now"),CategoryList("Saving Private Ryan")))
        )


        categoryListRv.setHasFixedSize(true)
        categoryListRv.layoutManager = LinearLayoutManager(this)
        adapter.setExpandCollapseListener(object : ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
            }

            override fun onListItemCollapsed(position: Int) {

            }

        })
        categoryListRv.adapter = adapter
        adapter.setExpandableParentItemList(data)
    }
}