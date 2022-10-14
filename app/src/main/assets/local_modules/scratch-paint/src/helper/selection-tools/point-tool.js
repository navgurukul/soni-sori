import paper from '@scratch/paper';
import {HANDLE_RATIO, snapDeltaToAngle} from '../math';
import {ART_BOARD_WIDTH, ART_BOARD_HEIGHT} from '../view';
import {clearSelection, getSelectedLeafItems, getSelectedSegments} from '../selection';

/** Subtool of ReshapeTool for moving control points. */
class PointTool {
    /**
     * @param {function} setSelectedItems Callback to set the set of selected items in the Redux state
     * @param {function} clearSelectedItems Callback to clear the set of selected items in the Redux state
     * @param {!function} onUpdateImage A callback to call when the image visibly changes
     */
    constructor (setSelectedItems, clearSelectedItems, onUpdateImage) {
        /**
         * Deselection often does not happen until mouse up. If the mouse is dragged before
         * mouse up, deselection is cancelled. This variable keeps track of which paper.Item to deselect.
         */
        this.deselectOnMouseUp = null;
        /**
         * Delete control point does not happen until mouse up. If the mouse is dragged before
         * mouse up, delete is cancelled. This variable keeps track of the hitResult that triggers delete.
         */
        this.deleteOnMouseUp = null;
        /**
         * There are 2 cases for deselection: Deselect this, or deselect everything but this.
         * When invert deselect is true, deselect everything but the item in deselectOnMouseUp.
         */
        this.invertDeselect = false;
        this.selectedItems = null;
        this.setSelectedItems = setSelectedItems;
        this.clearSelectedItems = clearSelectedItems;
        this.lastPoint = null;
        this.onUpdateImage = onUpdateImage;
    }

