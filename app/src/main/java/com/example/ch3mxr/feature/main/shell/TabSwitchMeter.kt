package com.example.ch3mxr.feature.main.shell

import android.os.SystemClock
import android.util.Log

/** Prueba temporal. Borrar este archivo después del diagnóstico. */
internal class TabSwitchMeter {
    private var target: MainTab? = null
    private var beganAt: Long = 0L
    private var compositionLogged = false

    fun onTap(tab: MainTab) {
        target = tab
        beganAt = SystemClock.elapsedRealtimeNanos()
        compositionLogged = false
        Log.d("ChemXRPerf", "TOQUE tab=${tab.name}")
    }

    fun onComposed(tab: MainTab) {
        if (target == tab && !compositionLogged) {
            compositionLogged = true
            Log.d("ChemXRPerf", "COMPUESTO tab=${tab.name} en ${elapsedMs()} ms")
        }
    }

    fun onDraw(tab: MainTab) {
        if (target == tab) {
            Log.d("ChemXRPerf", "DIBUJADO tab=${tab.name} en ${elapsedMs()} ms")
            target = null
        }
    }

    private fun elapsedMs(): Long = (SystemClock.elapsedRealtimeNanos() - beganAt) / 1_000_000
}
