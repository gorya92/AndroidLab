package app.web.drjackycv.features.app.presentation

import android.content.Context
import android.content.SharedPreferences

class ProfilePrefManager(_context: Context) {


    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor


    var isFirstTimeLaunch: Boolean
        get() {
            return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        }
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    var isDarkTheme: Boolean
        get() {
            return pref.getBoolean(IS_DARK_THEME, true)
        }
        set(isFirstTime) {
            editor.putBoolean(IS_DARK_THEME, isFirstTime)
            editor.commit()
        }

    var isRussianActive: Boolean
        get() {
            return pref.getBoolean(IS_RUSSIAN_ACTIVE, true)
        }
        set(isFirstTime) {
            editor.putBoolean(IS_RUSSIAN_ACTIVE, isFirstTime)
            editor.commit()
        }

    var isLanguagedChoised: Boolean
        get() {
            return pref.getBoolean(IS_LANGUAGE_VOTED, true)
        }
        set(isFirstTime) {
            editor.putBoolean(IS_LANGUAGE_VOTED, isFirstTime)
            editor.commit()
        }

    var isAuth: Boolean
        get() {
            return pref.getBoolean(IS_AUTH, true)
        }
        set(isFirstTime) {
            editor.putBoolean(IS_AUTH, isFirstTime)
            editor.commit()
        }

    var firstName: String?
        get() {
            return pref.getString(FIRST_NAME, "")
        }
        set(isFirstTime) {
            editor.putString(FIRST_NAME, isFirstTime)
            editor.commit()
        }

    var lastName: String?
        get() {
            return pref.getString(LAST_NAME, "")
        }
        set(isFirstTime) {
            editor.putString(LAST_NAME, isFirstTime)
            editor.commit()
        }

    var pastAnimalGame: String?
        get() {
            return pref.getString(PAST_ANIMAL, "")
        }
        set(isFirstTime) {
            editor.putString(PAST_ANIMAL, isFirstTime)
            editor.commit()
        }

    var pastCorrectGame2: Int
        get() {
            return pref.getInt(PAST_CORRECT2, 0)
        }
        set(isFirstTime) {
            editor.putInt(PAST_CORRECT2, isFirstTime)
            editor.commit()
        }
    var pastRU: Boolean
        get() {
            return pref.getBoolean(PAST_RU, false)
        }
        set(isFirstTime) {
            editor.putBoolean(PAST_RU, isFirstTime)
            editor.commit()
        }

    var points: Float
        get() {
            return pref.getFloat(POINTS, 0f)
        }
        set(isFirstTime) {
            editor.putFloat(POINTS, isFirstTime)
            editor.commit()
        }

    var isStreak: Int
        get() {
            return pref.getInt(IS_STREAK, 0)
        }
        set(isFirstTime) {
            editor.putInt(IS_STREAK, isFirstTime)
            editor.commit()
        }


    var email: String?
        get() {
            return pref.getString(EMAIL, "")
        }
        set(isFirstTime) {
            editor.putString(EMAIL, isFirstTime)
            editor.commit()
        }

    var id: String?
        get() {
            return pref.getString(ID, "")
        }
        set(isFirstTime) {
            editor.putString(ID, isFirstTime)
            editor.commit()
        }


    init {
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    companion object {
        private const val PAST_CORRECT2 = "PAST_CORRECT2"
        private const val PAST_RU = "PAST_RU"
        private const val PAST_ANIMAL = "PAST_ANIMAL"
        private const val IS_DARK_THEME = "IS_DARK_THEME"
        private const val IS_RUSSIAN_ACTIVE = "IS_RUSSIAN_ACTIVE"
        private const val ID = "ID"
        private const val FIRST_NAME = "FIRST_NAME"
        private const val LAST_NAME = "LAST_NAME"
        private const val EMAIL = "EMAIL"
        private const val IS_AUTH = "IS_AUTH"
        private const val IS_STREAK = "IS_STREAK"
        private const val POINTS = "POINTS"
        private const val IS_LANGUAGE_VOTED = "IS_LANGUAGE_VOTED"
        private const val IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH"
        private const val PREF_NAME = "PREF_NAME"
    }

}