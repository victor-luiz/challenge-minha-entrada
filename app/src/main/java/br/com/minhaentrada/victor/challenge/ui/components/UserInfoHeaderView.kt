package br.com.minhaentrada.victor.challenge.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import br.com.minhaentrada.victor.challenge.data.user.User
import br.com.minhaentrada.victor.challenge.databinding.ComponentUserInfoHeaderBinding

class UserInfoHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentUserInfoHeaderBinding

    init {
        binding = ComponentUserInfoHeaderBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun bindUser(user: User) {
        binding.usernameTextView.text = user.username
        binding.emailTextView.text = user.email
    }
}