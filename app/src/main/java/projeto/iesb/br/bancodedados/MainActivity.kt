package projeto.iesb.br.bancodedados


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var dao: PersonDao

    private var person: Person = Person()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Room
                .databaseBuilder(applicationContext, AppDatabase::class.java, "meubanco")
                .build()

        dao = database.getPersonDao()

        btNew.setOnClickListener { insertPerson() }
        btSave.setOnClickListener { updatePerson() }
        btDelete.setOnClickListener { deletePerson() }
        ivSearch.setOnClickListener { searchPerson() }
    }

    private fun insertPerson() {
        GlobalScope.launch {
            val p = Person(
                    name = etName.text.toString(),
                    address = etAddress.text.toString(),
                    occupation = etOcupation.text.toString()
            )

            dao.insertPerson(p)
        }
    }

    private fun updatePerson() {
        GlobalScope.launch(Dispatchers.Main) {
            person.name = etName.text.toString()
            person.address = etAddress.text.toString()
            person.occupation = etOcupation.text.toString()
            dao.updatePerson(person)
        }
    }

    private fun deletePerson() {
        GlobalScope.launch(Dispatchers.Main) {
            dao.deletePerson(person)
            etName.setText("")
            etAddress.setText("")
            etOcupation.setText("")
        }
    }

    private fun searchPerson() {
        GlobalScope.launch(Dispatchers.Main) {
            person = dao.getPerson(etSearch.text.toString())
            etName.setText(person.name)
            etAddress.setText(person.address)
            etOcupation.setText(person.occupation)
        }
    }

}


/*
  Entidade Pessoa do banco de dados
*/
@Entity
data class Person(
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,

        var name: String = "",
        var address: String = "",
        var occupation: String = ""
)


/*
  Data Access Object (DAO) de Pessoa
*/
@Dao
interface PersonDao {

    @Query("select * from person where name like '%' || :name || '%' ")
    suspend fun getPerson(name: String): Person

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPerson(p: Person)

    @Update
    suspend fun updatePerson(p: Person)

    @Delete
    suspend fun deletePerson(p: Person)

}

/*
  Banco de dados do aplicativo
*/
@Database(entities = [Person::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPersonDao(): PersonDao
}

