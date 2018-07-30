package algo.com.carbookingandroid.UIComponents;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

        private TimePickerDialog.OnTimeSetListener mListener;
        long prevTime = 0;

        public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
            mListener = listener;
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

            int hour = c.get(Calendar.HOUR_OF_DAY);

            //set minute to zero in perspective of UX
            return new TimePickerDialog(getActivity(), mListener, hour, 0, DateFormat.is24HourFormat(getActivity()));
        }

    }