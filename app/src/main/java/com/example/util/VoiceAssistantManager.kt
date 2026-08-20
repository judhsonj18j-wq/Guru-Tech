package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAssistantManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "VoiceAssistantManager"

    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecognizerAvailable = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _spokenRms = MutableStateFlow(0f)
    val spokenRms: StateFlow<Boolean> = _isListening.asStateFlow()

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
            isRecognizerAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Voice Assistant: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech?.setLanguage(Locale.US)
            }
            textToSpeech?.setSpeechRate(1.0f)
            textToSpeech?.setPitch(1.0f)
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
            isTtsReady = true
        } else {
            Log.e(TAG, "TTS Initialization failed with status $status")
        }
    }

    fun speak(text: String) {
        if (!isTtsReady || textToSpeech == null) {
            Log.w(TAG, "TTS is not ready")
            return
        }
        stopSpeaking()
        val cleanText = cleanMarkdownForSpeech(text)
        if (cleanText.isBlank()) return

        val utteranceId = "EduAI_${System.currentTimeMillis()}"
        _isSpeaking.value = true
        textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        _isSpeaking.value = false
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            stopSpeaking()
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask anything on the internet...")
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _isListening.value = true
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _isListening.value = false
                }

                override fun onError(error: Int) {
                    _isListening.value = false
                    val errorMsg = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                        SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timed out"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please try again."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                        SpeechRecognizer.ERROR_SERVER -> "Recognition server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                        else -> "Speech recognition error ($error)"
                    }
                    Log.e(TAG, "Speech Recognizer Error: $errorMsg")
                    onError(errorMsg)
                }

                override fun onResults(results: Bundle?) {
                    _isListening.value = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spokenText = matches?.firstOrNull()
                    if (!spokenText.isNullOrBlank()) {
                        onResult(spokenText)
                    } else {
                        onError("Could not catch that, please try speaking again.")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            Log.e(TAG, "Failed to start speech recognition: ${e.message}")
            onError("Speech recognition unavailable: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognizer: ${e.message}")
        }
        _isListening.value = false
    }

    fun release() {
        stopSpeaking()
        textToSpeech?.shutdown()
        textToSpeech = null
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying speech recognizer: ${e.message}")
        }
    }

    companion object {
        fun cleanMarkdownForSpeech(input: String): String {
            return input
                .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
                .replace(Regex("\\*(.*?)\\*"), "$1")
                .replace(Regex("`{1,3}(.*?)`{1,3}"), "$1")
                .replace(Regex("#+\\s*"), "")
                .replace(Regex("^[•\\-*]\\s+", RegexOption.MULTILINE), "")
                .replace(Regex("[\n\r]+"), ". ")
                .trim()
        }
    }
}
