package com.castigador56.gestiondeusuario

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabasOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DatabasOpenHelper(context = this)
        setContent {
            AddUserScreen()
        }
    }

    @Composable
    fun AddUserScreen() {
        val context = LocalContext.current

        var name by remember { mutableStateOf("") }
        var lastname by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }
        val genderOptions = listOf("Male", "Female", "Other")
        var expanded by remember { mutableStateOf(false) }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        var users by remember { mutableStateOf(dbHelper.getAllUsers()) }

        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // Campos de texto con validación
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = name.isEmpty()
            )
            if (name.isEmpty()) {
                Text("Please enter a name", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = lastname.isEmpty()
            )
            if (lastname.isEmpty()) {
                Text("Please enter a last name", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                isError = age.isEmpty() || age.toIntOrNull() == null
            )
            if (age.isEmpty() || age.toIntOrNull() == null) {
                Text("Please enter a valid age", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Dropdown for Gender selection
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expand Gender Menu")
                    }
                }
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                isError = phone.isEmpty()
            )
            if (phone.isEmpty()) {
                Text("Please enter a phone number", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Text("Please enter a valid email", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para insertar el usuario
            Button(
                onClick = {
                    if (name.isNotEmpty() && lastname.isNotEmpty() && age.isNotEmpty()  && phone.isNotEmpty() && email.isNotEmpty()) {
                        if (dbHelper.insertUser(name, lastname, age.toIntOrNull() ?: 0, "male", phone, email)) {
                            Toast.makeText(context, "Usuario insertado correctamente", Toast.LENGTH_LONG).show()
                            users = dbHelper.getAllUsers()
                        } else {
                            Toast.makeText(context, "Error al insertar el usuario", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Insert User")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar la lista de usuarios
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users) { user ->
                    UserRow(user)
                }
            }
        }
    }


    @Composable
    fun UserRow(user: Map<String, Any>) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)) // Fondo suave con bordes redondeados
                .padding(16.dp) // Espaciado interno
        ) {
            Text(
                text = "Name: ${user["name"]}",
                style = MaterialTheme.typography.bodyLarge, // Usamos un estilo para hacerlo más destacado
                color = Color(0xFF333333) // Color de texto oscuro para mejor legibilidad
            )
            Spacer(modifier = Modifier.height(4.dp)) // Un pequeño espacio entre las filas
            Text(
                text = "Last Name: ${user["lastname"]}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF555555)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Age: ${user["age"]}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF555555)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gender: ${user["gender"]}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF555555)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Phone: ${user["phone"]}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF555555)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Email: ${user["email"]}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF555555)
            )
            Spacer(modifier = Modifier.height(8.dp)) // Espacio adicional al final
        }
    }
}
