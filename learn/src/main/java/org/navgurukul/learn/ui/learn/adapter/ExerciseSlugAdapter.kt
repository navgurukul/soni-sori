package org.navgurukul.learn.ui.learn.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import br.tiagohm.markdownview.css.styles.Github
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.databinding.ItemSlugDetailBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


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
        private const val TYPE_MD = "markdown"
        private const val TYPE_PYTHON = "python"
        private const val TYPE_YOUTUBE_VIDEO = "youtube"
        private const val TYPE_IMAGE = "image"
        private const val TAG = "ExerciseSlugAdapter"

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
            TYPE_IMAGE -> {
                initImageView(item, binding)
            }
        }
    }

    private fun initMarkDownContent(
        value: String?,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE

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
        binding.imageView.visibility = View.GONE
        binding.markDownContent.visibility = View.VISIBLE
        val value = item.value
        val content = StringBuilder()
        val gson = Gson()
        try {
            val code = gson.fromJson(gson.toJson(value), PythonCode::class.java).code
            content.append("```python").append("\n").append(code).append("\n").append("```")
        } catch (ex: Exception) {
            Log.e(TAG, "initPythonCodeView: ", ex)
        }

        binding.markDownContent.apply {
            this.addStyleSheet(Github())
            this.loadMarkdown(content.toString())
        }
    }


    private fun initYouTubeView(item: Exercise.ExerciseSlugDetail, binding: ItemSlugDetailBinding) {
        binding.markDownContent.visibility = View.GONE
        binding.imageView.visibility = View.GONE

        binding.youtubeView.visibility = View.VISIBLE

        binding.youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                item.value?.toString()?.let { youTubePlayer.loadVideo(it, 0f) }
            }
        })
    }


    private fun initImageView(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.markDownContent.visibility = View.GONE
        binding.imageView.visibility = View.VISIBLE

        val value = item.value
        val gson = Gson()
        try {
            val url = gson.fromJson(gson.toJson(value), Image::class.java).url
            Glide.with(binding.imageView.context).load(url).into(binding.imageView);
        } catch (ex: Exception) {
            Log.e(TAG, "initImageView: ", ex)
        }
    }

    data class PythonCode(val code: String?, val testCases: Any?)

    data class Image(val url:String?)

}
