package com.blackwood3.driveroo;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonFragment extends Fragment {

    private Button startBtn2;
    private Button endBtn;

    public interface OnDataPasser {
        public void sendMsg(String data);
    }

    OnDataPasser dataPasser;

    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPasser) context;
    }

    public void passData(String data) {
        dataPasser.sendMsg(data);
    }

    public ButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_button, container, false);
        startBtn2 = (Button) mView.findViewById(R.id.startBtn2);
        endBtn = (Button) mView.findViewById(R.id.endBtn);

        startBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent clickBtn = new Intent(getActivity(), MainActivity.class);
//                clickBtn.putExtra("command", "start");
//                startActivity(clickBtn);
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.getChronometer();
                Log.d("dev", "pass data");
                passData("start");
            }
        });

//        startBtn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                TextView timeTv = getActivity().findViewById(R.id.timeTv);
////                timeTv.setText("Started!");
//                MainActivity mainActivity = (MainActivity) getActivity();
//
//                Chronometer mChronometer = mainActivity.getmChronometer();
//                Context mainContext = mainActivity.getMainContext();
//
//                mainActivity.startWorking(mChronometer, mainContext);
//
//            }
//        });
        


        return mView;
    }

}
