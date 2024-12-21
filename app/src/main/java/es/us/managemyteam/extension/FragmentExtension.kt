package es.us.managemyteam.extension

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import es.managemyteam.app.R

fun Fragment.popBack() {
    getFocusedView().hideKeyboard()
    findNavController().navigateUp()
}

fun Fragment.showErrorDialog(
    errorMessage: String,
    dialogOnClickListener: DialogInterface.OnClickListener? = null
) {
    context?.let { con ->
        val dialog = AlertDialog.Builder(con)
            .setCancelable(true)
            .setMessage(errorMessage)
            .setTitle(null)
            .setPositiveButton(R.string.accept, dialogOnClickListener)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(con, R.color.black))
        }
        dialog.show()
    }
}

fun Fragment.showInformationDialog(
    message: String,
    dialogOnClickListener: DialogInterface.OnClickListener? = null
) {
    context?.let { con ->
        val dialog = AlertDialog.Builder(con)
            .setCancelable(true)
            .setMessage(message)
            .setTitle(null)
            .setPositiveButton(R.string.accept, dialogOnClickListener)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(con, R.color.black))
        }
        dialog.show()
    }
}

fun Fragment.getDefaultDialogErrorListener(): DialogInterface.OnClickListener {
    return DialogInterface.OnClickListener { dialog, _ ->
        popBack()
    }
}

fun Fragment.getFocusedView(): View {
    return activity?.currentFocus ?: view?.rootView ?: View(activity)
}
