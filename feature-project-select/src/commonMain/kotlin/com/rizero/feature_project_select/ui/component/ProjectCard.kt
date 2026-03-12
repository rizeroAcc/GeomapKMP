package com.rizero.feature_project_select.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.shared_core_component.theme.AppTheme
import com.rizero.shared_core_component.ui.ColoredCircle
import com.rizero.shared_core_data.model.Project

@Composable
fun ProjectCard(project: Project, onClick : () -> Unit){
    Card(
        modifier = Modifier
            .clip(CardDefaults.elevatedShape)
            .clickable{ onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp, pressedElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.Colors.CardBackgroundColor),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.padding(top = 8.dp),
                contentAlignment = Alignment.CenterStart
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = project.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ColoredCircle(
                        size = 16.dp,
                        color = when(project.serverID){
                            null -> Color.Red
                            else -> Color.Green
                        }
                    )
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = 6.dp, bottom = 8.dp)
                .background(color = AppTheme.Colors.InfoSpotColor, shape = RoundedCornerShape(12.dp))
                .height(64.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Роль: ${project.role}",
                    modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(vertical = 2.dp),
                    color = Color.Black,
                    thickness = 1.dp
                )
                Text(
                    text = "Количество участников: ${project.membersCount}",
                    modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
@Preview
fun ProjectCardPreview(){
    ProjectCard(
        Project(
            name = "Проект 1",
            serverID = null,
            membersCount = 4,
            id = "yafd543",
            role = 2
        ),{

        }
    )
}
