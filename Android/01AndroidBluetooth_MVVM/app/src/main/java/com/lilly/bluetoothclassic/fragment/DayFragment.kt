package com.lilly.bluetoothclassic.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
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
 * Use the [DayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db: LogDB

    @RequiresApi(Build.VERSION_CODES.O)
    var onlyDate: LocalDate = LocalDate.now()

    lateinit var picker: DatePicker
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_day, container, false)

        // db 연결
        db = Room.databaseBuilder(view.context, LogDB::class.java, "LogDB").allowMainThreadQueries().build()

        // only for test
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 1))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 2))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 2))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 2))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 3))
//        db.getDao().insertLog(LogEntity(LocalDate.now().minusDays(1), LocalTime.now(), 3))

        picker = view.findViewById(R.id.dpSpinner)
        picker.init(
            picker.year,
            picker.month,
            picker.dayOfMonth,
            DatePicker.OnDateChangedListener {view, year, monthOfYear, dayOfMonth ->
                onlyDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                setDataToPieChart()
            }
        )

        pieChart = view.findViewById(R.id.pieChart)
        initPieChart()

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        setDataToPieChart()

        super.onResume()
    }

    private fun initPieChart() {
        pieChart.setUsePercentValues(false)
        pieChart.description.text = ""
        //hollow pie chart
        pieChart.isDrawHoleEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.setDrawEntryLabels(false)
        //adding padding
        pieChart.setExtraOffsets(20f, 0f, 20f, 20f)
        pieChart.isRotationEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isWordWrapEnabled = true

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataToPieChart() {
        Log.d("DATE : ", onlyDate.year.toString() + onlyDate.month.toString() + onlyDate.dayOfMonth.toString())
        var list1: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(1, onlyDate.minusDays(1), onlyDate)
        var list2: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(2, onlyDate.minusDays(1), onlyDate)
        var list3: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(3, onlyDate.minusDays(1), onlyDate)

        var noOfLvl = ArrayList<PieEntry>()
        noOfLvl.add(PieEntry(list1.size.toFloat(), "1단계"))
        noOfLvl.add(PieEntry(list2.size.toFloat(), "2단계"))
        noOfLvl.add(PieEntry(list3.size.toFloat(), "3단계"))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#4DD0E1"))
        colors.add(Color.parseColor("#FFF176"))
        colors.add(Color.parseColor("#FF8A65"))

        val dataSet = PieDataSet(noOfLvl, "Number Of Levels")
        val data = PieData(dataSet)

        var IntFormatter : ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "회"
            }
        }
        data.setValueFormatter(IntFormatter)
        dataSet.sliceSpace = 3f
        dataSet.colors = colors
        pieChart.data = data
        data.setValueTextSize(15f)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        //create hole in center
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        //add text in center
        pieChart.setDrawCenterText(true);
        pieChart.centerText = "Number Of Levels"

        pieChart.invalidate()
    }
}