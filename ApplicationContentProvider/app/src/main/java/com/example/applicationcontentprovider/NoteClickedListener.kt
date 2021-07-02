package com.example.applicationcontentprovider

import android.database.Cursor

//responsável pelas ações de click dentro do adapter, vai ser implementada na activity e levada para o adapter
interface NoteClickedListener {

    fun noteClickedItem(cursor: Cursor)
    fun noteRemoveItem(cursor: Cursor?) //Pode ser null
}