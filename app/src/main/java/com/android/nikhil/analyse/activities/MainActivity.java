package com.android.nikhil.analyse.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.nikhil.analyse.AnalyseApplication;
import com.android.nikhil.analyse.R;
import com.android.nikhil.analyse.database.ContractClass;
import com.android.nikhil.analyse.widget.WidgetRefreshService;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionScores;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;

import java.util.List;

import at.markushi.ui.CircleButton;

import static com.android.nikhil.analyse.widget.AnalyseWidget.ACTION_UPDATEWIDGET;

public class MainActivity extends AppCompatActivity {

    public final String NLP_USERNAME = "YOUR-USERNAME";
    public final String NLP_PASSWORD = "YOUR-PASSWORD";

    public TextView textView;
    public EditText editText;
    public CircleButton analyzeButton;
    public Toolbar toolbar;
    public TextView entityTextView;
    public TextView conceptTextView;
    public CardView entityContainer;
    public CardView conceptContainer;

    public RoundCornerProgressBar joyProgress;
    public RoundCornerProgressBar angerProgress;
    public RoundCornerProgressBar fearProgress;
    public RoundCornerProgressBar sadProgress;
    public ScrollView scrollview;
    public SpinKitView loadingView;

    public Tracker tracker;
    public String mEntities = "";
    public String mConcepts = "";
    public Boolean analized = false;
    public String error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Views
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.inputText);
        analyzeButton = findViewById(R.id.analyzeButton);
        joyProgress = findViewById(R.id.progressJoy);
        sadProgress = findViewById(R.id.progressSad);
        fearProgress = findViewById(R.id.progressFear);
        angerProgress = findViewById(R.id.progressAngry);
        scrollview = findViewById(R.id.resultsContainer);
        entityTextView = findViewById(R.id.entityText);
        conceptTextView = findViewById(R.id.conceptText);
        entityContainer = findViewById(R.id.entityContainer);
        conceptContainer = findViewById(R.id.conceptContainer);
        loadingView = findViewById(R.id.loadingView);
        setSupportActionBar(toolbar);

        // Setup Analytics Tracker
        AnalyseApplication application = (AnalyseApplication) getApplication();
        tracker = application.getDefaultTracker();

        if (savedInstanceState != null) {
            mEntities = savedInstanceState.getString(getString(R.string.constant_entities));
            mConcepts = savedInstanceState.getString(getString(R.string.constant_concepts));
            analized = savedInstanceState.getBoolean(getString(R.string.constant_anaylized_bool));
            if (analized) {
                scrollview.setVisibility(View.VISIBLE);
            }
            if (mConcepts.length() > 0) {
                conceptTextView.setText(mConcepts);
                conceptContainer.setVisibility(View.VISIBLE);
            }
            if (mEntities.length() > 0) {
                entityTextView.setText(mEntities);
                entityContainer.setVisibility(View.VISIBLE);
            }
        }

        // Setup OnClickListener
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (isNetworkAvailable()) {
                    if (text.length() > 10) {
                        new ProcessingAsyncTask(getApplicationContext()).execute(text);
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.tracker_category))
                                .setAction(getString(R.string.tracker_action))
                                .build());
                    } else if (text.length() > 0) {
                        Toast.makeText(MainActivity.this, R.string.not_enough_length, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.no_text_entered, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.history:
                Bundle bundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this)
                        .toBundle();
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent, bundle);
                break;
            case R.id.docs:
                Intent docsIntent = new Intent(MainActivity.this, DocsActivity.class);
                startActivity(docsIntent);
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.constant_entities), mEntities);
        outState.putString(getString(R.string.constant_concepts), mConcepts);
        if (scrollview.getVisibility() == View.VISIBLE)
            analized = true;
        outState.putBoolean(getString(R.string.constant_anaylized_bool), analized);
        super.onSaveInstanceState(outState);
    }

    public class ProcessingAsyncTask extends AsyncTask<String, Void, AnalysisResults> {

        private Context mContext;
        private String mText;
        private AppWidgetManager appWidgetManager;

        private ProcessingAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            appWidgetManager = AppWidgetManager.getInstance(mContext);
            loadingView.setVisibility(View.VISIBLE);
            scrollview.setVisibility(View.GONE);
        }

        @Override
        protected AnalysisResults doInBackground(String... strings) {
            mText = strings[0];

            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                    NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                    NLP_USERNAME, NLP_PASSWORD
            );

            EmotionOptions emotionOptions = new EmotionOptions.Builder()
                    .document(true)
                    .build();

            EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
                    .emotion(true)
                    .mentions(true)
                    .sentiment(true)
                    .build();

            ConceptsOptions conceptsOptions = new ConceptsOptions.Builder()
                    .limit(4)
                    .build();

            Features features = new Features.Builder()
                    .emotion(emotionOptions)
                    .concepts(conceptsOptions)
                    .entities(entitiesOptions)
                    .build();

            AnalyzeOptions analyzeOptions = new AnalyzeOptions.Builder()
                    .text(mText)
                    .features(features)
                    .build();

            try {
                return service.analyze(analyzeOptions).execute();
            } catch (BadRequestException e) {
                error = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AnalysisResults analysisResults) {
            if (analysisResults != null) {

                ContentValues values = new ContentValues();
                values.put(ContractClass.TextProvider.COLUMN_MAIN_TEXT, mText);

                EmotionResult emotionResult = analysisResults.getEmotion();
                List<ConceptsResult> conceptsResults = analysisResults.getConcepts();
                List<EntitiesResult> entitiesResults = analysisResults.getEntities();

                EmotionScores emotionScores = emotionResult.getDocument().getEmotion();
                String emotions = "";
                if (emotionScores != null) {

                    String anger = String.valueOf(emotionScores.getAnger());
                    String joy = String.valueOf(emotionScores.getJoy());
                    String sadness = String.valueOf(emotionScores.getSadness());
                    String fear = String.valueOf(emotionScores.getFear());

                    angerProgress.setProgress(Float.parseFloat(anger));
                    joyProgress.setProgress(Float.parseFloat(joy));
                    sadProgress.setProgress(Float.parseFloat(sadness));
                    fearProgress.setProgress(Float.parseFloat(fear));

                    emotions = emotions + "EMOTIONS : \n"
                            + "Anger : " + anger
                            + "\nJoy : " + joy
                            + "\nSadness : " + sadness
                            + "\nFear : " + fear + "\n";

                    values.put(ContractClass.TextProvider.COLUMN_EMOTION, emotions);
                }
                String concepts = "";
                if (conceptsResults != null && conceptsResults.size() > 0) {
                    concepts = concepts + "CONCEPTS : \n";
                    for (ConceptsResult result : conceptsResults) {
                        concepts = concepts + "Text: " + result.getText() + "\n"
                                + "Relevance: " + String.valueOf(result.getRelevance()) + "\n"
                                + "DbpediaRelevance: " + result.getDbpediaResource() + "\n";
                    }
                    values.put(ContractClass.TextProvider.COLUMN_CONCEPT, concepts);
                }
                String entities = "";
                if (entitiesResults != null && entitiesResults.size() > 0) {
                    entities = entities + "ENTITIES : \n";
                    for (EntitiesResult result : entitiesResults) {
                        entities = entities + "Type: " + result.getType() + "\n"
                                + "Text: " + result.getText() + "\n"
                                + "Relevance: " + result.getRelevance() + "\n";
                    }
                    values.put(ContractClass.TextProvider.COLUMN_ENTITY, entities);
                }
                String finalResult = concepts + "\n" + entities;
                if (finalResult.contains("null"))
                    finalResult.replace("null", "");
                //textView.setText(finalResult);
                if (concepts.length() > 0) {
                    mConcepts = concepts;
                    conceptContainer.setVisibility(View.VISIBLE);
                    conceptTextView.setText(concepts);
                }
                if (entities.length() > 0) {
                    mEntities = entities;
                    entityContainer.setVisibility(View.VISIBLE);
                    entityTextView.setText(entities);
                }
                textView.setVisibility(View.GONE);
                loadingView.setVisibility(View.INVISIBLE);
                scrollview.setVisibility(View.VISIBLE);
                Cursor cursor = mContext.getContentResolver().query(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
                        null, null, null, null);
                boolean duplicateFound = false;
                if (cursor != null) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        int index = cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_MAIN_TEXT);
                        String text = cursor.getString(index);
                        if (text.equals(mText)) {
                            duplicateFound = true;
                        }
                        cursor.moveToNext();
                    }
                }
                if (!duplicateFound) {
                    // Add the data to database.
                    mContext.getContentResolver().insert(
                            Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), values);
                    // Update all the widgets.
                    int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, MainActivity.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView);
                    Intent refreshIntent = new Intent(mContext, WidgetRefreshService.class);
                    refreshIntent.setAction(ACTION_UPDATEWIDGET);
                    mContext.startService(refreshIntent);
                }
            } else {
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
