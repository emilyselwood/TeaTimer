package org.wselwood.common.gui

import javafx.beans.value.ObservableValue
import javafx.scene.control.Label


/**
 *
 * User: wselwood
 * Date: 13/06/12
 * Time: 07:52
 *
 */

object ChangeListener {

    /**
     * Wrapper around the change listener so we can feed it a closure that takes two arguments.
     * @param f the closure
     * @tparam T the type of the change listener.
     * @return the change listener we want
     */
    def apply[T](f: (T, T) => Unit ) : javafx.beans.value.ChangeListener[T] = {
        new javafx.beans.value.ChangeListener[T] {
            def changed(observableValue: ObservableValue[_ <: T], oldValue : T, newValue : T) {
                f(oldValue, newValue)
            }
        }
    }

    /**
     * Deals with keeping labels in sync with a number property
     *
     * Sorts out the s or not being on the postfix. so "Second" is normally "Seconds"
     * unless the new value is 1 in which case its "1 Second"
     *
     * @param label the label to keep up-to-date.
     * @param postFix the post fix to append to the end of the label
     */
    def pluralLabelUpdater(label : Label, postFix : String) : javafx.beans.value.ChangeListener[Number] = {
        new javafx.beans.value.ChangeListener[Number] {

            def changed(value: ObservableValue[_ <: Number], oldValue: Number, newValue: Number) {
                val toBe = newValue.intValue()
                label.setText(toBe match {
                    case 1 => "1 " + postFix
                    case i: Int => i + " " + postFix + "s"
                })
            }
        }
    }

}
