package br.com.minhaentrada.victor.challenge.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import br.com.minhaentrada.victor.challenge.data.event.EnumEventCategory
import com.google.android.material.chip.Chip

class CategoryChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.chipStyle
) : Chip(context, attrs, defStyleAttr) {

    fun setCategory(category: EnumEventCategory) {
        text = context.getString(category.displayNameResId)
        val backgroundColor = ContextCompat.getColor(context, category.colorResId)
        setChipBackgroundColor(ColorStateList.valueOf(backgroundColor))
    }
}