package org.navgurukul.learn.ui.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.network.NetworkDataProvider
import org.navgurukul.learn.courses.network.retrofit.RetrofitClient
import org.navgurukul.learn.courses.network.retrofit.RetrofitDataProvider
import org.navgurukul.learn.courses.repository.CoursesRepositoryImpl

class LearnFragment : Fragment() {

    private lateinit var coursesDatabase: CoursesDatabase
    private lateinit var networkDataProvider: NetworkDataProvider
    private lateinit var learnViewModel: LearnViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Use koin to instantiate these
        coursesDatabase = CoursesDatabase.getDatabase(context)
        networkDataProvider = RetrofitDataProvider(RetrofitClient.client)
        val coursesRepositoryImpl = CoursesRepositoryImpl(coursesDatabase, networkDataProvider)
        learnViewModel = ViewModelProviders.of(this, LearnViewModel.Factory(coursesRepositoryImpl))
            .get(LearnViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}