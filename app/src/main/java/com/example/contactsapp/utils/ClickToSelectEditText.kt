package com.example.contactsapp.utils

import android.content.DialogInterface
import android.widget.ListAdapter


import android.app.AlertDialog
import android.content.Context

import android.graphics.Canvas
import android.util.AttributeSet
import com.example.contactsapp.R

class ClickToSelectEditText<T> : com.google.android.material.textfield.TextInputEditText{
    var mHint: CharSequence
    private lateinit var onItemSelectedListener: OnItemSelectedListener<T>
    var mSpinnerAdapter: ListAdapter? = null

    constructor(context: Context?) : super(context!!) {
        mHint = hint.toString()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        mHint = hint.toString()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        mHint = hint.toString()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        isFocusable = true
        isClickable = true
    }

    fun setAdapter(adapter: ListAdapter?) {
        mSpinnerAdapter = adapter
        configureOnClickListener()
    }

    private fun configureOnClickListener() {
        setOnClickListener { view ->
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder.setTitle(mHint)
            builder.setAdapter(mSpinnerAdapter,
                DialogInterface.OnClickListener { dialogInterface, selectedIndex ->
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelectedListener(
                            mSpinnerAdapter?.getItem(
                                selectedIndex
                            ) as T, selectedIndex
                        )
                    }
                })
            builder.setPositiveButton(R.string.dialog_close_button, null)
       /*     builder.setNegativeButton(R.string.clearall) { dialog, which ->
                dialog.dismiss()
            }*/
            builder.setNeutralButton(R.string.clearall) { dialog, which ->
                onItemSelectedListener.onItemSelectedListener(
                    "clear" as T,0
                )
            }
            builder.create().show()
        }
    }

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener<T>?) {
        this.onItemSelectedListener = onItemSelectedListener!!
    }

    interface OnItemSelectedListener<T> {
        fun onItemSelectedListener(item: T, selectedIndex: Int)
    }
}