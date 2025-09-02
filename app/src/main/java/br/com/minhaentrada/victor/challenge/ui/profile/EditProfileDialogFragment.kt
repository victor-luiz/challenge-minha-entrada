package br.com.minhaentrada.victor.challenge.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import br.com.minhaentrada.victor.challenge.R
import br.com.minhaentrada.victor.challenge.databinding.DialogEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileDialogFragment : DialogFragment() {

    private val viewModel: EditProfileViewModel by viewModels()

    private var _binding: DialogEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditProfileBinding.inflate(LayoutInflater.from(requireContext()))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
            .setTitle("Editar Perfil")
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar") { _, _ -> dismiss() }

        val userId = arguments?.getLong(ARG_USER_ID) ?: -1L
        if (userId != -1L) {
            viewModel.loadUser(userId)
        }

        observeViewModel()

        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        val alertDialog = dialog as AlertDialog
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            handleSaveClick()
        }
    }

    private fun handleSaveClick() {
        val userId = arguments?.getLong(ARG_USER_ID) ?: -1L
        binding.usernameInputLayoutEdit.error = null
        val newUsername = binding.usernameEditTextEdit.text.toString().trim()
        if (newUsername.isNotEmpty()) {
            viewModel.updateUser(newUsername)
        } else {
            binding.usernameInputLayoutEdit.error = "O nome não pode ser vazio."
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            user?.let {
                binding.usernameEditTextEdit.setText(it.username)
            }
        }
        viewModel.updateStatus.observe(this) { state ->
            when (state) {
                is EditProfileViewModel.UpdateState.Success -> {
                    parentFragmentManager.setFragmentResult("profileEdited", Bundle.EMPTY)
                    dismiss()
                }
                is EditProfileViewModel.UpdateState.UsernameAlreadyExists -> {
                    binding.usernameInputLayoutEdit.error = "Este nome de usuário já está em uso."
                }
                is EditProfileViewModel.UpdateState.Idle -> { }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        fun newInstance(userId: Long): EditProfileDialogFragment {
            val args = Bundle()
            args.putLong(ARG_USER_ID, userId)
            val fragment = EditProfileDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}