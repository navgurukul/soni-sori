package org.merakilearn.scratchjr

import android.content.res.AssetManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import android.util.SparseArray
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.util.Arrays


/**
 * Manages sound playing for ScratchJr.
 *
 * @author markroth8
 */
class SoundManager(
    /** Reference to the activity  */
    private val _application: ScratchJrActivity
) {
    /** Pool of pre-loaded sound effects  */
    private var _soundEffectPool: SoundPool? = null

    /** Maps filename to sound id in the sound effect pool  */
    private val _soundEffectMap: MutableMap<String, Int> = HashMap()

    /** Active sounds playing currently   */
    private val _activeSoundMap = SparseArray<MediaPlayer>()

    /** Running count of active sounds, so each one has a unique id  */
    private var _activeSoundCount = 0

    /** Set of assets in the HTML5 directory (cached for performance)  */
    private val _html5AssetList: Set<String?>

    init {
        var assetList: Set<String?>
        try {
            assetList = HashSet(listHTML5Assets(_application))
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Could not retrieve list of assets from application", e)
            assetList = emptySet<String>()
        }
        _html5AssetList = assetList
        loadSoundEffects()
    }

    /**
     * Play the sound at the given path, interrupting any current sound.
     */
    @Synchronized
    fun playSoundEffect(name: String) {
        playSoundEffectWithVolume(name, 1.0f)
    }

    /**
     * Play the sound at the given path, with the given volume, interrupting any current sound.
     */
    @Synchronized
    fun playSoundEffectWithVolume(name: String, volume: Float) {
        if (_soundEffectPool == null) {
            Log.e(
                LOG_TAG,
                "Sound effect pool is closed. Cannot play '$name' right now."
            )
        } else {
            val soundId = _soundEffectMap[name]
            if (soundId == null) {
                Log.e(
                    LOG_TAG,
                    "Could not find sound effect '$name'"
                )
            } else {
                _soundEffectPool!!.play(soundId, volume, volume, 0, 0, 1.0f)
            }
        }
    }

    /**
     * Play the given sound and return an id that can be used to stop the sound later.
     *
     * @param file Path to sound to play. If relative, sound will come from assets HTML5/ directory, else sound
     * comes from an absolute path.
     * @return An id which can be used to stop the sound later.
     */
    @Synchronized
    fun playSound(file: String): Int {
        if (file == "pop.mp3") {
            // We special-case pop.mp3 because it is easier to get it to play as a sound effect than a sound resource
            playSoundEffect(file)
            return -1
        }
        val result = _activeSoundCount++
        val player = MediaPlayer()
        _activeSoundMap.put(result, player)
        try {
            // If file starts with / it is an absolute path and use it.
            // Otherwise, path is relative, and find path relative to assets HTML5 directory.
            val fd: FileDescriptor
            val startOffset: Long
            val length: Long
            val closeTask: Runnable
            if (file.startsWith("/")) {
                val fis = FileInputStream(file)
                fd = fis.fd
                startOffset = 0
                length = File(file).length()
                val finalFile = file
                closeTask = Runnable {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                        Log.e(
                            LOG_TAG,
                            "Could not close file '$finalFile'", e
                        )
                    }
                }
            } else if (_html5AssetList.contains(file)) {
                val path = "HTML5/$file"
                val afd = _application.assets.openFd(path)
                fd = afd.fileDescriptor
                startOffset = afd.startOffset
                length = afd.length
                closeTask = Runnable {
                    try {
                        afd.close()
                    } catch (e: IOException) {
                        Log.e(
                            LOG_TAG,
                            "Could not close asset '$path'", e
                        )
                    }
                }
            } else {
                val soundFile = File(_application.filesDir, file)
                val `in` = FileInputStream(soundFile)
                fd = `in`.fd
                startOffset = 0
                length = soundFile.length()
                closeTask = Runnable {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                        Log.e(
                            LOG_TAG,
                            "Could not close asset '$soundFile'", e
                        )
                    }
                }
            }
            player.setDataSource(fd, startOffset, length)
            player.prepare()
            player.setOnCompletionListener { mp ->
                synchronized(this@SoundManager) {
                    mp.release()
                    _activeSoundMap.remove(result)
                    closeTask.run()
                }
            }
            player.start()
        } catch (e: IllegalArgumentException) {
            Log.e(LOG_TAG, "Could not play sound '$file'", e)
        } catch (e: IllegalStateException) {
            Log.e(LOG_TAG, "Could not play sound '$file'", e)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Could not play sound '$file'", e)
        }
        return result
    }

    /**
     * Returns true if the sound for the given id is playing, or false if not.
     */
    @Synchronized
    fun isPlaying(soundId: Int): Boolean {
        var result = false
        val player = _activeSoundMap[soundId]
        if (player != null) {
            result = try {
                player.isPlaying
            } catch (e: IllegalStateException) {
                false
            }
        }
        return result
    }

    /**
     * Returns the number of milliseconds long the given sound is, in duration.
     *
     * @throws IllegalArgumentException If there was no sound with the provided sound id.
     */
    @Synchronized
    @Throws(IllegalArgumentException::class)
    fun soundDuration(soundId: Int): Int {
        val result: Int
        val player = _activeSoundMap[soundId]
        if (player != null) {
            result = player.duration
        } else {
            throw IllegalArgumentException("No sound found for id '$soundId'")
        }
        return result
    }

    /**
     * Stop the sound with the given id.
     *
     * @param id The id of the sound to stop. If already stopped, does nothing.
     */
    @Synchronized
    fun stopSound(soundId: Int) {
        val player = _activeSoundMap[soundId]
        player?.stop()
    }

    /**
     * Load all sound effects
     */
    @Synchronized
    fun open() {
        loadSoundEffects()
    }

    /**
     * Release all resources
     */
    @Synchronized
    fun close() {
        releaseSoundEffects()
    }

    /**
     * Release the media player if it already exists.
     */
    private fun releaseSoundEffects() {
        if (_soundEffectPool != null) {
            _soundEffectPool!!.release()
            _soundEffectPool = null
            _soundEffectMap.clear()
        }
        _activeSoundMap.clear()
    }

    private fun loadSoundEffects() {
        if (_soundEffectPool == null) {
            _soundEffectPool = SoundPool(11, AudioManager.STREAM_MUSIC, 0)

            // Load all sound effects into memory
            val assetManager = _application.assets
            try {
                val soundEffects = assetManager.list("HTML5/sounds")
                loadSoundEffects(assetManager, "HTML5/sounds/", *soundEffects!!)
                loadSoundEffects(assetManager, "HTML5/", "pop.mp3")
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Could not list sound assets", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun loadSoundEffects(
        assetManager: AssetManager,
        basePath: String,
        vararg soundEffects: String
    ) {
        for (filename in soundEffects) {
            val fd = assetManager.openFd(basePath + filename)
            val soundId = _soundEffectPool!!.load(fd, 1)
            _soundEffectMap[filename] = soundId
        }
    }

    @Throws(IOException::class)
    private fun listHTML5Assets(application: ScratchJrActivity): List<String?> {
        val result = ArrayList<String?>()
        result.addAll(Arrays.asList(*application.assets.list("HTML5")))
        for (path in application.assets.list("HTML5/samples")!!) {
            result.add("samples/$path")
        }
        return result
    }

    companion object {
        private const val LOG_TAG = "ScratchJr.SoundManager"
    }
}