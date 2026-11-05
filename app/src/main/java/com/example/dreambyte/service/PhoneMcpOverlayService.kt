package com.example.dreambyte.service

import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity

/**
 * PhoneMCP Overlay Service
 *
 * Implements the overlay architecture requested for PhoneMCP:
 * 1. POINTER WINDOW: Uses FLAG_NOT_TOUCHABLE + FLAG_NOT_FOCUSABLE. Size is WRAP_CONTENT.
 *    Touches pass through 100% transparently to whatever is underneath.
 * 2. STOP BUTTON WINDOW: A completely separate window using WRAP_CONTENT (NOT MATCH_PARENT).
 *    Only the button itself intercepts touch events, allowing the user to abort/stop automation.
 * 3. NO MATCH_PARENT anywhere: Ensures zero screen blocking.
 */
class PhoneMcpOverlayService : Service() {

  companion object {
    const val ACTION_START = "com.example.dreambyte.ACTION_START_PHONEMCP_OVERLAY"
    const val ACTION_STOP = "com.example.dreambyte.ACTION_STOP_PHONEMCP_OVERLAY"
    const val ACTION_MOVE_POINTER = "com.example.dreambyte.ACTION_MOVE_POINTER"
    const val ACTION_TRIGGER_CLICK = "com.example.dreambyte.ACTION_TRIGGER_CLICK"

    const val EXTRA_X = "extra_x"
    const val EXTRA_Y = "extra_y"

    const val NOTIFICATION_CHANNEL_ID = "phonemcp_overlay_channel"
    const val NOTIFICATION_ID = 4099

    var isServiceRunning = false
      private set
  }

  private var windowManager: WindowManager? = null

  // Window 1: Pointer (FLAG_NOT_TOUCHABLE, WRAP_CONTENT)
  private var pointerView: View? = null
  private var pointerParams: WindowManager.LayoutParams? = null

  // Window 2: Stop button (separate WRAP_CONTENT window)
  private var stopButtonView: View? = null
  private var stopButtonParams: WindowManager.LayoutParams? = null

