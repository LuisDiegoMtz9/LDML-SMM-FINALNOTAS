package com.bersyte.noteapp.fragmentos

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePicker (val listener: (hour:Int,minute:Int) -> Unit): DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        listener(hour,minute)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario: Calendar = Calendar.getInstance()
        val hora: Int = calendario.get(Calendar.HOUR_OF_DAY)
        val minutos: Int = calendario.get(Calendar.MINUTE)

        return TimePickerDialog(activity as Context, this, hora, minutos, false)
    }
}