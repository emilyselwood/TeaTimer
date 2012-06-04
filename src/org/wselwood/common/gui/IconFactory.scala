package org.wselwood.common.gui

import javafx.scene.image.{Image, ImageView}
import collection.mutable


/**
 * Simple two level cache of images which has methods to return new ImageViews so the same images
 * can be used in several places with the smallest amount of overhead.
 *
 * Expects to find the images in "/res/icons/" by default. Change this if your icons are some where else
 *
 *
 * User: Sparrow
 * Date: 16/02/12
 * Time: 08:09
 *
 */

object IconFactory {

    val iconPath = "/res/icons/"
    var cache: mutable.Map[String, mutable.Map[String, Image]] = mutable.Map.empty[String, mutable.Map[String, Image]]

    def apply(name: String): ImageView = {
        getImage(name, "16x16")
    }

    def apply(name: String, size: String): ImageView = {
        getImage(name, size)
    }

    private def getImage(name: String, size: String): ImageView = {
        val realSize = convertSize(size)
        new ImageView(cache.getOrElseUpdate(realSize, {
            // fetch from icon size level of cache
            mutable.Map.empty[String, Image]
        }).getOrElseUpdate(name, {
            // fetch the actual icon.
            new Image(getClass.getResourceAsStream(iconPath + realSize + "/" + name + ".png"))
        }))
    }

    private def convertSize(size: String): String = {
        if (size.contains("x")) {
            size
        }
        else {
            size + "x" + size
        }
    }

}
