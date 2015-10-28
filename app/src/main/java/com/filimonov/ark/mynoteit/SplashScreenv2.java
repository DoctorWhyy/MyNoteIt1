package com.filimonov.ark.mynoteit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by ArkFil on 29.09.2015.
 */
public class SplashScreenv2 extends Activity {


        //how long until we go to the next activity
        protected int _splashTime = 2000;

        private Thread splashTread;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);

            final SplashScreenv2 sPlashScreen = this;

            // thread for displaying the SplashScreen
            splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized(this){

                            //wait 5 sec
                            wait(_splashTime);
                        }

                    } catch(InterruptedException e) {}
                    finally {
                        finish();

                        //start a new activity
                        Intent i = new Intent();
                        i.setClass(sPlashScreen, MainActivity.class);
                        startActivity(i);

                        finish();
                    }
                }
            };

            splashTread.start();
        }

        //Function that will handle the touch
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                synchronized(splashTread){
                    splashTread.notifyAll();
                }
            }
            return true;
        }

    }
