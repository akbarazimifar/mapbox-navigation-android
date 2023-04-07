package com.mapbox.navigation.navigator.internal.router.utils

import com.mapbox.geojson.Point
import com.mapbox.navigation.base.internal.route.LegWaypointFactory
import com.mapbox.navigation.base.internal.route.Waypoint
import com.mapbox.navigation.base.internal.utils.WaypointFactory
import com.mapbox.navigation.base.internal.utils.internalWaypoints
import com.mapbox.navigation.base.route.LegWaypoint
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.navigator.internal.TripStatus
import com.mapbox.navigation.navigator.internal.utils.calculateRemainingWaypoints
import com.mapbox.navigation.navigator.internal.utils.getCurrentLegDestination
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

class TripStatusExTest {

    @RunWith(Parameterized::class)
    class CalculateRemainingWaypointsTest(
        val description: String,
        val routeWithWaypoints: NavigationRoute?,
        val nextWaypointIndex: Int,
        val expectedResult: Int,
    ) {
        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun data() = listOf(
                arrayOf(
                    "null route returns 0",
                    null,
                    Int.MIN_VALUE,
                    0,
                ),
                arrayOf(
                    "next waypoint index normalized is normalized to default initial leg target",
                    mockk<NavigationRoute> {
                        every {
                            internalWaypoints()
                        } returns provideMockListOfWaypoints(
                            Waypoint.REGULAR,
                            Waypoint.REGULAR
                        )
                    },
                    0,
                    1,
                ),
                arrayOf(
                    "all waypoints are taken into account to get remaining waypoints",
                    mockk<NavigationRoute> {
                        every {
                            internalWaypoints()
                        } returns provideMockListOfWaypoints(
                            Waypoint.REGULAR,
                            Waypoint.SILENT,
                            Waypoint.EV_CHARGING,
                            Waypoint.SILENT,
                            Waypoint.REGULAR,
                            Waypoint.REGULAR,
                            Waypoint.SILENT,
                            Waypoint.EV_CHARGING,
                            Waypoint.EV_CHARGING,
                            Waypoint.REGULAR,
                            Waypoint.REGULAR,
                        )
                    },
                    8,
                    3,
                ),
            )

            private fun provideMockListOfWaypoints(
                @Waypoint.Type vararg types: Int
            ): List<Waypoint> =
                types.map { type ->
                    WaypointFactory.provideWaypoint(
                        Point.fromLngLat(0.0, 0.0),
                        "",
                        null,
                        type
                    )
                }
        }

        @Test
        fun testCases() {
            val tripStatus = TripStatus(
                routeWithWaypoints,
                mockk {
                    every {
                        nextWaypointIndex
                    } returns this@CalculateRemainingWaypointsTest.nextWaypointIndex
                }
            )

            val result = tripStatus.calculateRemainingWaypoints()

            assertEquals(description, expectedResult, result)
        }
    }

    @RunWith(Parameterized::class)
    class CalculateNextLegWaypointTest(
        private val description: String,
        private val routeWithWaypoints: NavigationRoute,
        private val nextWaypointIndex: Int,
        private val expectedResult: LegWaypoint?,
    ) {

        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun data(): List<Array<Any?>> {
                val regularWaypoint1 = WaypointFactory.provideWaypoint(
                    Point.fromLngLat(0.0, 0.0),
                    "",
                    null,
                    Waypoint.REGULAR
                )
                val regularWaypoint2 = WaypointFactory.provideWaypoint(
                    Point.fromLngLat(1.0, 1.0),
                    "regular waypoint",
                    Point.fromLngLat(1.1, 1.1),
                    Waypoint.REGULAR
                )
                val silentWaypoint = WaypointFactory.provideWaypoint(
                    Point.fromLngLat(2.0, 2.0),
                    "",
                    null,
                    Waypoint.SILENT
                )
                val evWaypoint = WaypointFactory.provideWaypoint(
                    Point.fromLngLat(3.0, 3.0),
                    "ev waypoint",
                    Point.fromLngLat(3.1, 3.1),
                    Waypoint.EV_CHARGING
                )
                return listOf(
                    arrayOf(
                        "no waypoints",
                        mockk<NavigationRoute> {
                            every { internalWaypoints() } returns emptyList()
                        },
                        0,
                        null
                    ),
                    arrayOf(
                        "nextWaypointIndex is too big",
                        mockk<NavigationRoute> {
                            every { internalWaypoints() } returns listOf(
                                regularWaypoint1,
                                regularWaypoint2
                            )
                        },
                        2,
                        null
                    ),
                    arrayOf(
                        "no leg waypoints left",
                        mockk<NavigationRoute> {
                            every { internalWaypoints() } returns listOf(
                                regularWaypoint1,
                                regularWaypoint2,
                                silentWaypoint,
                            )
                        },
                        2,
                        null
                    ),
                    arrayOf(
                        "next waypoint is regular",
                        mockk<NavigationRoute> {
                            every { internalWaypoints() } returns listOf(
                                regularWaypoint1,
                                silentWaypoint,
                                evWaypoint,
                                regularWaypoint2
                            )
                        },
                        3,
                        LegWaypointFactory.createLegWaypoint(
                            Point.fromLngLat(1.0, 1.0),
                            "regular waypoint",
                            Point.fromLngLat(1.1, 1.1),
                            LegWaypoint.REGULAR
                        )
                    ),
                    arrayOf(
                        "next waypoint is EV",
                        mockk<NavigationRoute> {
                            every { internalWaypoints() } returns listOf(
                                regularWaypoint1,
                                silentWaypoint,
                                regularWaypoint2,
                                evWaypoint,
                            )
                        },
                        3,
                        LegWaypointFactory.createLegWaypoint(
                            Point.fromLngLat(3.0, 3.0),
                            "ev waypoint",
                            Point.fromLngLat(3.1, 3.1),
                            LegWaypoint.EV_CHARGING_ADDED
                        )
                    )
                )
            }
        }

        @Test
        fun calculateNextLegWaypoint() {
            val tripStatus = TripStatus(
                null,
                mockk {
                    every {
                        nextWaypointIndex
                    } returns this@CalculateNextLegWaypointTest.nextWaypointIndex
                }
            )

            assertEquals(expectedResult, tripStatus.getCurrentLegDestination(routeWithWaypoints))
        }
    }
}
