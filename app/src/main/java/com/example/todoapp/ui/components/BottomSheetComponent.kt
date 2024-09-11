package com.example.todoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    placeholder: String = "",
    buttonText: String = "Confirm",
    initialText: String = "",
) {
    var inputText by remember { mutableStateOf(initialText) }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Solicita el enfoque al abrir el bottom sheet
    LaunchedEffect(showSheet) {
        if (showSheet) {
            focusRequester.requestFocus()
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .background(colorScheme.background)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(text = placeholder) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        onConfirm(inputText)
                        inputText = ""
                        coroutineScope.launch { sheetState.hide() }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {

                    TextFieldComponent(
                        value = "Categoría",
                        onValueChange = { },
                        cornerRadius = 10.dp,
                        borderWidth = 1.dp,
                        borderColor = colorScheme.inverseSurface,
                        textStyle = typography.labelSmall.copy(textAlign = TextAlign.Center),
                        rowModifier = Modifier.padding(4.dp)
                    )

                    Button(
                        onClick = {
                            onConfirm(inputText)
                            inputText = ""
                            coroutineScope.launch { sheetState.hide() }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = buttonText)
                    }
                }

            }
        }
    }
}