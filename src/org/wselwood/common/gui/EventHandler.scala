package org.wselwood.common.gui


/**
 *
 * User: wselwood
 * Date: 13/06/12
 * Time: 19:28
 *
 */

object EventHandler {

    /**
     * Very simple wrapper around the the EventHandler This version makes no use of the event passed in.
     * @param f the function to call when the event happens.
     * @tparam T the type of event.
     * @return the event handler.
     */
    def apply[T <: javafx.event.Event](f : => Unit) : javafx.event.EventHandler[T] = {
        new javafx.event.EventHandler[T]() {
            def handle(p1: T) {
                f
            }
        }
    }

    /**
     * Another wrapper that makes use of the parameter this time.
     * @param f Function with one parameter which is the event.
     * @tparam T The type of event we expect to receive.
     * @return the event handler.
     */
    def withEvent[T <: javafx.event.Event] (f: ( T ) => Unit) : javafx.event.EventHandler[T] = {
        new javafx.event.EventHandler[T]() {
            def handle(p1: T) {
                f(p1)
            }
        }
    }

}
