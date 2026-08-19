import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arif.jetpackcomposed.whatappclone.ui.chat.ChatScreen
import com.arif.jetpackcomposed.whatappclone.viewmodel.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatData(
    val uid: String,
    val name: String,
    val message: String,
    val time: String,
    val unreadCount: Int
)

@Composable
fun HomePage(
    homeViewModel: HomeViewModel = viewModel()
) {
    val whatsappGreen = Color(0xFF075E54)

    var searchText by remember { mutableStateOf("") }
    var selectedChat by remember { mutableStateOf<ChatData?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.loadUsers()

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            homeViewModel.loadChats(currentUserUid)
        }
    }

    // Direct state reading from ViewModel
    val usersList = homeViewModel.users.value
    val chatsList = homeViewModel.chats.value

    val chats = usersList.map { user ->
        val chat = chatsList.find { it.userId == user.uid }

        ChatData(
            uid = user.uid,
            name = user.name,
            message = chat?.lastMessage ?: "",
            time = if (chat?.lastMessageTime != null && chat.lastMessageTime > 0L) {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(chat.lastMessageTime))
            } else {
                ""
            },
            unreadCount = chat?.unreadCount ?: 0
        )
    }

    if (selectedChat != null) {
        ChatScreen(
            userName = selectedChat!!.name,
            receiverId = selectedChat!!.uid,
            onBack = {
                selectedChat = null
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ================= HEADER =================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(whatsappGreen)
                .padding(start = 16.dp, end = 8.dp, top = 18.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chating App",
                modifier = Modifier.weight(1f),
                fontSize = 25.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera",
                    tint = Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }

        // ================= TABS =================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(whatsappGreen),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CHATS",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "UPDATES",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "CALLS",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ================= SEARCH =================
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search chats") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(30.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ================= CHAT LIST (LazyColumn for better performance) =================
        val filteredChats = chats.filter {
            it.name.contains(searchText, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                filteredChats.filter { it.uid.isNotBlank() },
                key = { it.uid }
            ) { chat ->
                ChatItem(
                    name = chat.name,
                    message = chat.message,
                    time = chat.time,
                    unreadCount = chat.unreadCount,
                    onClick = {
                        selectedChat = chat
                    }
                )
            }
        }
    }
}

@Composable
fun ChatItem(
    name: String,
    message: String,
    time: String,
    unreadCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Circle with centered text
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (time.isNotEmpty()) {
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}