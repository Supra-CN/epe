package tw.supra.web;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class TintWebViewActivity extends BaseActivity {
	private static final String LOG_TAG = TintWebViewActivity.class
			.getSimpleName();
	public static final String EXTRA_URL = "url";
	private static final String SCHEME_SMS = "sms";

	private WebView mWebView;
	private TintWebChromeClient mChromeClient;
	private TintWebViewClient mWebClient;
	private TextView mTvLabel;
	View mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String url = getIntent().getStringExtra(EXTRA_URL);
		mChromeClient = new TintWebChromeClient();
		mWebClient = new TintWebViewClient();

		setContentView(R.layout.activity_tint_browser);
		mTvLabel = (TextView) findViewById(R.id.label);
		mWebView = (WebView) findViewById(R.id.web);
		mProgress = findViewById(R.id.progress);

		mWebView.setWebChromeClient(mChromeClient);
		mWebView.setWebViewClient(mWebClient);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.loadUrl(url);
	}

	private class TintWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress > 99) {
				mProgress.setVisibility(View.GONE);
			} else {
				mProgress.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			mTvLabel.setText(title);
		}
	}

	private class TintWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Uri uri = Uri.parse(url);
			if (SCHEME_SMS.equals(uri.getScheme())) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				startActivity(intent);
				return true;
			}
			return false;
		}
	}
}
