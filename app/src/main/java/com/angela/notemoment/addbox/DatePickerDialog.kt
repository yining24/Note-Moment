package com.angela.notemoment.addbox

import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.ParseException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.savvi.rangedatepicker.CalendarPickerView
import com.savvi.rangedatepicker.SubTitle
import kotlinx.android.synthetic.main.dialog_datepicker.*
import kotlinx.android.synthetic.main.fragment_add_box.*
import java.util.*



// will add in add box fragment

class DatePickerDialog :  AppCompatDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 10)

        val lastYear = Calendar.getInstance()
        lastYear.add(Calendar.YEAR, -10)

        val calendar: CalendarPickerView = calendar_view
//        val button = findViewById(R.id.get_selected_dates)
        val list = ArrayList<Int>()
        list.add(2)

        calendar.deactivateDates(list)
        val arrayList = ArrayList<Date>()
        try {
            val dateformat = SimpleDateFormat("dd-MM-yyyy")

            val strdate = "22-4-2019"
            val strdate2 = "26-4-2019"

            val newdate = dateformat.parse(strdate)
            val newdate2 = dateformat.parse(strdate2)
            arrayList.add(newdate)
            arrayList.add(newdate2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        calendar.init(
            lastYear.time,
            nextYear.time
//            ("MMMM, YYYY", Locale.getDefault())
        )
            .inMode(CalendarPickerView.SelectionMode.RANGE)
            .withDeactivateDates(list)
            .withSubTitles(getSubTitles())
            .withHighlightedDates(arrayList)

        calendar.scrollToDate(Date())

        return null
    }


    private fun getSubTitles(): ArrayList<SubTitle> {
        val subTitles = ArrayList<SubTitle>()
        val tmrw = Calendar.getInstance()
        tmrw.add(Calendar.DAY_OF_MONTH, 1)
        subTitles.add(SubTitle(tmrw.time, "₹1000"))
        return subTitles
    }
}