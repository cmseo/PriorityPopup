package com.example.prioritybasedpopup

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prioritybasedpopup.ui.theme.PriorityBasedPopupTheme
import com.example.prioritybasedpopup.ui.theme.aitsuki.DialogQueue
import com.example.prioritybasedpopup.ui.theme.popup.ItemListDialogFragment

class MainActivity : AppCompatActivity() {
    private val queue = DialogQueue(this, supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PriorityBasedPopupTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        Text(
                            modifier = Modifier.padding(30.dp)
                                .clickable {
                                    queue.offer("foo", 1) { next ->
                                            AlertDialog.Builder(this@MainActivity)
                                            .setMessage("priority 1!")
                                            .setOnDismissListener { next() }
                                            .create()
                                    }
                                    queue.offer("boo", 2) { next ->
                                        AlertDialog.Builder(this@MainActivity)
                                            .setMessage("priority 2!")
                                            .setOnDismissListener { next() }
                                            .create()
                                    }
                                    queue.offer("bar", 3) { next ->
                                        AlertDialog.Builder(this@MainActivity)
                                            .setMessage("priority 3!")
                                            .setOnDismissListener { next() }
                                            .create()
                                    }

                                },
                            text = "show 3 alert pop up"
                        )

                        Text(
                            modifier = Modifier.padding(30.dp)
                                .clickable {

                                    queue.offer("bardds", 2) { next ->
                                        ItemListDialogFragment.newInstance(next, 2)
                                    }

                                    queue.offer("bardd", 3) { next ->
                                        ItemListDialogFragment.newInstance(next,3)
                                    }


                                    queue.offer("dsbardd", 4) { next ->
                                        ItemListDialogFragment.newInstance(next, 4)
                                    }
                                },
                            text = "show bottom Sheet"
                        )

                    }
                    Greeting("Android")
                }
            }
        }
        queue.offer("bar", 2) { next ->
            AlertDialog.Builder(this@MainActivity)
                .setMessage("priority 2!")
                .setOnDismissListener {                     Log.d("MainLog", "dialog AlertDialog")
                    next() }
                .create()
        }

        queue.offer("foo", 3) { next ->
            AlertDialog.Builder(this@MainActivity)
                .setMessage("priority 3!")
                .setOnDismissListener {
                    Log.d("MainLog", "dialog AlertDialog")

                    next() }
                .create()
        }

        queue.offer("boo", 4) { next ->
            AlertDialog.Builder(this@MainActivity)
                .setMessage("priority 4!")
                .setOnDismissListener {
                    Log.d("MainLog", "dialog AlertDialog")

                    next() }
                .create()
        }

        queue.offer("dsbardd", 5) { next ->
            ItemListDialogFragment.newInstance(next, 5)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PriorityBasedPopupTheme {
        Greeting("Android")
    }
}