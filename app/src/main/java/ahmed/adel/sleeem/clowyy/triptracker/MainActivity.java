package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.User;
import ahmed.adel.sleeem.clowyy.triptracker.managers.DialogAlert;
import ahmed.adel.sleeem.clowyy.triptracker.service.MyService;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    SessionManager session;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //check shared preferences and user login status

        // Session class instance
        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        String userID = //FirebaseAuth.getInstance().getCurrentUser().getUid();
                "uYUjhir14BaF4VZTRU5VskSRSon2";

        getUserTrips(userID);


        session.checkLogin();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialize trip activity intent
                Intent trip  = new Intent(MainActivity.this,TripActivity.class);
                startActivity(trip);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_upcomingTrips, R.id.nav_history)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //components of header view
        View headerView = navigationView.getHeaderView(0);
        ImageView userImage = headerView.findViewById(R.id.userImage);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        String imageUrl = user.get(SessionManager.KEY_ImageURL);
        Log.i("TAG", "onCreate: "+imageUrl);
        switch(imageUrl){
            case "":
                break;
            default:
                Glide.with(getApplicationContext()).load(imageUrl).into(userImage);
                break;
        }

        // email
        String email = user.get(SessionManager.KEY_EMAIL);
        String username = user.get(SessionManager.KEY_NAME);
        TextView userName = headerView.findViewById(R.id.userName);
        userName.setText(username);
        TextView userEmail = headerView.findViewById(R.id.userEmail);
        userEmail.setText(email);


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                int menuId = destination.getId();
                switch (menuId){
                    case R.id.nav_history:
                        fab.hide();
                        break;
                    default:
                        fab.show();
                        break;
                }
            }
        });

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(MainActivity.this, "logout is clicked", Toast.LENGTH_SHORT).show();
            logout(MainActivity.this);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_sync).setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(MainActivity.this, "Sync is clicked", Toast.LENGTH_SHORT).show();
            //add sync function
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(final Activity activity) {


        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Logout");
        //set message
        builder.setMessage(getString(R.string.logoutMSG));
        //positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //call method logout in session class
                session.logoutUser();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
            }
        });
        //negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void getUserTrips(String userID){
        List<Trip> userTrips = new ArrayList<>();
        DatabaseReference userTripsRef = FirebaseDatabase.getInstance().getReference("trips").child(userID);
        userTripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userTrips.add(dataSnapshot.getValue(Trip.class));
                }
              //  userTripsRef.addListenerForSingleValueEvent(null);
                // Change UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveImage(Bitmap bitmap, String fileName){


        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String decodeImage(String imageName){
        UUID uuid = UUID.nameUUIDFromBytes(imageName.getBytes());
            return uuid.toString();
    }

    void syncDataWithFirebaseDatabase(final List<Trip> tripList) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

        reference.child("trips").removeValue();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (int indx = 0; indx < tripList.size(); ++indx) {
            Trip trip = tripList.get(indx);
            reference.child("trips").child(uid).push().setValue(trip).addOnCompleteListener(task -> {
                Toast.makeText(getBaseContext(), "done", Toast.LENGTH_SHORT).show();
            });
        }
    }

}