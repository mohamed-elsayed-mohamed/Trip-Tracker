package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logoImage;
    TextView sloganTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        logoImage=findViewById(R.id.imageView);

        sloganTxt = findViewById(R.id.textView);
        logoImage.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.side_anim));
        sloganTxt.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.bottom_anim));

        new Handler().postDelayed(()->{
            startActivity(new Intent(getBaseContext(),Login.class));
        },5005);
    }
}