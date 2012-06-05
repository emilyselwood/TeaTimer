package org.wselwood.teatimer.gui

import javafx.scene.control.{Label, Slider, Button}
import javafx.fxml.{Initializable, FXML}
import java.net.URL
import java.util.ResourceBundle
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.beans.property.{SimpleIntegerProperty, SimpleBooleanProperty}
import javafx.event.EventHandler
import javafx.concurrent.{Task, WorkerStateEvent}
import javafx.scene.media.AudioClip

/**
 *
 * User: Sparrow
 * Date: 04/06/12
 * Time: 13:04
 *
 */

class TeaTimerController extends Initializable {

    @FXML var secondsSlider     : Slider = null
    @FXML var minutesSlider     : Slider = null
    @FXML var hoursSlider       : Slider = null

    @FXML var secondsLabel      : Label  = null
    @FXML var minutesLabel      : Label  = null
    @FXML var hoursLabel        : Label  = null

    @FXML var startStopButton   : Button = null
    @FXML var resetButton       : Button = null

    var timerRunning            : SimpleBooleanProperty = new SimpleBooleanProperty(false)
    var timerCount              : SimpleIntegerProperty = new SimpleIntegerProperty(0)

    var activeThread            : Thread = null

    var lastStartTime           : Int = -1

    def initialize(here: URL, res: ResourceBundle) {
        secondsSlider.valueProperty().addListener(new SliderChangeListener(secondsLabel, "Second"))
        minutesSlider.valueProperty().addListener(new SliderChangeListener(minutesLabel, "Minute"))
        hoursSlider.valueProperty().addListener(new SliderChangeListener(hoursLabel, "Hour"))
        timerRunning.addListener(new RunningStateChangeListener(this))
        timerCount.addListener(new TimerCountChangeListener(this))
    }

    def startStopButtonHandler() {
        if (activeThread != null && activeThread.isAlive) {
           activeThread.interrupt()    // this should stop the thread when we press the stop button.
        }
        timerRunning.setValue(! timerRunning.get())
    }

    def resetButtonHandler() {
        if (lastStartTime > 0) {
            setSlidersToCurrentTime(lastStartTime)
            lastStartTime = 0
        }
        else {
            setSlidersToCurrentTime(0)
        }
    }

    def startRun() {
        startStopButton.setText("Stop")
        resetButton.setDisable(true)
        lastStartTime = calculateNumberOfSeconds()
        timerCount.set(lastStartTime)

        waitOneSecond()
    }

    def stopRun() {
        startStopButton.setText("Start")
        resetButton.setDisable(false)

        if (timerCount.get() <= 0 && lastStartTime > 0) {
            timerExpired()
        }
    }

    def timerExpired() {
        val boomSound : AudioClip = new AudioClip(this.getClass.getResource("/res/Explode.wav").toString)
        boomSound.play()

        setSlidersToCurrentTime(lastStartTime)
    }

    def calculateNumberOfSeconds() : Int = {
        secondsSlider.getValue.toInt + (minutesSlider.getValue.toInt * 60) + (hoursSlider.getValue.toInt * 60 * 60)
    }

    def setSlidersToCurrentTime(time : Int) {
        var t = time
        if (t == 0) {
            secondsSlider.setValue(0D)
            minutesSlider.setValue(0D)
            hoursSlider.setValue(0D)
        }
        else {
            val hours = t / (60 * 60)
            hoursSlider.setValue(hours)
            t = t % (60 * 60 )

            val minutes = t / 60
            minutesSlider.setValue(minutes)
            t = t % 60

            secondsSlider.setValue(t)
        }
    }

    /**
     * Deals with the delayed execution of the TimerTickEvent
     *
     * We have to run this in another thread so as not to stop the gui from responding.
     */
    def waitOneSecond() {
        val task : Task[Boolean] = new Task[Boolean]() {
            def call() : Boolean = {
                Thread.sleep(1000)
                true
            }
        }
        task.setOnSucceeded(new TimerTickEvent(this))

        activeThread = new Thread(task)
        activeThread.setDaemon(true)
        activeThread.start()
    }

}

/**
 * Deals with changes to the running state.
 *
 * @param controller the controller that this operates on.
 */
private class RunningStateChangeListener(controller: TeaTimerController) extends ChangeListener[java.lang.Boolean] {
    def changed(value: ObservableValue[_ <: java.lang.Boolean], oldValue: java.lang.Boolean, newValue: java.lang.Boolean) {
        if (newValue) {
            // we are about to start the timer.
            controller.startRun()
        }
        else {
            // we are stopping the timer.
            controller.stopRun()
        }
    }
}

/**
 * Used once a second to decrement the counters.
 * @param controller the controller we are working on.
 */
private class TimerTickEvent(controller: TeaTimerController) extends EventHandler[WorkerStateEvent] {
    def handle(p1: WorkerStateEvent) {
        controller.timerCount.set(controller.timerCount.get() - 1)
        if (controller.timerCount.get() > 0 && controller.timerRunning.get() == true) {
            // if there is still time wait another second.
            controller.waitOneSecond()

        }
    }
}

/**
 * Deals with the updates when the timer count changes. Triggers the update to the sliders and stops every thing running
 * when we get to 0 seconds remaining.
 *
 * @param controller the controller that contains the sliders we need to trigger the update on.
 */
private class TimerCountChangeListener(controller: TeaTimerController) extends ChangeListener[Number] {
    def changed(value: ObservableValue[_ <: Number], oldValue: Number, newValue: Number) {
        controller.setSlidersToCurrentTime(newValue.intValue())
        if (newValue.intValue() <= 0) {
            controller.timerRunning.set(false)
        }
    }
}

/**
 * Deals with keeping the labels in sync with the sliders.
 *
 * Sorts out the s or not being on the postfix. so "Second" is normally "Seconds"
 * unless the new value is 1 in which case its "1 Second"
 *
 * @param label the label to keep up-to-date.
 * @param postFix the post fix to append to the end of the label
 */
private class SliderChangeListener(label : Label, postFix : String) extends ChangeListener[Number] {

    def changed(value: ObservableValue[_ <: Number], oldValue: Number, newValue: Number) {
        val toBe = newValue.intValue()
        label.setText(toBe match {
            case 1 => "1 " + postFix
            case i: Int => i + " " + postFix + "s"
        })
    }
}


