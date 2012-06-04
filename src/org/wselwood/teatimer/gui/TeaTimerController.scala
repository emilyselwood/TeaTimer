package org.wselwood.teatimer.gui

import javafx.scene.control.{Label, Slider}
import javafx.fxml.{Initializable, FXML}
import java.net.URL
import java.util.ResourceBundle
import javafx.beans.value.{ObservableValue, ChangeListener}

/**
 *
 * User: Sparrow
 * Date: 04/06/12
 * Time: 13:04
 *
 */

class TeaTimerController extends Initializable {

    @FXML var secondsSlider : Slider = null
    @FXML var minutesSlider : Slider = null
    @FXML var hoursSlider   : Slider = null

    @FXML var secondsLabel  : Label  = null
    @FXML var minutesLabel  : Label  = null
    @FXML var hoursLabel    : Label  = null

    def initialize(here: URL, res: ResourceBundle) {
        secondsSlider.valueProperty().addListener(new sliderChangeListener(secondsLabel, "Second"))
        minutesSlider.valueProperty().addListener(new sliderChangeListener(minutesLabel, "Minute"))
        hoursSlider.valueProperty().addListener(new sliderChangeListener(hoursLabel, "Hour"))
    }

    def startStopButtonHandler() {

    }

    def resetButtonHandler() {
        secondsSlider.setValue(0D)
        minutesSlider.setValue(0D)
        hoursSlider.setValue(0D)
    }

}

class sliderChangeListener(label : Label, postFix : String) extends ChangeListener[Number] {

    def changed(value: ObservableValue[_ <: Number], oldValue: Number, newValue: Number) {
        val toBe = newValue.intValue()
        label.setText(toBe match {
            case 1 => "1 " + postFix
            case i: Int => i + " " + postFix + "s"
        })
    }
}


