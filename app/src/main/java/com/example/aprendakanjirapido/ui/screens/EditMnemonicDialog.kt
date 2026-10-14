package com.example.aprendakanjirapido.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aprendakanjirapido.model.KanjiItem

@Composable
fun EditMnemonicDialog(
    kanjiItem: KanjiItem,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember(kanjiItem) { mutableStateOf(kanjiItem.mnemonic) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Mnemônica (${kanjiItem.kanji})") },
        text = {
            Column {
                Text(text = "Significado: ${kanjiItem.significado}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Nova mnemônica") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(text) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
