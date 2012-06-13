package org.wselwood.teatimer.gui

import javafx.scene.control.{Label, Slider, Button}
import javafx.fxml.{Initializable, FXML}
import java.net.URL
import java.util.ResourceBundle
import javafx.beans.property.{SimpleIntegerProperty, SimpleBooleanProperty}
import javafx.scene.media.AudioClip
import org.wselwood.teatimer.TimeState
import org.wselwood.common.gui.ChangeListener
import org.wselwood.common.tasks.RecurringEvent

/**
 * Controller for our tea timer.
 *
 * User: Sparrow
 * Date: 04/06/12
 * Time: 13:04
 *
 */
class TeaTimerController extends Initializable {

    // the current state of what should be shown on the screen.
    var timerState = new TimeState()

    @FXML var secondsSlider     : Slider = null
    @FXML var minutesSlider     : Slider = null
    @FXML var hoursSlider       : Slider = null

    @FXML var secondsLabel      : Label  = null
    @FXML var minutesLabel      : Label  = null
    @FXML var hoursLabel        : Label  = null

    @FXML var startStopButton   : Button = null
    @FXML var resetButton       : Button = null

    // Keeps track of the state. Are we currently counting down or setting the values.
    var timerRunning            : SimpleBooleanProperty = new SimpleBooleanProperty(false)
    // The current value of the count down.
    var timerCount              : SimpleIntegerProperty = new SimpleIntegerProperty(0)

    // The thread that will be used to wait for a second and then fire another event.
    // Held at the controller level so we can interrupt it when we need to stop.
    private var ticker            : RecurringEvent = null

    // The last time we ran, how long was the timer set for.
    // Keep this so we can reset back when the timer expires or the user hits the reset button after stopping the timer
    // half way through.
    private var lastStartTime           : Int = -1

    /**
     * Setup the various listeners.
     * @param here ignored
     * @param res ignored as well.
     */
    def initialize(here: URL, res: ResourceBundle) {

        secondsSlider.valueProperty().bindBidirectional(timerState.seconds)
        minutesSlider.valueProperty().bindBidirectional(timerState.minutes)
        hoursSlider.valueProperty().bindBidirectional(timerState.hours)

        timerState.seconds.addListener(ChangeListener.pluralLabelUpdater(secondsLabel, "second"))
        timerState.minutes.addListener(ChangeListener.pluralLabelUpdater(minutesLabel, "minute"))
        timerState.hours.addListener(ChangeListener.pluralLabelUpdater(hoursLabel, "hour"))


        timerRunning.addListener(ChangeListener( { (oldValue: java.lang.Boolean, newValue: java.lang.Boolean) =>
            if (newValue) {
                // we are about to start the timer.
                this.startRun()
            }
            else {
                // we are stopping the timer.
                this.stopRun()
            }
        }))

        timerCount.addListener(ChangeListener( { (oldValue: Number, newValue: Number) =>
            this.setSlidersToCurrentTime(newValue.intValue())
            if (newValue.intValue() <= 0) {
                this.timerRunning.set(false)
            }
        }))
    }

    /**
     * Takes care of the start/stop button
     *
     * If there is a currently running thread we need to interrupt it so we don't get another tick a short time
     * after pressing the stop button.
     */
    def startStopButtonHandler() {
        if (ticker != null ) {
           ticker.stop()
        }
        timerRunning.setValue(! timerRunning.get()) // flip the state, the listener on the timerRunning property will
                                                    // take care of every thing else for us.
    }

    /**
     * Takes care of the reset button.
     *
     * If we stopped half way through a run the reset should be back to the last started time, rather than zero.
     * The second time we press it though we should reset back to zero.
     */
    def resetButtonHandler() {
        if (lastStartTime > 0) {
            setSlidersToCurrentTime(lastStartTime)
            lastStartTime = -1
        }
        else {
            setSlidersToCurrentTime(0)
        }
    }

    /**
     * Start the timer counting.
     */
    def startRun() {
        startStopButton.setText("Stop")
        resetButton.setDisable(true)
        if(lastStartTime == -1) {
            lastStartTime = calculateNumberOfSeconds()
            timerCount.set(lastStartTime)
        }
        else {
            timerCount.set(calculateNumberOfSeconds())
        }

        if (timerCount.get() == 0) {
            stopRun()
        }
        else {
            ticker = new RecurringEvent(1000, { () => this.timerCount.set(this.timerCount.get() - 1) })
            ticker.runAgain = {() => this.timerCount.get() > 0 && this.timerRunning.get()}
            ticker.start()


        }
    }

    /**
     * Stop the timer counting, either by expiry or the user pressing the stop button.
     */
    def stopRun() {
        startStopButton.setText("Start")
        resetButton.setDisable(false)

        if (timerCount.get() <= 0 && lastStartTime > 0) {
            timerExpired()
        }
    }

    /**
     * Effect at the end when the timer expires.
     */
    def timerExpired() {
        val boomSound : AudioClip = new AudioClip(this.getClass.getResource("/res/Explode.wav").toString)
        boomSound.play()

        setSlidersToCurrentTime(lastStartTime)
        lastStartTime = -1
    }

    /**
     * Work out the number of seconds the sliders are currently displaying.
     * @return the number of seconds that the three sliders are currently displaying.
     */
    def calculateNumberOfSeconds() : Int = {
        timerState.seconds.get() + (timerState.minutes.get() * 60) + (timerState.hours.get() * 60 * 60)
    }

    /**
     * Given a time, set the sliders to show it. This will ripple through to the labels thanks to the listeners.
     * @param time number of seconds to set the sliders to.
     */
    def setSlidersToCurrentTime(time : Int) {
        var t = time
        if (t == 0) {   // short cut the zero option so we don't have worry about div by zero
            timerState.seconds.set(0)
            timerState.minutes.set(0)
            timerState.hours.set(0)
        }
        else {
            val hours = t / (60 * 60)
            timerState.hours.set(hours)
            t = t % (60 * 60 )

            val minutes = t / 60
            timerState.minutes.set(minutes)
            t = t % 60

            timerState.seconds.set(t)
        }
    }

    def shutDown() {
        if (ticker != null ) {
           ticker.stop()
        }
    }
}
