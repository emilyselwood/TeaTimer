TeaTimer
========

Simple Egg Timer

Explosion sound effect from here: http://www.flashkit.com/soundfx/Mayhem/Explosives/Explode-eRco_Inc-7833/index.php
Icon from the oxygen set. http://www.oxygen-icons.org/

Top of the build.gradle there is a path to the javaFX installation rt/lib folder, make sure it is valid before building.

Once built with gradle run with (Change the class path for your javafx install) :
scala -classpath "c:/Program Files/Oracle/JavaFX 2.1 SDK/rt/lib/*.jar" build/libs/TeaTimer-1.0.jar
