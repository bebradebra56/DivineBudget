package com.divinebudget.app.ejgpoer.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import com.divinebudget.app.ejgpoer.presentation.ui.load.DivineBudgetLoadFragment
import org.koin.android.ext.android.inject

class DivineBudgetV : Fragment(){

    private lateinit var divineBudgetPhoto: Uri
    private var divineBudgetFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val divineBudgetTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        divineBudgetFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        divineBudgetFilePathFromChrome = null
    }

    private val divineBudgetTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            divineBudgetFilePathFromChrome?.onReceiveValue(arrayOf(divineBudgetPhoto))
            divineBudgetFilePathFromChrome = null
        } else {
            divineBudgetFilePathFromChrome?.onReceiveValue(null)
            divineBudgetFilePathFromChrome = null
        }
    }

    private val divineBudgetDataStore by activityViewModels<DivineBudgetDataStore>()


    private val divineBudgetViFun by inject<DivineBudgetViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (divineBudgetDataStore.divineBudgetView.canGoBack()) {
                        divineBudgetDataStore.divineBudgetView.goBack()
                        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "WebView can go back")
                    } else if (divineBudgetDataStore.divineBudgetViList.size > 1) {
                        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "WebView can`t go back")
                        divineBudgetDataStore.divineBudgetViList.removeAt(divineBudgetDataStore.divineBudgetViList.lastIndex)
                        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "WebView list size ${divineBudgetDataStore.divineBudgetViList.size}")
                        divineBudgetDataStore.divineBudgetView.destroy()
                        val previousWebView = divineBudgetDataStore.divineBudgetViList.last()
                        divineBudgetAttachWebViewToContainer(previousWebView)
                        divineBudgetDataStore.divineBudgetView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (divineBudgetDataStore.divineBudgetIsFirstCreate) {
            divineBudgetDataStore.divineBudgetIsFirstCreate = false
            divineBudgetDataStore.divineBudgetContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return divineBudgetDataStore.divineBudgetContainerView
        } else {
            return divineBudgetDataStore.divineBudgetContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "onViewCreated")
        if (divineBudgetDataStore.divineBudgetViList.isEmpty()) {
            divineBudgetDataStore.divineBudgetView = DivineBudgetVi(requireContext(), object :
                DivineBudgetCallBack {
                override fun divineBudgetHandleCreateWebWindowRequest(divineBudgetVi: DivineBudgetVi) {
                    divineBudgetDataStore.divineBudgetViList.add(divineBudgetVi)
                    Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "WebView list size = ${divineBudgetDataStore.divineBudgetViList.size}")
                    Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "CreateWebWindowRequest")
                    divineBudgetDataStore.divineBudgetView = divineBudgetVi
                    divineBudgetVi.divineBudgetSetFileChooserHandler { callback ->
                        divineBudgetHandleFileChooser(callback)
                    }
                    divineBudgetAttachWebViewToContainer(divineBudgetVi)
                }

            }, divineBudgetWindow = requireActivity().window).apply {
                divineBudgetSetFileChooserHandler { callback ->
                    divineBudgetHandleFileChooser(callback)
                }
            }
            divineBudgetDataStore.divineBudgetView.divineBudgetFLoad(arguments?.getString(
                DivineBudgetLoadFragment.DIVINE_BUDGET_D) ?: "")
//            ejvview.fLoad("www.google.com")
            divineBudgetDataStore.divineBudgetViList.add(divineBudgetDataStore.divineBudgetView)
            divineBudgetAttachWebViewToContainer(divineBudgetDataStore.divineBudgetView)
        } else {
            divineBudgetDataStore.divineBudgetViList.forEach { webView ->
                webView.divineBudgetSetFileChooserHandler { callback ->
                    divineBudgetHandleFileChooser(callback)
                }
            }
            divineBudgetDataStore.divineBudgetView = divineBudgetDataStore.divineBudgetViList.last()

            divineBudgetAttachWebViewToContainer(divineBudgetDataStore.divineBudgetView)
        }
        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "WebView list size = ${divineBudgetDataStore.divineBudgetViList.size}")
    }

    private fun divineBudgetHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        divineBudgetFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Launching file picker")
                    divineBudgetTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Launching camera")
                    divineBudgetPhoto = divineBudgetViFun.divineBudgetSavePhoto()
                    divineBudgetTakePhoto.launch(divineBudgetPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                divineBudgetFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun divineBudgetAttachWebViewToContainer(w: DivineBudgetVi) {
        divineBudgetDataStore.divineBudgetContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            divineBudgetDataStore.divineBudgetContainerView.removeAllViews()
            divineBudgetDataStore.divineBudgetContainerView.addView(w)
        }
    }


}