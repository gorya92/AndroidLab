package app.web.drjackycv.splash.presentation


import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.web.drjackycv.features.onboarding.R
import app.web.drjackycv.features.onboarding.presentation.ChoiceLanguage
import app.web.drjackycv.features.onboarding.presentation.OnBoarding
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class MyFragmentTest {

    @Test
    fun testFragment() {
        // Запуск фрагмента в изоляции в контейнере
        val scenario: FragmentScenario<ChoiceLanguage> = launchFragmentInContainer()

        onView(withId(R.id.language_select)).check(matches(withText(R.string.language_select)))


        // Дополнительные проверки, если необходимо
    }
}