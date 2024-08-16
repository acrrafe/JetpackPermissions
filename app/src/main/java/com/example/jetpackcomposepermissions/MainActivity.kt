package com.example.jetpackcomposepermissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposepermissions.ui.theme.JetpackComposePermissionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePermissionsTheme {

                val viewModel = viewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val cameraPermission = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        viewModel.onPermissionResult(
                            permission =  Manifest.permission.CAMERA,
                            isGranted = isGranted
                        )

                    }
                )

                val multiplePermission = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        perms.keys.forEach{ permission ->
                            viewModel.onPermissionResult(
                                permission =  permission,
                                isGranted = perms[permission] == true
                            )
                        }

                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(onClick = {
                        cameraPermission.launch(
                            Manifest.permission.CAMERA
                        )
                    }) {
                        Text(text = "Camera Permission")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        multiplePermission.launch(
                           arrayOf(
                               Manifest.permission.RECORD_AUDIO,
                               Manifest.permission.CALL_PHONE
                           )
                        )
                    }) {
                        Text(text = "Multiple Permission")
                    }
                }
                dialogQueue
                    .reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when(permission){
                                   Manifest.permission.CAMERA -> {
                                       CameraPermissionProvider()
                                   }
                                   Manifest.permission.RECORD_AUDIO -> {
                                       AudioRecorderPermissionProvider()
                                   }
                                   Manifest.permission.CALL_PHONE -> {
                                       PhoneCallPermissionProvider()
                                   }
                                else -> return@forEach
                            },
                            onOkClick = {
                                        viewModel.dismissDialog()
                                multiplePermission.launch(
                                    arrayOf(permission)
                                )
                            },
                            onDismiss = viewModel::dismissDialog,
                            gotoAppSettings = ::openAppSettings,
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission)
                        )
                }
            }
        }
    }
}

/**
 * Extension function for directing the user from the application to the application settings
 */
fun Activity.openAppSettings(){
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
