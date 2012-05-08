package fi.harism.shaderize;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button mButtonMenu;
	private GLSurfaceView mGLSurfaceView;
	private MainRenderer mMainRenderer;

	private TextView mTextViewInfo;
	private TextView mTextViewTitle;
	private Timer mTimerFramesPerSecond;

	private View mViewMenu;

	@Override
	public void onBackPressed() {
		if (mViewMenu.getVisibility() == View.VISIBLE) {
			showMenu(false);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		mTextViewTitle = (TextView) findViewById(R.id.text_title);
		mTextViewInfo = (TextView) findViewById(R.id.text_info);
		mViewMenu = findViewById(R.id.menu);

		mButtonMenu = (Button) findViewById(R.id.button_menu);
		mButtonMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View button) {
				showMenu(mViewMenu.getVisibility() == View.GONE);
			}
		});

		mMainRenderer = new MainRenderer();
		mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLSurfaceView.setRenderer(mMainRenderer);
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		mTimerFramesPerSecond = new Timer();
		mTimerFramesPerSecond.schedule(new FramesPerSecondTask(), 0, 1000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mTimerFramesPerSecond.cancel();
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (mViewMenu.getVisibility() == View.VISIBLE) {
			showMenu(false);
			return true;
		}
		return super.onTouchEvent(me);
	}

	private void showMenu(boolean showMenu) {
		if (!showMenu && mViewMenu.getVisibility() == View.VISIBLE) {
			AlphaAnimation anim = new AlphaAnimation(1f, 0f);
			mViewMenu.setAnimation(anim);
			anim.setDuration(500);

			anim.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					mViewMenu.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation anim) {
				}

				@Override
				public void onAnimationStart(Animation anim) {
				}
			});
			anim.startNow();
			mViewMenu.invalidate();
		} else if (showMenu && mViewMenu.getVisibility() == View.GONE) {
			mViewMenu.setVisibility(View.VISIBLE);
			AlphaAnimation anim = new AlphaAnimation(0f, 1f);
			mViewMenu.setAnimation(anim);
			anim.setDuration(500);
			anim.startNow();
		}
	}

	private class FramesPerSecondTask extends TimerTask {
		@Override
		public void run() {
			if (Looper.myLooper() != Looper.getMainLooper()) {
				runOnUiThread(this);
				return;
			}

			String text = getResources().getString(R.string.title,
					mMainRenderer.getFramesPerSecond());
			mTextViewTitle.setText(text);
		}
	}
}
