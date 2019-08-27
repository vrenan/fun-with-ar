package io.vrenan.augmentedimage

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.filament.Filament.init
import kotlinx.android.synthetic.main.wink_animation_view_layout.view.*

class WinkAnimationView : LinearLayout {
    enum class State{
        DISABLED,
        ENABLED
    }

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context)
                .inflate(R.layout.wink_animation_view_layout, this, true)
        setBackgroundResource(R.drawable.rounded_bg)
        setPadding(16,16,16,16)
        setState(State.DISABLED)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        LayoutInflater.from(context)
                .inflate(R.layout.wink_animation_view_layout, this, true)
        setState(State.DISABLED)
    }

    public fun setState(state: State ) {
        when(state) {
            State.ENABLED -> {
                animationView.playAnimation()
                animationText.text = "OBRIGADO"
            }
            State.DISABLED -> {
                animationText.text = "POSTE SUA IDEIA AQUI"
            }
        }
    }
}