    /**
     * @param {!object} hitProperties Describes the mouse event
     * @param {!paper.HitResult} hitProperties.hitResult Data about the location of the mouse click
     * @param {?boolean} hitProperties.multiselect Whether to multiselect on mouse down (e.g. shift key held)
     * @param {?boolean} hitProperties.doubleClicked Whether this is the second click in a short time
     */
    onMouseDown (hitProperties) {
        // Remove point
        if (hitProperties.doubleClicked) {
            this.deleteOnMouseUp = hitProperties.hitResult;
        }
        if (hitProperties.hitResult.segment.selected) {
            // selected points with no handles get handles if selected again
            if (hitProperties.multiselect) {
                this.deselectOnMouseUp = hitProperties.hitResult.segment;
            } else {
                this.deselectOnMouseUp = hitProperties.hitResult.segment;
                this.invertDeselect = true;
                hitProperties.hitResult.segment.selected = true;
            }
        } else {
            if (!hitProperties.multiselect) {
                clearSelection(this.clearSelectedItems);
            }
            hitProperties.hitResult.segment.selected = true;
        }
        
        this.selectedItems = getSelectedLeafItems();
    }
    /**
     * @param {!object} hitProperties Describes the mouse event
     * @param {!paper.HitResult} hitProperties.hitResult Data about the location of the mouse click
     * @param {?boolean} hitProperties.multiselect Whether to multiselect on mouse down (e.g. shift key held)
     */
    addPoint (hitProperties) {
        // Length of curve from previous point to new point
        const beforeCurveLength = hitProperties.hitResult.location.curveOffset;
        const afterCurveLength =
            hitProperties.hitResult.location.curve.length - hitProperties.hitResult.location.curveOffset;

        // Handle length based on curve length until next point
        let handleIn = hitProperties.hitResult.location.tangent.multiply(-beforeCurveLength * HANDLE_RATIO);
        let handleOut = hitProperties.hitResult.location.tangent.multiply(afterCurveLength * HANDLE_RATIO);
        // Don't let one handle overwhelm the other (results in path doubling back on itself weirdly)
        if (handleIn.length > 3 * handleOut.length) {
            handleIn = handleIn.multiply(3 * handleOut.length / handleIn.length);
        }
        if (handleOut.length > 3 * handleIn.length) {
            handleOut = handleOut.multiply(3 * handleIn.length / handleOut.length);
        }

        const beforeSegment = hitProperties.hitResult.item.segments[hitProperties.hitResult.location.index];
        const afterSegment = hitProperties.hitResult.item.segments[hitProperties.hitResult.location.index + 1];

        // Add segment
        const newSegment = new paper.Segment(hitProperties.hitResult.location.point, handleIn, handleOut);
        hitProperties.hitResult.item.insert(hitProperties.hitResult.location.index + 1, newSegment);
        hitProperties.hitResult.segment = newSegment;
        if (!hitProperties.multiselect) {
            clearSelection(this.clearSelectedItems);
        }
        newSegment.selected = true;

        // Adjust handles of curve before and curve after to account for new curve length
        if (beforeSegment && beforeSegment.handleOut) {
            if (afterSegment) {
                beforeSegment.handleOut =
                    beforeSegment.handleOut.multiply(beforeCurveLength * HANDLE_RATIO / beforeSegment.handleOut.length);
            } else {
                beforeSegment.handleOut = null;
            }
        }
        if (afterSegment && afterSegment.handleIn) {
            if (beforeSegment) {
                afterSegment.handleIn =
                    afterSegment.handleIn.multiply(afterCurveLength * HANDLE_RATIO / afterSegment.handleIn.length);
            } else {
                afterSegment.handleIn = null;
            }
        }
    }
    removePoint (hitResult) {
        const index = hitResult.segment.index;
        hitResult.item.removeSegment(index);

        // Adjust handles of curve before and curve after to account for new curve length
        const beforeSegment = hitResult.item.segments[index - 1];
        const afterSegment = hitResult.item.segments[index];
        const curveLength = beforeSegment ? beforeSegment.curve ? beforeSegment.curve.length : null : null;
        if (beforeSegment && beforeSegment.handleOut) {
            if (afterSegment) {
                beforeSegment.handleOut =
                    beforeSegment.handleOut.multiply(curveLength * HANDLE_RATIO / beforeSegment.handleOut.length);
            } else {
                beforeSegment.handleOut = null;
            }
        }
        if (afterSegment && afterSegment.handleIn) {
            if (beforeSegment) {
                afterSegment.handleIn =
                    afterSegment.handleIn.multiply(curveLength * HANDLE_RATIO / afterSegment.handleIn.length);
            } else {
                afterSegment.handleIn = null;
            }
        }
    }
    onMouseDrag (event) {
        // A click will deselect, but a drag will not
        this.deselectOnMouseUp = null;
        this.invertDeselect = false;
        this.deleteOnMouseUp = null;
        
        const point = event.point;
        point.x = Math.max(0, Math.min(point.x, ART_BOARD_WIDTH));
        point.y = Math.max(0, Math.min(point.y, ART_BOARD_HEIGHT));

        if (!this.lastPoint) this.lastPoint = event.lastPoint;
        const dragVector = point.subtract(event.downPoint);
        const delta = point.subtract(this.lastPoint);
        this.lastPoint = point;
        
        const selectedSegments = getSelectedSegments();
        for (const seg of selectedSegments) {
            // add the point of the segment before the drag started
            // for later use in the snap calculation
            if (!seg.origPoint) {
                seg.origPoint = seg.point.clone();
            }

            if (event.modifiers.shift) {
                seg.point = seg.origPoint.add(snapDeltaToAngle(dragVector, Math.PI / 4));
            } else {
                seg.point = seg.point.add(delta);
            }
        }
    }
    onMouseUp () {
        this.lastPoint = null;
        
        // resetting the items and segments origin points for the next usage
        let moved = false;
        const selectedSegments = getSelectedSegments();
        for (const seg of selectedSegments) {
            if (seg.origPoint && !seg.equals(seg.origPoint)) {
                moved = true;
            }
            seg.origPoint = null;
        }

        // If no drag occurred between mouse down and mouse up, then we can go through with deselect
        // and delete
        if (this.deselectOnMouseUp) {
            if (this.invertDeselect) {
                clearSelection(this.clearSelectedItems);
                this.deselectOnMouseUp.selected = true;
            } else {
                this.deselectOnMouseUp.selected = false;
            }
            this.deselectOnMouseUp = null;
            this.invertDeselect = false;
        }
        if (this.deleteOnMouseUp) {
            this.removePoint(this.deleteOnMouseUp);
        }
        this.selectedItems = null;
        this.setSelectedItems();
        if (moved || this.deleteOnMouseUp) {
            this.deleteOnMouseUp = null;
            this.onUpdateImage();
        }
    }
}

export default PointTool;
