package br.com.minhaentrada.victor.challenge.ui.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.data.AppDatabase
import br.com.minhaentrada.victor.challenge.data.UserRepository
import br.com.minhaentrada.victor.challenge.ui.components.LocationSelectorView
import java.util.Calendar

class EditProfileDialogFragment : DialogFragment() {

    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory(
            UserRepository(AppDatabase.getDatabase(requireContext()).userDao())
        )
    }

    private lateinit var usernameEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var locationSelector: LocationSelectorView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_profile, null)

        usernameEditText = view.findViewById(R.id.username_edit_text_edit)
        birthdateEditText = view.findViewById(R.id.birthdate_edit_text_edit)
        locationSelector = view.findViewById(R.id.location_selector_edit)

        val userId = arguments?.getLong(ARG_USER_ID) ?: -1L

        if (userId != -1L) {
            viewModel.loadUser(userId)
        }

        observeViewModel()
        setupBirthDateListener()

        builder.setView(view)
            .setTitle("Editar Perfil")
            .setPositiveButton("Editar") { dialog, id ->
                val newUsername = usernameEditText.text.toString().trim()
                val newBirthDate = birthdateEditText.text.toString().trim()
                val newCity = locationSelector.selectedCity
                val newState = locationSelector.selectedState
                if (newUsername.isNotEmpty()) {
                    viewModel.updateUser(userId, newUsername, newBirthDate, newState, newCity)
                }
            }
            .setNegativeButton("Cancelar") { dialog, id ->
                dialog.cancel()
            }
        return builder.create()
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            user?.let {
                usernameEditText.setText(it.username)
                birthdateEditText.setText(it.birthDate)
                locationSelector.setInitialLocation(it.state, it.city)
            }
        }
        viewModel.updateStatus.observe(this) { isSuccess ->
            if (isSuccess) {
                parentFragmentManager.setFragmentResult("profileEdited", Bundle.EMPTY)
                dismiss()
            }
        }
    }

    private fun setupBirthDateListener() {
        birthdateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    birthdateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): EditProfileDialogFragment {
            val args = Bundle()
            args.putLong(ARG_USER_ID, userId)
            val  fragment = EditProfileDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}