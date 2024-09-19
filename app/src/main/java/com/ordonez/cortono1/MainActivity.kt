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
    // Estado para los nombres de los jugadores
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }
    var namesSubmitted by remember { mutableStateOf(false) }

    if (!namesSubmitted) {
        // Pantalla de entrada de nombres de los jugadores
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Campo de entrada para el nombre del Jugador 1
            OutlinedTextField(
                value = player1Name,
                onValueChange = { player1Name = it },
                label = { Text("Nombre del Jugador 1") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Campo de entrada para el nombre del Jugador 2
            OutlinedTextField(
                value = player2Name,
                onValueChange = { player2Name = it },
                label = { Text("Nombre del Jugador 2") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Botón para confirmar los nombres
            Button(
                onClick = {
                    if (player1Name.isNotBlank() && player2Name.isNotBlank()) {
                        namesSubmitted = true
                    }
                }
            ) {
                Text("Comenzar Juego")
            }
        }
    } else {
        // Juego de Tic-Tac-Toe con los nombres de los jugadores
        TicTacToeBoard(player1Name, player2Name)
    }
}

@Composable
fun TicTacToeBoard(player1Name: String, player2Name: String) {
    var board by remember { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    var scaleFactor by remember { mutableStateOf(1f) }

    val scale by animateFloatAsState(
        targetValue = scaleFactor,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(gameOver) {
        if (gameOver) {
            delay(1000)
            board = Array(3) { Array(3) { "" } }
            currentPlayer = "X"
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

        // Dibuja el tablero 3x3
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
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
                                    if (checkWinner(board)) {
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
                board = Array(3) { Array(3) { "" } }
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

fun checkWinner(board: Array<Array<String>>): Boolean {
    val lines = listOf(
        listOf(board[0][0], board[0][1], board[0][2]),
        listOf(board[1][0], board[1][1], board[1][2]),
        listOf(board[2][0], board[2][1], board[2][2]),
        listOf(board[0][0], board[1][0], board[2][0]),
        listOf(board[0][1], board[1][1], board[2][1]),
        listOf(board[0][2], board[1][2], board[2][2]),
        listOf(board[0][0], board[1][1], board[2][2]),
        listOf(board[0][2], board[1][1], board[2][0])
    )
    return lines.any { line -> line.all { it == "X" } || line.all { it == "O" } }
}

fun isBoardFull(board: Array<Array<String>>): Boolean {
    return board.all { row -> row.all { it.isNotEmpty() } }
}

@Preview(showBackground = true)
@Composable
fun PreviewTicTacToeGame() {
    TicTacToeGame()
}
