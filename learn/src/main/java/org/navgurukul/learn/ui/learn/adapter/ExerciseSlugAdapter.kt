package org.navgurukul.learn.ui.learn.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.ItemSlugDetailBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class ExerciseSlugAdapter(callback: (ExerciseSlugDetail) -> Unit) :
    DataBoundListAdapter<ExerciseSlugDetail, ItemSlugDetailBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<ExerciseSlugDetail>() {
            override fun areItemsTheSame(
                oldItem: ExerciseSlugDetail,
                newItem: ExerciseSlugDetail
            ): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ExerciseSlugDetail,
                newItem: ExerciseSlugDetail
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemSlugDetailBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_slug_detail, parent, false
        )
    }

    override fun bind(
        holder: DataBoundViewHolder<ItemSlugDetailBinding>,
        item: ExerciseSlugDetail
    ) {
        val binding = holder.binding
        binding.imageViewPlay.setOnClickListener {
            mCallback.invoke(item)
        }
        binding.typing.setOnClickListener {
            mCallback.invoke(item)
        }
        binding.practiceTyping.setOnClickListener {
            mCallback.invoke(item)
        }
        bindItem(item, binding)
    }

    private fun bindItem(
        item: ExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        when (item) {
            is TextExerciseSlugDetail -> {
                initTextContent(item, binding)
            }
            is CodeExerciseSlugDetail -> {
                initCodeView(item, binding)
            }
            is YoutubeExerciseSlugDetail -> {
                initYouTubeView(item, binding)
            }
            is ImageExerciseSlugDetail -> {
                initImageView(item, binding)
            }
            is TypingExerciseSlugDetail -> {
                initTryTypingView(item, binding)
            }
            is UnknownExerciseSlugDetail -> {
                initUnknown(binding)
            }
        }
    }

    private fun initUnknown(binding: ItemSlugDetailBinding) {
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
        binding.textContent.visibility = View.GONE
    }

    private fun initTextContent(
        item: TextExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE

        binding.textContent.visibility = View.VISIBLE

        binding.textContent.apply {
            this.text = (item.value)
        }
    }


    private fun initCodeView(
        item: CodeExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
        binding.textContent.visibility = View.VISIBLE
        val content = StringBuilder()
        when(item.component) {
            ExerciseSlugDetail.TYPE_JS -> {
                content.append("```javascript").append("\n").append(item.value?.code).append("\n").append("```")
                binding.imageViewPlay.visibility = View.GONE
            }
            ExerciseSlugDetail.TYPE_PYTHON -> {
                content.append("```python").append("\n").append(item.value?.code).append("\n").append("```")
                binding.imageViewPlay.visibility = View.VISIBLE
            }
            else -> {
                content.append("```").append("\n").append(item.value?.code).append("\n").append("```")
                binding.imageViewPlay.visibility = View.GONE
            }
        }

//        binding.textContent.apply {
//            this.loadFromText(content.toString())
//        }
    }


    private fun initYouTubeView(item: YoutubeExerciseSlugDetail, binding: ItemSlugDetailBinding) {
        binding.textContent.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
        binding.youtubeView.visibility = View.VISIBLE

        binding.youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                item.value?.let { youTubePlayer.loadVideo(it, 0f) }
            }
        })
    }


    private fun initImageView(
        item: ImageExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.textContent.visibility = View.GONE
        binding.imageView.visibility = View.VISIBLE
        binding.imageViewPlay.visibility = View.GONE
        binding.relStartTyping.visibility = View.GONE
        binding.relPracticeTyping.visibility = View.GONE
        item.value?.let { url ->
            Glide.with(binding.imageView.context).load(url).into(binding.imageView);
        }
    }

    private fun initTryTypingView(
        item: TypingExerciseSlugDetail,
        binding: ItemSlugDetailBinding
    ) {
        binding.youtubeView.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.textContent.visibility = View.GONE
        binding.imageViewPlay.visibility = View.GONE

        binding.relStartTyping.isVisible = item.component == ExerciseSlugDetail.TYPE_TRY_TYPING
        binding.relPracticeTyping.isVisible = item.component == ExerciseSlugDetail.TYPE_PRACTICE_TYPING
    }

}
