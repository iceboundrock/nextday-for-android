/*
 * Copyright 2015 Ruoshi Li
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package li.ruoshi.nextday.views

import android.app.Activity
import android.os.Bundle
import li.ruoshi.nextday.R
import android.graphics.Typeface
import android.widget.TextView

/**
 * Created by ruoshili on 1/15/15.
 */
public class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Thin.ttf");
        val tv =  findViewById(R.id.logo_text) as TextView;
        tv.setTypeface(tf);
    }

    override fun onResume() {
        // TODO: 发起网络请求，获取前5天的数据，并下载当天的图片
        super.onResume()
    }
}