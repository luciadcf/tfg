package es.us.managemyteam.ui.view.input

import es.managemyteam.app.R
import es.managemyteam.app.databinding.ViewEditableTextBinding
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import es.us.managemyteam.extension.getStyleTypeArray
import es.us.managemyteam.ui.view.input.CommonInputParams.DEFAULT_HEIGHT

class EditableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object

    val text get() = viewBinding.editableTextInputInfo.text.toString().trim()
    var listener = object : EditableTextChangeListener {
        override fun onTextChanged(text: String) {
            // no-op
        }
    }

    private val viewBinding = ViewEditableTextBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        applyAttributes(attrs)
        initWatcher()
    }

    fun setText(text: String?) {
        viewBinding.editableTextInputInfo.setText(text)
    }

    private fun setInputType(inputType: Int) {
        viewBinding.editableTextInputInfo.inputType = when (inputType) {
            CommonInputType.NONE -> InputType.TYPE_NULL
            CommonInputType.TEXT -> InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            CommonInputType.PASSWORD -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            CommonInputType.EMAIL -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            CommonInputType.MULTI_LINE -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            CommonInputType.PHONE -> InputType.TYPE_CLASS_PHONE
            CommonInputType.NUMBER -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            else -> InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        }

        if (inputType == CommonInputType.NONE) {
            viewBinding.editableTextInputInfo.isFocusable = false
            viewBinding.editableTextInputInfo.isClickable = true
        }
    }

    fun clickListener(listener: (View) -> Unit) {
        viewBinding.editableTextInputInfo.setOnClickListener(listener)
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = getStyleTypeArray(attrs, R.styleable.EditableTextView)
            val hint = typedArray.getString(R.styleable.EditableTextView_input_hint)
            val hintColor = typedArray.getColor(
                R.styleable.EditableTextView_input_hint_color,
                ContextCompat.getColor(context, R.color.primaryWithAlpha)
            )
            val inputType =
                typedArray.getInt(
                    R.styleable.EditableTextView_input_type,
                    CommonInputType.TEXT
                )
            val imeOption =
                typedArray.getInt(R.styleable.EditableTextView_input_ime, CommonInputIme.DONE)
            val height = typedArray.getDimension(
                R.styleable.EditableTextView_input_height,
                DEFAULT_HEIGHT
            )
            val header = typedArray.getString(R.styleable.EditableTextView_input_header)
            val background =
                typedArray.getDrawable(R.styleable.EditableTextView_input_background)
            val solidBackground = typedArray.getBoolean(
                R.styleable.EditableTextView_input_solid_background,
                false
            )

            typedArray.recycle()

            setInputType(inputType)
            setHint(hint)
            setHintColor(hintColor)
            setImeOptions(imeOption)
            setHeader(header)
            setInputBackground(background, solidBackground)

            if (height != DEFAULT_HEIGHT) {
                setHeight(height)
            }
        }
    }

    private fun setInputBackground(background: Drawable?, solidBackground: Boolean = false) {
        background?.let {
            viewBinding.editableTextImgBorder.apply {
                setImageDrawable(background)
                if (solidBackground) {
                    alpha = 1f
                }
            }
        }
    }

    private fun setHeight(height: Float) {
        viewBinding.editableTextInputInfo.gravity = Gravity.START and Gravity.TOP
        viewBinding.editableTextContainerBackground.layoutParams =
            viewBinding.editableTextContainerBackground.layoutParams.apply {
                this.height = height.toInt()
            }
    }

    private fun setHeader(header: String?) {
        viewBinding.editableTextLabelHeader.text = header
        viewBinding.editableTextLabelHeader.visibility = if (TextUtils.isEmpty(header)) {
            GONE
        } else {
            VISIBLE
        }
    }

    private fun setHint(hint: String?) {
        viewBinding.editableTextInputInfo.hint = hint
    }

    private fun setHintColor(colorId: Int) {
        viewBinding.editableTextInputInfo.setHintTextColor(colorId)
    }

    private fun setImeOptions(imeOption: Int) {
        val ime = when (imeOption) {
            CommonInputIme.NEXT -> EditorInfo.IME_ACTION_NEXT
            CommonInputIme.DONE -> EditorInfo.IME_ACTION_DONE
            else -> EditorInfo.IME_ACTION_DONE
        }

        viewBinding.editableTextInputInfo.imeOptions = ime
    }

    private fun initWatcher() {
        viewBinding.editableTextInputInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no-op
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listener.onTextChanged(text?.toString() ?: "")
            }

            override fun afterTextChanged(text: Editable?) {
                // no-op
            }
        })
    }

}