package org.wselwood.common.tasks

import org.wselwood.common.gui.Event
import javafx.concurrent.WorkerStateEvent


/**
 *
 * User: Sparrow
 * Date: 13/06/12
 * Time: 20:13
 *
 */

class RecurringEvent(delay : Int, onEvent :() => Unit) {

    var runAgain : () => Boolean = null
    var activeThread : Thread = null

    def start() {
        val task : javafx.concurrent.Task[Boolean] = new javafx.concurrent.Task[Boolean]() {
            def call() : Boolean = {
                Thread.sleep(delay)
                true
            }
        }
        activeThread = new Thread(task)
        task.setOnSucceeded(Event[WorkerStateEvent]({() =>
            onEvent()
            if (runAgain == null) {
                start()
            }
            else if (runAgain()) {
                start()
            }
            else {
                activeThread = null
            }
        }))
        activeThread.setDaemon(true)
        activeThread.start()
    }

    def stop() {
        if (activeThread != null) {
            activeThread.interrupt()
        }
    }
}
