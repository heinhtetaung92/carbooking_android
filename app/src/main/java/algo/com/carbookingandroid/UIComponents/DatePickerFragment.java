package algo.com.carbookingandroid.UIComponents;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.lang.reflect.Field;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener mListener;
        private boolean disablePrevDates = false;
        private long prevTime = 0;

        public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
            mListener = listener;
        }

        public void disablePreviousDate(boolean isDisable){
            disablePrevDates = isDisable;
        }

        public void init(long timestamp){
            prevTime = timestamp;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();

            if(prevTime != 0){
                c.setTimeInMillis(prevTime);
            }

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), mListener, year, month, day);

            if(disablePrevDates) {
                Field mDatePickerField;
                try {
                    mDatePickerField = dialog.getClass().getDeclaredField("mDatePicker");
                    mDatePickerField.setAccessible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }

            return dialog;

        }
    }