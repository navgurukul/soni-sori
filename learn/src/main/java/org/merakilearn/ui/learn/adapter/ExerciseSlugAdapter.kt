package org.merakilearn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import br.tiagohm.markdownview.css.styles.Github
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.merakilearn.R
import org.merakilearn.courses.db.models.Exercise
import org.merakilearn.databinding.ItemSlugDetailBinding
import org.merakilearn.ui.common.DataBoundListAdapter


class ExerciseSlugAdapter(callback: (Exercise.ExerciseSlugDetail) -> Unit) :
    DataBoundListAdapter<Exercise.ExerciseSlugDetail, ItemSlugDetailBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Exercise.ExerciseSlugDetail>() {
            override fun areItemsTheSame(
                oldItem: Exercise.ExerciseSlugDetail,
                newItem: Exercise.ExerciseSlugDetail
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: Exercise.ExerciseSlugDetail,
                newItem: Exercise.ExerciseSlugDetail
            ): Boolean {
                return false
            }
        }
    ) {
    companion object {
        private val TYPE_MD = "markdown"
        private val TYPE_PYTHON = ""
        private val TYPE_YOUTUBE_VIDEO = "youtube"
    }

    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemSlugDetailBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_slug_detail, parent, false
        )
    }

    override fun bind(binding: ItemSlugDetailBinding, item: Exercise.ExerciseSlugDetail) {
        binding.root.setOnClickListener {
            mCallback.invoke(item)
        }
        bindItem(item, binding)
    }

    private fun bindItem(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        when (item.type) {
            TYPE_MD -> {
                initMarkDownContent(item.value?.toString(), binding)
            }
            TYPE_PYTHON -> {
                initPythonCodeView(item, binding)
            }
            TYPE_YOUTUBE_VIDEO -> {
                initYouTubeView(item, binding)
            }
            else -> {
                initDefaultView(item, binding)
            }
        }
    }

    private fun initMarkDownContent(
        value: String?,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.pythonCodeView.visibility = View.GONE
        binding.defaultTextView.visibility = View.GONE

        binding.markDownContent.visibility = View.VISIBLE

        binding.markDownContent.apply {
            this.addStyleSheet(Github())
            this.loadMarkdown(value)
        }
    }


    private fun initPythonCodeView(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.defaultTextView.visibility = View.GONE
        binding.markDownContent.visibility = View.GONE

        binding.pythonCodeView.visibility = View.VISIBLE


    }


    private fun initYouTubeView(item: Exercise.ExerciseSlugDetail, binding: ItemSlugDetailBinding) {
        binding.pythonCodeView.visibility = View.GONE
        binding.markDownContent.visibility = View.GONE
        binding.defaultTextView.visibility = View.GONE

        binding.youtubeView.visibility = View.VISIBLE

        binding.youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                item.value?.toString()?.let { youTubePlayer.loadVideo(it, 0f) }
            }
        })
    }


    private fun initDefaultView(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.pythonCodeView.visibility = View.GONE
        binding.markDownContent.visibility = View.GONE

        binding.defaultTextView.visibility = View.VISIBLE
        binding.defaultTextView.text = item.value.toString()

    }

}