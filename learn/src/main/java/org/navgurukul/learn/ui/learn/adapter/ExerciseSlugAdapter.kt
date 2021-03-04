package org.navgurukul.learn.ui.learn.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
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
        const val TYPE_PYTHON = "python"
        private const val TYPE_YOUTUBE_VIDEO = "youtube"
        private const val TYPE_IMAGE = "image"
        const val TYPE_TRY_TYPING = "trytyping"
        const val TYPE_PRACTICE_TYPING = "practicetyping"
        private const val TAG = "ExerciseSlugAdapter"

        fun parsePythonCode(item: Exercise.ExerciseSlugDetail): String? {
            val gson = Gson()
            return try {
                gson.fromJson(gson.toJson(item.value), PythonCode::class.java).code
            } catch (ex: Exception) {
                Log.e(TAG, "initPythonCodeView: ", ex)
                null
            }
        }
    }

    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemSlugDetailBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_slug_detail, parent, false
        )
    }

    override fun bind(
        holder: DataBoundViewHolder<ItemSlugDetailBinding>,
        item: Exercise.ExerciseSlugDetail
    ) {
        val binding = holder.binding
        binding.imageViewPlay.setOnClickListener {
            mCallback.invoke(item)
        }
        binding.typing.setOnClickListener {
            Log.d(TAG, "Type : "+item.type)
            mCallback.invoke(item)
        }
        binding.practiceTyping.setOnClickListener {
            Log.d(TAG, "Type : "+item.type)
            mCallback.invoke(item)
        }
        bindItem(item, binding)
    }

    private fun bindItem(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        Log.d(TAG, "Type : "+item.type)
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
            TYPE_TRY_TYPING -> {
                initTryTypingView(item, binding)
            }
            TYPE_PRACTICE_TYPING -> {
                initPracticeTypingView(item, binding)
            }
        }
    }

    private fun initMarkDownContent(
        value: String?,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE

        binding.markDownContent.visibility = View.VISIBLE

        binding.markDownContent.apply {
            //this.addStyleSheet(Github())
           // this.loadMarkdown(value)
            this.loadFromText(value)
        }
    }


    private fun initPythonCodeView(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        Log.d(TAG, "Value  : "+item.value)
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
        binding.markDownContent.visibility = View.VISIBLE
        binding.imageViewPlay.visibility = View.VISIBLE
        val code = parsePythonCode(item)
        val content = StringBuilder()
        content.append("```python").append("\n").append(code).append("\n").append("```")


        binding.markDownContent.apply {
            //this.addStyleSheet(Github())
            // this.loadMarkdown(content.toString())
            this.loadFromText(content.toString())
        }
    }


    private fun initYouTubeView(item: Exercise.ExerciseSlugDetail, binding: ItemSlugDetailBinding) {
        binding.markDownContent.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
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
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
        val value = item.value
        val gson = Gson()
        try {
            val url = gson.fromJson(gson.toJson(value), Image::class.java).url
            Glide.with(binding.imageView.context).load(url).into(binding.imageView);
        } catch (ex: Exception) {
            Log.e(TAG, "initImageView: ", ex)
        }
    }

    private fun initTryTypingView(
        item: Exercise.ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        Log.d(TAG, "Value  : "+item.value)
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.markDownContent.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.VISIBLE
        binding.relPracticeTyping.visibility = View.GONE
    }

    private fun initPracticeTypingView(
            item: Exercise.ExerciseSlugDetail,
            binding: ItemSlugDetailBinding
    ) {
        Log.d(TAG, "Value  : "+item.value)
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.markDownContent.visibility = View.VISIBLE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.VISIBLE
    }

    data class PythonCode(val code: String?, val testCases: Any?)

    data class Image(val url: String?)

}
