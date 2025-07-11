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
    primary = Color(0xFF4FA4FF),         // 밝은 파랑 (주요 강조 색)
    onPrimary = Color.White,             // primary 위에 텍스트 등
    primaryContainer = Color(0xFFD4EBFF),

    secondary = Color(0xFF3370B6),       // 보조 파랑
    onSecondary = Color.White,

    background = Color(0xFF101217),      // 전체 배경 - 거의 검은색에 가까운 짙은 회색
    onBackground = Color(0xFFE0E0E0),    // 배경 위 텍스트 - 연회색

    surface = Color(0xFF1C1F24),         // 카드나 다이얼로그 등 surface
    onSurface = Color(0xFFE8EAF0),       // surface 위 텍스트

    surfaceVariant = Color(0xFF2B2F36),  // 추가적인 surface variant (예: 팝업 배경)
    onSurfaceVariant = Color(0xFFB0B8C1),

    outline = Color(0xFF4FA4FF),         // 테두리 - 연한 파랑
    error = Color(0xFFFF6B6B),           // 에러 색상
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Blue60,
    onPrimary = Color.White,
    // 밝은 강조 배경 (Container용)
    primaryContainer = Color(0xFFD0E5FF),   // 연한 블루
    onPrimaryContainer = Color(0xFF002D6C),

    // 배경
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),

    // 카드/서페이스 영역
    surface = Color.White,
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