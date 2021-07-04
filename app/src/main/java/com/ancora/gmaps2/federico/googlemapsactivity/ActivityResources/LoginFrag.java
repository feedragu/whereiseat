package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.ancora.gmaps2.federico.googlemapsactivity.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private Toolbar toolbar;
    private MainActivityNavigation ma;
    private TopActivity ta;
    private PreferitiNew pa;
    private int c=-1;
    public EditText user;
    public EditText pass;
    public TextInputLayout tUser;
    public TextInputLayout tPas;

    public LoginFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFrag newInstance(String param1, String param2) {
        LoginFrag fragment = new LoginFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(getActivity() instanceof MainActivityNavigation) {
            ma=(MainActivityNavigation)getActivity();
        }else if (getActivity() instanceof TopActivity) {
            ta=(TopActivity) getActivity();
        }else if (getActivity() instanceof PreferitiNew) {
            pa=(PreferitiNew) getActivity();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(" Accedi al tuo account");
        toolbar.setTitleTextColor(Color.WHITE);
        Button bt=(Button) rootView.findViewById(R.id.btn_signup);
        user=(EditText) rootView.findViewById(R.id.etUser);
        pass=(EditText) rootView.findViewById(R.id.passE);
        tUser=(TextInputLayout) rootView.findViewById(R.id.logUs);
        tPas=(TextInputLayout) rootView.findViewById(R.id.logPass);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c==0) {
                    ma.checkLog(user.getText().toString(), pass.getText().toString());
                    InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                } else if(c==1) {
                    ta.checkLog(user.getText().toString(), pass.getText().toString());
                }else {
                    pa.checkLog(user.getText().toString(), pass.getText().toString());
                }
            }
        });
    }

    public void changeActivity() {
        if(getActivity() instanceof MainActivityNavigation) {
            ma=(MainActivityNavigation)getActivity();
            c=0;
        }else if (getActivity() instanceof TopActivity) {
            ta=(TopActivity) getActivity();
            c=1;
        }else if (getActivity() instanceof PreferitiNew) {
            pa=(PreferitiNew) getActivity();
            c=2;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_login, container, false);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void callPopUp() {
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(
                getContext()).create();
        alertDialog.setTitle("Entrato");
        alertDialog.setMessage("LogIn eseguito con successo\n");
        alertDialog.setMessage("\n");
        alertDialog.setMessage("Benvenuto "+user.getText().toString());
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