  private var pointerAnimator: ValueAnimator? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    startForegroundServiceWithNotification()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP -> {
        stopOverlay()
        stopSelf()
        return START_NOT_STICKY
      }
      ACTION_MOVE_POINTER -> {
        val x = intent.getIntExtra(EXTRA_X, 200)
        val y = intent.getIntExtra(EXTRA_Y, 400)
        updatePointerPosition(x, y)
        return START_STICKY
      }
      ACTION_TRIGGER_CLICK -> {
        val x = intent.getIntExtra(EXTRA_X, 200)
        val y = intent.getIntExtra(EXTRA_Y, 400)
        triggerClickAnimation(x, y)
        return START_STICKY
      }
      else -> {
        initOverlays()
      }
    }
    return START_STICKY
  }

  private fun startForegroundServiceWithNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        "PhoneMCP Overlay Activo",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Muestra el puntero y botón de parada de PhoneMCP"
      }
      val nm = getSystemService(NotificationManager::class.java)
      nm?.createNotificationChannel(channel)
    }

    val openIntent = Intent(this, MainActivity::class.java)
    val pendingOpen = PendingIntent.getActivity(
      this, 0, openIntent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val stopIntent = Intent(this, PhoneMcpOverlayService::class.java).apply {
      action = ACTION_STOP
    }
    val pendingStop = PendingIntent.getService(
      this, 1, stopIntent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle("PhoneMCP Overlay Activo")
      .setContentText("Puntero y botón PARAR en pantalla (Sin bloqueo táctil)")
      .setSmallIcon(android.R.drawable.ic_menu_compass)
      .setContentIntent(pendingOpen)
      .addAction(android.R.drawable.ic_delete, "PARAR", pendingStop)
      .setOngoing(true)
      .build()

    startForeground(NOTIFICATION_ID, notification)
  }

  private fun initOverlays() {
    if (isServiceRunning) return
    isServiceRunning = true

    setupPointerOverlay()
    setupStopButtonOverlay()
  }

  /**
   * 1. POINTER OVERLAY WINDOW
   * Rules:
   * - Must use FLAG_NOT_TOUCHABLE so touches pass through completely to apps below.
   * - Must use WRAP_CONTENT (NOT MATCH_PARENT).
   */
  private fun setupPointerOverlay() {
    val wm = windowManager ?: return

    val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    // Explicitly WRAP_CONTENT (strictly NOT MATCH_PARENT)
    // FLAG_NOT_TOUCHABLE ensures the pointer does not intercept any taps or gestures
    pointerParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      overlayType,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
          WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
          WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.START
      x = 300
      y = 600
    }

    val pointerCustomView = PointerCustomView(this)
    pointerView = pointerCustomView

    try {
      wm.addView(pointerView, pointerParams)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * 2. STOP BUTTON OVERLAY WINDOW
   * Rules:
   * - Must be a separate window.
   * - Must use WRAP_CONTENT (NOT MATCH_PARENT).
   * - Only the button itself is touchable.
   */
  private fun setupStopButtonOverlay() {
    val wm = windowManager ?: return

    val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    // Strictly WRAP_CONTENT for both width and height (NO MATCH_PARENT)
    stopButtonParams = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      overlayType,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
          WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.END
      x = 24
      y = 150
    }

    val container = LinearLayout(this).apply {
      orientation = LinearLayout.HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      setPadding(dpToPx(12), dpToPx(8), dpToPx(14), dpToPx(8))

      val bgDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dpToPx(24).toFloat()
        setColor(Color.parseColor("#E6111827")) // Cyber dark with transparency
        setStroke(dpToPx(2), Color.parseColor("#EF4444")) // Red neon border
      }
      background = bgDrawable
      elevation = dpToPx(8).toFloat()

      // Pulsing red indicator
      val pulseDot = View(context).apply {
        layoutParams = LinearLayout.LayoutParams(dpToPx(10), dpToPx(10)).apply {
          marginEnd = dpToPx(8)
        }
        background = GradientDrawable().apply {
          shape = GradientDrawable.OVAL
          setColor(Color.parseColor("#EF4444"))
        }
      }
      addView(pulseDot)

      // STOP Label
      val stopText = TextView(context).apply {
        text = "PARAR"
        textSize = 13f
        setTextColor(Color.parseColor("#FFFFFF"))
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
      }
      addView(stopText)

      // Sub-badge for PhoneMCP
      val badge = TextView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.WRAP_CONTENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
          marginStart = dpToPx(6)
        }
        text = "MCP"
        textSize = 9f
        setTextColor(Color.parseColor("#9CA3AF"))
        typeface = android.graphics.Typeface.DEFAULT_BOLD
      }
      addView(badge)
    }

    // Touch listener for dragging or clicking the STOP button
    container.setOnTouchListener(object : View.OnTouchListener {
      private var initialX = 0
      private var initialY = 0
      private var initialTouchX = 0f
      private var initialTouchY = 0f
      private var isClick = false

      override fun onTouch(v: View, event: MotionEvent): Boolean {
        val params = stopButtonParams ?: return false
        when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            initialX = params.x
            initialY = params.y
            initialTouchX = event.rawX
            initialTouchY = event.rawY
            isClick = true
            return true
          }
          MotionEvent.ACTION_MOVE -> {
            val deltaX = (initialTouchX - event.rawX).toInt()
            val deltaY = (event.rawY - initialTouchY).toInt()
            if (Math.abs(deltaX) > 8 || Math.abs(deltaY) > 8) {
              isClick = false
            }
            params.x = initialX + deltaX
            params.y = initialY + deltaY
            windowManager?.updateViewLayout(stopButtonView, params)
            return true
          }
          MotionEvent.ACTION_UP -> {
            if (isClick) {
              handleStopRequested()
            }
            return true
          }
        }
        return false
      }
    })

    stopButtonView = container

    try {
      wm.addView(stopButtonView, stopButtonParams)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun handleStopRequested() {
    vibrateDevice()
    Toast.makeText(this, "⏹ Automatización PhoneMCP detenida", Toast.LENGTH_SHORT).show()
    stopOverlay()
    stopSelf()
  }

  private fun vibrateDevice() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(100)
      }
    } catch (_: Exception) {}
  }

  fun updatePointerPosition(x: Int, y: Int) {
    val wm = windowManager ?: return
    val params = pointerParams ?: return
    val view = pointerView ?: return

    params.x = x - dpToPx(24) // Center pointer on coordinate
    params.y = y - dpToPx(24)
    try {
      wm.updateViewLayout(view, params)
      (view as? PointerCustomView)?.triggerPulse()
    } catch (_: Exception) {}
  }

  fun triggerClickAnimation(x: Int, y: Int) {
    updatePointerPosition(x, y)
    (pointerView as? PointerCustomView)?.triggerClickRipple()
  }

  private fun stopOverlay() {
    val wm = windowManager ?: return
    pointerAnimator?.cancel()
    pointerAnimator = null

    pointerView?.let {
      try {
        wm.removeView(it)
      } catch (_: Exception) {}
      pointerView = null
    }

    stopButtonView?.let {
      try {
        wm.removeView(it)
      } catch (_: Exception) {}
      stopButtonView = null
    }

    isServiceRunning = false
  }

  override fun onDestroy() {
    stopOverlay()
    super.onDestroy()
  }

  private fun dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
  }

  /**
   * Custom Canvas View for Pointer with Cybernetic Holographic Glow.
   * Fixed size (48dp x 48dp), zero MATCH_PARENT.
   */
  private inner class PointerCustomView(context: Context) : View(context) {
    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.parseColor("#4DE0FF")
      style = Paint.Style.STROKE
      strokeWidth = dpToPx(2).toFloat()
    }

    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.parseColor("#00E5FF")
      style = Paint.Style.FILL
    }

    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.parseColor("#8000E5FF")
      style = Paint.Style.STROKE
      strokeWidth = dpToPx(3).toFloat()
    }

    private var pulseRadius = dpToPx(16).toFloat()
    private var rippleRadius = 0f
    private var rippleAlpha = 0

    init {
      layoutParams = FrameLayout.LayoutParams(dpToPx(48), dpToPx(48))

      pointerAnimator = ValueAnimator.ofFloat(dpToPx(12).toFloat(), dpToPx(18).toFloat()).apply {
        duration = 900
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
          pulseRadius = it.animatedValue as Float
          invalidate()
        }
        start()
      }
    }

    fun triggerPulse() {
      invalidate()
    }

    fun triggerClickRipple() {
      ValueAnimator.ofFloat(dpToPx(8).toFloat(), dpToPx(24).toFloat()).apply {
        duration = 400
        addUpdateListener {
          rippleRadius = it.animatedValue as Float
          rippleAlpha = ((1f - it.animatedFraction) * 255).toInt()
          ripplePaint.alpha = rippleAlpha
          invalidate()
        }
        start()
      }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      val size = dpToPx(48)
      setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      val cx = width / 2f
      val cy = height / 2f

      // Click ripple if active
      if (rippleAlpha > 0) {
        canvas.drawCircle(cx, cy, rippleRadius, ripplePaint)
      }

      // Outer animated holographic ring
      canvas.drawCircle(cx, cy, pulseRadius, outerPaint)

      // Inner precise pointer dot
      canvas.drawCircle(cx, cy, dpToPx(5).toFloat(), innerPaint)

      // Crosshair tick marks
      val tickLen = dpToPx(4).toFloat()
      canvas.drawLine(cx - pulseRadius - tickLen, cy, cx - pulseRadius, cy, outerPaint)
      canvas.drawLine(cx + pulseRadius, cy, cx + pulseRadius + tickLen, cy, outerPaint)
      canvas.drawLine(cx, cy - pulseRadius - tickLen, cx, cy - pulseRadius, outerPaint)
      canvas.drawLine(cx, cy + pulseRadius, cx, cy + pulseRadius + tickLen, outerPaint)
    }
  }
}
