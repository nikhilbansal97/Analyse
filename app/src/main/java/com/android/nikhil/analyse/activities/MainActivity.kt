package com.android.nikhil.analyse.activities

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.nikhil.analyse.AnalyseApplication
import com.android.nikhil.analyse.R
import com.android.nikhil.analyse.database.ContractClass
import com.android.nikhil.analyse.widget.WidgetRefreshService
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features
import com.ibm.watson.developer_cloud.service.exception.BadRequestException
import com.android.nikhil.analyse.widget.AnalyseWidget.Companion.ACTION_UPDATEWIDGET
import kotlinx.android.synthetic.main.activity_main.analyzeButton
import kotlinx.android.synthetic.main.activity_main.conceptContainer
import kotlinx.android.synthetic.main.activity_main.conceptText
import kotlinx.android.synthetic.main.activity_main.entityContainer
import kotlinx.android.synthetic.main.activity_main.entityText
import kotlinx.android.synthetic.main.activity_main.inputText
import kotlinx.android.synthetic.main.activity_main.loadingView
import kotlinx.android.synthetic.main.activity_main.progressAngry
import kotlinx.android.synthetic.main.activity_main.progressFear
import kotlinx.android.synthetic.main.activity_main.progressJoy
import kotlinx.android.synthetic.main.activity_main.progressSad
import kotlinx.android.synthetic.main.activity_main.resultsContainer
import kotlinx.android.synthetic.main.activity_main.textView
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : AppCompatActivity() {

  private val NLP_USERNAME = "YOUR-USERNAME"
  private val NLP_PASSWORD = "YOUR-PASSWORD"

  lateinit var tracker: Tracker
  var mEntities: String? = ""
  var mConcepts: String? = ""
  var analized: Boolean? = false
  var error = ""

  val isNetworkAvailable: Boolean
    get() {
      val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val info = manager.activeNetworkInfo
      return info != null && info.isConnected
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(toolbar)

    // Setup Analytics Tracker
    val application = application as AnalyseApplication
    tracker = application.defaultTracker

    if (savedInstanceState != null) {
      mEntities = savedInstanceState.getString(getString(R.string.constant_entities))
      mConcepts = savedInstanceState.getString(getString(R.string.constant_concepts))
      analized = savedInstanceState.getBoolean(getString(R.string.constant_anaylized_bool))
      if (analized!!) {
        resultsContainer.visibility = View.VISIBLE
      }
      if (mConcepts!!.isNotEmpty()) {
        conceptText.text = mConcepts
        conceptContainer.visibility = View.VISIBLE
      }
      if (mEntities!!.length > 0) {
        entityText.text = mEntities
        entityContainer.visibility = View.VISIBLE
      }
    }

    // Setup OnClickListener
    analyzeButton.setOnClickListener {
      val text = inputText.text.toString()
      if (isNetworkAvailable) {
        if (text.length > 10) {
          ProcessingAsyncTask(applicationContext).execute(text)
          tracker.send(
              HitBuilders.EventBuilder()
                  .setCategory(getString(R.string.tracker_category))
                  .setAction(getString(R.string.tracker_action))
                  .build()
          )
        } else if (text.length > 0) {
          Toast.makeText(this@MainActivity, R.string.not_enough_length, Toast.LENGTH_SHORT)
              .show()
        } else {
          Toast.makeText(this@MainActivity, R.string.no_text_entered, Toast.LENGTH_SHORT)
              .show()
        }
      } else {
        Toast.makeText(this@MainActivity, R.string.no_internet, Toast.LENGTH_SHORT)
            .show()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    when (id) {
      R.id.history -> {
        val bundle = ActivityOptionsCompat
            .makeSceneTransitionAnimation(this)
            .toBundle()
        val intent = Intent(this@MainActivity, HistoryActivity::class.java)
        startActivity(intent, bundle)
      }
      R.id.docs -> {
        val docsIntent = Intent(this@MainActivity, DocsActivity::class.java)
        startActivity(docsIntent)
      }
    }
    return true
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(getString(R.string.constant_entities), mEntities)
    outState.putString(getString(R.string.constant_concepts), mConcepts)
    if (resultsContainer.visibility == View.VISIBLE)
      analized = true
    outState.putBoolean(getString(R.string.constant_anaylized_bool), analized!!)
    super.onSaveInstanceState(outState)
  }

  inner class ProcessingAsyncTask (private val mContext: Context) : AsyncTask<String, Void, AnalysisResults>() {
    private var mText: String? = null
    private var appWidgetManager: AppWidgetManager? = null

    override fun onPreExecute() {
      appWidgetManager = AppWidgetManager.getInstance(mContext)
      loadingView.visibility = View.VISIBLE
      resultsContainer.visibility = View.GONE
    }

    override fun doInBackground(vararg strings: String): AnalysisResults? {
      mText = strings[0]

      val service = NaturalLanguageUnderstanding(
          NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
          NLP_USERNAME, NLP_PASSWORD
      )

      val emotionOptions = EmotionOptions.Builder()
          .document(true)
          .build()

      val entitiesOptions = EntitiesOptions.Builder()
          .emotion(true)
          .mentions(true)
          .sentiment(true)
          .build()

      val conceptsOptions = ConceptsOptions.Builder()
          .limit(4)
          .build()

      val features = Features.Builder()
          .emotion(emotionOptions)
          .concepts(conceptsOptions)
          .entities(entitiesOptions)
          .build()

      val analyzeOptions = AnalyzeOptions.Builder()
          .text(mText)
          .features(features)
          .build()

      try {
        return service.analyze(analyzeOptions)
            .execute()
      } catch (e: BadRequestException) {
        error = e.localizedMessage
        return null
      }

    }

    override fun onPostExecute(analysisResults: AnalysisResults?) {
      if (analysisResults != null) {

        val values = ContentValues()
        values.put(ContractClass.TextProvider.COLUMN_MAIN_TEXT, mText)

        val emotionResult = analysisResults.emotion
        val conceptsResults = analysisResults.concepts
        val entitiesResults = analysisResults.entities

        val emotionScores = emotionResult.document.emotion
        var emotions = ""
        if (emotionScores != null) {

          val anger = emotionScores.anger.toString()
          val joy = emotionScores.joy.toString()
          val sadness = emotionScores.sadness.toString()
          val fear = emotionScores.fear.toString()

          progressAngry.progress = java.lang.Float.parseFloat(anger)
          progressJoy.progress = java.lang.Float.parseFloat(joy)
          progressSad.progress = java.lang.Float.parseFloat(sadness)
          progressFear.progress = java.lang.Float.parseFloat(fear)

          emotions = (emotions + "EMOTIONS : \n"
              + "Anger : " + anger
              + "\nJoy : " + joy
              + "\nSadness : " + sadness
              + "\nFear : " + fear + "\n")

          values.put(ContractClass.TextProvider.COLUMN_EMOTION, emotions)
        }
        var concepts = ""
        if (conceptsResults != null && conceptsResults.size > 0) {
          concepts = concepts + "CONCEPTS : \n"
          for (result in conceptsResults) {
            concepts = (concepts + "Text: " + result.text + "\n"
                + "Relevance: " + result.relevance.toString() + "\n"
                + "DbpediaRelevance: " + result.dbpediaResource + "\n")
          }
          values.put(ContractClass.TextProvider.COLUMN_CONCEPT, concepts)
        }
        var entities = ""
        if (entitiesResults != null && entitiesResults.size > 0) {
          entities = entities + "ENTITIES : \n"
          for (result in entitiesResults) {
            entities = (entities + "Type: " + result.type + "\n"
                + "Text: " + result.text + "\n"
                + "Relevance: " + result.relevance + "\n")
          }
          values.put(ContractClass.TextProvider.COLUMN_ENTITY, entities)
        }
        val finalResult = concepts + "\n" + entities
        if (finalResult.contains("null"))
          finalResult.replace("null", "")
        //textView.setText(finalResult);
        if (concepts.length > 0) {
          mConcepts = concepts
          conceptContainer.visibility = View.VISIBLE
          conceptText.text = concepts
        }
        if (entities.length > 0) {
          mEntities = entities
          entityContainer.visibility = View.VISIBLE
          entityText.text = entities
        }
        textView.visibility = View.GONE
        loadingView.visibility = View.INVISIBLE
        resultsContainer.visibility = View.VISIBLE
        val cursor =
          mContext.contentResolver.query(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), null, null, null, null)
        var duplicateFound = false
        if (cursor != null) {
          cursor.moveToFirst()
          for (i in 0 until cursor.count) {
            val index = cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_MAIN_TEXT)
            val text = cursor.getString(index)
            if (text == mText) {
              duplicateFound = true
            }
            cursor.moveToNext()
          }
        }
        if (!duplicateFound) {
          // Add the data to database.
          mContext.contentResolver.insert(
              Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), values
          )
          // Update all the widgets.
          val ids = appWidgetManager!!.getAppWidgetIds(ComponentName(mContext, MainActivity::class.java))
          appWidgetManager!!.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
          val refreshIntent = Intent(mContext, WidgetRefreshService::class.java)
          refreshIntent.action = ACTION_UPDATEWIDGET
          mContext.startService(refreshIntent)
        }
      } else {
        Toast.makeText(mContext, error, Toast.LENGTH_SHORT)
            .show()
      }
    }
  }
}
