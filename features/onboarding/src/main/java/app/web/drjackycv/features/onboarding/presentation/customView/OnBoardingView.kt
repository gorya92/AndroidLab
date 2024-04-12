package app.web.drjackycv.features.onboarding.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import app.web.drjackycv.features.onboarding.databinding.OnboardingViewBinding
import app.web.drjackycv.features.onboarding.presentation.OnBoarding
import app.web.drjackycv.features.onboarding.presentation.OnBoardingPrefManager
import com.omni.onboardingscreen.core.setParallaxTransformation
import com.omni.onboardingscreen.feature.onboarding.OnBoardingPagerAdapter
import app.web.drjackycv.features.onboarding.presentation.entity.OnBoardingPage

class OnBoardingView @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val numberOfPages by lazy { OnBoardingPage.values().size }
    private val prefManager: OnBoardingPrefManager
    private lateinit var navController: NavController

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    init {
        val binding = OnboardingViewBinding.inflate(LayoutInflater.from(context), this, true)
        with(binding) {
            setUpSlider()
            addingButtonsClickListeners()
            prefManager = OnBoardingPrefManager(root.context)
        }

    }

    private fun OnboardingViewBinding.setUpSlider() {
        with(slider) {
            adapter = OnBoardingPagerAdapter()

            setPageTransformer { page, position ->
                setParallaxTransformation(page, position)
            }
//
//            setPageTransformer(pageCompositePageTransformer)

            addSlideChangeListener()

            val wormDotsIndicator = pageIndicator
            wormDotsIndicator.setViewPager2(this)
        }
    }


    private fun OnboardingViewBinding.addSlideChangeListener() {

        slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (numberOfPages > 1) {
                    if (position == 0) {
                        next.text = NEXT
                    }
                    if (position == 1) {
                        next.text = MORE
                    }
                    if (position == 2) {
                        next.text = CHANGE_LANGUAGE
                    }
                    val newProgress = (position + positionOffset) / (numberOfPages - 1)
                }
            }
        })
    }

    private fun OnboardingViewBinding.addingButtonsClickListeners() {
        next.setOnClickListener {
            navigateToNextSlide(slider)
            if (next.text == CHANGE_LANGUAGE) {
                val request = NavDeepLinkRequest.Builder
                    .fromUri("android-app://app.web.drjackycv/language".toUri())
                    .build()
                navController.navigate(request)
                setFirstTimeLaunchToFalse()
            }
        }
        skip.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://app.web.drjackycv/language".toUri())
                .build()
            navController.navigate(request)
            setFirstTimeLaunchToFalse()
        }
    }

    private fun setFirstTimeLaunchToFalse() {
        prefManager.isFirstTimeLaunch = false
    }

    private fun navigateToNextSlide(slider: ViewPager2?) {
        val nextSlidePos: Int = slider?.currentItem?.plus(1) ?: 0
        slider?.setCurrentItem(nextSlidePos, true)
    }

    companion object {
        private const val NEXT = "Next"
        private const val CHANGE_LANGUAGE = "Choose a language"
        private const val MORE = "More"
    }


}