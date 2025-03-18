package com.drag0n.ideaplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drag0n.ideaplatform.data.Item
import com.drag0n.ideaplatform.data.RoomDB
import com.drag0n.ideaplatform.ui.theme.Light_blue
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val db by lazy { RoomDB.getDatabase(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(color = Light_blue)

                Column() {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(Light_blue)
                            .padding(25.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = "Список товаров",
                            fontSize = 28.sp
                        )
                    }
                    Item(db = db)
                }



        }
    }
}

fun parsText(text: String): List<String> {
    return text.removeSurrounding("[", "]")
        .split(",")
        .map { it.trim().removeSurrounding("\"") }
}

private fun parsDate(time: Int): String {
    val dateFormat = "dd.MM.yyyy"
    val date = SimpleDateFormat(dateFormat, Locale.US)
    val resultDate = date.format(time)
    return resultDate
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item(db: RoomDB) {
    val list by db.CourseDao().getAll().observeAsState(listOf())

    val openDeleteDialog = remember { mutableStateOf(false) }
    val openEditDialog = remember { mutableStateOf(false) }
    val stateItem = remember { mutableStateOf<Item?>(null) }
    val stateAmount = remember { mutableIntStateOf(0) }
    val stateInputText = remember { mutableStateOf("") }
    val filterList =
        list.filter { item -> item.name.contains(stateInputText.value, ignoreCase = true) }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        SimpleOutlinedTextFieldSample(stateInputText)
        filterList.forEach { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),

                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.elevatedCardElevation(3.dp)
            ) {
                Column(
                    Modifier
                        .background(Color.White)
                        .padding(8.dp)) {
                    Row {
                        Text(
                            modifier = Modifier.fillMaxWidth(0.8F),
                            text = item.name,
                            fontSize = 25.sp,
                            softWrap = true
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Row {
                                Image(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable {
                                            openEditDialog.value = true
                                            stateItem.value = item
                                            stateAmount.intValue = item.amount
                                        },
                                    painter = painterResource(
                                        id = R.drawable.ic_create
                                    ),
                                    contentDescription = "",

                                    )
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.ic_del
                                    ),
                                    contentDescription = "",
                                    modifier = Modifier.clickable {
                                        openDeleteDialog.value = true
                                        stateItem.value = item
                                    }

                                )
                            }
                        }
                    }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top
                    )
                    {
                        val text = parsText(item.tags)
                        text.forEach { textItem ->
                            if (textItem.isNotEmpty()) {
                                Text(
                                    text = textItem, modifier = Modifier
                                        .padding(2.dp)
                                        .border(
                                            1.dp,
                                            Color.Gray,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(10.dp),
                                )
                            }
                        }


                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.fillMaxSize(0.5F)) {
                            Text(text = "На складе")
                            Text(
                                text = if (item.amount > 0) item.amount.toString()
                                else "Нет в наличии",
                                Modifier.padding(top = 8.dp)
                            )
                        }
                        Column(Modifier.fillMaxSize()) {
                            Text(text = "Дата добавления")
                            Text(
                                text = parsDate(item.time),
                                Modifier.padding(top = 8.dp)
                            )
                        }

                    }


                }

            }
        }

    }

    if (openDeleteDialog.value) {
        stateItem.value?.let {
            AlertDialogDelete(
                db = db,
                item = it,
                state = openDeleteDialog
            )
        }
    }
    if (openEditDialog.value) {
        stateItem.value?.let {
            AlertDialogCreate(
                db = db,
                item = it,
                state = openEditDialog,
                stateAmount = stateAmount
            )
        }
    }


}

@Composable
fun AlertDialogDelete(
    db: RoomDB,
    item: Item,
    state: MutableState<Boolean>
) {

    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Удаление товара")
        },
        text = {
            Text(text = "Вы действительно хотите удалить выбранный товар?")
        },
        onDismissRequest = {
            state.value = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.CourseDao().deleteItem(item)
                    }
                    state.value = false
                }
            ) {
                Text("Да")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    state.value = false
                }
            ) {
                Text("Нет")
            }
        }
    )
}

@Composable
fun AlertDialogCreate(
    db: RoomDB,
    item: Item,
    state: MutableState<Boolean>,
    stateAmount: MutableState<Int>,
) {

    AlertDialog(
        icon = {
            Icon(Icons.Default.Settings, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Количество товара")
        },
        text = {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(painter = painterResource(id = R.drawable.ic_minus), contentDescription = "",
                    Modifier.clickable {
                        if (stateAmount.value >0) --stateAmount.value

                    })
                Text(
                    text = stateAmount.value.toString(),
                    fontSize = 35.sp
                )
                Image(painter = painterResource(id = R.drawable.ic_add), contentDescription = "",
                    Modifier.clickable {
                        ++stateAmount.value
                    })
            }

        },
        onDismissRequest = {
            state.value = false
        },

        confirmButton = {
            TextButton(
                onClick = {
                    state.value = false
                    CoroutineScope(Dispatchers.IO).launch {
                        db.CourseDao().updateItem(item.copy(amount = stateAmount.value))
                    }
                }
            ) {
                Text("Принять")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    state.value = false
                }
            ) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun SimpleOutlinedTextFieldSample(text: MutableState<String>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text("Поиск товаров") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "" )
        },
        trailingIcon = {
            if (text.value.isNotEmpty()) {
                IconButton(onClick = { text.value = "" }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Text")
                }
            }
        }
    )
}

