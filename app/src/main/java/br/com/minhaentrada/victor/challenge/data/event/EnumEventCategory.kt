package br.com.minhaentrada.victor.challenge.data.event

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import br.com.minhaentrada.victor.challenge.R

enum class EnumEventCategory(
    @get:StringRes val displayNameResId: Int,
    @get:ColorRes val colorResId: Int
) {
    PARTY(
        displayNameResId = R.string.category_event_party,
        colorResId = R.color.category_party
    ),
    SHOW(
        displayNameResId = R.string.category_event_show,
        colorResId = R.color.category_show
    ),
    SPORTS(
        displayNameResId = R.string.category_event_sports,
        colorResId = R.color.category_sports
    ),
    COMEDY(
        displayNameResId = R.string.category_event_comedy,
        colorResId = R.color.category_comedy
    ),
    THEATER(
        displayNameResId = R.string.category_event_theater,
        colorResId = R.color.category_theater
    )
}