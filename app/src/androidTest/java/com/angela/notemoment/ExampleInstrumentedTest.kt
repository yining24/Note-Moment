package com.angela.notemoment

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.listnote.ListNoteViewModel

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.angela.notemoment", appContext.packageName)
    }


    lateinit var viewModel: ListNoteViewModel

    @Test
    fun listNoteSorted() {

        val sortedNotes = listOf<ListNoteSorted>( ListNoteSorted("", mutableListOf(Note())
        ) ,ListNoteSorted("", mutableListOf(Note()))
        )
        var tempObj = ListNoteSorted()

        val notes = listOf(Note(), Note())
        viewModel.toListNoteSorted(notes)

        assertEquals(Note(), Note())
    }



}
