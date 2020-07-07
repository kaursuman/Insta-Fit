
package com.example.insta_fit;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {
    private static final int DEFAULT_VALUE = 3;
    private int initialValue;
    private NumberPicker numberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.numberpicker_dialog);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValueObject) {
        if (restorePersistedValue) {
            initialValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            initialValue = (Integer) defaultValueObject;
            persistInt(initialValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    public void onBindDialogView(View view) {
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        //Display the number picker values in thousands
        String[] displayedValues = new String[19];

        //Starting from 2000
        for (int i = 0; i < 19; i++)
            displayedValues [i] = String.valueOf((i + 2) * 1000);

        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(20);
        numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setValue(initialValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            initialValue = numberPicker.getValue();
            persistInt(initialValue);
        }
    }
}
