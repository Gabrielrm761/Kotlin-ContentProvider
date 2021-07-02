package com.example.applicationcontentprovider

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationcontentprovider.databse.NotesDatabaseHelper.Companion.TITLE_NOTES
import com.example.applicationcontentprovider.databse.NotesProvider.Companion.URI_NOTES
import com.google.android.material.floatingactionbutton.FloatingActionButton

//Primeiro o Recycler vai ser carregado com nada dentro, enquanto o LoaderManager vai fazer a busca no contentprovider em segundo plano. Assim que tiver a data nele, ele vai dar um setCursor
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    lateinit var noteRecyclerView: RecyclerView
    lateinit var noteAdd: FloatingActionButton

    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd = findViewById(R.id.notes_add)
        noteAdd.setOnClickListener{
            NotesDetailFragment().show(supportFragmentManager, "dialog")
        }

        adapter = NotesAdapter(object : NoteClickedListener{
            override fun noteClickedItem(cursor: Cursor) {
                //vai pegar o Id do click do RecycleView, do resultado que o cursor trouxer
                val id = cursor?.getLong(cursor.getColumnIndex(_ID))
                val fragment = NotesDetailFragment.newInstance(id)
                fragment.show(supportFragmentManager, "dialog")
            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id = cursor?.getLong(cursor.getColumnIndex(_ID))
                contentResolver.delete(Uri.withAppendedPath(URI_NOTES, id.toString()), null, null) //contentResolver é o objeto responsável por comunicação com o contentprovider
            }
        })
        adapter.setHasStableIds(true) //para que não tenham Id repetidos no adapter

        noteRecyclerView = findViewById(R.id.notes_recycler)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = adapter //Passando o adapter para o RecyclerView

        LoaderManager.getInstance(this).initLoader(0, null, this) // Background Thread
    }

    //Método para instanciar aquilo que será buscado, no caso, a pesquisa feita do contentprovider
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    //Método para pegar os dados recebidos e manipular
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null) { adapter.setCursor(data)}
    }

    //Método para finalizar a pesquisa em segundo plano do LoaderManager
    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.setCursor(null)
    }
}