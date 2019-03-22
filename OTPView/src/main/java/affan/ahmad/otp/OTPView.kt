package affan.ahmad.otp

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import java.util.*

/* Copyright (C) 2019 Affan Ahmad Fahmi - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the MIT license.
 */

class OTPView : LinearLayout, View.OnFocusChangeListener, TextWatcher, View.OnKeyListener {

    private var mContext: Context? = null
    private var fieldCount = 6
    private var fieldsList: ArrayList<EditText>? = null
    private var otpListener: OTPListener? = null
    private var move = false

    private val currentFocusIndex: Int
        get() {
            var index = -1
            for (i in 0 until fieldCount)
                if (fieldsList!![i].isFocused) {
                    index = i
                    break
                }
            return index
        }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    constructor(context: Context) : super(context) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun init(context: Context) {
        mContext = context

        var lp =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = CENTER
        this.orientation = LinearLayout.HORIZONTAL
        this.layoutParams = lp
        fieldsList = ArrayList()

        //Adding OTP Fields
        for (i in 0 until fieldCount) {

            val field = EditText(mContext)

            field.textAlignment = View.TEXT_ALIGNMENT_CENTER
            field.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            field.setTextColor(mContext!!.resources.getColor(R.color.h1black))
            field.inputType = InputType.TYPE_CLASS_NUMBER
            field.tag = "EditText:$i"
            field.isEnabled = false

            field.setBackgroundResource(R.drawable.otp_bg)
            field.setPadding(getPX(10f), getPX(10f), getPX(10f), getPX(10f))

            lp = LinearLayout.LayoutParams(getPX(36f), ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(getPX(5f), getPX(5f), getPX(5f), getPX(5f))
            field.layoutParams = lp
            field.isCursorVisible = false

            field.onFocusChangeListener = this
            field.addTextChangedListener(this)
            field.setOnKeyListener(this)

            fieldsList!!.add(field)

            this.addView(field)
        }

        //fieldsList!![fieldsList!!.size - 1].filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))

        selectEditText(fieldsList!![0])

    }

    fun setOTPText(otpText: String) {
        for (i in 0 until fieldCount)
            fieldsList!![i].setText(otpText[i].toString())
        selectEditText(fieldsList!![fieldsList!!.size - 1])
        if (this.otpListener != null)
            this.otpListener!!.onOTPEntered(getOTPText())
    }

    fun getOTPText(): String {
        val otpString = StringBuffer()
        for (editText in fieldsList!!) {
            otpString.append(editText.text)
        }
        return otpString.toString()
    }

    fun setFieldCount(fieldCount: Int) {
        this.fieldCount = fieldCount
    }


    private fun selectEditText(edit_txt: EditText) {
        edit_txt.isEnabled = true

        edit_txt.isFocusableInTouchMode = true
        edit_txt.requestFocus()


        val inputMethodManager = edit_txt.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(edit_txt, InputMethodManager.SHOW_IMPLICIT)


        edit_txt.isActivated = true
        edit_txt.isPressed = true

        //--
        for (f in fieldsList!!)
            f.isEnabled = f.isFocused

    }

    fun setOTPListener(otpListener: OTPListener) {
        this.otpListener = otpListener
    }

    private fun getPX(dip: Float): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        ).toInt()
    }

    private fun moveFocusForward() {
        try {
            selectEditText(fieldsList!![currentFocusIndex + 1])
            fieldsList!![currentFocusIndex].setText(" ")
            val ed = fieldsList!![currentFocusIndex]
            ed.setSelection(ed.text.length)
            move = true
        } catch (e: Exception) {
        }
    }

    private fun moveFocusBackwards() {
        try {
            selectEditText(fieldsList!![currentFocusIndex - 1])
        } catch (e: Exception) {
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        Log.d(TAG, "onFocusChange: " + v.tag)
        (v as EditText).setSelection(v.text.length)
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.otp_active_bg)
        } else {
            if (v.text.isEmpty())
                v.setBackgroundResource(R.drawable.otp_bg)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        Log.d(TAG, "beforeTextChanged: $s")
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.d(TAG, "onTextChanged: " + s.length)

        if (currentFocusIndex == fieldCount - 1)
            try {
                Integer.parseInt(getOTPText())
                otpListener!!.onOTPEntered(getOTPText())
            } catch (e: Exception) {
            }


        var ed = fieldsList!![currentFocusIndex]

        if (s.isEmpty()) {
            if (move) {
                moveFocusBackwards()
                move = false
                return
            } else {
                ed.setText(" ")
                move = true
                ed.setSelection(ed.text.length)
                return
            }
        }


        move = false


        if (currentFocusIndex < fieldCount - 1) {
            if (s.length == 1) {
                if (s[0] != ' ')
                    moveFocusForward()
            } else if (s.length > 1) {
                val c = s.toString()[s.length - 1]
                ed = fieldsList!![currentFocusIndex]
                ed.setText(c.toString())
                ed.setSelection(ed.text.length)
            }
        } else if (s.length > 1) {
            Log.d(TAG, "onTextChanged Text Entered:$s")
            val c = s.toString()[s.length - 1]
            Log.d(TAG, "onTextChanged SetText :$c")
            ed.setText(c.toString())
            ed.setSelection(ed.text.length)
        }
    }

    override fun afterTextChanged(s: Editable) {
        Log.d(TAG, "afterTextChanged: $s")
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        Log.d(TAG, "onKey: $event keyCode: $keyCode")
        /*(v as EditText).setSelection(v.text.length)
        if (keyCode == 67 && event.action == KeyEvent.ACTION_DOWN)
            if (v.text.isEmpty())
                moveFocusBackwards()*/
        return false
    }

    companion object {

        private val TAG = OTPView::class.java.simpleName
    }
}
