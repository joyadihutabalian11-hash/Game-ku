package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled

    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled

    private var musicJob: Job? = null

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        if (enabled) {
            startBackgroundMusic()
        } else {
            stopBackgroundMusic()
        }
    }

    fun playClickSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playTone(800.0, 40)
        }
    }

    fun playMoveSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playTone(523.25, 60) // C5
        }
    }

    fun playTurnSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playSweep(400.0, 600.0, 70)
        }
    }

    fun playJumpSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playSweep(300.0, 900.0, 120)
        }
    }

    fun playCollectSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playTone(587.33, 80) // D5
            delay(40)
            playTone(880.00, 120) // A5
        }
    }

    fun playWinSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for (note in notes) {
                playTone(note, 100)
                delay(30)
            }
        }
    }

    fun playErrorSound() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playTone(180.0, 120)
            delay(50)
            playTone(140.0, 150)
        }
    }

    private fun startBackgroundMusic() {
        if (musicJob?.isActive == true) return
        musicJob = scope.launch {
            val melody = doubleArrayOf(523.25, 659.25, 783.99, 659.25, 523.25, 783.99, 880.0, 783.99)
            var index = 0
            while (_isMusicEnabled.value) {
                if (_isMusicEnabled.value) {
                    playTone(melody[index % melody.size] * 0.5, 120, volume = 0.15f)
                    index++
                }
                delay(400)
            }
        }
    }

    private fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playTone(freqHz: Double, durationMs: Int, volume: Float = 0.5f) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val angle = 2.0 * PI * freqHz * time
                // Envelope decay to prevent clicks
                val envelope = 1.0 - (i.toDouble() / numSamples)
                sample[i] = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()
            scope.launch {
                delay(durationMs.toLong() + 50)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Ignore audio hardware exception fallback
        }
    }

    private fun playSweep(startFreq: Double, endFreq: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val time = i.toDouble() / sampleRate
                val angle = 2.0 * PI * currentFreq * time
                val envelope = 1.0 - progress
                sample[i] = (sin(angle) * Short.MAX_VALUE * 0.4 * envelope).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()
            scope.launch {
                delay(durationMs.toLong() + 50)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
