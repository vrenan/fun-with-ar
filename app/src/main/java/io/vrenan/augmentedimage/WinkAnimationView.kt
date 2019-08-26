package io.vrenan.augmentedimage

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.filament.Filament.init

class WinkView : LinearLayout {
    enum class State{
        DISABLED,
        ENABLED
    }

    lateinit var text: TextView
    lateinit var animationView: LottieAnimationView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet):    super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?,    defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setPreAnimationText(text: String) {

    }

    fun setPosAnimationText(text: String) {

    }

    fun setState(state: State) {
        when(state) {
            State.ENABLED -> {
                animationView.playAnimation()
            }
            State.DISABLED -> {

            }
        }
    }
}