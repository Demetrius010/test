package com.bignerdranch.android.composetest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Purple200,//устанавливает соответствующие цвета, которые определены в файле Color.kt
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ComposeTestTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {//Эта функция кроме того, что задает визуальный интерфейс, также обеспечивает соответствие приложения текущей теме (светлой или темной) устройства.
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(//Объект MaterialTheme задает визуальный интерфейс в стиле Material Design. Для этого он использует также настройки шрифта в виде объекта Typography из файла Type.kt и настройки формы в виде объекта Shapes из файла Shape.kt
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content//что передается в MaterialTheme? компонент Greeting.
    )
}