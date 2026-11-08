package com.example.ch3mxr.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.*
import com.example.ch3mxr.feature.auth.login.LoginScreen
import com.example.ch3mxr.feature.main.shell.MainShellScreen
import com.example.ch3mxr.feature.profile.model.DemoStudySpaces
import com.example.ch3mxr.feature.ads.ProWelcomeScreen
import com.example.ch3mxr.feature.learning.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ch3mxr_Main)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        setContent {
            val context=LocalContext.current
            val prefs=remember {context.getSharedPreferences("chemxr_demo_preferences",MODE_PRIVATE)}
            val learning=remember {LearningStore(context.applicationContext)}
            var showMain by rememberSaveable {mutableStateOf(false)}
            var showProWelcome by rememberSaveable {mutableStateOf(false)}
            var spaceId by remember {mutableStateOf(prefs.getString("selected_space_id","personal")?:"personal")}
            var themeMode by remember {
                mutableStateOf(runCatching {ThemeMode.valueOf(prefs.getString("theme_mode","DARK")?:"DARK")}.getOrDefault(ThemeMode.DARK))
            }
            var accent by remember {
                mutableStateOf(runCatching {ChemAccent.valueOf(prefs.getString("personal_accent","LIME")?:"LIME")}.getOrDefault(ChemAccent.LIME))
            }
            val space=DemoStudySpaces.available.firstOrNull {it.id==spaceId}?:DemoStudySpaces.available.first()
            val isLight=when(themeMode) {
                ThemeMode.LIGHT->true
                ThemeMode.DARK->false
                ThemeMode.SYSTEM->!androidx.compose.foundation.isSystemInDarkTheme()
            }
            SideEffect {
                WindowInsetsControllerCompat(window,window.decorView).apply {
                    isAppearanceLightStatusBars=isLight
                    isAppearanceLightNavigationBars=isLight
                }
            }
            ChemXRTheme(mode=themeMode,accentColor=if(space.academic) space.primary else accent.color) {
                CompositionLocalProvider(LocalLearningStore provides learning) {
                    BackHandler(enabled=showMain) {showMain=false}
                    if(showMain && showProWelcome && space.id=="personal" && !learning.isPro) {
                        ProWelcomeScreen(onPro={learning.setPlan(StudentPlan.PRO);showProWelcome=false},onSkip={showProWelcome=false})
                    } else if(showMain) {
                        MainShellScreen(themeMode=themeMode,accent=accent,selectedSpaceId=space.id,
                            onSpaceChange={id->if(DemoStudySpaces.available.any {it.id==id}) {
                                spaceId=id;prefs.edit().putString("selected_space_id",id).apply()
                            }},
                            onThemeChange={mode->themeMode=mode;prefs.edit().putString("theme_mode",mode.name).apply()},
                            onAccentChange={next->if(!space.academic) {accent=next;prefs.edit().putString("personal_accent",next.name).apply()}},
                            onLogout={showMain=false;showProWelcome=false})
                    } else {
                        val comingSoon:()->Unit={Toast.makeText(context,"Disponible próximamente",Toast.LENGTH_SHORT).show()}
                        LoginScreen(onLoginClick={_,_,_->showProWelcome=space.id=="personal" && !learning.isPro;showMain=true},
                            onForgotPasswordClick=comingSoon,onRegisterClick=comingSoon,onGoogleClick=comingSoon,onFacebookClick=comingSoon)
                    }
                }
            }
        }
    }
}
