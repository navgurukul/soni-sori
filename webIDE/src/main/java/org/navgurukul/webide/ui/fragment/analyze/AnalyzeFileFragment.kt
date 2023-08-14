package org.navgurukul.webide.ui.fragment.analyze

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import org.navgurukul.webide.databinding.FragmentAnalyzeFileBinding
import org.navgurukul.webide.extensions.compatColor
import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get
import org.navgurukul.webide.util.project.ProjectManager
import java.io.File
import java.util.*

class AnalyzeFileFragment : Fragment() {

    private lateinit var pieColors: ArrayList<Int>
    private lateinit var projectDir: File
    internal lateinit var activity: FragmentActivity

    private var _binding: FragmentAnalyzeFileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAnalyzeFileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = defaultPrefs(activity)
        val darkTheme = prefs["dark_theme", false]!!

        val lightColor = activity.compatColor(androidx.appcompat.R.color.abc_primary_text_material_light)
        val darkColor = activity.compatColor(androidx.appcompat.R.color.abc_primary_text_material_dark)

        projectDir = File(requireArguments().getString("project_file")!!)
        pieColors = ArrayList()

        for (c in ColorTemplate.VORDIPLOM_COLORS)
            pieColors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS)
            pieColors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS)
            pieColors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS)
            pieColors.add(c)

        pieColors.add(ColorTemplate.getHoloBlue())
        binding.pieChart.apply {
            description.isEnabled = false
            setExtraOffsets(8f, 12f, 8f, 8f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(0x00000000)
            setTransparentCircleColor(if (darkTheme) lightColor else darkColor)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
        }

        var byteSize = 0L
        projectDir.walkTopDown().forEach { byteSize += it.length() }
        binding.pieChart.centerText = ProjectManager.humanReadableByteCount(byteSize)

        binding.pieChart.apply {
            setCenterTextSize(48f)
            setCenterTextColor(if (darkTheme) darkColor else lightColor)
            setDrawCenterText(true)
            setDrawEntryLabels(false)
        }

        setData(false)

        val pieLegend = binding.pieChart.legend
        pieLegend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        pieLegend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        pieLegend.orientation = Legend.LegendOrientation.VERTICAL
        pieLegend.setDrawInside(false)
        pieLegend.xEntrySpace = 0f
        pieLegend.yEntrySpace = 0f
        pieLegend.yOffset = 10f
        pieLegend.formSize = 12f
        pieLegend.textSize = 12f
        pieLegend.typeface = Typeface.DEFAULT_BOLD
        pieLegend.textColor = if (darkTheme) darkColor else lightColor

        binding.switchFile.setOnCheckedChangeListener { _, b ->
            if (b) {
                binding.sizeText!!.animate().alpha(1f)
                binding.countText!!.animate().alpha(0.4f)
                setData(false)
            } else {
                binding.countText!!.animate().alpha(1f)
                binding.sizeText!!.animate().alpha(0.4f)
                setData(true)
            }
        }
    }

    private fun setData(isCount: Boolean) {
        if (isCount) {
            val entries = ArrayList<PieEntry>()
            val files = projectDir.listFiles()
            val filesAndCounts = HashMap<String, Int>()
            files.map { it.extension }
                    .forEach {
                        if (filesAndCounts.containsKey(it)) {
                            filesAndCounts[it] = filesAndCounts[it]!!.plus(1)
                        } else {
                            filesAndCounts[it] = 1
                        }
                    }

            for ((fileType, count) in filesAndCounts) {
                entries.add(PieEntry(count.toFloat(), fileType))
            }

            val pieDataSet = PieDataSet(entries, "")
            pieDataSet.setDrawIcons(false)
            pieDataSet.sliceSpace = 3f
            pieDataSet.selectionShift = 3f
            pieDataSet.colors = pieColors

            val pieData = PieData(pieDataSet)
            pieData.setValueFormatter(DefaultValueFormatter(0))
            pieData.setValueTextColor(-0x7b000000)
            pieData.setValueTextSize(20f)
            pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
            binding.pieChart.data = pieData
        } else {
            val entries = ArrayList<PieEntry>()
            val files = projectDir.listFiles()
            val filesAndCounts = HashMap<String, Long>()
            for (file in files) {
                val extension = file.extension
                if (filesAndCounts.containsKey(extension)) {
                    filesAndCounts[extension] = filesAndCounts[extension]!!.plus(file.length())
                } else {
                    filesAndCounts[extension] = file.length()
                }
            }

            for ((fileType, count) in filesAndCounts) {
                entries.add(PieEntry(count.toFloat(), fileType))
            }

            val pieDataSet = PieDataSet(entries, "")
            pieDataSet.setDrawIcons(false)
            pieDataSet.sliceSpace = 3f
            pieDataSet.selectionShift = 3f
            pieDataSet.colors = pieColors

            val pieData = PieData(pieDataSet)
            pieData.setValueFormatter(SizeValueFormatter())
            pieData.setValueTextColor(-0x7b000000)
            pieData.setValueTextSize(16f)
            pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
            binding.pieChart.data = pieData
        }

        binding.pieChart.highlightValues(null)
        binding.pieChart.invalidate()
        binding.pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad)
    }

    private inner class SizeValueFormatter : IValueFormatter {

        override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String =
                ProjectManager.humanReadableByteCount(value.toLong())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as FragmentActivity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
