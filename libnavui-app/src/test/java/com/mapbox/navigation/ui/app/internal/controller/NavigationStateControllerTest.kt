package com.mapbox.navigation.ui.app.internal.controller

import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.arrival.ArrivalObserver
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.testing.MainCoroutineRule
import com.mapbox.navigation.ui.app.internal.navigation.NavigationState
import com.mapbox.navigation.ui.app.internal.navigation.NavigationStateAction
import com.mapbox.navigation.ui.app.testing.TestStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class, ExperimentalCoroutinesApi::class)
internal class NavigationStateControllerTest {

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var testStore: TestStore

    lateinit var sut: NavigationStateController
    lateinit var mockMapboxNavigation: MapboxNavigation

    @Before
    fun setUp() {
        mockkObject(MapboxNavigationApp)
        mockMapboxNavigation = mockk()
        every { MapboxNavigationApp.current() } returns mockMapboxNavigation

        testStore = TestStore()
        sut = NavigationStateController(testStore)
    }

    @After
    fun teardown() {
        unmockkObject(MapboxNavigationApp)
    }

    @Test
    fun `should set new state on Update action`() = coroutineRule.runBlockingTest {
        sut.onAttached(mockMapboxNavigation)

        testStore.dispatch(NavigationStateAction.Update(NavigationState.RoutePreview))

        assertEquals(NavigationState.RoutePreview, testStore.state.value.navigation)
    }

    @Test
    fun `should detect final destination arrival and update NavigationState to Arrival`() {
        val observerSlot = slot<ArrivalObserver>()
        every { mockMapboxNavigation.registerArrivalObserver(capture(observerSlot)) } returns Unit
        sut.onAttached(mockMapboxNavigation)

        observerSlot.captured.onFinalDestinationArrival(mockk())

        assertEquals(NavigationState.Arrival, testStore.state.value.navigation)
    }
}
