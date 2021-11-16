package com.mapbox.navigation.base.internal.utils

import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

suspend fun parseDirectionsResponse(
    json: String,
    options: RouteOptions?,
    onMetadata: (String) -> Unit,
): List<DirectionsRoute> =
    withContext(Dispatchers.IO) {
        val jsonObject = JSONObject(json)
        val uuid: String? = if (jsonObject.has(UUID)) {
            jsonObject.getString(UUID)
        } else {
            null
        }

        // TODO remove after https://github.com/mapbox/navigation-sdks/issues/1229
        if (jsonObject.has(METADATA)) {
            onMetadata(jsonObject.getString(METADATA))
        }

        // TODO simplify when https://github.com/mapbox/mapbox-java/issues/1292 is finished
        val response = DirectionsResponse.fromJson(json, options, uuid)

        response.routes()
    }

suspend fun parseNativeDirectionsAlternative(
    json: String,
    options: RouteOptions?,
): DirectionsRoute? =
    withContext(Dispatchers.IO) {
        val jsonObject = JSONObject(json)
        val uuid: String? = if (jsonObject.has(UUID)) {
            jsonObject.getString(UUID)
        } else {
            null
        }

        val jsonRoutes = jsonObject.getJSONArray("routes")
        val jsonRoute: DirectionsRoute = jsonRoutes.let { routesArray ->
            for (i in 0 until routesArray.length()) {
                if (!routesArray.isNull(i)) {
                    // TODO simplify after https://github.com/mapbox/mapbox-java/issues/1292
                    return@let DirectionsRoute.fromJson(
                        routesArray.getJSONObject(i).toString(),
                        options,
                        uuid
                    ).run {
                        toBuilder()
                            .routeIndex(i.toString())
                            .build()
                    }
                }
            }
            return@withContext null
        }
        return@withContext jsonRoute
    }

private const val UUID = "uuid"
private const val METADATA = "metadata"
