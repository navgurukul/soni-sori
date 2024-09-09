package org.merakilearn.scratchjr

import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import java.nio.channels.FileChannel
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.concurrent.Volatile
import kotlin.math.abs
import kotlin.math.max


/**
 * Manages sound recording for ScratchJr.
 *
 * @author markroth8
 */
class SoundRecorderManager(application: ScratchJrActivity) {
    /** Reference to the activity  */
    private val _application: ScratchJrActivity

    /** True if there is a microphone present on this device, false if not.  */
    private val _hasMicrophone: Boolean

    /** Android AudioRecorder  */
    private var _audioRecorder: AudioRecord? = null

    /** True if running in emulator (and only 8000 Hz supported) or false if not  */
    private val _runningInEmulator = Build.PRODUCT.startsWith("sdk")

    /** Sample rate chosen based on whether running in emulation  */
    private val _sampleRateHz =
        if (_runningInEmulator) SAMPLE_RATE_IN_HZ_EMULATOR else SAMPLE_RATE_IN_HZ_DEVICE

    /** Minimum buffer size, based on sample rate  */
    private val _minBufferSize = max(
        640.0,
        AudioRecord.getMinBufferSize(_sampleRateHz, CHANNEL_CONFIG, AUDIO_FORMAT).toDouble()
    )
        .toInt()

    /** Current file being recorded to  */
    private var _soundFile: File? = null

    /** RandomAccessFile for the file being recorded to  */
    private var _soundRandomAccessFile: RandomAccessFile? = null

    /** Channel pointing to the file to be written to  */
    private var _soundFileChannel: FileChannel? = null

    /** Buffer into which to read data  */
    private val _audioBuffer: ByteBuffer =
        ByteBuffer.allocateDirect(_minBufferSize).order(ByteOrder.LITTLE_ENDIAN)

    /** Short view into audio buffer  */
    private val _audioBufferShort: ShortBuffer = _audioBuffer.asShortBuffer()

    /** Thread that is recording audio  */
    private val _audioRecordExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    /** Future of the audio thread in progress  */
    private var _audioWriterTask: Future<Void?>? = null

    /** Buffer for WAV header  */
    private val _wavHeaderBuffer: ByteBuffer =
        ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

    /** Id of sound currently playing  */
    private var _soundPlayingId: Int? = null

    /** Current volume level detected during recording, with slow decay  */
    @Volatile
    private var _slowDecayVolumeLevel = 0.0


    init {
        Log.i(LOG_TAG, Build.PRODUCT + " Using audio sample rate " + _sampleRateHz + " Hz")
        _application = application
        _hasMicrophone = _application.packageManager.hasSystemFeature(
            PackageManager.FEATURE_MICROPHONE
        )
        if (!_hasMicrophone) {
            Log.i(LOG_TAG, "No microphone detected. Sound recording will be disabled.")
        }
    }

    fun hasMicrophone(): Boolean {
        return _hasMicrophone
    }

    /** Called when application starts / resumes  */
    @Synchronized
    fun open() {
    }

    /** Called when application sleeps  */
    @Synchronized
    fun close() {
        releaseAudioRecorder()
        stopPlayingSound()
    }

    /**
     * Returns the sound name or null if error.
     */
    @Synchronized
    fun startRecord(): String? {
        if (!_hasMicrophone) return null

        var result: String?

        releaseAudioRecorder()
        stopPlayingSound()

        _audioRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC, _sampleRateHz,
            CHANNEL_CONFIG, AUDIO_FORMAT, _minBufferSize * 16
        )


        // Mimic filename from iOS: time in seconds since 1970 as a double. Name is the md5 of the time.
        val now = String.format(Locale.US, "%f", System.currentTimeMillis() / 1000.0)
        val filename = java.lang.String.format("SND%s.wav", _application.iOManager.md5(now))
        _soundFile = File(_application.filesDir, filename)
        val parentDir = _soundFile!!.parentFile
        if (!parentDir.exists()) {
            parentDir.mkdirs()
        }
        Log.i(LOG_TAG, "Saving audio to file '" + _soundFile!!.path + "'")

        try {
            _soundRandomAccessFile = RandomAccessFile(_soundFile, "rw")
            _soundFileChannel = _soundRandomAccessFile!!.channel

            writeWAVHeader(_soundFileChannel, _sampleRateHz)

            _audioRecorder!!.startRecording()
            _audioWriterTask = _audioRecordExecutor.submit({
                val filename = _soundFile!!.path
                var totalBytesWritten: Long = 0
                try {
                    val ar: AudioRecord = _audioRecorder as AudioRecord
                    val raf: RandomAccessFile = _soundRandomAccessFile as RandomAccessFile
                    val buffer = _audioBuffer
                    val shortBuffer = _audioBufferShort // Little-endian buffer
                    val c = _soundFileChannel
                    while (true) {
                        // Read from buffer
                        buffer.rewind().limit(buffer.capacity())
                        val len = ar.read(buffer, _minBufferSize)
                        if ((len == -1) || ((len == 0) && (ar.recordingState == AudioRecord.RECORDSTATE_STOPPED))) {
                            break
                        }
                        if (len == AudioRecord.ERROR_BAD_VALUE) {
                            Log.e(LOG_TAG, "AudioRecord.read() returned BAD_VALUE")
                            break
                        }
                        if (len == AudioRecord.ERROR_INVALID_OPERATION) {
                            Log.e(LOG_TAG, "AudioRecord.read() returned INVALID_OPERATION")
                            break
                        }


                        // Write to file
                        buffer.rewind().limit(len)
                        c!!.write(buffer)


                        // Get current volume level (max of all samples taken this period)
                        shortBuffer.rewind()
                        var max = 0
                        while (shortBuffer.hasRemaining()) {
                            val s = abs(shortBuffer.get().toDouble()).toInt()
                            if (s > max) {
                                max = s
                            }
                        }
                        _slowDecayVolumeLevel =
                            max(1.0 * max / Short.MAX_VALUE, _slowDecayVolumeLevel * 0.85)

                        totalBytesWritten += len.toLong()
                    }
                    updateWAVHeader(raf, _soundFileChannel, totalBytesWritten)
                    c!!.close()
                } catch (e: IOException) {
                    Log.e(
                        LOG_TAG,
                        "Error writing wav file '$filename'", e
                    )
                }
            }, null)
            result = filename
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error opening wav file '" + _soundFile!!.path + "'", e)
            result = null
        }

