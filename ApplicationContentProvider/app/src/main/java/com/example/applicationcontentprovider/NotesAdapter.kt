package com.example.applicationcontentprovider

import android.database.Cursor
import android.net.sip.SipSession
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationcontentprovider.databse.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import com.example.applicationcontentprovider.databse.NotesDatabaseHelper.Companion.TITLE_NOTES

class NotesAdapter(private val listener: NoteClickedListener): RecyclerView.Adapter<NotesViewHolder>() {

    private var mCursor: Cursor? = null

    //Instância do layout do adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false))

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        mCursor?.moveToPosition(position)

        //Pegando o valor de dentro do cursor "objetoCursor.getString(coluna em que está o valor)"
        holder.noteTitle.text = mCursor?.getString(mCursor?.getColumnIndex(TITLE_NOTES) as Int)
        holder.noteDescription.text = mCursor?.getString(mCursor?.getColumnIndex(DESCRIPTION_NOTES) as Int)
        holder.noteButtonRemove.setOnClickListener{
            //Como o click vem de fora do adapter, o valor de position deve ser pego novamente para que o cursor não se perca
            mCursor?.moveToPosition(position)
            listener.noteRemoveItem(mCursor)
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener{ listener.noteClickedItem(mCursor as Cursor)}
    }

    //Vai popular a variável dentro do adapter e vai notificar o recycleView de que teve uma mudança
    fun setCursor(newCursor: Cursor?){
        mCursor = newCursor
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (mCursor != null) mCursor?.count as Int else 0


}

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    //Instanciando as variáveis do layout
    val noteTitle = itemView.findViewById(R.id.note_title) as TextView
    val noteDescription = itemView.findViewById(R.id.note_description) as TextView
    val noteButtonRemove = itemView.findViewById(R.id.note_button_remove) as Button
}