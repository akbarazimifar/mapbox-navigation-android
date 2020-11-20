package com.mapbox.navigation.ui.maps.route.arrow.model

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.mapbox.navigation.ui.base.internal.route.RouteConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class RouteArrowOptionsTest {

    lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun withArrowColorTest() {
        val options = RouteArrowOptions.Builder(ctx).withArrowColor(5).build()

        assertEquals(5, options.arrowColor)
    }

    @Test
    fun withArrowBorderColorTest() {
        val options = RouteArrowOptions.Builder(ctx).withArrowBorderColor(5).build()

        assertEquals(5, options.arrowBorderColor)
    }

    @Test
    fun withArrowHeadIconDrawableTest() {
        val options = RouteArrowOptions.Builder(ctx)
            .withArrowHeadIconDrawable(RouteConstants.MANEUVER_ARROWHEAD_ICON_DRAWABLE)
            .build()

        assertNotNull(options.arrowHeadIcon)
    }

    @Test
    fun withArrowHeadIconCasingDrawableTest() {
        val options = RouteArrowOptions.Builder(ctx)
            .withArrowHeadIconCasingDrawable(RouteConstants.MANEUVER_ARROWHEAD_ICON_DRAWABLE)
            .build()

        assertNotNull(options.arrowHeadIconBorder)
    }

    @Test
    fun withAboveLayerIdTest() {
        val options = RouteArrowOptions.Builder(ctx).withAboveLayerId("someLayerId").build()

        assertEquals("someLayerId", options.aboveLayerId)
    }

    @Test
    fun toBuilder() {
        val options = RouteArrowOptions.Builder(ctx)
            .withArrowColor(1)
            .withAboveLayerId("someLayerId")
            .withArrowBorderColor(2)
            .withArrowHeadIconDrawable(RouteConstants.MANEUVER_ARROWHEAD_ICON_DRAWABLE)
            .withArrowHeadIconCasingDrawable(RouteConstants.MANEUVER_ARROWHEAD_ICON_DRAWABLE)
            .build()
            .toBuilder(ctx)
            .build()

        assertEquals(1, options.arrowColor)
        assertEquals(2, options.arrowBorderColor)
        assertEquals("someLayerId", options.aboveLayerId)
        assertNotNull(options.arrowHeadIcon)
        assertNotNull(options.arrowHeadIconBorder)
    }
}
