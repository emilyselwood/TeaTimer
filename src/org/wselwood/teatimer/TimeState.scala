package org.wselwood.teatimer

import javafx.beans.property.{SimpleIntegerProperty, IntegerProperty, Property}


/**
 *
 * User: Sparrow
 * Date: 13/06/12
 * Time: 18:19
 *
 */

class TimeState {
    val seconds : IntegerProperty = new SimpleIntegerProperty(0)
    val minutes : IntegerProperty = new SimpleIntegerProperty(0)
    val hours : IntegerProperty = new SimpleIntegerProperty(0)
}
