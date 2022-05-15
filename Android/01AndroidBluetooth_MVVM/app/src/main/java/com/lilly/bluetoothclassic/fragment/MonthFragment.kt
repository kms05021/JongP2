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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.lilly.bluetoothclassic.R
import com.lilly.bluetoothclassic.log.LogDB
import com.lilly.bluetoothclassic.log.LogEntity
import java.time.LocalDate
import java.time.LocalTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MonthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthFragment : Fragment(), AdapterView.OnItemSelectedListener {
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
        var view: View = inflater.inflate(R.layout.fragment_month, container, false)

        // db 연결
        db = Room.databaseBuilder(view.context, LogDB::class.java, "LogDB").allowMainThreadQueries().build()

        // only for test
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 2))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 2))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 2))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 3))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusMonths(4), LocalTime.now(), 3))

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
         * @return A new instance of fragment MonthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonthFragment().apply {
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
        barChart.axisLeft.spaceBottom = 2f

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
        xAxis.setLabelCount(12, true)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 11f
        xAxis.granularity = 1f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataToBarChart() {
        var list1: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 1, 1), LocalDate.of(onlyDate.year, 1, 31))
        var list2: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 2, 1), LocalDate.of(onlyDate.year, 2, 28))
        var list3: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 3, 1), LocalDate.of(onlyDate.year, 3, 31))
        var list4: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 4, 1), LocalDate.of(onlyDate.year, 4, 30))
        var list5: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 5, 1), LocalDate.of(onlyDate.year, 5, 31))
        var list6: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 6, 1), LocalDate.of(onlyDate.year, 6, 30))
        var list7: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 7, 1), LocalDate.of(onlyDate.year, 7, 31))
        var list8: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 8, 1), LocalDate.of(onlyDate.year, 8, 31))
        var list9: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 9, 1), LocalDate.of(onlyDate.year, 9, 30))
        var list10: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 10, 1), LocalDate.of(onlyDate.year, 10, 31))
        var list11: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 11, 1), LocalDate.of(onlyDate.year, 11, 30))
        var list12: List<LogEntity> = db.getDao().getByLevelAndMonth(level, LocalDate.of(onlyDate.year, 12, 1), LocalDate.of(onlyDate.year, 12, 31))

        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(0.0f, list1.size.toFloat()))
        entries.add(BarEntry(1.0f, list2.size.toFloat()))
        entries.add(BarEntry(2.0f, list3.size.toFloat()))
        entries.add(BarEntry(3.0f, list4.size.toFloat()))
        entries.add(BarEntry(4.0f, list5.size.toFloat()))
        entries.add(BarEntry(5.0f, list6.size.toFloat()))
        entries.add(BarEntry(6.0f, list7.size.toFloat()))
        entries.add(BarEntry(7.0f, list8.size.toFloat()))
        entries.add(BarEntry(8.0f, list9.size.toFloat()))
        entries.add(BarEntry(9.0f, list10.size.toFloat()))
        entries.add(BarEntry(10.0f, list11.size.toFloat()))
        entries.add(BarEntry(11.0f, list12.size.toFloat()))

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barDataSet)
        var IntFormatter : ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "회"
            }
        }
        data.setValueFormatter(IntFormatter)

        barChart.data = data
        barChart.setFitBars(true)
        barChart.invalidate()
    }

    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            when(value.toInt()) {
                0 -> {
                    return "1월"
                }1 -> {
                    return "2월"
                }2 -> {
                    return "3월"
                }3 -> {
                    return "4월"
                }4 -> {
                    return "5월"
                }5 -> {
                    return "6월"
                }6 -> {
                    return "7월"
                }7 -> {
                    return "8월"
                }8 -> {
                    return "9월"
                }9 -> {
                    return "10월"
                }10 -> {
                    return "11월"
                }11 -> {
                    return "12월"
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