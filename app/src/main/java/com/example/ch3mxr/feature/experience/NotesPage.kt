package com.example.ch3mxr.feature.experience

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.feature.main.components.*
import org.json.JSONArray
import org.json.JSONObject

private data class UserNote(val text:String, val photoUri:String="")

private fun readNotes(context:Context):List<UserNote> = runCatching {
    val raw=context.getSharedPreferences("chemxr_user_notes",Context.MODE_PRIVATE).getString("notes","[]") ?: "[]"
    val json=JSONArray(raw)
    (0 until json.length()).map { i ->
        val obj=json.getJSONObject(i)
        UserNote(obj.optString("text"),obj.optString("photo"))
    }
}.getOrDefault(emptyList())

private fun saveNotes(context:Context, notes:List<UserNote>) {
    val json=JSONArray()
    notes.forEach {note->json.put(JSONObject().put("text",note.text).put("photo",note.photoUri))}
    context.getSharedPreferences("chemxr_user_notes",Context.MODE_PRIVATE).edit().putString("notes",json.toString()).apply()
}

/** Apuntes con persistencia local real: texto y URI de imagen con permiso persistente. */
@Composable
fun NotesPage(modifier:Modifier=Modifier) {
    val context=LocalContext.current
    val notes=remember { mutableStateListOf<UserNote>().apply{addAll(readNotes(context))} }
    var draft by remember {mutableStateOf("")}
    var photo by remember {mutableStateOf("")}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri:Uri? ->
        if(uri!=null) {
            runCatching {context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)}
            photo=uri.toString()
        }
    }
    MainPage(modifier) {
        Eyebrow("Estante · Apuntes privados")
        Heading("Mis apuntes.")
        GroupPanel("Crear nota", "Guardada en este dispositivo; la sincronización con API vendrá después") {
            OutlinedTextField(draft,{draft=it},label={Text("Texto, referencias y enlaces")},modifier=Modifier.fillMaxWidth(),minLines=4)
            Spacer(Modifier.height(9.dp))
            ChemButton("Adjuntar imagen",{picker.launch(arrayOf("image/*"))},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED,leadingIcon=Icons.Outlined.AddPhotoAlternate)
            if(photo.isNotBlank()) Text("Foto seleccionada: ${Uri.parse(photo).lastPathSegment ?: "imagen"}",style=MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            ChemButton("Guardar apunte", {
                if(draft.isNotBlank()||photo.isNotBlank()) {
                    notes.add(0,UserNote(draft,photo))
                    saveNotes(context,notes)
                    draft="";photo=""
                }
            },Modifier.fillMaxWidth())
        }
        if(notes.isEmpty()) Text("Todavía no tienes apuntes.")
        notes.toList().forEachIndexed { index,note ->
            GroupPanel("Apunte ${notes.size-index}","Privado · Guardado en el teléfono") {
                if(note.text.isNotBlank()) Text(note.text)
                if(note.photoUri.isNotBlank()) {
                    val thumbnail=remember(note.photoUri) {
                        runCatching {
                            context.contentResolver.openInputStream(Uri.parse(note.photoUri))?.use { stream ->
                                val options=BitmapFactory.Options().apply {inSampleSize=4}
                                BitmapFactory.decodeStream(stream,null,options)?.asImageBitmap()
                            }
                        }.getOrNull()
                    }
                    if(thumbnail!=null) Image(thumbnail,"Imagen del apunte",modifier=Modifier.fillMaxWidth().heightIn(max=230.dp))
                    else Text("Imagen no disponible en este dispositivo")
                }
                Spacer(Modifier.height(8.dp))
                ChemButton("Eliminar apunte",{notes.remove(note);saveNotes(context,notes)},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
            }
        }
    }
}
