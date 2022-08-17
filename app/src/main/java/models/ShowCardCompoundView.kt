package models

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater.*
import android.widget.LinearLayout
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.FragmentShowDetailsBinding

class ShowCardCompoundView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attr, defStyleAttr){

    lateinit var binding: FragmentShowDetailsBinding

    init {
        binding = FragmentShowDetailsBinding.inflate(from(context))

        clipToOutline = false
        clipToPadding = false

        setPadding(
            context.resources.getDimensionPixelSize(R.dimen.spacing_2x),
            context.resources.getDimensionPixelSize(R.dimen.spacing_1x),
            context.resources.getDimensionPixelSize(R.dimen.spacing_2x),
            context.resources.getDimensionPixelSize(R.dimen.spacing_1x)
        )
    }
}