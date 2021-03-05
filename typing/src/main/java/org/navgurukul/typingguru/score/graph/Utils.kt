package org.navgurukul.typingguru.score.graph




/**
 * callback to draw custom TickLabel for each Tick.
 *
 * @param [tickPosition] position of ticks, start from 0.
 * @param [tick] speed value at the tick.
 * @return label to draw.
 */
typealias OnPrintTickLabelListener = (tickPosition :Int, tick :Float) -> CharSequence?


/**
 * do an action on all [Gauge.sections], with
 * only one redraw (after complete) to avoid redrawing
 * the speedometer on every change.
 * @param [action] an action to invoke for every section.
 */
fun Gauge.doOnSections(action: (section: Section) -> Unit) {
    val sections = ArrayList(this.sections)
    // this will also clear observers.
    this.clearSections()
    sections.forEach { action.invoke(it) }
    this.addSections(sections)
}