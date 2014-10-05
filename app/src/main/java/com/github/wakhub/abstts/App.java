/**
 * Copyright (C) 2014 wak
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.wakhub.abstts;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Locale;

/**
 * Created by wak on 7/22/14.
 */
public class App extends Application {

    public static final String PACKAGE_NAME = "com.github.wakhub.abstts";

    public static final String ENGINE_NAME = "Abstract Text-to-Speech";

    public static final Locale THAI_LOCALE = new Locale("tha");

    public static final Locale RUSSIAN_LOCALE = new Locale("rus");

    public static final Locale HINDI_LOCALE = new Locale("hin");

    public static PackageInfo getPackageInfo(Context context) {
        String packageName = context.getApplicationInfo().packageName;
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
