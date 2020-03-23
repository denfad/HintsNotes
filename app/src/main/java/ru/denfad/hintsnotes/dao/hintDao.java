package ru.denfad.hintsnotes.dao;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;
import java.util.List;

import ru.denfad.hintsnotes.models.Hint;


public class hintDao extends AppCompatActivity {

    private static hintDao instance;

    private ArrayList<Hint> hints;

    private int mapSize=0;





    private hintDao(){

        hints=new ArrayList<>();
    }

    public static hintDao getInstance(){
        Log.e("get instance", "get instance");
        if(instance == null){		//если объект еще не создан
            instance = new hintDao();	//создать новый объект

        }

        return instance;
    }

    public ArrayList<Hint> getHints(){
        return hints;
    }


    public void addAllHints(List<Hint> hints1){
        mapSize= hints1.size();
        hints.addAll(hints1);
    }

    public void addHint(Hint hint){
        mapSize++;
        Log.e("ADD", hint.getTitle()+ " "+hint.getPosition());
        hints.add(hint);
    }

    public Hint findHint(String title) throws NullPointerException{
        for(Hint h: hints) {
            if (h.getTitle().toLowerCase().equals(title.toLowerCase())) return h;
        }
        throw new NullPointerException();
    }

    public void updateHint(Hint hint){

        Log.e("UPDATE", hint.getTitle()+ " "+hint.getPosition());

        hints.set(hint.getPosition(),hint);

    }

    public Hint getHint(int id){
        return hints.get(id);
    }

    public int getMapSize() {
        return mapSize;
    }

    public void deleteHint(Hint hint) {
        mapSize--;
        hints.remove(hint.getPosition());

        Log.e("Delete", hint.getTitle()+ " "+hint.getPosition());
    }

    public List<Hint> getAllHints(){
        return hints;
    }


}
