package com.liniumtech.mobile.uRun;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.liniumtech.mobile.uRun.utils_import.PedometerSettings;
import com.liniumtech.mobile.uRun.utils_import.StepService;
import com.liniumtech.mobile.uRun.utils_import.Utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class URunMainActivity extends Activity {

	private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
        
    private int mStepValue;
    private float mDistanceValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private float mSpeedValue;
    private int mMaintain;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
    private boolean mIsRunning; // true when background service is running
    
    /* for media player*/
    private static MediaPlayer mediaPlayer;
    public TextView songName,startTimeField,endTimeField;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000; 
    private int backwardTime = 5000;
    //private SeekBar seekbar;5
    //private ImageButton playButton,pauseButton;
    public static int oneTimeOnly = 0;  
    private int filesToBePlayed = 0;
    /* end of media player*/
        
    /* for new UI*/
    private TextView duration, calories, distance, steps, speed, bodyworkOutDate;
    private long startTimeWorkout = 0L;

	private Handler customHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	private SharedPreferences sharedPreferences;
	private boolean pauseAlready = false;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[ACTIVITY] onCreate");
        super.onCreate(savedInstanceState);
        
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         
        mStepValue = 0;
        setContentView(R.layout.body_workout);
        mUtils = Utils.getInstance();
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
        
        /* for media player*/
/*        songName = (TextView)findViewById(R.id.textView4);
        startTimeField =(TextView)findViewById(R.id.textView1);
        endTimeField =(TextView)findViewById(R.id.textView2);
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        playButton = (ImageButton)findViewById(R.id.imageButton1);
        pauseButton = (ImageButton)findViewById(R.id.imageButton2);
        songName.setText("song.mp3");
        seekbar.setClickable(false);
        pauseButton.setEnabled(false);*/
        
        final int[] files = { R.raw.skrillex };
        mediaPlayer  = MediaPlayer.create(getApplicationContext(), files[filesToBePlayed]);
        this.play(null);
        /*mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				
				mediaPlayer.stop();
				//mediaPlayer =  null;
				//mediaPlayer = MediaPlayer.create(getApplicationContext(), files[filesToBePlayed]);
				play(null);
			}
		});*/
        
        /* end media player*/
        
        
        /* for timer and new UI*/
        startTimeWorkout = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		
		duration = (TextView) findViewById(R.id.durationValueTextView);
		steps = (TextView) findViewById(R.id.stepsValueTextView);
		distance = (TextView) findViewById(R.id.distanceValueTextView);
		calories = (TextView) findViewById(R.id.caloriesValueTextView);
		speed = (TextView) findViewById(R.id.speedValueTextView);
		bodyworkOutDate = (TextView) findViewById(R.id.bodyWorkOutDateTextView);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy (EEEE)");
		String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
		bodyworkOutDate.setText(currentDateandTime);
		/* end timer and new UI */  
    }
    
    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
        
        if(!mediaPlayer.isPlaying()) {
			this.play(null);
		} 
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();
        
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        
        mUtils.setSpeak(mSettings.getBoolean("speak", false));
        
        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();
        
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }
        
        mPedometerSettings.clearServiceRunning();
        
    }
        
    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }

        super.onPause();
        savePaceSetting();
        
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
          
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }
    
    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onDestroy();
    }
    
    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(URunMainActivity.this,
                    StepService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(URunMainActivity.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(URunMainActivity.this,
                  StepService.class));
        }
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
    	
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
        	steps.setText("0");
            distance.setText("0");
            calories.setText("0");
            speed.setText("0");
            duration.setText("00:00:000");
            
            Runnable resetTimerThread = new Runnable() {

    			public void run() {

    				timeInMilliseconds = (long) (SystemClock.uptimeMillis() - startTimeWorkout);

    				int secs = (int) (timeInMilliseconds / 1000);
    				int mins = secs / 60;
    				secs = secs % 60;
    				int milliseconds = (int) (timeInMilliseconds % 1000);
    				duration.setText("" + mins + ":"
    						+ String.format("%02d", secs) + ":"
    						+ String.format("%03d", milliseconds));
    				
    				customHandler.postDelayed(this, 0);
    			}
    		}; 
    		
    		customHandler.removeCallbacks(resetTimerThread);
    		
            
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_BODY_DETAILS = 7;
    private static final int MENU_QUIT     = 9;
    private static final int MENU_GOALS = 8;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
    	
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
            .setIcon(android.R.drawable.ic_media_pause)
            .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
            .setIcon(android.R.drawable.ic_media_play)
            .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        .setShortcut('2', 'r');
        
        menu.add(0, MENU_BODY_DETAILS, 0, "Edit Body Details")
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setShortcut('7', 's');
        
        menu.add(0, MENU_GOALS, 0, "Edit Goals")
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setShortcut('8', 's');
        
        menu.add(0, MENU_QUIT, 0, R.string.quit)
        .setIcon(android.R.drawable.ic_lock_power_off)
        .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                pauseTimer();
                pauseAlready = true;
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                resumeTimer();
                pauseAlready = false;
                return true;
            case MENU_RESET:
                resetValues(true);
                pauseAlready = false;
                return true;
            case MENU_QUIT:
                resetValues(false);
                
                if(!pauseAlready) {
                    unbindStepService();
                    stopStepService();
                } 
                pauseAlready = false;
                mediaPlayer.stop();
                mQuitting = true;
                finish();
                return true;
            case MENU_BODY_DETAILS:
            	
            	// call popup window here
            	
            	final View settingsPrompt = getWindow().getLayoutInflater().inflate(R.layout.body_info, null);
				final Dialog dialog = new Dialog(URunMainActivity.this, R.style.AppTheme);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            	
				final EditText weightEditText = (EditText) settingsPrompt.findViewById(R.id.weightEditText);
				final EditText ageEditText = (EditText) settingsPrompt.findViewById(R.id.ageEditText);
				final RadioGroup genderRadioGroup = (RadioGroup) settingsPrompt.findViewById(R.id.genderRadioGroup);
				
				Button saveInfoBtn = (Button) settingsPrompt.findViewById(R.id.bodyInfoSubmitBtn);
						
				String weightTemp = mSettings.getString("uRun_weight", "");
				String ageTemp = mSettings.getString("uRun_age", "");
				String genderTemp = mSettings.getString("uRun_gender", "");
				 
				 weightEditText.setText(weightTemp);
				 ageEditText.setText(ageTemp);
				 
				 int id = 0;
				 if(genderTemp.equalsIgnoreCase("Female")) {
					 id = 1;
				 }
				 genderRadioGroup.check(genderRadioGroup.getChildAt(id).getId()); 
				
				 saveInfoBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							String weight = weightEditText.getText().toString();
							String age = ageEditText.getText().toString();
							int selected =  genderRadioGroup.getCheckedRadioButtonId();
							RadioButton selectedGenderRadioBtn = (RadioButton) settingsPrompt.findViewById(selected);
							String gender = selectedGenderRadioBtn.getText().toString();
							
							if(weight.trim().isEmpty() && age.trim().isEmpty() && gender.trim().isEmpty()) {
								Toast.makeText(URunMainActivity.this,
										"Missing mandatory fields! Please fill them up!", Toast.LENGTH_SHORT).show();
							} else {
								
								final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								
								SharedPreferences.Editor editor = sharedPreferences.edit();
			                    editor.putString("uRun_weight", weight);
			                    editor.putString("uRun_age", age);
			                    editor.putString("uRun_gender", gender);
			                    editor.commit();
								
			                    dialog.dismiss();
							}
							
						}
					});
				 
				 	LinearLayout.LayoutParams dialogLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				 
					WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
					lp.dimAmount=0.90f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
					dialog.getWindow().setAttributes(lp);
					dialog.getWindow().setLayout(dialogLP.width, dialogLP.height);
					dialog.setContentView(settingsPrompt);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				
            	return true;
            	
            case MENU_GOALS:
            	
            	// call popup window here
            	
            	final View goalsPrompt = getWindow().getLayoutInflater().inflate(R.layout.goals_info, null);
				final Dialog goalsDialog = new Dialog(URunMainActivity.this, R.style.AppTheme);
				goalsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            	
				final EditText calorieEditText = (EditText) goalsPrompt.findViewById(R.id.goalCaloriesEditText);
				final EditText stepsEditText = (EditText) goalsPrompt.findViewById(R.id.goalStepsEditText);
				
				Button saveBtn = (Button) goalsPrompt.findViewById(R.id.startWorkOutBtn);
						
				String calorieTemp = mSettings.getString("uRun_goal_calories", "");
				String stepTemp = mSettings.getString("uRun_goal_steps", "");
				 
				calorieEditText.setText(calorieTemp);
				stepsEditText.setText(stepTemp);
				 
				 saveBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							String calText = calorieEditText.getText().toString();
							String stepText = stepsEditText.getText().toString();
							
							if(calText.trim().isEmpty() && stepText.trim().isEmpty()) {
								Toast.makeText(URunMainActivity.this,
										"Missing mandatory fields! Please fill them up!", Toast.LENGTH_SHORT).show();
							} else {
								
								final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								
								SharedPreferences.Editor editor = sharedPreferences.edit();
			                    editor.putString("uRun_goal_calories", calText);
			                    editor.putString("uRun_goal_steps", stepText);
			                    editor.commit();
								
			                    goalsDialog.dismiss();
							}
							
						}
					});
				 
				 	LinearLayout.LayoutParams dialogLP2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				 
					WindowManager.LayoutParams lp2 = goalsDialog.getWindow().getAttributes();  
					lp2.dimAmount=0.90f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
					goalsDialog.getWindow().setAttributes(lp2);
					goalsDialog.getWindow().setLayout(dialogLP2.width, dialogLP2.height);
					goalsDialog.setContentView(goalsPrompt);
					goalsDialog.setCanceledOnTouchOutside(false);
					goalsDialog.show();
				
            	return true;            	
            	
        }
        return false;
    }

	// TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int) msg.arg1;
                    String stepsGoal = sharedPreferences.getString("uRun_goal_steps", "null");
                    steps.setText("" + mStepValue);
                    
                    if(Integer.parseInt(stepsGoal) == mStepValue) {
                    	Toast.makeText(getApplicationContext(), "You've reached your step goals", Toast.LENGTH_SHORT).show();
                    } 
                    
                    break;
                case PACE_MSG:
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int) msg.arg1)/1000f; // miles value
                	//mDistanceValue = (int) msg.arg1; // km value
                    if (mDistanceValue <= 0) { 
                    	distance.setText("0");
                    }
                    else {
                        distance.setText(("" + (mDistanceValue + 0.000001f)).substring(0, 5)); 
                    }
                    break;
                case SPEED_MSG:
                	mSpeedValue = ((int) msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        speed.setText("0");
                    }
                    else {
                        speed.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = (int) msg.arg1;
                    String caloriesGoal = sharedPreferences.getString("uRun_goal_calories", "null");
                    
                    if (mCaloriesValue <= 0) { 
                    	calories.setText("0");
                    }
                    else {
                    	calories.setText("" + (int) mCaloriesValue);
                    	
                    	if(Integer.parseInt(caloriesGoal) == mCaloriesValue) {
                        	Toast.makeText(getApplicationContext(), "You've reached your calories goals", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };

    /* methods for media player */
    public void play(View view){
    	   Toast.makeText(getApplicationContext(), "Playing sound", 
    	   Toast.LENGTH_SHORT).show();
    	      mediaPlayer.start();
    	      
    	      finalTime = mediaPlayer.getDuration();
    	      startTime = mediaPlayer.getCurrentPosition();
 //   	      if(oneTimeOnly == 0){
 //   	         seekbar.setMax((int) finalTime);
 //   	         oneTimeOnly = 1;
 //   	      } 

//    	      endTimeField.setText(String.format("%d min, %d sec", 
//    	         TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//    	         TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - 
//    	         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//    	         toMinutes((long) finalTime)))
//    	      );
//    	      startTimeField.setText(String.format("%d min, %d sec", 
//    	         TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//    	         TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
//    	         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//    	         toMinutes((long) startTime)))
//    	      );
//    	      seekbar.setProgress((int)startTime);
    	      myHandler.postDelayed(UpdateSongTime,100);
/*    	      pauseButton.setEnabled(true);
    	      playButton.setEnabled(false);*/
    	}

	   private Runnable UpdateSongTime = new Runnable() {
	      public void run() {
	         startTime = mediaPlayer.getCurrentPosition();
	         /*startTimeField.setText(String.format("%d min, %d sec", 
	            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
	            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
	            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
	            toMinutes((long) startTime)))
	         );
	         seekbar.setProgress((int)startTime);*/
	         myHandler.postDelayed(this, 100);
	      }
	   };
	   
	   public void pause(View view){
	      Toast.makeText(getApplicationContext(), "Pausing sound", 
	      Toast.LENGTH_SHORT).show();

	      mediaPlayer.pause();
/*	      pauseButton.setEnabled(false);
	      playButton.setEnabled(true);*/
	   }
	   
	   public void forward(View view){
	      int temp = (int)startTime;
	      if((temp+forwardTime)<=finalTime){
	         startTime = startTime + forwardTime;
	         mediaPlayer.seekTo((int) startTime);
	      }
	      else{
	         Toast.makeText(getApplicationContext(), 
	         "Cannot jump forward 5 seconds", 
	         Toast.LENGTH_SHORT).show();
	      }
	   }
	   
	   public void rewind(View view){
	      int temp = (int)startTime;
	      if((temp-backwardTime)>0){
	         startTime = startTime - backwardTime;
	         mediaPlayer.seekTo((int) startTime);
	      }
	      else{
	         Toast.makeText(getApplicationContext(), 
	         "Cannot jump backward 5 seconds",
	         Toast.LENGTH_SHORT).show();
	      }
	   }    
    /* end media player*/
    	   
    /* for timer thread */
	   
	 private void resumeTimer() {
		 startTimeWorkout = SystemClock.uptimeMillis();
		 customHandler.postDelayed(updateTimerThread, 0);
	 }
	   
	  private void pauseTimer() {

		  timeSwapBuff += timeInMilliseconds;
		  customHandler.removeCallbacks(updateTimerThread);	
		}	   
	   
	   private Runnable updateTimerThread = new Runnable() {

			public void run() {

				timeInMilliseconds = (long) (SystemClock.uptimeMillis() - startTimeWorkout);

				updatedTime = timeSwapBuff + timeInMilliseconds;

				int secs = (int) (updatedTime / 1000);
				int mins = secs / 60;
				secs = secs % 60;
				int milliseconds = (int) (updatedTime % 1000);
				duration.setText("" + mins + ":"
						+ String.format("%02d", secs) + ":"
						+ String.format("%03d", milliseconds));
				customHandler.postDelayed(this, 0);
			}

		};    	   
    /* end timer thread */	   
    	   
    
}