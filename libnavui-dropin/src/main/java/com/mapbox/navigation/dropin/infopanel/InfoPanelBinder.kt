package com.mapbox.navigation.dropin.infopanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.graphics.Insets
import com.mapbox.navigation.core.internal.extensions.navigationListOf
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.dropin.navigationview.NavigationViewContext
import com.mapbox.navigation.ui.base.lifecycle.UIBinder
import com.mapbox.navigation.utils.internal.ifNonNull

/**
 * Base Binder class used for inflating and binding Info Panel layout.
 * Use [InfoPanelBinder.defaultBinder] to access default implementation.
 */
abstract class InfoPanelBinder : UIBinder {

    private var headerBinder: UIBinder? = null
    private var contentBinder: UIBinder? = null
    internal var context: NavigationViewContext? = null

    /**
     * Create layout that will host both header and content layouts.
     *
     * @param layoutInflater The LayoutInflater service.
     * @param root Parent view that provides a set of LayoutParams values for root of the returned hierarchy.
     *
     * @return ViewGroup that can host both header and content layouts. Returned view must not be attached to [root] view.
     */
    @UiThread
    abstract fun onCreateLayout(layoutInflater: LayoutInflater, root: ViewGroup): ViewGroup

    /**
     * Get layout that can be passed to header [UIBinder].
     *
     * @param layout ViewGroup returned by [onCreateLayout]
     *
     * @return ViewGroup that will be passed to the header UIBinder to install header view or
     *  `null` if header view is not supported by parent [layout].
     */
    @UiThread
    abstract fun getHeaderLayout(layout: ViewGroup): ViewGroup?

    /**
     * Get layout that can be passed to content [UIBinder].
     *
     * @param layout ViewGroup returned by [onCreateLayout]
     *
     * @return ViewGroup that will be passed to the content UIBinder to install content view or
     *  `null` if content view is not supported by parent [layout].
     */
    @UiThread
    abstract fun getContentLayout(layout: ViewGroup): ViewGroup?

    /**
     * Called when the Info Panel layout should apply system bar insets.
     *
     * This method should be overridden by a subclass to apply a policy different from the default behavior.
     * The default behavior applies insets with a value of [Insets.NONE].
     *
     * @param layout ViewGroup returned by [onCreateLayout]
     * @param insets system bars insets
     */
    @UiThread
    open fun applySystemBarsInsets(layout: ViewGroup, insets: Insets) = Unit

    internal fun setBinders(headerBinder: UIBinder?, contentBinder: UIBinder?) {
        this.headerBinder = headerBinder
        this.contentBinder = contentBinder
    }

    internal fun setNavigationViewContext(context: NavigationViewContext) {
        this.context = context
    }

    /**
     * Triggered when this view binder instance is attached. The [viewGroup] returns a
     * [MapboxNavigationObserver] which gives this view a simple lifecycle.
     */
    override fun bind(viewGroup: ViewGroup): MapboxNavigationObserver {
        val layout = onCreateLayout(
            LayoutInflater.from(viewGroup.context),
            viewGroup
        )
        viewGroup.removeAllViews()
        viewGroup.addView(layout)

        val binders = mutableListOf<MapboxNavigationObserver>()
        ifNonNull(headerBinder, getHeaderLayout(layout)) { binder, headerLayout ->
            binders.add(binder.bind(headerLayout))
        }
        ifNonNull(contentBinder, getContentLayout(layout)) { binder, contentLayout ->
            binders.add(binder.bind(contentLayout))
        }

        context?.apply {
            applySystemBarsInsets(layout, systemBarsInsets.value)
        }

        return navigationListOf(*binders.toTypedArray())
    }

    companion object {
        /**
         * Default Info Panel Binder.
         */
        fun defaultBinder(): InfoPanelBinder = MapboxInfoPanelBinder()
    }
}
