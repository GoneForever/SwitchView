package hd.switchview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((SwitchView)findViewById(R.id.sv)).setOnCheckedChangedListener(new SwitchView.OnCheckedChangedListener() {
            @Override
            public void onCheckedChanged(boolean isChecked) {
                System.out.println(isChecked);
            }
        });
    }

}
