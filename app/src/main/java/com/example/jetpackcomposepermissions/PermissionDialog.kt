package com.example.jetpackcomposepermissions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    onOkClick: () -> Unit,
    onDismiss: () -> Unit,
    gotoAppSettings: () -> Unit,
    isPermanentlyDeclined: Boolean,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()){
                Divider(modifier = Modifier.fillMaxWidth())
                Text(
                    text = if(isPermanentlyDeclined){
                        "Grant Permission"
                    }else{
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                gotoAppSettings()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = { Text(text = "Permission Required") },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined)
            )
        },
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * We're following here SOLID Principle instead of doing
 * when block condition of text permission providers
 */
interface PermissionTextProvider{
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems that you permanently declined camera permission. " +
                    "You can go to app settings to grant it"
        }else {
            "This application needs camera permission so that your friends" +
                    "can see you while doing phone call"
        }
    }
}

class AudioRecorderPermissionProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems that you permanently declined microphone permission. " +
                    "You can go to app settings to grant it"
        }else {
            "This application needs microphone permission so that your friends" +
                    "can hear you while doing phone call"
        }
    }
}


class PhoneCallPermissionProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems that you permanently declined phone call permission. " +
                    "You can go to app settings to grant it"
        }else {
            "This application needs your phone call permission so that you " +
                    "can talk to your friends"
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PermissionDialogPreview() {
    PermissionDialog(
        permissionTextProvider = CameraPermissionProvider(),
        onOkClick = {},
        onDismiss = {},
        gotoAppSettings = {},
        isPermanentlyDeclined = true
    )
}

