buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
    }
}

// Apply plugins at the top level
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}