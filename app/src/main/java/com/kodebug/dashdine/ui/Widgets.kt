package com.kodebug.dashdine.ui

import android.R.attr.letterSpacing
import android.annotation.SuppressLint
import android.icu.text.CaseMap
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodebug.dashdine.R
import com.kodebug.dashdine.components.ShadowButton
import com.kodebug.dashdine.data.oauth.BaseAuthViewModel
import com.kodebug.dashdine.ui.theme.Orange


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GroupSocialButtons(
    title: Int,
    color: Color = Color.White,
    viewModel : BaseAuthViewModel,
) {
    val context = LocalContext.current as ComponentActivity
    BoxWithConstraints {
        val screenWidth = maxWidth
        val spacing = when {
            screenWidth < 360.dp -> 24.dp
            screenWidth < 600.dp -> 52.dp
            else -> 62.dp
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 22.dp), color = color
                )
                Text(
                    text = stringResource(title),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = color,
                    ),
                )
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 22.dp), color = color
                )
            }
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                SocialButton(
                    onClick = { viewModel.onFacebookClicked(context = context) },
                    icon = R.drawable.ic_facebook,
                    title = R.string.facebook,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
                SocialButton(
                    onClick = { viewModel.onGoogleClicked(context = context)},
                    icon = R.drawable.ic_google,
                    title = R.string.google,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
            }
        }
    }
}


@Composable
fun SocialButton(icon: Int, title: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ShadowButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        containerColor = Color.White,
        shadowColor = Color.LightGray.copy(alpha = .4f)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title.toString(),
            tint = Color.Unspecified,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}





@Composable
fun DashDineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedIndicatorColor = Orange,
        unfocusedIndicatorColor = Color.LightGray.copy(alpha = .4f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.DarkGray
    )
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        label?.let {
            Row {
                Spacer(modifier = Modifier.size(4.dp))
                it()
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(fontWeight = FontWeight.SemiBold),
            label = null,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
    }
}