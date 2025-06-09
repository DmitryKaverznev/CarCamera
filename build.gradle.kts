// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // id("com.android.application") version "8.7.0-rc01" apply false
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("androidx.navigation.safeargs") version "2.7.7" apply false
}