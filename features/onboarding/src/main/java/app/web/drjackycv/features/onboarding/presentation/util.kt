package app.web.drjackycv.features.onboarding.presentation

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = context.resources
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
}