package com.example.sistemadeproyectosuaq.ui.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemadeproyectosuaq.data.network.Project
import com.example.sistemadeproyectosuaq.ui.theme.SistemaDeProyectosUAQTheme

@Composable
fun ProjectListScreen(
    onProjectClick: (Project) -> Unit,
    viewModel: ProjectListViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is ProjectListUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProjectListUiState.Error -> {
                Text(text = uiState.message, color = Color.Red)
            }
            is ProjectListUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.projects) { project ->
                        ProjectListItem(project = project, onClick = { onProjectClick(project) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectListItem(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(project.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(project.description, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectListScreenPreview() {
    SistemaDeProyectosUAQTheme {
        val projects = listOf(
            Project("1", "Project Alpha", "Description for Alpha", "", ""),
            Project("2", "Project Beta", "Description for Beta", "", "")
        )
        LazyColumn {
            items(projects) {
                ProjectListItem(project = it, onClick = {})
                HorizontalDivider()
            }
        }
    }
}
