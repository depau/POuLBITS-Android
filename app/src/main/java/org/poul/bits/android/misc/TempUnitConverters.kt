package org.poul.bits.android.misc

fun celsiusToFahrenheit(temp: Double): Double = (temp * 9.0 / 5.0) + 32
fun fahrenheitToCelsius(temp: Double): Double = (temp - 32) * 5.0 / 9.0

fun celsiusToKelvin(temp: Double): Double = temp + 273.15
fun kelvinToCelsius(temp: Double): Double = temp - 273.15

fun fahrenheitToKelvin(temp: Double) = celsiusToKelvin(fahrenheitToCelsius(temp))
fun kelvinToFahrenheit(temp: Double) = celsiusToFahrenheit(kelvinToCelsius(temp))