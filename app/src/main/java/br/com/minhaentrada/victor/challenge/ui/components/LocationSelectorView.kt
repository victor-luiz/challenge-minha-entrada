package br.com.minhaentrada.victor.challenge.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.databinding.ComponentLocationSelectorBinding


class LocationSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentLocationSelectorBinding

    val selectedState: String
        get() = binding.stateAutocomplete.text.toString()

    val selectedCity: String
        get() = binding.cityAutocomplete.text.toString()

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.component_location_selector, this, true)
        binding = ComponentLocationSelectorBinding.bind(this)

        setDropdowns()
    }

    fun setInitialLocation(initialState: String?, initialCity: String?) {
        if (!initialState.isNullOrEmpty()) {
            binding.stateAutocomplete.setText(initialState, false)
            val citiesArrayId = getCitiesArrayIdFor(initialState)
            if (citiesArrayId != 0) {
                val cities = resources.getStringArray(citiesArrayId)
                val citiesAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, cities)
                binding.cityAutocomplete.setAdapter(citiesAdapter)
                binding.cityInputLayout.isEnabled = true
                if (!initialCity.isNullOrEmpty()) {
                    binding.cityAutocomplete.setText(initialCity, false)
                }
            }
        }
    }

    private fun setDropdowns() {
        val state = resources.getStringArray(R.array.states)
        val stateAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, state)
        binding.stateAutocomplete.setAdapter(stateAdapter)
        binding.cityInputLayout.isEnabled = false
        binding.stateAutocomplete.setOnItemClickListener { parent, view, position, id ->
            val selectedState = parent.getItemAtPosition(position).toString()
            binding.cityAutocomplete.setText("", false)

            val citiesArrayId = getCitiesArrayIdFor(selectedState)

            if (citiesArrayId != 0) {
                val cities = resources.getStringArray(citiesArrayId)
                val citiesAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, cities)
                binding.cityAutocomplete.setAdapter(citiesAdapter)
                binding.cityAutocomplete.isEnabled = true
            } else {
                binding.cityAutocomplete.setAdapter(null)
                binding.cityInputLayout.isEnabled = false
            }
        }
    }

    private fun getCitiesArrayIdFor(state: String): Int {
        return when (state) {
            "Acre" -> R.array.acre
            "Alagoas" -> R.array.alagoas
            "Amapa" -> R.array.amapa
            "Amazonas" -> R.array.amazonas
            "Bahia" -> R.array.bahia
            "Ceará" -> R.array.ceara
            "Distrito Federal" -> R.array.distrito_federal
            "Espírito Santo" -> R.array.espirito_santo
            "Goiás" -> R.array.goias
            "Maranhão" -> R.array.maranhao
            "Mato Grosso" -> R.array.mato_grosso
            "Mato Grosso do Sul" -> R.array.mato_grosso_do_sul
            "Minas Gerais" -> R.array.minas_gerais
            "Pará" -> R.array.para
            "Paraíba" -> R.array.paraiba
            "Paraná" -> R.array.parana
            "Pernambuco" -> R.array.pernambuco
            "Piauí" -> R.array.piaui
            "Rio de Janeiro" -> R.array.rio_de_janeiro
            "Rio Grande do Norte" -> R.array.rio_grande_do_norte
            "Rio Grande do Sul" -> R.array.rio_grande_do_sul
            "Rondônia" -> R.array.rondonia
            "Roraima" -> R.array.roraima
            "Santa Catarina" -> R.array.santa_catarina
            "São Paulo" -> R.array.sao_paulo
            "Sergipe" -> R.array.sergipe
            "Tocantins" -> R.array.tocantins
            else -> {0}
        }
    }

    fun validateFields(): Boolean {
        clearErrors()
        if (selectedState.isEmpty() ) {
            binding.stateInputLayout.error = context.getString(R.string.error_empty_field)
            return false
        }
        if (selectedCity.isEmpty()) {
            binding.cityInputLayout.error = context.getString(R.string.error_empty_field)
            return false
        }
        return true
    }

    fun clearErrors() {
        binding.stateInputLayout.error = null
        binding.cityInputLayout.error = null
    }

}