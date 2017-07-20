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
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import mahmud.picosoft.islamiclecturecollection.R;

public class ForegroundActivity extends Activity {

    ArrayList<Actors> actorsList;
    ActorAdapter adapter;

    private AsyncTask mAsyncTask;

    private int pageNumber;// = 1;

    Intent ii;

    boolean isProgressBarLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();


    }

    private void initView() {

        if (!isNetworkAvailable()) {
            showNetworkErrorMessage();
        } else {

            setContentView(R.layout.activity_main);
            actorsList = new ArrayList<Actors>();

            pageNumber = 1;
            getDataFromServer();

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

                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable()) {
                        getDataFromServer();
                    } else {
                        Toast.makeText(ForegroundActivity.this, "Internet connection lost!!!", Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }


    private void getDataFromServer() {

        //if (isNetworkAvailable()) {
        mAsyncTask = new JSONAsyncTask()
                .execute("http://mahmud.vubon.com/android/jason.php?page=" + pageNumber);
        //.execute("http://ekushay.com/picosoft/auth_islamic_lecture/jason.php");
        pageNumber++;
        isProgressBarLoading = false;

        //} else {

        //showNetworkErrorMessage();
        //}

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
            dialog.setCancelable(true);
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

            if (!result) {
                pageNumber -- ;
                Toast.makeText(ForegroundActivity.this, "Seems to reach at the end of the list!!!", Toast.LENGTH_LONG).show();
            }

        }
    }


    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    private void showNetworkErrorMessage() {

        LinearLayout layout = new LinearLayout(ForegroundActivity.this);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(5, 5, 5, 5);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(ForegroundActivity.this);
        imageView.setImageResource(R.drawable.icon2);
        layout.addView(imageView);

        TextView textView = new TextView(ForegroundActivity.this);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(60);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "font/always.ttf"));

        textView.setPadding(0, 12, 0, 12);
        textView.setText("Make sure you have an internet connection.");
        layout.addView(textView);

        Button b = new Button(ForegroundActivity.this);
        b.setWidth(80);
        b.setText("RETRY...");
        b.setTypeface(Typeface.createFromAsset(getAssets(), "font/always.ttf"));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
            }
        });
        layout.addView(b);

        setContentView(layout);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //finish();
            //startActivity(new Intent(getApplicationContext(), LecturesList.class));
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
