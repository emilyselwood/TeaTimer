package org.wselwood.common.tasks

/**
 *
 * User: wselwood
 * Date: 14/06/12
 * Time: 21:16
 *
 */

object Task {
    /**
     * Simple wrapper to create tasks.
     * @param f the function for the task to do.
     * @tparam T The return type of the task
     * @return the task.
     */
    def apply[T]( f : => T ) : javafx.concurrent.Task[T] = {
        new javafx.concurrent.Task[T]() {
            def call() : T = {
                f
            }
        }
    }
}
