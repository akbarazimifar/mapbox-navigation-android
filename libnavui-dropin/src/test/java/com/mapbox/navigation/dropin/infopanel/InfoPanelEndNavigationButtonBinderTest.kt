package com.mapbox.navigation.dropin.infopanel

import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.dropin.extendablebutton.EndNavigationButtonComponent
import com.mapbox.navigation.dropin.navigationview.NavigationViewContext
import com.mapbox.navigation.dropin.navigationview.NavigationViewModel
import com.mapbox.navigation.dropin.testutil.TestLifecycleOwner
import com.mapbox.navigation.dropin.util.TestStore
import com.mapbox.navigation.dropin.util.TestingUtil.findComponent
import com.mapbox.navigation.testing.LoggingFrontendTestRule
import com.mapbox.navigation.testing.MainCoroutineRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class, ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class InfoPanelEndNavigationButtonBinderTest {

    @get:Rule
    val loggerRule = LoggingFrontendTestRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var ctx: Context
    private lateinit var navContext: NavigationViewContext
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var sut: InfoPanelEndNavigationButtonBinder

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        navContext = NavigationViewContext(
            context = ctx,
            lifecycleOwner = TestLifecycleOwner(),
            viewModel = NavigationViewModel(),
            storeProvider = { TestStore() }
        )
        mapboxNavigation = mockk(relaxed = true)

        sut = InfoPanelEndNavigationButtonBinder(navContext)
    }

    @Test
    fun `bind should return and bind EndNavigationButtonComponent`() {
        val component = sut.bind(FrameLayout(ctx))
        component.onAttached(mapboxNavigation)

        assertNotNull(component.findComponent { it is EndNavigationButtonComponent })
    }
}