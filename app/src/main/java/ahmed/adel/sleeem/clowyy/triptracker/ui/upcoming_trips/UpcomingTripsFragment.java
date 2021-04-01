package ahmed.adel.sleeem.clowyy.triptracker.ui.upcoming_trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.R;
import ahmed.adel.sleeem.clowyy.triptracker.adapters.UpcomingTripsAdapter;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;

public class UpcomingTripsFragment extends Fragment implements OnUpcomingAdapterItemClicked {

    //private UpcomingTripsViewModel upcomingTripsViewModel;
    private RecyclerView rv;
    private List<Trip> trips;

    @Override
    public void onStart() {
        super.onStart();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        trips = TripDatabase.getInstance(getContext()).getTripDao().selectAllTrips(userID);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new UpcomingTripsAdapter(getActivity(),trips,this));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //upcomingTripsViewModel = new ViewModelProvider(this).get(UpcomingTripsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_upcomingtrips, container, false);
//        final TextView textView = root.findViewById(R.id.text_upcomingtrips);
//        upcomingTripsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        rv = root.findViewById(R.id.recyclerView);

        return root;
    }

    @Override
    public void onDetailsIconClicked(int position) {

    }

    @Override
    public void onDeleteIconClicked(int position) {

    }

    @Override
    public void onEditIconClicked(int position) {

    }

    @Override
    public void onStartButtonClicked(int position) {

    }
}