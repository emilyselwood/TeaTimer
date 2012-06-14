package org.wselwood.common.tasks

import org.wselwood.common.gui.EventHandler
import javafx.concurrent.WorkerStateEvent


/**
 * Basic recurring delayed execution takes a delay and action to perform after each delay.
 *
 * Can also supply runAgain which will allow you to control when this stops running. Defaults to looping for ever.
 *
 * User: wselwood
 * Date: 13/06/12
 * Time: 20:13
 *
 */
class RecurringEvent(delay : Int, onEvent : => Unit) {

    var runAgain : () => Boolean = { () => true } // default to keep looping.
    private var activeThread : Option[Thread] = None

    def start() {
        val task = Task[Boolean]({
            Thread.sleep(delay)
            true
        })
        activeThread = Some(new Thread(task))
        task.setOnSucceeded(EventHandler[WorkerStateEvent]({
            onEvent
            if(runAgain()) {
                start()
            }
            else {
                activeThread = None
            }
        }))
        activeThread.get.setDaemon(true)
        activeThread.get.start()
    }

    def stop() {
        activeThread.foreach({ _.interrupt() })
        activeThread = None
    }
}
