package com.ancora.gmaps2.federico.googlemapsactivity.ActivityResources;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.ancora.gmaps2.federico.googlemapsactivity.models.CategoriesAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String url="";

    private OnFragmentInteractionListener mListener;
    private int[] array;
    private String[] names;
    private MainActivityNavigation ma;
    private TopActivity ta;
    private PreferitiNew pa;
    private CategoriesAdapter ph;
    private View rootView;
    public GridView gv;
    String[] values = new String[] { "Android List View",
            "Adapter implementation",
            "Simple List View In Android",
            "Create List View Android",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };
    ArrayAdapter<String> mArrayAdapter;
    private String keyword;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_categories, container, false);
        gv=(GridView) rootView.findViewById(R.id.categoriesGrid);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ma.names[position].equals("Pasticceria")) {
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + ma.latitude + "," + ma.longitude + "&type=bakery&rankby=distance&key=" + ma.key;
                    Log.i("url", url);
                    ma.secondMoveCat();
                    ma.startSearch(url);
                }else if(ma.names[position].equals("Caffe")){
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + ma.latitude + "," + ma.longitude + "&type=cafe&rankby=distance&key=" + ma.key;
                    Log.i("url", url);
                    ma.secondMoveCat();
                    ma.startSearch(url);
                }else if(ma.names[position].equals("Drink")){
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + ma.latitude + "," + ma.longitude + "&type=restaurant&&keyword=pub|drink&rankby=distance&key=" + ma.key;
                    Log.i("url", url);
                    ma.secondMoveCat();
                    ma.startSearch(url);
                }else if(ma.names[position].equals("Vicino a me")){
                    ma.setOld();
                    ma.secondMoveCat();
                }else {
                    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + ma.latitude + "," + ma.longitude + "&type=restaurant&keyword=" + ma.names[position] + "&rankby=distance&key=" + ma.key;
                    Log.i("url", url);
                    ma.secondMoveCat();
                    ma.startSearch(url);
                }

            }
        });

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
        super.onActivityCreated(savedInstanceState);


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
