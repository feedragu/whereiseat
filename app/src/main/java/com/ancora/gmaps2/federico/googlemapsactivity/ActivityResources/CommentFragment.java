package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ancora.gmaps2.federico.googlemapsactivity.Database.DataBasePref;
import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.models.CommentsAdapter;
import com.ancora.gmaps2.federico.googlemapsactivity.models.DividerItemDecorator;
import com.ancora.gmaps2.federico.googlemapsactivity.models.PlacesModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String s;
    private View rootView;
    private Button padd;
    private DetailsComment dc;
    public boolean visible=true;
    private TextView emptyV;
    private LinearLayoutManager mLayoutManager;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinkedList<PlacesModel> address;
    private Socket client;
    public TextView comment;
    private String commento;
    private int pos;
    private PhotoAdapter ph;
    private LinkedList<String> a;
    private LinkedList<String> listSql;
    public RecyclerView lv;
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private RatingBar rat;
    private String place_id;
    private DataBasePref sql;
    private Button btn, btn2;
    public View commentContainer;
    public View commentCont2;


    private OnFragmentInteractionListener mListener;


    public CommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
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

        dc= (DetailsComment) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_comment, container, false);
        lv=(RecyclerView)rootView.findViewById(R.id.commenti);
        /*mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv.setLayoutManager(mLayoutManager);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setHasFixedSize(true);*/
        CommentsAdapter ph=new CommentsAdapter(getContext(), R.layout.listcomment, a, dc.u, dc.listP);
        lv.setAdapter(ph);
        Log.i("perche no", lv.toString());

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String s = bundle.getString("String");
        }

        //dc.exec();
       /* final TextView txt= (TextView) text.getActionView().findViewById(R.id.txtFine);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dc.secondMove();
                commentContainer.setPadding(0,600,0,0);
            }
        });*/

        lv.setHasFixedSize(true);
        lv.addItemDecoration(new DividerItemDecorator(getContext(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv.setLayoutManager(linearLayoutManager);

        super.onActivityCreated(savedInstanceState);
    }

    public void gui() {
        comment=(TextView)rootView.findViewById(R.id.txtComment);
        emptyV=(TextView)rootView.findViewById(R.id.CemptyView);
        checkVis(dc.a.isEmpty());
        final View s=rootView.findViewById(android.R.id.empty);
        commentCont2=rootView.findViewById(R.id.frameIns2);


       // dc.binding();

        final TextView txt2=(TextView)rootView.findViewById(R.id.txtComment2);
        btn2=(Button) rootView.findViewById(R.id.btnComm2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt2.getText().length() != 0) {
                    lv.smoothScrollToPosition(0);
                    dc.onAnswer(String.valueOf(txt2.getText()));
                    txt2.setText("");
                    InputMethodManager imm = (InputMethodManager) dc.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                } else {
                    final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(
                            getContext()).create();
                    alertDialog.setTitle("Impossibile inserire il commento");
                    alertDialog.setMessage("Inserire un commento valido");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.show();
                }
            }
        });
        /*Button btc=(Button) rootView.findViewById(R.id.commentB);
        btc.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition((ViewGroup) commentContainer);
                commentContainer.setPadding(0, 0, 0, 0);
                commentContainer.setVisibility(View.VISIBLE);
                dc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        });*/
        if(visible) {
            commentCont2.setVisibility(View.VISIBLE);
        }else {
            commentCont2.setVisibility(View.GONE);
        }

        address=dc.address;
        pos=dc.pos;
        if(address.get(pos).getId()!=null) {
            place_id = address.get(pos).getId();
        }else {
            place_id=address.get(pos).getLat();
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

    public void checkVis(boolean empty) {
        if(!empty) {
            emptyV.setVisibility(View.GONE);
        }else {
            emptyV.setVisibility(View.VISIBLE);
        }
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




    public class PhotoAdapter extends ArrayAdapter implements View.OnClickListener {
        private LinkedList<String> pm;
        private int resource;
        private LayoutInflater inflater;

        public PhotoAdapter(Context context, int resource, LinkedList<String> objects) {
            super(context, resource, objects);
            this.pm = objects;
            this.resource = resource;
            inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }
            TextView v = (TextView) convertView.findViewById(R.id.txtC);
            v.setText(a.get(position));


            return convertView;
        }

        @Override
        public void onClick(View v) {

        }
    }


}
