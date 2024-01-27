package com.example.askandanswer

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.askandanswer.databinding.LoadingIndicatorBinding
import com.example.askandanswer.views.activities.AuthActivity
import com.google.android.material.snackbar.Snackbar
import io.github.cdimascio.dotenv.dotenv
import java.text.SimpleDateFormat
import java.util.Locale

class MyApplication : Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private var loadingDialog: Dialog? = null
    private var confirmationDialog: AlertDialog? = null
    private val inputDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    private val outputDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy - HH:mm", Locale.getDefault())
    lateinit var errMsg: String; private set
    lateinit var invalidTokenMsg: String; private set

    companion object {
        private lateinit var INSTANCE: MyApplication
        val env = dotenv {
            directory = "./assets"
            filename = "env"
        }
        const val LOGOUT_MSG_KEY = "logoutMsg"
        const val SUCCESS_MSG_KEY = "successMsg"
        const val QUESTION_ID_KEY = "questionId"
        const val ANSWER_ID_KEY = "answerId"

        fun getInstance(): MyApplication {
            return INSTANCE
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        errMsg = getString(R.string.error_message)
        invalidTokenMsg = getString(R.string.invalid_token_message)
    }

    fun logout(context: Context, msg: String = "") {
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(LOGOUT_MSG_KEY, msg)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun showLoading(context: Context, layoutInflater: LayoutInflater) {
        loadingDialog?.dismiss()
        loadingDialog = Dialog(context, R.style.LoadingDialogStyle)
        loadingDialog?.setContentView(LoadingIndicatorBinding.inflate(layoutInflater).root)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    fun stopLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun showConfirmationDialog(context: Context, msgResId: Int, onConfirmListener: DialogInterface.OnClickListener) {
        confirmationDialog?.dismiss()
        confirmationDialog = AlertDialog.Builder(context, R.style.ConfirmationDialogStyle)
            .setMessage(msgResId)
            .setPositiveButton(R.string.yes, onConfirmListener)
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun showSnackbar(context: Context, rootView: View, msg: String, isSuccess: Boolean = false) {
        val snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(
            ContextCompat.getColor(
                context,
                if (isSuccess) R.color.green else R.color.red
            )
        )
        val snackbarLayout = snackbar.view

        val textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        textView.textSize = 16f
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isSuccess) R.drawable.ic_success else R.drawable.ic_danger,
            0,
            0,
            0
        )
        textView.compoundDrawablePadding = dpToPx(10)

        snackbar.show()
    }

    fun formatDateString(dateString: String): String {
        try {
            val date = inputDateFormatter.parse(dateString) ?: return ""
            return outputDateFormatter.format(date) ?: ""
        } catch (e: Exception) {
            Log.e("MyApplication", "Error from formatDateString method.", e)
            return ""
        }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = this.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}