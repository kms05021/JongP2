package com.lilly.bluetoothclassic.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.lilly.bluetoothclassic.R
import com.lilly.bluetoothclassic.log.LogDB
import com.lilly.bluetoothclassic.log.LogEntity
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeekFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeekFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: LogDB

    @RequiresApi(Build.VERSION_CODES.O)
    val onlyDate: LocalDate = LocalDate.now()
    var level: Int = 1

    lateinit var levelSpinner: Spinner
    lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_week, container, false)

        // db 연결
        db = Room.databaseBuilder(view.context, LogDB::class.java, "LogDB").allowMainThreadQueries().build()

        levelSpinner = view.findViewById(R.id.levelSpinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.levels_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            levelSpinner.adapter = adapter
        }
        levelSpinner.onItemSelectedListener = this

        barChart = view.findViewById(R.id.barChart)

        initBarChart()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeekFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeekFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        initBarChart()
        setDataToBarChart()

        super.onResume()
    }



    private fun initBarChart() {
        //hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        barChart.axisRight.isEnabled = false

        //remove legend
        barChart.legend.isEnabled = false

        //remove description label
        barChart.description.isEnabled = false

        //add animation
        barChart.animateY(3000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataToBarChart() {
        var list1: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(7), onlyDate.minusDays(6))
        var list2: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(6), onlyDate.minusDays(5))
        var list3: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(5), onlyDate.minusDays(4))
        var list4: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(4), onlyDate.minusDays(3))
        var list5: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(3), onlyDate.minusDays(2))
        var list6: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(2), onlyDate.minusDays(1))
        var list7: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(level, onlyDate.minusDays(1), onlyDate)

        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(0.0f, list1.size.toFloat()))
        entries.add(BarEntry(1.0f, list2.size.toFloat()))
        entries.add(BarEntry(2.0f, list3.size.toFloat()))
        entries.add(BarEntry(3.0f, list4.size.toFloat()))
        entries.add(BarEntry(4.0f, list5.size.toFloat()))
        entries.add(BarEntry(5.0f, list6.size.toFloat()))
        entries.add(BarEntry(6.0f, list7.size.toFloat()))

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barDataSet)
        barChart.data = data

        barChart.invalidate()
    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            when(index) {
                0 -> {
                    return (onlyDate.dayOfWeek-6).toString().substring(0,3)
                }
                1 -> {
                    return (onlyDate.dayOfWeek-5).toString().substring(0,3)
                }
                2 -> {
                    return (onlyDate.dayOfWeek-4).toString().substring(0,3)
                }
                3 -> {
                    return (onlyDate.dayOfWeek-3).toString().substring(0,3)
                }
                4 -> {
                    return (onlyDate.dayOfWeek-2).toString().substring(0,3)
                }
                5 -> {
                    return (onlyDate.dayOfWeek-1).toString().substring(0,3)
                }
                6 -> {
                    return onlyDate.dayOfWeek.toString().substring(0,3)
                }
            }
            return ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(levelSpinner.getItemAtPosition(p2)) {
            "level 1" -> {
                level = 1
                initBarChart()
                setDataToBarChart()
            }"level 2" -> {
                level = 2
                initBarChart()
                setDataToBarChart()
            }else -> {
                level = 3
                initBarChart()
                setDataToBarChart()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}