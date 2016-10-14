package mahmud.picosoft.islamiclecturecollection;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import mahmud.picosoft.islamiclecturecollection.R;

public class ForegroundActivity extends Activity {

	ArrayList<Actors> actorsList;
	ActorAdapter adapter;

	Intent ii;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lectures);

		actorsList = new ArrayList<Actors>();

		new JSONAsyncTask()
				.execute("http://ekushay.com/picosoft/auth_islamic_lecture/jason.php");

		ListView listview = (ListView) findViewById(R.id.list);
		adapter = new ActorAdapter(getApplicationContext(), R.layout.row,
				actorsList);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Intent i = new Intent(ForegroundActivity.this,
						LecturesList.class);
				i.putExtra("positionRow", actorsList.get(position).getLink());
				i.putExtra("lecture", actorsList.get(position).getName());
				startActivity(i);

				finish();

				// if (!boolMusicPlaying) {
				// if (isPlay) {
				// buttonPlayStop.setBackgroundResource(R.drawable.pause);
				// isPlay = false;
				// } else if (!isPlay) {
				// buttonPlayStop.setBackgroundResource(R.drawable.play);
				// isPlay = true;
				// }

				// if (!boolMusicPlaying) {
				// link = actorsList.get(position).getLink();
				// lectureTitle = actorsList.get(position).getName();
				// buttonPlayStop.setBackgroundResource(R.drawable.pause);
				// playAudio();
				// boolMusicPlaying = true;
				// } else {
				// Toast.makeText(getApplicationContext(),
				// "Stop the running one fast, then select another",
				// Toast.LENGTH_LONG).show();
				// }

			}
		});
	}

	class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ForegroundActivity.this);
			dialog.setMessage("Loading, please wait");
			dialog.setTitle("Connecting server");
			dialog.show();
			dialog.setCancelable(false);
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			try {

				// ------------------>>
				HttpGet httppost = new HttpGet(urls[0]);
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(httppost);

				// StatusLine stat = response.getStatusLine();
				int status = response.getStatusLine().getStatusCode();

				if (status == 200) {
					HttpEntity entity = response.getEntity();
					String data = EntityUtils.toString(entity);

					JSONObject jsono = new JSONObject(data);
					JSONArray jarray = jsono.getJSONArray("mahmud");

					for (int i = 0; i < jarray.length(); i++) {
						JSONObject object = jarray.getJSONObject(i);

						Actors actor = new Actors();

						actor.setName(object.getString("lecture"));
						actor.setLink(object.getString("link"));
						actor.setContributor(object.getString("contributor"));
						actor.setTime(object.getString("time"));

						actorsList.add(actor);
					}
					return true;
				}

				// ------------------>>

			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.cancel();
			adapter.notifyDataSetChanged();
			if (result == false) {
				ii = new Intent(ForegroundActivity.this, LecturesList.class);
				startActivity(ii);
				Toast.makeText(getApplicationContext(),
						"Make sure you have an internet connection.",
						Toast.LENGTH_LONG) // Unable to fetch data from server
						.show();
				finish();
			}

		}

		// private void terminat() {
		// finish();
		//
		// }
	}

	// @Override
	// protected void onPause() {
	//
	// ii = new Intent(ForegroundActivity.this,
	// LecturesList.class);
	// startActivity(ii);
	// finish();
	// super.onPause();
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			startActivity(new Intent(getApplicationContext(),
					LecturesList.class));
		} else {
			return super.onKeyDown(keyCode, event);
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.foreground, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.share:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent
					.putExtra(
							Intent.EXTRA_TEXT,
							"Awesome Islamic Authentic Lecture Collection. Download it now http://goo.gl/YNeLhY");
			startActivity(Intent.createChooser(shareIntent, "Share this app.."));
			break;

		case R.id.rate:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://goo.gl/YNeLhY"));
			startActivity(browserIntent);
			break;
		case R.id.more:
			Intent browserIntent2 = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/developer?id=PicoSoft"));
			startActivity(browserIntent2);
			break;
		}
		return true;
	}

}
