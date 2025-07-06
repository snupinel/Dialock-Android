package com.example.dailysummary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    //primary = Purple80,
    //secondary = PurpleGrey80,
    //tertiary = Pink80
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val LightColorScheme = lightColorScheme(
    primary = Blue60,
    onPrimary = Color.White,
    // 밝은 강조 배경 (Container용)
    primaryContainer = Color(0xFFD0E5FF),   // 연한 블루
    onPrimaryContainer = Color(0xFF002D6C),

    // 배경
    background = Color(0xFFFDFDFD),         // 거의 흰색
    onBackground = Color(0xFF1C1B1F),

    // 카드/서페이스 영역
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),

    // 보조 색상 (선택적 사용)
    secondary = Color(0xFF9AA0A6),          // 중립 회색
    onSecondary = Color.White,

    // 강조 포인트용 tertiary (optional)
    tertiary = Color(0xFFF7C3DA),           // 핑크 강조
    onTertiary = Color(0xFF3F001F),

    // 오류 색상
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun DailySummaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    isOverlay: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode && !isOverlay) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}