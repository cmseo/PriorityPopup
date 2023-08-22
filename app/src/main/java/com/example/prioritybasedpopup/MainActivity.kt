package com.example.prioritybasedpopup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.lifecycleScope
import com.example.prioritybasedpopup.ui.theme.PriorityBasedPopupTheme
import com.example.prioritybasedpopup.ui.theme.popup.ItemListDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MainActivity : AppCompatActivity() {
    private val priorityPopupViewModel : PriorityPopupViewModel by viewModels()

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
                                    priorityPopupViewModel.offer("foo", 1) { 
                                            AlertDialog.Builder(this@MainActivity)
                                            .setMessage("priority 1!")
                                            .setOnDismissListener { takeNext() }
                                            .create()
                                    }
                                    priorityPopupViewModel.offer("boo", 2) {
                                        AlertDialog.Builder(this@MainActivity)
                                            .setMessage("priority 2!")
                                            .setOnDismissListener { takeNext() }
                                            .create()
                                    }
                                    priorityPopupViewModel.offer("bar", 3) {
                                        AlertDialog.Builder(this@MainActivity)
                                            .setMessage("priority 3!")
                                            .setOnDismissListener { takeNext() }
                                            .create()
                                    }

                                },
                            text = "show 3 alert pop up"
                        )

                        Text(
                            modifier = Modifier.padding(30.dp)
                                .clickable {
                                    priorityPopupViewModel.offer("bardds", 2) {
                                        ItemListDialogFragment.newInstance(2)
                                    }

                                    priorityPopupViewModel.offer("bardd", 3) {
                                        ItemListDialogFragment.newInstance(3)
                                    }


                                    priorityPopupViewModel.offer("dsbardd", 4) {
                                        ItemListDialogFragment.newInstance(4)
                                    }
                                },
                            text = "show bottom Sheet"
                        )

                    }
                    Greeting("Android")
                }
            }
        }
        priorityPopupViewModel.offer("bar", 2) {
            AlertDialog.Builder(this@MainActivity)
                .setMessage("priority 2!")
                .setOnDismissListener {
                    Log.d("MainLog", "dialog AlertDialog")
                    takeNext()
                }
                .create()
        }

        priorityPopupViewModel.offer("foo", 3) {
            AlertDialog.Builder(this@MainActivity)
                .setMessage("priority 3!")
                .setOnDismissListener {
                    Log.d("MainLog", "dialog AlertDialog")

                    takeNext() }
                .create()
        }

        priorityPopupViewModel.offer("boo", 4) {
            AlertDialog.Builder(this@MainActivity)
                .setMessage("priority 4!")
                .setOnDismissListener {
                    Log.d("MainLog", "dialog AlertDialog")

                    takeNext() }
                .create()
        }

        priorityPopupViewModel.offer("dsbardd", 5) {
            ItemListDialogFragment.newInstance(5)
        }

        lifecycleScope.launchWhenResumed {
            priorityPopupViewModel.popupState.collect {
                when (it) {
                    is Dialog -> it.show()
                    is BottomSheetDialogFragment -> it.show(supportFragmentManager, "")
                }
            }
        }
    }

    fun takeNext() = priorityPopupViewModel.next().invoke()
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