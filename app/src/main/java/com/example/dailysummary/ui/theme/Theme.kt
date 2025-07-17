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
    primary = Color(0xFF4FA4FF),           // 밝은 파랑 (주요 강조 색)
    onPrimary = Color.White,
    primaryContainer = Color(0xFF003A73),  // primary용 컨테이너(짙은 블루)
    onPrimaryContainer = Color(0xFFD4EBFF),
    inversePrimary = Color(0xFF82C2FF),    // 라이트톤 블루(반전용)

    secondary = Color(0xFF3370B6),         // 보조 파랑
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF274C73),// 더 어두운 보조 파랑 컨테이너
    onSecondaryContainer = Color(0xFFC6E0FF),

    tertiary = Color(0xFF9E77FF),          // 보조 강조(퍼플 계열, 선택적)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3A2C60), // 보라 계열 어두운 컨테이너
    onTertiaryContainer = Color(0xFFE4DAFF),

    background = Color(0xFF101217),        // 전체 배경
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1C1F24),           // 카드/다이얼로그 표면
    onSurface = Color(0xFFE8EAF0),
    surfaceVariant = Color(0xFF2B2F36),    // 팝업/보조 surface
    onSurfaceVariant = Color(0xFFB0B8C1),
    surfaceTint = Color(0xFF4FA4FF),       // surface 위 살짝 입혀지는 틴트(주로 primary)

    inverseSurface = Color(0xFFE0E0E0),    // 라이트한 서페이스(반전용)
    inverseOnSurface = Color(0xFF101217),

    error = Color(0xFFFF6B6B),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),    // 다크 컨테이너용 에러
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF4FA4FF),           // 파랑 테두리
    outlineVariant = Color(0xFF3B4551),    // 어두운 테두리 변형
    scrim = Color(0x66000000)              // 반투명 블랙(모달/다이얼로그 배경)
)


private val LightColorScheme = lightColorScheme(
    primary = Blue60,                        // 기존 값
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E5FF),    // 연한 블루
    onPrimaryContainer = Color(0xFF002D6C),
    inversePrimary = Color(0xFF4FA4FF),      // primary의 반전용(보통 약간 어두운 블루)

    secondary = Color(0xFF9AA0A6),           // 중립 회색
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E3E5),  // 밝은 회색 컨테이너
    onSecondaryContainer = Color(0xFF2C2F33),

    tertiary = Color(0xFFF7C3DA),            // 핑크 강조
    onTertiary = Color(0xFF3F001F),
    tertiaryContainer = Color(0xFFFFD8EB),   // 밝은 핑크 컨테이너
    onTertiaryContainer = Color(0xFF36001A),

    background = Color.White,
    onBackground = Color(0xFF1C1B1F),

    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8EAED),      // 카드/배경 구분용 연한 회색
    onSurfaceVariant = Color(0xFF45474A),
    surfaceTint = Blue60,                    // surface에 살짝 섞이는 틴트 (primary와 동일)

    inverseSurface = Color(0xFF313033),      // 반전 서페이스(주로 다크 배경 위 텍스트)
    inverseOnSurface = Color(0xFFF4EFF4),

    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCD8DF),      // 연한 에러 컨테이너
    onErrorContainer = Color(0xFF400014),

    outline = Color(0xFF73777F),             // 테두리선용
    outlineVariant = Color(0xFFC4C6CA),      // 더 연한 테두리선
    scrim = Color(0x66000000)                // 반투명 블랙 (모달 배경 등)
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}