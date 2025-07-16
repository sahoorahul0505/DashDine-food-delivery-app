package com.kodebug.dashdine.ui.features.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodebug.dashdine.R
import com.kodebug.dashdine.ui.DashDineTextField
import com.kodebug.dashdine.ui.GroupSocialButtons
import com.kodebug.dashdine.ui.ShadowButton
import com.kodebug.dashdine.ui.theme.Orange


@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = modifier
        .fillMaxSize()
        .background(color = Color.White)) {
        Image(
            painter = painterResource(id = R.drawable.ic_auth_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier.fillMaxSize()
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 60.dp, horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.Sign_Up),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, modifier = modifier.fillMaxWidth()
            )
            Spacer(modifier = modifier.size(26.dp))
            DashDineTextField(
                value = name,
                onValueChange = {name = it},
                label = {
                    Text(
                        text = stringResource(R.string.full_name),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )
            Spacer(modifier = modifier.size(26.dp))
            DashDineTextField(
                value = email,
                onValueChange = {email = it},
                label = {
                    Text(
                        text = stringResource(R.string.email),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )
            Spacer(modifier = modifier.size(26.dp))
            DashDineTextField(
                value = password,
                onValueChange = {password = it},
                label = {
                    Text(
                        text = stringResource(R.string.password),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = "hide password",
                            modifier = modifier.size(24.dp)
                        )
                    }

                },
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )
            Spacer(modifier = modifier.size(30.dp))
            ShadowButton(
                onClick = {},
                text = R.string.sign_up_button,
                textColor = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(.7f).height(64.dp),
                containerColor = Orange,
                shadowColor = Orange.copy(alpha = .3f)
            )
//            Button(
//                onClick = {},
//                modifier = modifier
//                    .height(64.dp)
//                    .fillMaxWidth(.7f)
//            ) {
//                Text(
//                    text = stringResource(R.string.sign_up_button),
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    letterSpacing = 2.sp,
//                    color = Color.White
//                )
//            }
            Spacer(modifier = modifier.height(20.dp))
            TextButton(
                onClick = {},
                modifier = modifier.fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.already_have_account),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = modifier.width(6.dp))
                    Text(
                        text = stringResource(id = R.string.login),
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Spacer(modifier = modifier.height(32.dp))
            GroupSocialButtons(
                title = R.string.sign_up_title,
                color = Color.Black,
                onGoogleClick = {},
                onFacebookClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(modifier: Modifier = Modifier) {
    SignUpScreen()
}