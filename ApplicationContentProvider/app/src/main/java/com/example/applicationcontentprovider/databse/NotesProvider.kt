package com.example.applicationcontentprovider.databse

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns._ID
import com.example.applicationcontentprovider.databse.NotesDatabaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    private lateinit var mUriMatcher: UriMatcher  // O objeto responsável por fazer a validação da URL de requisição do contentprovider
    private  lateinit var  dbHelper: NotesDatabaseHelper

    override fun onCreate(): Boolean {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH) //Sempre instanciando ele vazio
        //É aqui onde se define os endereços e identificações que o contentprovider vai ter
        mUriMatcher.addURI(AUTHORITY,"notes", NOTES)
        mUriMatcher.addURI(AUTHORITY,"notes/#", NOTES_BY_ID) // notes/# indica que tem uma query string
        if (context != null) {dbHelper = NotesDatabaseHelper(context as Context)
        }
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        //utilizamos o match para verificar se a uri passada é válida
        if (mUriMatcher.match(uri) == NOTES_BY_ID){
            val db: SQLiteDatabase = dbHelper.writableDatabase //Significa que ele está habilitado para mexer no SQLite
            val linesAffect = db.delete(TABLE_NOTES, "_ID =?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else{
            throw UnsupportedSchemeException("Uri inválida para exclusão!")
        }
    }

    //Utilizado apenas para requisição de arquivos, não será utilizado pois o projeto utiliza apenas dados simples
    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("Uri não implementado!")

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (mUriMatcher.match(uri) == NOTES){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id = db.insert(TABLE_NOTES, null, values)
            // BASE_URI é o endereço do contentprovider, no insertUri ele está inserindo o valor "id.toString()" dentro do SQLite
            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri,null)
            return insertUri
        } else{
            throw UnsupportedSchemeException("Uri inválida para inserção!")
        }
    }

    //SELECT do contentprovider
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when{
            mUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                //O cursor é o retorno de um contentprovider
                val cursor = db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor // retorno
            }
            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(TABLE_NOTES, projection, "$_ID = ?", arrayOf(uri.lastPathSegment), null, null, sortOrder)
                cursor.setNotificationUri((context as Context).contentResolver, uri)
                cursor
            }
            else -> {
                throw UnsupportedSchemeException("Uri não implementada.")
            }
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect = db.update(TABLE_NOTES, values, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else{
            throw UnsupportedSchemeException("Uri não implementada.")
        }
    }

    companion object{
        const val AUTHORITY = "com.example.applicationcontentprovider.provider"
        val BASE_URI = Uri.parse("content:/$AUTHORITY") // Forma para se requisitar o contentprovider em qualquer aplicação
        val URI_NOTES = Uri.withAppendedPath(BASE_URI,"notes") // nomeando uma URL "notes"
        // O withAppendedPath é responsável por adicionar o "/" e o pathSegmente "notes" ao endereço
        //"content://com.example.applicationcontentprovider.provider/notes" esse é o endereço responsável por acessar todos dados que estiverem no contentprovider


        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}