        return result
    }

    @Throws(IOException::class)
    private fun writeWAVHeader(fileChannel: FileChannel?, sampleRateHz: Int) {
        val b = _wavHeaderBuffer
        b.rewind().limit(b.capacity())
        b.put("RIFF".toByteArray())
        val totalDataLen: Long = 0 // Placeholder until all audio is recorded
        b.putInt(totalDataLen.toInt())
        b.put("WAVE".toByteArray())
        b.put("fmt ".toByteArray())
        b.putInt(16) // size of fmt chunk
        b.putShort(1.toShort()) // format = 1 (PCM)
        val channels = 1.toShort()
        b.putShort(channels)
        b.putInt(sampleRateHz)
        val bitsPerSample: Short = 16
        val bytesPerSample = (bitsPerSample / 8).toShort()
        b.putInt(sampleRateHz * channels * bytesPerSample)
        b.putShort((channels * bytesPerSample).toShort())
        b.putShort(bitsPerSample)
        b.put("data".toByteArray())
        b.putInt(0) // Placeholder until all audio is recorded
        b.limit(b.position()).rewind()
        fileChannel!!.write(b)
    }

    @Throws(IOException::class)
    private fun updateWAVHeader(
        randomAccessFile: RandomAccessFile,
        fileChannel: FileChannel?,
        totalBytesWritten: Long
    ) {
        val b = _wavHeaderBuffer


        // Write totalDataLen
        randomAccessFile.seek(4)
        b.limit(8).position(4).mark()
        b.putInt((totalBytesWritten + 36).toInt())
        b.reset()
        fileChannel!!.write(b)


        // Write data chunk size
        randomAccessFile.seek(40)
        b.limit(44).position(40).mark()
        b.putInt(totalBytesWritten.toInt())
        b.reset()
        fileChannel.write(b)
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    fun stopRecord(): Boolean {
        if (!_hasMicrophone) return false

        var result: Boolean

        if (_audioRecorder == null) {
            Log.e(LOG_TAG, "Attempt to stop recording when no recording is taking place")
            result = false
        } else {
            _audioRecorder!!.stop()
            try {
                _audioWriterTask!![5, TimeUnit.SECONDS]
                result = true
                Log.i(LOG_TAG, "Stopped recording. File is " + _soundFile!!.length() + " bytes")
            } catch (e: InterruptedException) {
                Log.e(LOG_TAG, "Interrupted while waiting for audio writer to complete", e)
                result = false
            } catch (e: ExecutionException) {
                Log.e(LOG_TAG, "Execution exception while waiting for audio writer to complete", e)
                result = false
            } catch (e: TimeoutException) {
                Log.e(LOG_TAG, "Timeout while waiting for audio writer to complete", e)
                result = false
            } finally {
                releaseAudioRecorder()
            }
        }

        return result
    }

    /**
     * @return The number of seconds for the sound to play
     */
    @Synchronized
    @Throws(IllegalStateException::class)
    fun startPlay(): Double {
        requireNotNull(_soundFile) { "No sound available." }
        stopPlayingSound()
        val soundManager: SoundManager? = _application.soundManager
        if (soundManager != null) {
            _soundPlayingId = soundManager.playSound(_soundFile!!.path)
        }
        Log.i(LOG_TAG, "Sound id: $_soundPlayingId")
        if (soundManager != null) {
            return _soundPlayingId?.let { soundManager.soundDuration(it) }?.div(1000.0) ?: 0.0
        }
    }

    @Synchronized
    fun stopPlay() {
        stopPlayingSound()
    }

    @Synchronized
    fun recordClose(keep: Boolean) {
        stopPlayingSound()
        if (!keep) {
            if (_soundFile != null) {
                _soundFile!!.delete()
            }
        }
        _soundFile = null
    }

    val volume: Double
        /**
         * Return the volume level, from 0.0 to 1.0
         */
        get() {
            if (!_hasMicrophone) return 0.0
            return _slowDecayVolumeLevel
        }

    private fun releaseAudioRecorder() {
        if (_audioRecorder != null) {
            _audioRecorder!!.release()
            _audioRecorder = null
        }
    }

    private fun stopPlayingSound() {
        if (_soundPlayingId != null) {
            _application.soundManager?.stopSound(_soundPlayingId!!)
            _soundPlayingId = null
        }
    }

    companion object {
        private const val LOG_TAG = "SoundRecorderManager"

        // Recording parameters
        private const val SAMPLE_RATE_IN_HZ_DEVICE = 22050
        private const val SAMPLE_RATE_IN_HZ_EMULATOR = 8000 // Emulator only supports 8Khz
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}