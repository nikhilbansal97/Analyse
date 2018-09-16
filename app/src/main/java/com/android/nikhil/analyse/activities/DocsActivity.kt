package com.android.nikhil.analyse.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.nikhil.analyse.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_docs.adView
import kotlinx.android.synthetic.main.activity_docs.docsTextView

class DocsActivity : AppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_docs)

    docsTextView.text = getString(R.string.docs)

    val adRequest = AdRequest.Builder()
        .build()
    adView.loadAd(adRequest)
  }
}
