package com.ordonez.cortono1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.random.Random
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeGame()
        }
    }
}

@Composable
fun TicTacToeGame() {
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }
    var boardSize by remember { mutableStateOf(3) } // Tamaño inicial del tablero 3x3
    var namesSubmitted by remember { mutableStateOf(false) }
    var sizeSelected by remember { mutableStateOf(false) }

    if (!namesSubmitted) {
        // Pantalla de entrada de nombres de los jugadores
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = player1Name,
                onValueChange = { player1Name = it },
                label = { Text("Nombre del Jugador 1") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = player2Name,
                onValueChange = { player2Name = it },
                label = { Text("Nombre del Jugador 2") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (player1Name.isNotBlank() && player2Name.isNotBlank()) {
                        namesSubmitted = true
                    }
                }
            ) {
                Text("Confirmar Nombres")
            }
        }
    } else if (!sizeSelected) {
        // Pantalla para seleccionar el tamaño del tablero
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Selecciona el tamaño del tablero:", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { boardSize = 3; sizeSelected = true }) {
                    Text("3x3")
                }
                Button(onClick = { boardSize = 4; sizeSelected = true }) {
                    Text("4x4")
                }
                Button(onClick = { boardSize = 5; sizeSelected = true }) {
                    Text("5x5")
                }
            }
        }
    } else {
        // Juego de Tic-Tac-Toe adaptado al tamaño del tablero
        TicTacToeBoard(player1Name, player2Name, boardSize)
    }
}

@Composable
fun TicTacToeBoard(player1Name: String, player2Name: String, boardSize: Int) {
    var board by remember { mutableStateOf(Array(boardSize) { Array(boardSize) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    var scaleFactor by remember { mutableStateOf(1f) }

    // Obtener el ancho de la pantalla
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // Calcular el tamaño de las celdas basado en el tamaño del tablero
    val cellSize = (screenWidthDp / boardSize) - 8.dp  // 8.dp es el padding entre las celdas

    val scale by animateFloatAsState(
        targetValue = scaleFactor,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(gameOver) {
        if (gameOver) {
            delay(1000)
            board = Array(boardSize) { Array(boardSize) { "" } }
            currentPlayer = if (Random.nextBoolean()) "X" else "O"  // Jugador inicial aleatorio
            winner = null
            gameOver = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mostrar el ganador si lo hay, o el turno del jugador
        winner?.let {
            Text(
                text = "Ganador: ${if (it == "X") player1Name else player2Name}",
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )
        } ?: run {
            Text(
                text = "Turno de: ${if (currentPlayer == "X") player1Name else player2Name}",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dibuja el tablero de acuerdo al tamaño seleccionado
        for (i in 0 until boardSize) {
            Row {
                for (j in 0 until boardSize) {
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(4.dp)
                            .scale(scale)
                            .background(
                                if (board[i][j] == "X") Color.Red else if (board[i][j] == "O") Color.Blue else Color.Gray,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (board[i][j] == "" && winner == null) {
                                    board[i][j] = currentPlayer
                                    scaleFactor = 1.2f
                                    if (checkWinner(board, boardSize)) {
                                        winner = currentPlayer
                                        gameOver = true
                                    } else if (isBoardFull(board)) {
                                        winner = "Empate"
                                        gameOver = true
                                    }
                                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                                    scaleFactor = 1f
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[i][j],
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                board = Array(boardSize) { Array(boardSize) { "" } }
                currentPlayer = "X"
                winner = null
                gameOver = false
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Reiniciar")
        }
    }
}

fun checkWinner(board: Array<Array<String>>, boardSize: Int): Boolean {

    // Verificar filas
    for (i in 0 until boardSize) {
        for (j in 0..boardSize - 3) {
            // Verifica cada sublista de 3 elementos consecutivos en la fila
            if (board[i][j] != "" && board[i][j] == board[i][j + 1] && board[i][j] == board[i][j + 2]) {
                return true
            }
        }
    }

    // Verificar columnas
    for (i in 0 until boardSize) {
        for (j in 0..boardSize - 3) {
            // Verifica cada sublista de 3 elementos consecutivos en la columna
            if (board[j][i] != "" && board[j][i] == board[j + 1][i] && board[j][i] == board[j + 2][i]) {
                return true
            }
        }
    }

    // Verificar diagonales (principal y secundaria)
    for (i in 0..boardSize - 3) {
        for (j in 0..boardSize - 3) {
            // Diagonal principal (de arriba a abajo)
            if (board[i][j] != "" && board[i][j] == board[i + 1][j + 1] && board[i][j] == board[i + 2][j + 2]) {
                return true
            }

            // Diagonal inversa (de arriba a abajo)
            if (board[i][boardSize - j - 1] != "" && board[i][boardSize - j - 1] == board[i + 1][boardSize - j - 2] && board[i][boardSize - j - 1] == board[i + 2][boardSize - j - 3]) {
                return true
            }
        }
    }

    return false
}


fun isBoardFull(board: Array<Array<String>>): Boolean {
    return board.all { row -> row.all { it.isNotEmpty() } }
}

@Preview(showBackground = true)
@Composable
fun PreviewTicTacToeGame() {
    TicTacToeGame()
}
