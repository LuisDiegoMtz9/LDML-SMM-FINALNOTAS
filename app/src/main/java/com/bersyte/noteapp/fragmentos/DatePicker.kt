package com.bersyte.noteapp.fragmentos

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePicker (val listener: (day:Int, month:Int, year:Int) -> Unit): DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        listener(day, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario: Calendar = Calendar.getInstance()
        val dia: Int = calendario.get(Calendar.DAY_OF_MONTH)
        var mes: Int = calendario.get(Calendar.MONTH)
        val año: Int = calendario.get(Calendar.YEAR)

        return DatePickerDialog(activity as Context, this, año, mes, dia)
    }
}