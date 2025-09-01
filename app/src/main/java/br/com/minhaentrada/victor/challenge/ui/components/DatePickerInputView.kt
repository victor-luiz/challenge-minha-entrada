package br.com.minhaentrada.victor.challenge.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import br.com.minhaentrada.victor.challenge.databinding.ComponentDatePickerInputBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import br.com.minhaentrada.victor.challenge.R

class DatePickerInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentDatePickerInputBinding

    var text: String
        get() = binding.dateEditTextComponent.text.toString()
        set(value) {
            binding.dateEditTextComponent.setText(value)
        }
    var hint: String
        get() = binding.root.hint.toString()
        set(value) {
            binding.root.hint = value
        }

    init {
        binding = ComponentDatePickerInputBinding.inflate(LayoutInflater.from(context), this, true)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.DatePickerInputView, 0, 0)
            val hintText = typedArray.getString(R.styleable.DatePickerInputView_android_hint)
            this.hint = hintText ?: ""
            typedArray.recycle()
        }
    }

    fun setupDatePicker(fragmentManager: FragmentManager) {
        binding.dateEditTextComponent.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
            val dateValidator = DateValidatorPointBackward.now()
            constraintsBuilder.setValidator(dateValidator)

            val currentSelection = textToTimestamp()

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(currentSelection)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            picker.addOnPositiveButtonClickListener { timestamp ->
                this.text = timestampToFormattedString(timestamp)
            }

            picker.show(fragmentManager, "MATERIAL_DATE_PICKER")
        }
    }

    private fun textToTimestamp(): Long {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date = format.parse(this.text)
            date?.time ?: MaterialDatePicker.todayInUtcMilliseconds()
        } catch (e: Exception) {
            MaterialDatePicker.todayInUtcMilliseconds()
        }
    }

    private fun timestampToFormattedString(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = Date(timestamp)
        return sdf.format(date)
    }
}