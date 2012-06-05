package org.wselwood.teatimer

import gui.TeaTimerController
import javafx.application.Application
import javafx.event.EventHandler
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.stage.{Stage, WindowEvent}
import javafx.scene.Scene
import javafx.scene.image.Image
import org.wselwood.common.gui.{IconFactory, SceneLoader}

/**
 * Main set up of the random dice gui
 * User: wselwood
 * Date: 18/05/12
 * Time: 18:37
 *
 * The object just starts the application and has a few bits of default state.
 */
object TeaTimer {

    val title = "Tea Timer"

    def main(args: Array[String]) {
        Application.launch(classOf[TeaTimer], args: _*) // have to use :_* to pass the array through to the var args
    }
}

// Just sets up the stage. Has to be a class as it is instantiated by the Application.launch above.
class TeaTimer extends Application {

    var controller : TeaTimerController = null
    override def start(primaryStage: Stage) {

        // Initial setup of the stage
        primaryStage.setTitle(TeaTimer.title)
        // In this case this shouldn't be re-sizable.
        primaryStage.setResizable(false)
        // Set the window icon, use the icon factory to deal with the detritus.
        primaryStage.getIcons.add(IconFactory.getImage("alarmclock", "32x32"))

        // Set a few handlers that can be useful. Remove if you don't need them.
        primaryStage.setOnHiding(new ShutdownHandler(this))
        // Disabled as we have stopped resizing
        //primaryStage.heightProperty().addListener(new WindowSizeListener("window.Height", primaryStage))
        //primaryStage.widthProperty().addListener(new WindowSizeListener("window.Width", primaryStage))

        // Actually load our scene. As long as your controller and fxml file are in the same place this should work fine.
        // The second parameter is to the properties file for localization.
        val loaded = SceneLoader[TeaTimerController](classOf[TeaTimerController].getResource("TeaTimer.fxml"), "org.wselwood.teatimer.gui.TeaTimer")
        controller = loaded.controller
        // Create the scene from the one we loaded.
        val scene = new Scene(loaded.parent)

        // Attach to the stage and show the stage
        primaryStage.setScene(scene)
        primaryStage.show()
    }
}

// Event handlers for shut down and window size.
class ShutdownHandler(parent : TeaTimer) extends EventHandler[WindowEvent] {
    def handle(event: WindowEvent) {
        if (parent.controller != null) {
            parent.controller.shutDown()
        }
    }
}

// There is some fun here due to the differences in the class hierarchy between java and Scala. Mainly there is no Number.
class WindowSizeListener(val attribute: String, stage: Stage) extends ChangeListener[Number] {
    def changed(value: ObservableValue[_ <: Number], old: Number, updated: Number) {

    }
}
