package com.divinebudget.app.ejgpoer.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.divinebudget.app.MainActivity
import com.divinebudget.app.R
import com.divinebudget.app.databinding.FragmentLoadDivineBudgetBinding
import com.divinebudget.app.ejgpoer.data.shar.DivineBudgetSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class DivineBudgetLoadFragment : Fragment(R.layout.fragment_load_divine_budget) {
    private lateinit var divineBudgetLoadBinding: FragmentLoadDivineBudgetBinding

    private val divineBudgetLoadViewModel by viewModel<DivineBudgetLoadViewModel>()

    private val divineBudgetSharedPreference by inject<DivineBudgetSharedPreference>()

    private var divineBudgetUrl = ""

    private val divineBudgetRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            divineBudgetNavigateToSuccess(divineBudgetUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                divineBudgetSharedPreference.divineBudgetNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                divineBudgetNavigateToSuccess(divineBudgetUrl)
            } else {
                divineBudgetNavigateToSuccess(divineBudgetUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        divineBudgetLoadBinding = FragmentLoadDivineBudgetBinding.bind(view)

        divineBudgetLoadBinding.divineBudgetGrandButton.setOnClickListener {
            val divineBudgetPermission = Manifest.permission.POST_NOTIFICATIONS
            divineBudgetRequestNotificationPermission.launch(divineBudgetPermission)
            divineBudgetSharedPreference.divineBudgetNotificationRequestedBefore = true
        }

        divineBudgetLoadBinding.divineBudgetSkipButton.setOnClickListener {
            divineBudgetSharedPreference.divineBudgetNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            divineBudgetNavigateToSuccess(divineBudgetUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                divineBudgetLoadViewModel.divineBudgetHomeScreenState.collect {
                    when (it) {
                        is DivineBudgetLoadViewModel.DivineBudgetHomeScreenState.DivineBudgetLoading -> {

                        }

                        is DivineBudgetLoadViewModel.DivineBudgetHomeScreenState.DivineBudgetError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is DivineBudgetLoadViewModel.DivineBudgetHomeScreenState.DivineBudgetSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val divineBudgetPermission = Manifest.permission.POST_NOTIFICATIONS
                                val divineBudgetPermissionRequestedBefore = divineBudgetSharedPreference.divineBudgetNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), divineBudgetPermission) == PackageManager.PERMISSION_GRANTED) {
                                    divineBudgetNavigateToSuccess(it.data)
                                } else if (!divineBudgetPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > divineBudgetSharedPreference.divineBudgetNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    divineBudgetLoadBinding.divineBudgetNotiGroup.visibility = View.VISIBLE
                                    divineBudgetLoadBinding.divineBudgetLoadingGroup.visibility = View.GONE
                                    divineBudgetUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(divineBudgetPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > divineBudgetSharedPreference.divineBudgetNotificationRequest) {
                                        divineBudgetLoadBinding.divineBudgetNotiGroup.visibility = View.VISIBLE
                                        divineBudgetLoadBinding.divineBudgetLoadingGroup.visibility = View.GONE
                                        divineBudgetUrl = it.data
                                    } else {
                                        divineBudgetNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    divineBudgetNavigateToSuccess(it.data)
                                }
                            } else {
                                divineBudgetNavigateToSuccess(it.data)
                            }
                        }

                        DivineBudgetLoadViewModel.DivineBudgetHomeScreenState.DivineBudgetNotInternet -> {
                            divineBudgetLoadBinding.divineBudgetStateGroup.visibility = View.VISIBLE
                            divineBudgetLoadBinding.divineBudgetLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun divineBudgetNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_divineBudgetLoadFragment_to_divineBudgetV,
            bundleOf(DIVINE_BUDGET_D to data)
        )
    }

    companion object {
        const val DIVINE_BUDGET_D = "divineBudgetData"
    }
}