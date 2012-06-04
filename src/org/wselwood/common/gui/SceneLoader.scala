package org.wselwood.common.gui

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{JavaFXBuilderFactory, FXMLLoader}
import javafx.scene.Parent

/**
 * Simple wrapper around the creation of an fxml scene. Gives back both the Parent and the controller object.
 *
 * User: Sparrow
 * Date: 13/02/12
 * Time: 07:06
 *
 */
// Holder class to wrap up the return type nicely.
case class LoadedScene[T](parent: Parent, controller: T)

object SceneLoader {

    /**
     * Load a scene and return both the Parent object from the scene and the controller.
     * @param location The URL to the .fxml file that describes the scene.
     * @param resource The name of the resource bundle that contains the properties for the scene
     * @tparam T The type of the controller.
     * @return LoadedScene object containing both the parent and the controller.
     */
    def apply[T](location: URL, resource: String): LoadedScene[T] = {
        val loader = new FXMLLoader()
        loader.setLocation(location)
        loader.setResources(ResourceBundle.getBundle(resource))

        loader.setBuilderFactory(new JavaFXBuilderFactory())

        val root = loader.load(location.openStream()).asInstanceOf[Parent]
        val controller = loader.getController

        new LoadedScene[T](root, controller.asInstanceOf[T])

    }

}
