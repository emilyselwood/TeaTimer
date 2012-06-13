package org.wselwood.common.gui

import javafx.event.EventHandler

/**
 *
 * User: Sparrow
 * Date: 13/06/12
 * Time: 19:28
 *
 */

object Event {

    def apply[T <: javafx.event.Event](f : () => Unit) : EventHandler[T] = {
        new EventHandler[T]() {
            def handle(p1: T) {
                f()
            }
        }
    }

    def withEvent[T <: javafx.event.Event] (f: ( T ) => Unit) : EventHandler[T] = {
        new EventHandler[T]() {
            def handle(p1: T) {
                f(p1)
            }
        }
    }

}
